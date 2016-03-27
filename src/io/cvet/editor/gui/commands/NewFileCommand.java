package io.cvet.editor.gui.commands;

import io.cvet.editor.Editor;
import io.cvet.editor.gui.Buffer;

public class NewFileCommand extends Command {

	public NewFileCommand() {
		super("new", 1);
	}

	@Override
	public void action(String[] arguments) {
		Editor.getInstance().mainView.addTab(new Buffer(arguments[0]));
	}

	@Override
	public String getHelp() {
		return "Creates a new file with the given name.";
	}
	
}
