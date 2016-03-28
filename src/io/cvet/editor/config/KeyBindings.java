package io.cvet.editor.config;

import java.util.HashMap;

import org.lwjgl.input.Keyboard;

import io.cvet.editor.gui.commands.CloseFileCommand;
import io.cvet.editor.gui.commands.Command;
import io.cvet.editor.gui.commands.EditSettingsCommand;
import io.cvet.editor.gui.commands.ExitCommand;
import io.cvet.editor.gui.commands.GotoCommand;
import io.cvet.editor.gui.commands.HelpCommand;
import io.cvet.editor.gui.commands.NewFileCommand;
import io.cvet.editor.gui.commands.OpenFileCommand;
import io.cvet.editor.gui.commands.RenameCommand;
import io.cvet.editor.gui.commands.SaveFileCommand;

public class KeyBindings {

	public static final int[] DEFAULT_SUPER_MODIFIER = new int[] {
		Keyboard.KEY_LCONTROL, Keyboard.KEY_RCONTROL,
	};
	
	public static final int[] DEFAULT_SHIFT_MODIFIER = new int[] {
		Keyboard.KEY_LSHIFT, Keyboard.KEY_RSHIFT,
	};
	
	public static final int[] DEFAULT_ALT_MODIFIER = new int[] {
		Keyboard.KEY_LMETA, Keyboard.KEY_RMETA,
	};
	
	public static final int[] NEW_FILE = new int[] {
		DEFAULT_SUPER_MODIFIER[0], DEFAULT_SUPER_MODIFIER[1], 
		Keyboard.KEY_N,	
	};
	
	public static HashMap<String, Command> commands = new HashMap<String, Command>();
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
	
}
