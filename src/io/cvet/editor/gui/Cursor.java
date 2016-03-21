package io.cvet.editor.gui;

public class Cursor {

	private TextArea owner;
	
	// where the cursor is positioned in the buffer
	private int insertionPoint; // ;)
	
	// cursor size
	private int w, h;

	public Cursor(TextArea owner) {
		this.owner = owner;
	}
	
}
