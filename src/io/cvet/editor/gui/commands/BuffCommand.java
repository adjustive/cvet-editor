package io.cvet.editor.gui.commands;

import io.cvet.editor.Editor;
import io.cvet.editor.gui.Buffer;

public class BuffCommand extends Command {

	public BuffCommand() {
		super("buff", 1);
	}

	@Override
	public void action(String[] arguments) {
		String buffer = arguments[0];
		Buffer buff = Editor.getInstance().getBuffers().get(buffer);
		if (buff == null) {
			System.err.println("buffer " + buffer + " no exist :(");
			return;
		}
		Editor.getInstance().pushBuffer(buff);
	}

	@Override
	public String getHelp() {
		return "Opens the given buffer in the current view\n";
	}

}
