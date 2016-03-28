package io.cvet.editor.gui.commands;

import io.cvet.editor.Editor;
import io.cvet.editor.gui.Buffer;

import java.util.HashMap;

public class HelpCommand extends Command {

	public HelpCommand() {
		super("help", 0);
	}

	@Override
	public void action(String[] arguments) {
		String helpMessage = "# Help\n" +
				"Here are a list of commands, what\n" +
				"action they perform, and their usage.\n" +
				"\n";
		
		HashMap<String, Command> commands = CommandPalette.getCommands();
		for (String s : commands.keySet()) {
			Command c = commands.get(s);
			String desc = c.getHelp();
			helpMessage += "* " + s + "\n";
			for (String line : desc.split("\n")) {
				helpMessage += "  " + line + "\n";
			}
			helpMessage += "\n";
		}
		
		Editor.getMainView().addTab(new Buffer("help", helpMessage));
	}

	@Override
	public String getHelp() {
		return "Displays commands and what they do.";
	}
	
	

}
