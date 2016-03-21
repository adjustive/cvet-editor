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
	private int insertionPoint; // ;)

	private CursorStyle cursorStyle;
	private int xOffset, yOffset;
	private int charWidth, charHeight;
	
	public Cursor(TextArea owner, CursorStyle style) {
		this.owner = owner;
		this.cursorStyle = style;
		this.insertionPoint = 0;
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
		
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				switch (Keyboard.getEventKey()) {
				case Keyboard.KEY_RETURN:
					place('\n');
					move(0, 1);
					reset();
					break;
				default:
					place(Keyboard.getEventCharacter());
					break;
				}
			}
		}
	}

	@Override
	public void render() {
		Render.colour(255, 0, 255);
		Render.rect(x + xOffset, y + yOffset, w, h);
	}
	
	public void insert(char c) {
		owner.insert(c, insertionPoint);
	}
	
	public void reset() {
		xOffset = 0;
	}
	
	public void move(int x, int y) {
		xOffset += charWidth * x;
		yOffset += charHeight * y;
	}
	
	public void place(char c) {
		owner.place(c, insertionPoint);
		insertionPoint++;
		move(1, 0);
	}
	
}
