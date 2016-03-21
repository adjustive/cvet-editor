package io.cvet.editor.gui;

import org.lwjgl.input.Keyboard;

import io.cvet.editor.gfx.Render;

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
					// nothing
					break;
				case Keyboard.KEY_LEFT:
					move(-1, 0);
					break;
				case Keyboard.KEY_RIGHT:
					move(1, 0);
					break;
				case Keyboard.KEY_UP:
					move(0, -1);
					break;
				case Keyboard.KEY_DOWN:
					move(0, 1);
					break;
				case Keyboard.KEY_RETURN:
					owner.newline();
					move(0, 1);
					reset();
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
		ix += Math.signum(x);
		iy += Math.signum(y);
		xOffset += charWidth * x;
		yOffset += charHeight * y;
	}
	
}
