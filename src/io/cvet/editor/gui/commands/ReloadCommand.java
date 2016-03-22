package io.cvet.editor.gui.commands;

import io.cvet.editor.config.Settings;

/**
 * Will reload the settings, note this
 * will likely not effect everything and
 * it is best to fully restart the editor.
 * 
 * @author felix
 */
public class ReloadCommand extends Command {

	public ReloadCommand() {
		super("reload", 0);
	}

	@Override
	public void action(String[] arguments) {
		Settings.reload();
	}
	
}
