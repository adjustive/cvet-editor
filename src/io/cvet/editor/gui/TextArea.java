package io.cvet.editor.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import io.cvet.editor.gfx.Colour;
import io.cvet.editor.gfx.Render;
import io.cvet.editor.gui.Cursor.CursorStyle;
import io.cvet.editor.util.Input;

public class TextArea extends Component {

	private String name;
	private List<StringBuilder> buffer;
	private int xOffset, yOffset;
	private Cursor caret;
	
	private Colour background = Colour.WHITE;
	private Colour foreground = Colour.BLACK;
	
	private int padding = 5;
	private int tabSize = 4;
	private int charHeight = Render.MONOSPACED_FONT.getHeight();
	private int wheelDelta = 0;
	
	public TextArea(String name, int w, int h) {
		this.name = name;
		this.w = w;
		this.h = h;
		this.buffer = new ArrayList<StringBuilder>();
		buffer.add(new StringBuilder());
		caret = new Cursor(this, CursorStyle.Block);
		caret.setOffset(padding);
		addChild(caret);
		setBackground(new Colour(0x3D3331));
		setCursorColour(new Colour(0x61A598));
		setForeground(Colour.PINK);
	}
	
	public TextArea(int w, int h) {
		this("", w, h);
	}
	
	public TextArea(String name) {
		this(name, Display.getWidth(), Display.getHeight());
	}
	
	@Override
	public void init() {
		
	}

	@Override
	public void update() {
		wheelDelta = Mouse.getDWheel();
		
		// only scroll if we are inside of the textarea
		if (Input.intersects(this) && wheelDelta != 0) {
			yOffset += wheelDelta ;
		}
		
		updateChildren(children);
	}
	
	@Override
	public void render() {
		Render.colour(background);
		Render.rect(x, y, w, h);

		renderChildren(children);
		
		Render.colour(foreground);
		int line = 0;
		for (StringBuilder s : buffer) {
			Render.drawString(s.toString(), x + xOffset + padding, y + yOffset + padding + (line * charHeight));
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

	public Cursor getCaret() {
		return caret;
	}

	public void setBackground(Colour background) {
		this.background = background;
	}
	
	public void setForeground(Colour foreground) {
		this.foreground = foreground;
	}
	
	public List<StringBuilder> getBuffer() {
		return buffer;
	}

	public void clear() {
		caret.carriageReturn();
		buffer.clear();
		buffer.add(new StringBuilder());
		caret.reset();
	}

	public String getName() {
		return name;
	}

	public void setCursorColour(Colour colour) {
		this.caret.setColour(colour);
	}
	
}
