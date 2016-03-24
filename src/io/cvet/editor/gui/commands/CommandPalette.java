package io.cvet.editor.gui.commands;

import io.cvet.editor.Layout;
import io.cvet.editor.gfx.Colour;
import io.cvet.editor.gfx.Render;
import io.cvet.editor.gui.Component;
import io.cvet.editor.gui.CursorAction;
import io.cvet.editor.gui.cursor.Cursor;
import io.cvet.editor.gui.cursor.Cursor.CursorStyle;
import io.cvet.editor.gui.text.Line;
import io.cvet.editor.gui.text.TextArea;
import io.cvet.editor.util.Input;
import io.cvet.editor.util.Theme;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

public class CommandPalette extends Component implements CursorAction {

	private TextArea buffer;
	private Cursor caret;
	
	private int defaultHeight;
	private int selectedSuggestion = 0;
	private boolean enteredCommand = false;
	
	private ArrayList<Command> suggestions = new ArrayList<Command>();
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
	}
	
	public CommandPalette() {
		this.defaultHeight = Render.EDITING_FONT.getHeight() + 10;
		this.w = 512;
		this.h = defaultHeight;
		this.x = (Display.getWidth() / 2) - (this.w / 2);
		this.y = 5;
		
		this.buffer = new TextArea(this.w, defaultHeight);
		this.caret = buffer.getCaret();
		
		buffer.setBackground(Theme.ACCENT);
		buffer.setFocus(true);
		buffer.setFont(Render.EDITING_FONT);
		caret.setCursorAction(this);
		caret.setColour(Theme.BASE);
		caret.setCursorStyle(CursorStyle.Line);
		caret.setHungryBackspace(false);
		
		addChild(buffer, Layout.Child);
	}
	
	@Override
	public void init() {
		
	}

	public void findSuggestions(String input) {
		// we've already got the command
		// lets get the fuc outta here
		if (enteredCommand) {
			return;
		}
		
		if (input.length() == 0) {
			if (suggestions.size() > 0) {
				suggestions.clear();
			}
			return;
		}
		
		for (String s : commands.keySet()) {
			if (s.contains(input)) {
				if (!suggestions.contains(commands.get(s))) {
					suggestions.add(commands.get(s));
				}
			}
		}
	}
	
	@Override
	public void update() {
		updateChildren(children);
		
		if (suggestions.size() >= 0) {
			if (Input.getKeyPressed(Keyboard.KEY_DOWN)) {
				selectedSuggestion++;
			} else if (Input.getKeyPressed(Keyboard.KEY_UP)) {
				selectedSuggestion--;
			}
		}
		
		// wrap around
		if (selectedSuggestion >= suggestions.size()) {
			selectedSuggestion = 0;
		} else if (selectedSuggestion < 0) {
			selectedSuggestion = suggestions.size() - 1;
		}
		
		// if we have already written the 
		if (enteredCommand) {
			removeSuggestions();
			if (buffer.getLine().length() == 0) {
				enteredCommand = false;
			}
		}
	}
	
	@Override
	public void render() {
		Render.endClip();
		
		Render.colour(Colour.BLACK);
		Render.rect(x, y, w + 2, h + 2);
		
		renderChildren(children);
		
		for (int i = 0; i < suggestions.size(); i++) {
			Command sugg = suggestions.get(i);
			
			// render the background +
			// set the colour if its selected
			Render.colour(selectedSuggestion == i ? Theme.DARK_ACCENT : Theme.ACCENT);
			Render.rect(x, y + ((i + 1) * h), w, h);
			
			// render the command name
			Render.colour(Colour.WHITE);
			Render.font(caret.getFont());
			String suggName = sugg.name;
			Render.drawString(suggName, x + 5, y + 4 + ((i + 1) * h));
			
			// and the message
			Render.colour(Colour.GRAY);
			Render.drawString(" - " + sugg.getShortHelp(), x + 5 + caret.getFont().getWidth(suggName), y + 4 + ((i + 1) * h));
		}
	}
	
	public void processCommand(String[] command) {
		String commandName = command[0];
		
		Command cmd = null;
		if (commands.containsKey(commandName)) {
			cmd = commands.get(commandName);
			String args[] = Arrays.copyOfRange(command, 1, command.length);
			if (args.length == cmd.getArgumentCount()) {
				cmd.action(args);
			} else {
				System.err.println("todo fuck");
			}
		}
	}
	
	public void hide() {
		setVisible(false);
		setFocus(false);
		buffer.clear();
		removeSuggestions();
		enteredCommand = false;
	}
	
	private String filePrefix = "";

	@Override
	public boolean keyPress(int keyCode) {
		// lookup after each keypress
		findSuggestions(buffer.getLine().toString());
		
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
			if (suggestions.size() > 0 && selectedSuggestion != -1) {
				String suggested = suggestions.get(selectedSuggestion).name;
				String oldLine = buffer.getLine(0).toString();
				buffer.setLine(new Line(suggested));
				buffer.moveCursor(suggested.length() - oldLine.length(), 0);

				// only add a space after if the command
				// actually takes args!
				Command cmd = suggestions.get(selectedSuggestion);
				if (cmd.argumentCount > 0) {
					buffer.append(' ');
					buffer.moveCursor(1, 0);
				}
				removeSuggestions();
				enteredCommand = true;
			} else {
				String[] command = buffer.getBuffer().get(0).toString().split(" ");
				if (command[0].equals("?")) {
					// populate suggestions with the commands
					for (String c : commands.keySet()) {
						suggestions.add(commands.get(c));
					}
					return true;
				}
				
				if (!commands.containsKey(command[0])) {
					System.err.println("handle this (p.s its command not found)");
					hide();
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
			// FIXME, or does this work?
			// it actually works well.
			if (enteredCommand 
					&& buffer.getLine().charAt(buffer.getCaret().ix - 1) == ' ') {
				enteredCommand = false;
			}
			break;
		}
		if (suggestions.size() > 0) {
			String[] command = buffer.getBuffer().get(0).toString().split(" ");
			int suggestionIndex = getSuggestionIndex(command[0]);
			if (suggestionIndex != -1) {
				selectedSuggestion = suggestionIndex;
			}
		}
		
		return false;
	}
	
	private void removeSuggestions() {
		suggestions.clear();		
		selectedSuggestion = -1;
	}

	public int getSuggestionIndex(String name) {
		for (String key : commands.keySet()) {
			if (key.startsWith(name)) {
				return suggestions.indexOf(commands.get(key));
			}
		}
		return -1;
	}

	public static HashMap<String, Command> getCommands() {
		return commands;
	}

	public void setText(String input) {
		buffer.getBuffer().clear();
		buffer.getBuffer().add(new Line(input));
		buffer.getCaret().move(input.length(), 0);
	}
	
}
