package io.cvet.editor.gui.cursor;

import java.util.regex.Pattern;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.TrueTypeFont;

import io.cvet.editor.Editor;
import io.cvet.editor.config.Settings;
import io.cvet.editor.gfx.Colour;
import io.cvet.editor.gfx.ImmediateRenderer;
import io.cvet.editor.gfx.RenderBackend;
import io.cvet.editor.gfx.RenderContext;
import io.cvet.editor.gui.Buffer;
import io.cvet.editor.gui.Component;
import io.cvet.editor.gui.CursorAction;
import io.cvet.editor.gui.cursor.Mark.Span;
import io.cvet.editor.gui.text.Line;
import io.cvet.editor.gui.text.TextArea;
import io.cvet.editor.util.FileUtil;
import io.cvet.editor.util.Input;
import io.cvet.editor.util.Theme;

public class Cursor extends Component {

	public static enum CursorStyle {
		Block, Line,
	}

	private TextArea owner;
	private Colour colour = new Colour(30, 30, 30);
	private CursorStyle cursorStyle;
	private CursorAction cursorAction = null;
	private Mark selection;

	private boolean showCursor = true;
	public int ix, iy;
	public int padding;
	public int xOffset, yOffset;
	private long timer;

	private boolean hungryBackspace;
	private boolean matchBraces;
	private boolean highlightCurrentLine;
	private boolean shouldBlink;
	private int blinkLatencyMS;
	
	public Cursor(TextArea owner, CursorStyle style) {
		this.owner = owner;
		this.cursorStyle = style;
		this.ix = iy = 0;
		this.h = ImmediateRenderer.EDITING_FONT.getHeight();
		timer = System.currentTimeMillis();
		loadSettings();
	}

	public void loadSettings() {
		this.blinkLatencyMS = (int) Settings.getSetting("cursor_blink_latency");
		this.shouldBlink = (boolean) Settings.getSetting("blink_cursor");
		this.hungryBackspace = (boolean) Settings.getSetting("hungry_backspace");
		this.matchBraces = (boolean) Settings.getSetting("match_braces");
		this.highlightCurrentLine = (boolean) Settings.getSetting("highlight_current_line");
	}
	
	@Override
	public void init() {

	}
	
	public char getCharacterBefore(int ix) {
		if (atStartOfLine()) {
			return '\0';
		}
		return getCurrentLine().charAt(ix - 1);
	}
	
	public String getWordBefore(int ix) {
		// we're at the left most side.
		if (ix == 0) {
			return "";
		}
		
		StringBuilder result = new StringBuilder();
		String line = getCurrentLine();
		
		int initialX = ix;
		for (int i = ix - 1; i >= 0; i--) {
			char c = line.charAt(i);
			if ((!Character.isLetter(c) || c == ' ') && i != initialX - 1) {
				break;
			}
			result.append(c);
		}
		
		return result.reverse().toString();
	}
	
	public char getCharacterAfter(int ix) {
		if (atEndOfLine()) {
			return '\0';
		}
		return getCurrentLine().charAt(ix + 1);
	}
	
	public String getWordAfter(int ix) {
		if (atEndOfLine()) {
			return "";
		}
		
		StringBuilder result = new StringBuilder();
		String line = getCurrentLine();
		
		int initialX = ix;
		for (int i = initialX; i < line.length(); i++) {
			char c = line.charAt(i);
			if ((!Character.isLetter(c) || c == ' ') && i != initialX) {
				break;
			}
			result.append(c);
		}
		
		return result.toString();
	}

	public void handleControlCombo() {
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				Editor.commands = "CTRL + ";
				// TODO: clean this up, hashmap for easier
				// binding?
				int keyCode = Keyboard.getEventKey();
				Editor.commands += Keyboard.getKeyName(keyCode).toUpperCase();
				switch (keyCode) {
				case Keyboard.KEY_C: // copy
					// TODO:
					break;
				case Keyboard.KEY_V: // paste
					String s = FileUtil.getClipboardContents();
					owner.place(s, ix, iy);
					owner.moveCursor(s.length(), 0);
					break;
				case Keyboard.KEY_N:
					System.err.println("todo");
//					Editor.getInstance().showCommandPalette("new ");
					break;
				case Keyboard.KEY_W: // close file
					Editor.getMainView().closeCurrentTab();
					break;
				case Keyboard.KEY_S: // save
					Buffer b = Editor.getMainView().getCurrentTab().buff;
					if (b != null) {
						b.save();
					}
					break;
				case Keyboard.KEY_L: // line goto
					System.err.println("todo");
//					Editor.getInstance().showCommandPalette("goto ");
					break;
				case Keyboard.KEY_LEFT: // left word
					String previousWord = getWordBefore(ix);
					move(-previousWord.length(), 0);
					break;
				case Keyboard.KEY_RIGHT: // right word
					String nextWord = getWordAfter(ix);
					move(nextWord.length(), 0);
					break;
				case Keyboard.KEY_BACK: // delete previous word
					String prevWord = getWordBefore(ix);
					for (int i = 0; i < prevWord.length() + 1; i++) {
						owner.backspace(this);
					}
					break;
				case Keyboard.KEY_DELETE:
					// TODO: DELETE EVERYTHING IF NOT CHARS!
					String next = getWordAfter(ix);
					for (int i = 0; i < next.length(); i++) {
						owner.delete(ix, iy);
					}
					break;
				case Keyboard.KEY_D: // delete line
					if (iy >= 0 && iy < owner.getLineCount() - 1) {
						owner.deleteLine(iy);
						carriageReturn();
					} else if (iy == owner.getLineCount() - 1
							&& owner.getLine(iy).length() == 0) {
						if (iy != 0) {
							owner.deleteLine(iy);
							move(owner.getLine(iy - 1).length(), -1);
						}
					} else if (owner.getLine(iy).toString().trim().length() == 0) {
						owner.deleteLine(iy);
						move(owner.getLine(iy - 1).length(), -1);
					} else {
						owner.clearLine(iy);
						carriageReturn();
					}
					break;
				default:
					break;
				// IGNORE THESE
				case Keyboard.KEY_LSHIFT:
				case Keyboard.KEY_RSHIFT:
				case Keyboard.KEY_LCONTROL:
				case Keyboard.KEY_RCONTROL:
					break;
				}
			}
		}
	}

	public void handleShiftCombo() {
		if (selection == null) {
			selection = new Mark(this, new Span(ix, iy));
		}
		
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				Editor.commands = "SHIFT + ";
				// TODO: clean this up, hashmap for easier
				// binding?
				int keyCode = Keyboard.getEventKey();
				Editor.commands += Keyboard.getKeyName(keyCode).toUpperCase();
				switch (keyCode) {
				case Keyboard.KEY_LEFT: // left word select
					selection.end.x -= RenderBackend.CHARACTER_WIDTH;
					move(-1, 0);
					break;
				case Keyboard.KEY_RIGHT: // right word select
					selection.end.x += RenderBackend.CHARACTER_WIDTH;
					move(1, 0);
					System.out.println("hey");
					break;
				case Keyboard.KEY_DOWN:
					selection.end.y += RenderBackend.CHARACTER_HEIGHT;
					move(0, 1);
					break;
				case Keyboard.KEY_TAB: // shift tab!
					// can't shift tab!
					if (ix == 0) {
						String currentLine = getCurrentLine();
						if (currentLine.length() >= owner.getTabSize()) {
							int charsToDelete = owner.getTabSize();
							int initialIx = ix;
							for (int i = 0; i < owner.getTabSize(); i++) {
								if (currentLine.charAt(initialIx + i) != ' ') {
									charsToDelete = i;
									break;
								}
							}
							for (int i = 0; i < charsToDelete; i++) {
								owner.delete(ix, iy);
							}
						}
						break;
					}
					
					String currentLine = getCurrentLine().substring(0, ix);
					int trimmedLen = currentLine.trim().length();
					
					// we're at the start of our line
					// and there is enough space to shift+tab
					if (trimmedLen == 0 
							&& currentLine.length() >= owner.getTabSize()) {
						for (int i = 0; i < owner.getTabSize(); i++) {
							owner.backspace(this);
						}
					}
					// were not at the start, we need to remove
					// goto the start and remove -4
					else {
					}
					break;
				default:
					handleKeyCode(keyCode);
					break;
				// IGNORE THESE
				case Keyboard.KEY_LSHIFT:
				case Keyboard.KEY_RSHIFT:
				case Keyboard.KEY_LCONTROL:
				case Keyboard.KEY_RCONTROL:
					break;
				}
			}
		}
	}
	
	public boolean atStart() {
		return atStartOfLine() && iy == 0;
	}
	
	public boolean atStartOfLine() {
		return ix == 0;
	}
	
	public boolean atEndOfLine() {
		return ix >= getCurrentLine().length();
	}
	
	public void setLast() {
		if (atStart()) {
			return;
		}
		
		if (ix > 0) {
			return;
		}
		
		if (ix < 0) {
			for (int i = 0; i < owner.getLineCount(); i++) {
				String line = getLineOffsetBy(iy - i);
				if (line.trim().length() != 0) {
					return;
				}
			}
		}
	}

	public void handleKeyCode(int keyCode) {
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
		case Keyboard.KEY_CAPITAL:
		case Keyboard.KEY_INSERT:
			// nothing
			break;
		case Keyboard.KEY_BACK:
			// we're at the start, nothing to do
			if (atStart()) {
				return;
			}

			if (hungryBackspace && ix - owner.getTabSize() >= 0) {
				Line cut = owner.getLine(iy).substring(
						ix - owner.getTabSize(), ix);
				// if the last X characters are == to our tabSize
				// and if we trim the whitespace and it becomes
				// the length of zero, they are all spaces we can
				// remove.
				if (cut.length() == owner.getTabSize()
						&& cut.toString().trim().length() == 0) {
					for (int i = 0; i < owner.getTabSize(); i++) {
						owner.backspace(this);
					}
				} else {
					setLast();
					owner.backspace(this);
				}
			} else {
				setLast();
				owner.backspace(this);
			}
			break;
		case Keyboard.KEY_LEFT:
			setLast();
			move(ix > 0 ? -1 : 0, 0);
			selection = null;
			break;
		case Keyboard.KEY_SPACE:
			String bef = getWordBefore(ix).trim();
			System.out.println("word before is " + bef);
			
			Line line = owner.getLine(iy);
			Line prevWord = line.substring(ix - bef.length(), ix);
			System.out.println("hey " + prevWord.toString() + " " + bef.length() + ", " + line.toString());
			
			if (isKeyword(bef)) {
				for (int i = 0; i < prevWord.value.size(); i++) {
					prevWord.setColouringAt(Theme.KEYWORD, i);
				}
				line.set(ix - bef.length(), prevWord);
				owner.setLine(line, iy);			
			} else if (isType(bef)) {
				for (int i = 0; i < prevWord.value.size(); i++) {
					prevWord.setColouringAt(Theme.TYPE, i);
				}
				line.set(ix - bef.length(), prevWord);
				owner.setLine(line, iy);			
			} else if (Pattern.matches("'([^']|'')*'", bef)) {
				
			}
			
			// insert the space
			owner.place(Keyboard.getEventCharacter(), ix, iy);
			move(1, 0);
			break;
		case Keyboard.KEY_RIGHT:
			setLast();
			move(ix < getCurrentLine().length() ? 1 : 0, 0);
			selection = null;
			break;
		case Keyboard.KEY_DELETE:
			owner.delete(ix, iy);
			break;
		case Keyboard.KEY_LBRACKET:
			char openingBracket = Keyboard.getEventCharacter();
			owner.place(openingBracket, ix, iy);
			move(1, 0);
			if (matchBraces) {
				// opening bracket + 2 in ascii
				// will get us the closing bracket
				owner.place((char) ((int) (openingBracket + 2)), ix, iy);
			}
			break;
		case Keyboard.KEY_UP:
			if (iy > 0) {
				int prevLineLen = getLineOffsetBy(-1).length();
				move(ix >= prevLineLen ? prevLineLen - ix : 0, -1);
			}
			break;
		case Keyboard.KEY_DOWN:
			if (atEndOfLine() && iy < owner.getLineCount() - 1) {
				move(getLineOffsetBy(1).length() - ix, 1);
			} else if (iy < owner.getLineCount() - 1) {
				int nextLineLen = getLineOffsetBy(1).length();
				move(ix >= nextLineLen ? nextLineLen - ix : 0, 1);
			}
			break;
		case Keyboard.KEY_HOME:
			move(ix > 0 ? -ix : 0, 0);
			break;
		case Keyboard.KEY_END:
			moveToEnd();
			break;
		case Keyboard.KEY_RETURN:
			owner.newline(ix, iy);
			move(-ix, 1);
			carriageReturn();
			break;
		case Keyboard.KEY_TAB:
			move(owner.tab(ix, iy), 0);
			break;
		case Keyboard.KEY_PRIOR: // pgup
			set(0, 0);
			break;
		case Keyboard.KEY_NEXT: // pgdown
			set(0, owner.getLineCount() - 1);
			break;
		default:
			owner.place(Keyboard.getEventCharacter(), ix, iy);
			move(1, 0);
			break;
		}
	}
	
	public String getLineOffsetBy(int offs) {
		return owner.getLine(iy + offs).toString();
	}
	
	public String getCurrentLine() {
		return getLineOffsetBy(0);
	}
	
	public void moveToEnd() {
		if (atEndOfLine()) {
			return;
		}
		
		int initialX = ix;
		String line = owner.getLine(iy).toString();
		for (int i = 0; i < line.length() - initialX; i++) {
			setLast();
			move(1, 0);
		}
	}

	@Override
	public void update() {
		this.x = owner.x;
		this.y = owner.y;
		this.visible = owner.isVisible();
		this.w = cursorStyle == CursorStyle.Block ? RenderBackend.CHARACTER_WIDTH : 1;
		
		if (Input.isControlModifierDown()) {
			handleControlCombo();
		} else if (Input.isShiftModifierDown()) {
			handleShiftCombo();
		}

		if (selection != null) {
			selection.update();
		}
		
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				int keyCode = Keyboard.getEventKey();
				if (cursorAction != null 
						&& cursorAction.keyPress(keyCode)) {
					return;
				}
				handleKeyCode(keyCode);
			}
		}
		
		// no key is pressed, we can blink!
		if (shouldBlink) {
			if (!Keyboard.getEventKeyState()) {
				if (System.currentTimeMillis() - timer > blinkLatencyMS) {
					showCursor = !showCursor;
					timer += blinkLatencyMS;
				}
			} else {
				showCursor = true;
			}
		}
	}

	@Override
	public void render() {

		if (highlightCurrentLine) {
			RenderContext.colour(Theme.DARK_BASE);
			RenderContext.rect(owner.x + padding, y + yOffset + padding, owner.w, h);
		}

		if (showCursor) {
			RenderContext.colour(colour);
			RenderContext.rect(x + xOffset + padding, y + yOffset + padding, w, h);
		}
		
		if (selection != null) {
			selection.render();
		}
	}

	public void carriageReturn() {
		xOffset = 0;
		ix = 0;
	}

	public void setCursorAction(CursorAction action) {
		this.cursorAction = action;
	}
	
	public void move(int x, int y) {
		if (ix == 0 && Math.signum(x) < 0) {
			return;
		}
		ix += x;
		iy += y;
		
		int cw = owner.getFont().getWidth(" ");
		xOffset += x * cw;
		yOffset += owner.getFont().getHeight() * y;
	}

	public void set(int x, int y) {
		if (x < 0 || y < 0) {
			set(0, 0);
			return;
		}
		if (x > owner.getCharacterCount() || y > owner.getLineCount() - 1) {
			set(owner.getCharacterCount(), owner.getLineCount() - 1);
			return;
		}
		
		ix = x;
		iy = y;
		int cw = owner.getFont().getWidth(" ");
		xOffset = cw * x;
		yOffset = owner.getFont().getHeight() * y;
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

	public void setHungryBackspace(boolean hungryBackspace) {
		this.hungryBackspace = hungryBackspace;
	}

	public void setCursorStyle(CursorStyle style) {
		this.cursorStyle = style;
	}

	public TrueTypeFont getFont() {
		return owner.getFont();
	}

	public void setHighlightCurrentLine(boolean highlightCurrentLine) {
		this.highlightCurrentLine = highlightCurrentLine; 
	}
	
	private String[] keywords = new String[] {
		"return", "const", "if", "else", "goto",
		"include",
	};
	
	private String[] types = new String[] {
		"int", "void", "char", "struct",
	};
	
	public boolean isKeyword(String kw) {
		for (String s : keywords) {
			if (s.equals(kw)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isType(String typ) {
		for (String s : types) {
			if (s.equals(typ)) {
				return true;
			}
		}
		return false;
	}

}
