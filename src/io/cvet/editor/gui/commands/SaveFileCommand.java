package io.cvet.editor.gui.commands;

import io.cvet.editor.Editor;

public class SaveFileCommand extends Command {

	public SaveFileCommand() {
		super("save", 0);
	}

	@Override
	public void action(String[] arguments) {
		Editor.getInstance().getCurrentBuffer().save();
	}

	@Override
	public String getHelp() {
		return "Saves the current buffer. Will prompt for a location\n" +
				"if the file has never been saved before.";
	}
	
}
