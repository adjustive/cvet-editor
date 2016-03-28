package io.cvet.editor.gui.text;

public class SyntaxHighlighter {

	private Line current;
	
	public SyntaxHighlighter() {
		
	}
	
	public void highlightLine(Line line) {
		this.current = line;
		
		for (char c : line.toString().toCharArray()) {
			
		}
	}
	
}
