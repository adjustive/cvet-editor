package io.cvet.editor.gui.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import io.cvet.editor.Editor;
import io.cvet.editor.Layout;
import io.cvet.editor.gfx.ImmediateRenderer;
import io.cvet.editor.gfx.RenderContext;
import io.cvet.editor.gui.Component;
import io.cvet.editor.gui.CursorAction;
import io.cvet.editor.gui.commands.palette.PaletteSuggestion;
import io.cvet.editor.gui.commands.palette.PaletteSuggestion.SuggestionType;
import io.cvet.editor.gui.commands.palette.PaletteSuggestionList;
import io.cvet.editor.gui.cursor.Cursor;
import io.cvet.editor.gui.cursor.Cursor.CursorStyle;
import io.cvet.editor.gui.tab.Tab;
import io.cvet.editor.gui.text.Line;
import io.cvet.editor.gui.text.TextArea;
import io.cvet.editor.util.Theme;

public class CommandPalette extends Component implements CursorAction {

	private TextArea buffer;
	private Cursor caret;
	private PaletteSuggestionList suggestions;
	
	private int defaultHeight;
	private boolean enteredCommand = false;
	
	private static HashMap<String, Command> commands = new HashMap<String, Command>();
	static {
		commands.put("new", new NewFileCommand());
		commands.put("open", new OpenFileCommand());
		commands.put("close", new CloseFileCommand());
		commands.put("exit", new ExitCommand());
		commands.put("save", new SaveFileCommand());
		commands.put("configure", new EditSettingsCommand());
		commands.put("help", new HelpCommand());
		commands.put("goto", new GotoCommand());
		commands.put("rename", new RenameCommand());
	}
	
	public CommandPalette() {
		this.defaultHeight = ImmediateRenderer.EDITING_FONT.getHeight() + 10;
		this.w = Display.getWidth() / 3;
		this.h = defaultHeight;
		this.x = (Display.getWidth() / 2) - (this.w / 2);
		this.y = Display.getHeight() / 8;
		
		this.buffer = new TextArea(this.w, defaultHeight);
		this.caret = buffer.getCaret();
		
		buffer.setBackground(Theme.ACCENT);
		buffer.setFocus(true);
		buffer.setFont(ImmediateRenderer.EDITING_FONT);
		
		caret.setCursorAction(this);
		caret.setColour(Theme.BASE);
		caret.setCursorStyle(CursorStyle.Line);
		caret.setHungryBackspace(false);
		caret.setHighlightCurrentLine(false);
		
		suggestions = new PaletteSuggestionList(this);
		addChild(buffer, Layout.Child);
		addChild(suggestions, Layout.Child);
	}
	
	@Override
	public void init() {
		
	}

	@Override
	public void update() {
		updateChildren(children);
		
		// if we have already written the 
		if (enteredCommand) {
			suggestions.clear();
			if (buffer.getLine().length() == 0) {
				enteredCommand = false;
			}
		}
	}
	
	@Override
	public void render() {
		RenderContext.endClip();
		
		RenderContext.colour(Theme.DARK_BASE);
		RenderContext.rect(x, y, w + 2, h + 2);
		
		renderChildren(children);
	}
	
	public void processCommand(String[] command) {
		String commandName = command[0];
		System.out.println("Processing command " + commandName);
		
		Command cmd = commands.get(commandName);
		if (cmd != null) {
			String args[] = Arrays.copyOfRange(command, 1, command.length);
			if (cmd.getArgumentCount() <= args.length) {
				cmd.action(args);
			} else {
				System.err.println("todo !fuck " + args.length + " <= fuck " + cmd.getArgumentCount());
			}
		}
	}
	
	public void hide() {
		setVisible(false);
		setFocus(false);
		buffer.clear();
		suggestions.clear();
		enteredCommand = false;
	}
	
	private String filePrefix = "";

	@Override
	public boolean keyPress(int keyCode) {
		// lookup after each keypress
		suggestions.find(buffer.getLine().toString());
		
		switch (keyCode) {
		case Keyboard.KEY_TAB: // autocomplete?
			String foo[] = buffer.getLine().toString().split(" ");
			if (foo.length < 2) {
				return true;
			}
			String argument = foo[1];
			File file = new File(argument);
			if (!file.isDirectory()) {
				filePrefix = file.getName();
				file = file.getParentFile();
			}
			if (file != null) {
				File[] contents = file.listFiles();
				if (contents == null || contents.length == 0) {
					return true;
				}
				
				List<File> possibleFiles = new ArrayList<File>(file.listFiles().length);
				for (File entry : file.listFiles()) {
					if (entry.isFile() && entry.getName().equals(filePrefix)) {
						return true;
					}
					if (entry.isFile() && entry.getName().startsWith(filePrefix)) {
						System.out.println("could be " + entry.getName());
						possibleFiles.add(entry);
					}
				}
				if (possibleFiles.size() == 1) {
					String suggested = possibleFiles.get(0).getName();
					buffer.append(suggested.substring(filePrefix.length()));
					buffer.moveCursor(suggested.length() - filePrefix.length(), 0);
					possibleFiles.clear();
					filePrefix = "";
				}
			}
			return true;
		case Keyboard.KEY_SPACE:
			if (!enteredCommand) {
				enteredCommand = true;
			}
			break;
		case Keyboard.KEY_RETURN:
			if (suggestions.isPopulated()) {
				PaletteSuggestion suggested = suggestions.getCurrentSuggestion();

				switch (suggested.type) {
				case Command:
					buffer.setLineAndMove(new Line(suggested.key));
					break;
				case Buffer:
					Editor.getInstance().mainView.setTab(suggested.key);
					hide();
					return true;
				}

				Command cmd = suggestions.getCurrentSuggestion().lookupCommand();
				if (cmd != null && cmd.argumentCount > 0) {
					buffer.append(' ');
					buffer.moveCursor(1, 0);
				}
				suggestions.clear();
				enteredCommand = true;
			} 
			// it's not populated, this means it's
			// our "final" time entering
			else {
				String[] command = buffer.getLine().toString().split(" ");
				command[0] = command[0].trim();
				
				switch (command[0]) {
				case "?":
					for (String c : commands.keySet()) {
						suggestions.add(new PaletteSuggestion(commands.get(c).name, SuggestionType.Command));
					}
					return true;
				case "!":
					ArrayList<Tab> bufferNames = Editor.getInstance().mainView.getTabList();
					if (bufferNames.size() == 0) {
						return true;
					}
					for (Tab tab : bufferNames) {
						suggestions.add(new PaletteSuggestion(tab.buff.getName(), SuggestionType.Buffer));
					}
					return true;
				}
				
				processCommand(command);
				hide();
			}
			return true;
		case Keyboard.KEY_UP:
		case Keyboard.KEY_DOWN:
			return true;
		case Keyboard.KEY_ESCAPE: // esc exits out of the command palete
			hide();
			break;
		case Keyboard.KEY_BACK:
			// if we have entered the command
			// and we go past at least one
			// space... we haven't entered the
			// command.
			if (enteredCommand() 
					&& buffer.getLine().charAt(buffer.getCaret().ix - 1) == ' ') {
				enteredCommand = false;
			}
			break;
		}
		
		suggestions.autoSelect();
		return false;
	}
	
	public static HashMap<String, Command> getCommands() {
		return commands;
	}

	public void setText(String input) {
		buffer.getLines().clear();
		buffer.getLines().add(new Line(input));
		buffer.getCaret().move(input.length(), 0);
	}

	public Cursor getCaret() {
		return caret;
	}

	public TextArea getBuffer() {
		return buffer;
	}

	public boolean enteredCommand() {
		return enteredCommand;
	}
	
}
