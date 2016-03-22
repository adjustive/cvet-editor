package io.cvet.editor.gui.commands;

import io.cvet.editor.Editor;
import io.cvet.editor.config.Settings;
import io.cvet.editor.gui.Buffer;

public class EditSettingsCommand extends Command {

	public EditSettingsCommand() {
		super("config", 0);
	}

	@Override
	public void action(String[] arguments) {
		Editor.getInstance().setCurrentBuffer(new Buffer(Settings.getUserConfigFile()));
	}

}
