package io.cvet.editor.gui;

import io.cvet.editor.gfx.Render;
import io.cvet.editor.gui.Cursor.CursorStyle;
import io.cvet.editor.util.Input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

public class TextArea extends Component {

	private List<StringBuilder> buffer;
	private int xOffset, yOffset;
	private Cursor caret;
	
	int tabSize = 4;
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
		setLine(getLine().append(c), buffer.size() - 1);
	}

	public StringBuilder getLine(int lineNum) {
		return buffer.get(lineNum);
	}
	
	public StringBuilder getLine() {
		return buffer.get(buffer.size() - 1);
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
		if (ix >= line.length()) {
			line.append(c);
		} else {
			line.insert(ix, c);
		}
		setLine(line, iy);
	}

	public void newline(int ix, int iy) {
		StringBuilder currentLine = getLine(iy);
		if (currentLine.length() == 0) {
			buffer.add(iy + 1, new StringBuilder());
			return;
		}
		
		if (ix < 0 || ix > currentLine.length()) {
			return;
		}
		
		String first = currentLine.substring(0, ix);
		String excess = currentLine.substring(ix);
		buffer.set(iy, new StringBuilder(first));
		buffer.add(iy + 1, new StringBuilder(excess));
	}

	/*
	 * Will remove the end of the line
	 * returns if true if the cursor can
	 * go left a spot, if not will return
	 * false.
	 */
	public void backspace(Cursor caret, int ix, int iy) {
		StringBuilder current = getLine(iy);
		
		if (ix == 0) {
			// top left, nothing to do
			if (iy == 0) {
				return;
			}
			
			StringBuilder above = getLine(iy - 1);
			int aboveInitialLength = above.length();
			above.append(current);
			setLine(above, iy - 1);
			buffer.remove(iy);
			caret.move(aboveInitialLength, -1);
			return;
		}
		
		current.deleteCharAt(ix - 1);
		setLine(current, iy);
		caret.move(-1, 0);
		return;
	}

	public void loadFile(File file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = br.readLine()) != null) {
				buffer.add(new StringBuilder(line));
			}
			br.close();
		} 
		catch (Exception e) {
			System.err.println("todo");
		}
	}

	public int getLineCount() {
		return buffer.size();
	}

	public int tab(int ix, int iy) {
		for (int i = 0; i < tabSize; i++) {
			place(' ', ix, iy);
		}
		return tabSize;
	}

}
