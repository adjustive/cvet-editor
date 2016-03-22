package io.cvet.editor.gui.commands;

import io.cvet.editor.Editor;

public class ExitCommand extends Command {

	public ExitCommand() {
		super("exit", 0);
	}

	@Override
	public void action(String[] arguments) {
		Editor.getInstance().exit();
	}
	
}
