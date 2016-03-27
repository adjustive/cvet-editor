package io.cvet.editor.gui.commands.palette;

import io.cvet.editor.Editor;
import io.cvet.editor.gui.Buffer;
import io.cvet.editor.gui.commands.Command;
import io.cvet.editor.gui.commands.CommandPalette;

public class PaletteSuggestion {

	public static enum SuggestionType {
		Command,
		Buffer,
	}
	
	public String key;
	public SuggestionType type;
	
	public PaletteSuggestion(String key, SuggestionType type) {
		this.key = key;
		this.type = type;
	}
	
	public Command lookupCommand() {
		return CommandPalette.getCommands().get(key);
	}
	
	public Buffer lookupBuffer() {
		return Editor.getInstance().mainView.getBuffer(key);
	}
	
}
