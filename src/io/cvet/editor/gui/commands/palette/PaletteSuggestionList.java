package io.cvet.editor.gui.commands.palette;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import io.cvet.editor.gfx.Colour;
import io.cvet.editor.gfx.RenderBackend;
import io.cvet.editor.gfx.RenderContext;
import io.cvet.editor.gui.Component;
import io.cvet.editor.gui.commands.Command;
import io.cvet.editor.gui.commands.CommandPalette;
import io.cvet.editor.gui.commands.palette.PaletteSuggestion.SuggestionType;
import io.cvet.editor.util.Input;
import io.cvet.editor.util.Theme;

public class PaletteSuggestionList extends Component {

	private CommandPalette owner;
	private ArrayList<PaletteSuggestion> suggestions = new ArrayList<PaletteSuggestion>();
	private int selectedSuggestion;

	public PaletteSuggestionList(CommandPalette palette) {
		this.owner = palette;
		this.w = owner.w;
		this.h = owner.h;
		this.y = owner.y;
	}

	public void init() {
	}

	public void update() {
		if (isPopulated()) {
			if (Input.getKeyPressed(Keyboard.KEY_DOWN)) {
				selectedSuggestion++;
			} 
			else if (Input.getKeyPressed(Keyboard.KEY_UP)) {
				selectedSuggestion--;
			}
		}

		if (selectedSuggestion >= suggestions.size()) {
			selectedSuggestion = 0;
		} 
		else if (selectedSuggestion < 0) {
			selectedSuggestion = suggestions.size() - 1;
		}
	}

	public void render() {
		for (int i = 0; i < suggestions.size(); i++) {

			// render a cheeky shadow
			RenderContext.colour(Theme.DARK_BASE);
			RenderContext.rect(x, y + (i * h), w + 2, h + 2);

			// render the background +
			// set the colour if its selected
			RenderContext.colour(selectedSuggestion == i ? Theme.DARK_ACCENT : Theme.ACCENT);
			RenderContext.rect(x, y + (i * h), w, h);

			// render the command name
			RenderContext.colour(Colour.WHITE);
			RenderContext.font(owner.getCaret().getFont());
			String suggName = suggestions.get(i).key;
			RenderContext.drawString(suggName, x + 5, y + 4 + (i * h));

			// only show help msg if command
			Command c = CommandPalette.getCommands().get(suggName);
			if (c != null) {
				// and the message
				RenderContext.colour(Colour.GRAY);
				RenderContext.drawString(" - " + c.getShortHelp(),
						x + 5 + owner.getCaret().getFont().getWidth(suggName), y + 4 + (i * h));
			}
		}
	}

	public void autoSelect() {
		if (isPopulated()) {
			String[] trigger = owner.getBuffer().getLines().get(0).toString().split(" ");
			for (int i = 0; i < suggestions.size(); i++) {
				if (suggestions.get(i).key.startsWith(trigger[0].trim())) {
					selectedSuggestion = i;
				}
			}
		}
	}

	public boolean isPopulated() {
		return suggestions.size() > 0;
	}

	public void add(PaletteSuggestion paletteSuggestion) {
		suggestions.add(paletteSuggestion);
	}

	public PaletteSuggestion getCurrentSuggestion() {
		return suggestions.get(selectedSuggestion);
	}

	public void clear() {
		if (suggestions.size() == 0) {
			return;
		}
		suggestions.clear();
	}

	public void find(String needle) {
		// we've already got the command
		// lets get the fuc outta here
		if (owner.enteredCommand()) {
			return;
		}

		// dont show the suggestions if
		// the buffer is empty
		if (owner.getBuffer().getCharacterCount() == 0) {
			suggestions.clear();
			return;
		}

		for (String s : CommandPalette.getCommands().keySet()) {
			if (s.contains(needle)) {
				if (containsSuggestionWithKey(s) == -1) {
					suggestions.add(new PaletteSuggestion(s, SuggestionType.Command));
				}
			}
		}
	}
	
	public int containsSuggestionWithKey(String key) {
		for (int i = 0; i < suggestions.size(); i++) {
			if (suggestions.get(i).key.equals(key)) {
				return i;
			}
		}
		return -1;
	}

}
