package io.cvet.editor.gui;

import io.cvet.editor.gfx.Render;

import org.lwjgl.input.Keyboard;

public class Cursor extends Component {
	public static enum CursorStyle {
		Block,
		Line,
	}

	private TextArea owner;
	
	// where the cursor is positioned in the buffer
	// insertion x, y
	private int ix, iy;
	
	private CursorStyle cursorStyle;
	private int xOffset, yOffset;
	private int charWidth, charHeight;
	
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
		
		// todo we go out of buffer bounds
		
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				switch (Keyboard.getEventKey()) {
				case Keyboard.KEY_LSHIFT:
				case Keyboard.KEY_RSHIFT:
				case Keyboard.KEY_LCONTROL:
				case Keyboard.KEY_RCONTROL:
				case Keyboard.KEY_LMETA:
				case Keyboard.KEY_RMETA:
				case Keyboard.KEY_LMENU:
				case Keyboard.KEY_RMENU:
				case Keyboard.KEY_F2:
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
							System.out.println("Ye?");
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
					reset();
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
			}
		}
	}

	@Override
	public void render() {
		Render.colour(30, 30, 30);
		Render.rect(x + xOffset, y + yOffset, w, h);
	}
	
	public void reset() {
		xOffset = 0;
		ix = 0;
	}
	
	public void move(int x, int y) {
		System.out.println(Math.signum(y));
		ix += x;
		iy += y;
		xOffset += charWidth * x;
		yOffset += charHeight * y;
	}
	
}
