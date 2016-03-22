package io.cvet.editor.gui.commands;

import io.cvet.editor.Editor;
import io.cvet.editor.gfx.Colour;
import io.cvet.editor.gui.TextArea;

public class NewFileCommand extends Command {

	public NewFileCommand() {
		super("new", 1);
	}

	@Override
	public void action(String[] arguments) {
		String filename = arguments[0];
		TextArea area = new TextArea(filename);
		area.setBackground(new Colour(0x3D3331));
		area.setCursorColour(new Colour(0x61A598));
		area.setForeground(Colour.PINK);

		Editor.getInstance().clearFocus();
		Editor.getInstance().setCurrentTextArea(area);
	}

}
