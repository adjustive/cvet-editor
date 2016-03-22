package io.cvet.editor.gui.commands;

import io.cvet.editor.Editor;
import io.cvet.editor.gui.TextArea;

public class NewFileCommand extends Command {

	public NewFileCommand() {
		super("new", 1);
	}

	@Override
	public void action(String[] arguments) {
		String filename = arguments[0];
		Editor.getInstance().setCurrentTextArea(new TextArea(filename));
	}

}
