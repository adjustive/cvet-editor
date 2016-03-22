package io.cvet.editor.gui.commands;

import io.cvet.editor.Editor;

public class GotoCommand extends Command {

	public GotoCommand() {
		super("goto", 1);
	}

	@Override
	public void action(String[] arguments) {
		int lineNumber = Integer.parseInt(arguments[0]);
		int currentX = Editor.getInstance().getCurrentBuffer().getCaret().x;
		int currentY = Editor.getInstance().getCurrentBuffer().getCaret().y;
		int delta = 0;
		if (lineNumber > currentY) {
			delta += Math.abs(lineNumber - currentY - 1);
		} else {
			delta -= Math.abs(currentY - lineNumber);
		}
		Editor.getInstance().getCurrentBuffer().getCaret().move(currentX, delta);
	}

	@Override
	public String getHelp() {
		return "Goto the given line number (goto <n>)";
	}

}
