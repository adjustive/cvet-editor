package io.cvet.editor.gui.commands;

import javax.swing.JOptionPane;

import io.cvet.editor.Editor;
import io.cvet.editor.gui.Buffer;

public class CloseFileCommand extends Command {

	public CloseFileCommand() {
		super("close", 0);
	}

	@Override
	public void action(String[] arguments) {
		Buffer buff = Editor.getInstance().getCurrentBuffer();
		if (!buff.hasBeenSaved()) {
			int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to close " + buff.getName() + " has unsaved changes", "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);
			switch (result) {
			case JOptionPane.CANCEL_OPTION:
			case JOptionPane.NO_OPTION:
				return;
			case JOptionPane.YES_OPTION:
				break;
			}
		}
		Editor.getInstance().closeCurrentBuffer();
	}
	
	@Override
	public String getHelp() {
		return "Closes the current buffer.";
	}

}
