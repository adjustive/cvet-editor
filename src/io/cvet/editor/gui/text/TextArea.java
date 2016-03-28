package io.cvet.editor.gui.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.TrueTypeFont;

import io.cvet.editor.config.Settings;
import io.cvet.editor.gfx.Colour;
import io.cvet.editor.gfx.ImmediateRenderer;
import io.cvet.editor.gfx.RenderContext;
import io.cvet.editor.gui.Component;
import io.cvet.editor.gui.cursor.Cursor;
import io.cvet.editor.gui.cursor.Cursor.CursorStyle;
import io.cvet.editor.gui.text.Line.Glyph;
import io.cvet.editor.util.Input;
import io.cvet.editor.util.Theme;

public class TextArea extends Component {

	protected List<Line> buffer;
	private Cursor caret;
	private TrueTypeFont font;

	private Colour background = Colour.WHITE;
	private Colour foreground = Colour.BLACK;

	public int xOffset, yOffset;
	private int padding = 5;
	private int tabSize;
	private int charHeight = ImmediateRenderer.EDITING_FONT.getHeight();
	private int wheelDelta = 0;
	private boolean insertMode = false;
	
	public TextArea(int w, int h) {
		this.w = w;
		this.h = h;
		this.buffer = new ArrayList<Line>();
		loadSettings();
		caret = new Cursor(this, CursorStyle.Block);
		caret.setOffset(padding);
		addChild(caret);

		setBackground(Theme.BASE);
		setCursorColour(Theme.ACCENT);
		setForeground(Colour.PINK);
		setFont(ImmediateRenderer.EDITING_FONT);

		this.buffer.add(new Line());
	}
	
	public void loadSettings() {
		this.tabSize = (int) Settings.getSetting("tab_size");
	}

	public TextArea() {
		this(Display.getWidth(), Display.getHeight());
	}

	@Override
	public void init() {

	}

	@Override
	public void update() {
		wheelDelta = Mouse.getDWheel();

		// only scroll if we are inside of the textarea
		if (Input.intersects(this) && wheelDelta != 0) {
			yOffset -= wheelDelta;
		}

		if (Input.getKeyPressed(Keyboard.KEY_INSERT)) {
			insertMode = !insertMode;
		}

		updateChildren(children);
	}

	@Override
	public void render() {
		RenderContext.colour(background);
		RenderContext.rect(x, y, w, h);

		renderChildren(children);

		RenderContext.colour(foreground);
		int line = 0;
		RenderContext.font(font);

		for (Line s : buffer) {
			int glyph = 0;
			for (Glyph g : s.value) {
				int charWidth = ImmediateRenderer.CURRENT_FONT.getWidth(String.valueOf(g.value));
				RenderContext.colour(g.colouring);
				RenderContext.drawString(String.valueOf(g.value), x + padding + (glyph * charWidth),
						y + padding + (line * charHeight));
				glyph++;
			}
			line++;
		}
	}

	public void append(char c) {
		Line l = getLine();
		l.append(c);
		setLine(l, buffer.size() - 1);
	}

	public void append(String s) {
		for (char c : s.toCharArray()) {
			append(c);
		}
	}

	public Line getLine(int lineNum) {
		return buffer.get(lineNum);
	}

	public Line getLine() {
		return buffer.get(buffer.size() - 1);
	}

	public void setLine(Line to, int lineNum) {
		buffer.set(lineNum, to);
	}

	public void setLine(Line to) {
		buffer.set(buffer.size() - 1, to);
	}

	public void setLineAndMove(Line to) {
		int oldLen = buffer.get(buffer.size() - 1).length();
		buffer.set(buffer.size() - 1, to);
		moveCursor(to.length() - oldLen, 0);
	}

	public void insert(char c, int ix, int iy) {
		Line line = getLine(iy);
		line.setCharAt(ix, c);
		System.out.println("insert");
		setLine(line, iy);
	}

	public void place(String s, int ix, int iy) {
		int idx = 0;
		for (char c : s.toCharArray()) {
			place(c, ix + idx, iy);
			idx++;
		}
	}

	public void place(char c, int ix, int iy) {
		if (ix == 0 && iy == 0 && buffer.size() == 0) {
			buffer.add(new Line(1));
		}

		Line line = getLine(iy);
		if (ix >= line.length()) {
			line.append(c);
		} else {
			if (insertMode) {
				line.setCharAt(ix, c);
			} else {
				line.insert(ix, c);
			}
		}
		setLine(line, iy);
	}

	public void newline(int ix, int iy) {
		Line currentLine = getLine(iy);
		if (currentLine.length() == 0) {
			buffer.add(iy + 1, new Line());
			return;
		}

		if (ix < 0 || ix > currentLine.length()) {
			return;
		}

		Line first = currentLine.substring(0, ix);
		Line excess = currentLine.substring(ix);
		buffer.set(iy, first);
		buffer.add(iy + 1, excess);
	}

	/*
	 * Will remove the end of the line returns if true if the cursor can go left
	 * a spot, if not will return false.
	 */
	public void backspace(Cursor caret) {
		int ix = caret.ix;
		int iy = caret.iy;

		Line current = getLine(iy);

		if (ix == 0) {
			// top left, nothing to do
			if (iy == 0) {
				return;
			}

			Line above = getLine(iy - 1);
			int aboveInitialLength = above.length();
			above.append(current.toString());
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

	// TODO: FIXME optimize for HUGE files.
	// how?
	// load the file, cut it into pieces
	// put the first 2 views worth of file?
	// into the buffer, as we scroll load the
	// rest???
	public void loadFile(File file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";
			int currentLine = 1;
			while ((line = br.readLine()) != null) {
				// convert our \t into a string of {tabSize} amount of spaces
				buffer.add(new Line(line.replaceAll("\t", new String(new char[tabSize]).replace('\0', ' '))));
				System.out.println("# " + currentLine + " : " + line);
				currentLine++;
			}
			System.out.println("done");
			br.close();
		} catch (Exception e) {
			System.err.println("todo");
		}
	}

	public int getCharacterCount() {
		return getLine().length();
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

	public List<Line> getLines() {
		return buffer;
	}

	public void clear() {
		caret.carriageReturn();
		buffer.clear();
		buffer.add(new Line());
		caret.reset();
	}

	public void setCursorColour(Colour colour) {
		this.caret.setColour(colour);
	}

	public void delete(int ix, int iy) {
		Line line = getLine(iy);
		if (ix < line.length()) {
			line.deleteCharAt(ix);
		} else if (iy < getLineCount() - 1) {
			Line next = getLine(iy + 1);
			line.append(next.toString());
			buffer.remove(iy + 1);
		}
		setLine(line, iy);
	}

	public int getTabSize() {
		return tabSize;
	}

	public void deleteLine(int iy) {
		buffer.remove(iy);
	}

	public void clearLine(int iy) {
		buffer.set(iy, new Line());
	}

	public boolean isEmpty() {
		return getLineCount() == 0;
	}

	public void setText(String val) {
		buffer.clear();
		buffer.add(new Line(val));
	}

	public void moveCursor(int x, int y) {
		caret.move(x, y);
	}

	public void setFont(TrueTypeFont font) {
		this.font = font;
	}

	public TrueTypeFont getFont() {
		return font;
	}

}
