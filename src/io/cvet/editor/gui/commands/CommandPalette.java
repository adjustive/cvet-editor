package io.cvet.editor.gui.commands;

import io.cvet.editor.Layout;
import io.cvet.editor.gfx.Colour;
import io.cvet.editor.gfx.Render;
import io.cvet.editor.gui.Component;
import io.cvet.editor.gui.CursorAction;
import io.cvet.editor.gui.TextArea;

import java.util.Arrays;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

public class CommandPalette extends Component implements CursorAction {

	private Colour background = Colour.PINK;
	private TextArea buffer;
	private int defaultHeight;
	private int hack = 0;
	
	private static HashMap<String, Command> commands = new HashMap<String, Command>();
	static {
		commands.put("new", new NewFileCommand());
		commands.put("open", new OpenFileCommand());
		commands.put("close", new CloseFileCommand());
		commands.put("exit", new ExitCommand());
		commands.put("save", new SaveFileCommand());
		commands.put("configure", new EditSettingsCommand());
		commands.put("help", new HelpCommand());
	}
	
	public CommandPalette() {
		this.defaultHeight = Render.MONOSPACED_FONT.getHeight() + 10;
		this.w = 512;
		this.h = defaultHeight;
		this.x = (Display.getWidth() / 2) - (this.w / 2);
		this.y = 5;
		
		this.buffer = new TextArea(this.w, defaultHeight);
		buffer.setBackground(new Colour(0x61A598));
		buffer.setFocus(true);
		buffer.getCaret().setCursorAction(this);
		buffer.getCaret().setColour(new Colour(0x3D3331));
		addChild(buffer, Layout.Child);
	}
	
	@Override
	public void init() {
		
	}

	@Override
	public void update() {
		updateChildren(children);
		hack++;
	}

	@Override
	public void render() {
		Render.colour(background);
		Render.rect(x, y, w, h);
		Render.colour(Colour.BLACK);
		Render.rect(x, y, w + 2, h + 2);
		
		renderChildren(children);
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
		buffer.clear();
	}

	@Override
	public void keyPress(int keyCode) {
		switch (keyCode) {
		case Keyboard.KEY_RETURN:
			String command = buffer.getBuffer().get(0).toString();
			processCommand(command.split(" "));
			hide();
			break;
		case Keyboard.KEY_ESCAPE:
			if (hack > 5) {
				hide();
			}
			break;
		}
	}

	public static HashMap<String, Command> getCommands() {
		return commands;
	}
	
}
