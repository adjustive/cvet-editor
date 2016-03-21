package io.cvet.editor.gui;

import io.cvet.editor.gfx.Render;
import io.cvet.editor.gui.Cursor.CursorStyle;
import io.cvet.editor.util.Input;
import io.cvet.editor.util.RNG;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

public class TextArea extends Component {

	private List<StringBuilder> buffer;
	private int xOffset, yOffset;
	private Cursor caret;
	
	int charWidth = Render.MONOSPACED_FONT.getWidth("a");
	int charHeight = Render.MONOSPACED_FONT.getHeight();
	int wheelDelta = 0;
	
	public TextArea(int w, int h) {
		this.w = w;
		this.h = h;
		this.buffer = new ArrayList<StringBuilder>();
		buffer.add(new StringBuilder());
		caret = new Cursor(this, CursorStyle.Block);
	}
	
	@Override
	public void init() {
		
	}

	@Override
	public void update() {
		wheelDelta = Mouse.getDWheel();
		
		// only scroll if we are inside of the textarea
		if (Input.intersects(this) && wheelDelta != 0) {
			yOffset += wheelDelta * 0.1;
		}
		
		caret.update();
	}
	
	@Override
	public void render() {
		Render.colour(125, 0, 255);
		Render.rect(x, y, w, h);

		caret.render();

		int line = 0;
		for (StringBuilder s : buffer) {
			Render.drawString(s.toString(), x + xOffset, y + yOffset + (line * charHeight));
			line++;
		}
	}

	public void append(char c) {
	}

	public StringBuilder getLine(int lineNum) {
		return buffer.get(lineNum);
	}
	
	public void setLine(StringBuilder to, int lineNum) {
		buffer.set(lineNum, to);
	}
	
	public void insert(char c, int ix, int iy) {
		StringBuilder line = getLine(iy);
		line.setCharAt(ix, c);
		setLine(line, iy);
	}
	
	public void place(char c, int ix, int iy) {
		StringBuilder line = getLine(iy);
		line.insert(ix, c);
		setLine(line, iy);
	}

	public void newline() {
		buffer.add(new StringBuilder());
	}

}
