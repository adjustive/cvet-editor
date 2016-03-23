package io.cvet.editor.gui.cursor;

import io.cvet.editor.gfx.Render;

public class Mark {

	private Cursor caret;
	public Span start, end;
	
	public Mark(Cursor caret, Span start, Span end) {
		this.start = start;
		this.end = end;
		this.caret = caret;
		System.out.println(start.x + ", " + end.y);
	}
	
	public void update() {
		
	}
	
	public void render() {
		Render.colour(255, 0, 255);
		Render.rect(start.x + caret.padding, start.y + caret.padding, end.x, Render.EDITING_FONT.getHeight());
	}
	
	public static class Span {
		public int x, y;
		
		public Span(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public Span() {
			
		}
		
		public int getX() {
			// FIXME fml
			return x / Render.EDITING_FONT.getWidth(" ");
		}
		
		public int getY() {
			// FIXME fml
			return y / Render.EDITING_FONT.getHeight();
		}
	}

}
