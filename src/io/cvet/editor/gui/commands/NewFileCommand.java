package io.cvet.editor.gui.commands;

import io.cvet.editor.Editor;
import io.cvet.editor.gui.Buffer;

public class NewFileCommand extends Command {

	public NewFileCommand() {
		super("new", 1);
	}

	@Override
	public void action(String[] arguments) {
		String filename = arguments[0];
		Editor.getInstance().setCurrentBuffer(new Buffer(filename, false));
	}

}
