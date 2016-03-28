package io.cvet.editor.gui.commands;

import io.cvet.editor.Editor;

public class RenameCommand extends Command {

	public RenameCommand() {
		super("rename", 1);
	}

	@Override
	public void action(String[] arguments) {
		Editor.getMainView().getCurrentTab().buff.rename(arguments[0]);
	}

	@Override
	public String getHelp() {
		return "Renames the current buffer\n";
	}

}
