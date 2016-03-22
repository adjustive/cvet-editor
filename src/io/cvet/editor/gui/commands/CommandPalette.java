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
	
	private static HashMap<String, Command> commands = new HashMap<String, Command>();
	static {
		commands.put("new", new NewFileCommand());
		commands.put("close", new CloseFileCommand());
	}
	
	public CommandPalette() {
		this.defaultHeight = Render.MONOSPACED_FONT.getHeight() + 10;
		this.w = 512;
		this.h = defaultHeight;
		this.x = (Display.getWidth() / 2) - (this.w / 2);
		this.y = 0;
		
		this.buffer = new TextArea(this.w, defaultHeight);
		buffer.setFocus(true);
		buffer.getCaret().setCursorAction(this);
		addChild(buffer, Layout.Child);
	}
	
	@Override
	public void init() {
		
	}

	@Override
	public void update() {
		updateChildren(children);
	}

	@Override
	public void render() {
		Render.colour(background);
		Render.rect(x, y, w, h);
		
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

	@Override
	public void keyPress(int keyCode) {
		if (keyCode == Keyboard.KEY_RETURN) {
			String command = buffer.getBuffer().get(0).toString();
			processCommand(command.split(" "));
			buffer.clear();
			buffer.getCaret().reset();
			setVisible(false);
			setFocus(false);
		}
	}

}
