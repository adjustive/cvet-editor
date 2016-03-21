package io.cvet.editor.gui;

import io.cvet.editor.gfx.Render;
import io.cvet.editor.gui.Cursor.CursorStyle;
import io.cvet.editor.util.Input;
import io.cvet.editor.util.RNG;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

public class TextArea extends Component {

	private List<Character> buffer;
	private int xOffset, yOffset;
	private Cursor caret;
	
	int charWidth = Render.MONOSPACED_FONT.getWidth("a");
	int charHeight = Render.MONOSPACED_FONT.getHeight();
	int wheelDelta = 0;
	
	public TextArea(int w, int h) {
		this.w = w;
		this.h = h;
		this.buffer = new ArrayList<Character>();
		for (int i = 0; i < h * charHeight; i++) {
			int bufferWidth = (this.w / charWidth) - 10;
			if (i % bufferWidth == 0) {
				buffer.add('\n');
			}
			buffer.add((char) RNG.range(97, 122));
		}
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

	public String[] getBufferString() {
		StringBuilder res = new StringBuilder(buffer.size());
		for (int i = 0; i < buffer.size(); i++) {
			res.append(buffer.get(i));
		}
		return res.toString().split("\n");
	}
	
	@Override
	public void render() {
		Render.colour(125, 0, 255);
		Render.rect(x, y, w, h);

		caret.render();

		int line = 0;
		for (String s : getBufferString()) {
			Render.drawString(s, x + xOffset, y + yOffset + (line * charHeight));
			line++;
		}
	}

	public void append(char c) {
		buffer.add(c);
	}
	
	public void insert(char c, int where) {
		buffer.set(where, c);
	}
	
	public void place(char c, int where) {
		buffer.add(where, c);
	}

}
