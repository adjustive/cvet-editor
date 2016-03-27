package io.cvet.editor.gui.commands;

import io.cvet.editor.Editor;
import io.cvet.editor.gui.Buffer;

public class CloseFileCommand extends Command {

	public CloseFileCommand() {
		super("close", 0);
	}

	@Override
	public void action(String[] arguments) {
		Buffer buff = Editor.getInstance().mainView.getCurrentTab().buff;
		if (!buff.hasBeenSaved()) {
			// TODO: panel for y/n option
		}
		Editor.getInstance().mainView.closeCurrentTab();
	}
	
	@Override
	public String getHelp() {
		return "Closes the current buffer.";
	}

}
