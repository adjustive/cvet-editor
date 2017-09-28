package io.cvet.editor.gui.commands;

import io.cvet.editor.Editor;
import io.cvet.editor.gui.Buffer;
import io.cvet.editor.gui.tab.Tab;

public class CloseFileCommand extends Command {

	public CloseFileCommand() {
		super("close", 0);
	}

	@Override
	public void action(String[] arguments) {
		// there might be no tabs open
		Tab currTab = Editor.getMainView().getCurrentTab(); 
		if (currTab == null) {
			return;
		}
		
		Buffer buff = currTab.buff;
		if (!buff.hasBeenSaved()) {
			// TODO: panel for y/n option
		}
		Editor.getMainView().closeCurrentTab();
	}
	
	@Override
	public String getHelp() {
		return "Closes the current buffer.";
	}

}
