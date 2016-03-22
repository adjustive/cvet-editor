package io.cvet.editor.gui;

import org.lwjgl.input.Keyboard;

import io.cvet.editor.gfx.Colour;
import io.cvet.editor.gfx.Render;

public class Cursor extends Component {
	public static enum CursorStyle {
		Block,
		Line,
	}

	private TextArea owner;
	private Colour colour = new Colour(30, 30, 30);
	
	// where the cursor is positioned in the buffer
	// insertion x, y
	private int ix, iy;
	
	private CursorStyle cursorStyle;
	private int xOffset, yOffset;
	private int charWidth, charHeight;
	private int padding;
	
	private CursorAction cursorAction = null;
	
	public Cursor(TextArea owner, CursorStyle style) {
		this.owner = owner;
		this.cursorStyle = style;
		this.ix = iy = 0;
		charWidth = Render.MONOSPACED_FONT.getWidth("a");
		charHeight = Render.MONOSPACED_FONT.getHeight();
		this.h = charHeight;
		this.w = cursorStyle == CursorStyle.Block ? charWidth : 1;
	}
	
	@Override
	public void init() {
		
	}

	@Override
	public void update() {
		this.x = owner.x;
		this.y = owner.y;
		this.visible = owner.visible;
		
		while (this.visible && Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				int keyCode = Keyboard.getEventKey();
				switch (keyCode) {
				case Keyboard.KEY_LSHIFT:
				case Keyboard.KEY_RSHIFT:
				case Keyboard.KEY_LCONTROL:
				case Keyboard.KEY_RCONTROL:
				case Keyboard.KEY_LMETA:
				case Keyboard.KEY_RMETA:
				case Keyboard.KEY_LMENU:
				case Keyboard.KEY_RMENU:
				case Keyboard.KEY_F2:
				case Keyboard.KEY_ESCAPE:
					// nothing
					break;
				case Keyboard.KEY_BACK:
					owner.backspace(this, ix, iy);
					break;
				case Keyboard.KEY_LEFT:
					if (ix > 0) {
						move(-1, 0);
					}
					break;
				case Keyboard.KEY_RIGHT:
					if (ix < owner.getLine(iy).length()) {
						move(1, 0);
					}
					break;
				case Keyboard.KEY_UP:
					if (iy >= 0) {
						int prevLineLen = owner.getLine(iy - 1).length();
						if (ix >= prevLineLen) {
							move(prevLineLen - ix, -1);
						} else {
							move(0, -1);
						}
					}
					break;
				case Keyboard.KEY_DOWN:
					if (ix >= owner.getLine(iy).length()
						&& iy < owner.getLineCount() - 1) {
						int nextLineLen = owner.getLine(iy + 1).length();
						if (ix <= nextLineLen) {
							move(nextLineLen - ix, 1);
						} else if (ix >= nextLineLen) {
							move(nextLineLen - ix, 1);
						}
					} else if (iy < owner.getLineCount() - 1){
						move(0, 1);
					}
					break;
				case Keyboard.KEY_HOME:
					if (ix > 0) {
						move(-ix, 0);
					}
					break;
				case Keyboard.KEY_END:
					if (ix < owner.getLine(iy).length()) {
						move(owner.getLine(iy).length() - ix, 0);
					}
					break;
				case Keyboard.KEY_RETURN:
					owner.newline(ix, iy);
					move(-ix, 1);
					carriageReturn();
					break;
				case Keyboard.KEY_TAB:
					int tabSize = owner.tab(ix, iy);
					move(tabSize, 0);
					break;
				default:
					owner.place(Keyboard.getEventCharacter(), ix, iy);
					move(1, 0);
					break;
				}
				if (cursorAction != null) {
					cursorAction.keyPress(keyCode);
				}
			}
		}
	}

	@Override
	public void render() {
		Render.colour(colour);
		Render.rect(x + xOffset + padding, y + yOffset + padding, w, h);
	}
	
	public void carriageReturn() {
		xOffset = 0;
		ix = 0;
	}
	
	public void setCursorAction(CursorAction action) {
		this.cursorAction = action;
	}
	
	public void move(int x, int y) {
		ix += x;
		iy += y;
		xOffset += charWidth * x;
		yOffset += charHeight * y;
	}

	public void setOffset(int padding) {
		this.padding = padding;
	}

	public void reset() {
		yOffset = xOffset = ix = iy = 0;
	}

	public void setColour(Colour colour) {
		this.colour = colour;
	}
	
}
