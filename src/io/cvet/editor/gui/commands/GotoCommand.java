package io.cvet.editor.gui.commands;

import io.cvet.editor.Editor;

public class GotoCommand extends Command {

	public GotoCommand() {
		super("goto", 1);
	}

	@Override
	public void action(String[] arguments) {
		int lineNumber;
		try {
			lineNumber = Integer.parseInt(arguments[0]);
		} catch (Exception e) {
			// cheeky cunt might've put a NaN or some shit
			return;
		}
		int currentX = Editor.getInstance().getCurrentBuffer().getCaret().x;
		Editor.getInstance().getCurrentBuffer().getCaret().set(currentX, lineNumber - 1);
	}

	@Override
	public String getHelp() {
		return "Goto the given line number (goto <n>)";
	}

}
