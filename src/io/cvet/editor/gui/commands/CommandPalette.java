package io.cvet.editor.gui.commands;

import io.cvet.editor.Layout;
import io.cvet.editor.gfx.Colour;
import io.cvet.editor.gfx.Render;
import io.cvet.editor.gui.Component;
import io.cvet.editor.gui.Cursor;
import io.cvet.editor.gui.CursorAction;
import io.cvet.editor.gui.TextArea;
import io.cvet.editor.gui.Cursor.CursorStyle;
import io.cvet.editor.util.Input;
import io.cvet.editor.util.Theme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

public class CommandPalette extends Component implements CursorAction {

	private Colour background = Colour.PINK;
	private TextArea buffer;
	private Cursor caret;
	
	private int defaultHeight;
	private int hack = 0;
	private int lastTimeTyped = 0;
	private int selectionIndex = 0;
	
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
		
		buffer.setBackground(new Colour(0x61A598));
		buffer.setFocus(true);
		buffer.setFont(Render.INTERFACE_FONT);
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
		lastTimeTyped = 0;
	}
	
	@Override
	public void update() {
		lastTimeTyped++;
		updateChildren(children);
		
		if (lastTimeTyped >= 1) {
			findSuggestions(buffer.getLine().toString());
		}
		
		if (suggestions.size() >= 0) {
			if (Input.getKeyPressed(Keyboard.KEY_DOWN)) {
				selectionIndex++;
			} else if (Input.getKeyPressed(Keyboard.KEY_UP)) {
				selectionIndex--;
			}
		}
		
		// wrap around
		if (selectionIndex >= suggestions.size()) {
			selectionIndex = 0;
		} else if (selectionIndex < 0) {
			selectionIndex = suggestions.size() - 1;
		}
		
		hack++;
	}
	
	@Override
	public void render() {
		Render.endClip();
		
		Render.colour(background);
		Render.rect(x, y, w, h);
		Render.colour(Colour.BLACK);
		Render.rect(x, y, w + 2, h + 2);
		
		renderChildren(children);

		for (int i = 0; i < suggestions.size(); i++) {
			Render.colour(selectionIndex == i ? Theme.DARK_ACCENT : Theme.ACCENT);
			Render.rect(x, y + ((i + 1) * h), w, h);
			
			Render.colour(Colour.WHITE);
			Render.font(Render.INTERFACE_FONT);
			Render.drawString(suggestions.get(i).name, x + 5, y + 4 + ((i + 1) * h));
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
		hack = 0;
		lastTimeTyped = 0;
		buffer.clear();
		selectionIndex = -1;
		suggestions.clear();
	}

	@Override
	public boolean keyPress(int keyCode) {
		switch (keyCode) {
		case Keyboard.KEY_TAB:
			if (suggestions.size() > 0) {
				String suggested = suggestions.get(selectionIndex).name;
				String oldLine = buffer.getLine(0).toString();
				buffer.setLine(suggested);
				buffer.moveCursor(suggested.length() - oldLine.length(), 0);
				selectionIndex = -1;
				suggestions.clear();
			}
			return true;
		case Keyboard.KEY_RETURN:
			String command = buffer.getBuffer().get(0).toString();
			processCommand(command.split(" "));
			hide();
			return true;
		case Keyboard.KEY_ESCAPE:
			if (hack > 5) {
				hide();
			}
			break;
		}
		lastTimeTyped = 0;
		return false;
	}

	public static HashMap<String, Command> getCommands() {
		return commands;
	}

	public void setText(String input) {
		buffer.getBuffer().clear();
		buffer.getBuffer().add(new StringBuilder(input));
		buffer.getCaret().move(input.length(), 0);
	}
	
}
