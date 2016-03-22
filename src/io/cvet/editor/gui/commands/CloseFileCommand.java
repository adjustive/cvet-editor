package io.cvet.editor.gui.commands;

import io.cvet.editor.Editor;

public class CloseFileCommand extends Command {

	public CloseFileCommand() {
		super("close", 0);
	}

	@Override
	public void action(String[] arguments) {
		Editor.getInstance().closeCurrentBuffer();
	}
	
	@Override
	public String getHelp() {
		return "Closes the current buffer.";
	}

}
