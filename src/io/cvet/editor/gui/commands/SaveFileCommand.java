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
	
}
