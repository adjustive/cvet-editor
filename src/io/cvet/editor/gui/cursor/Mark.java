package io.cvet.editor.gui.cursor;

import io.cvet.editor.gfx.ImmediateRenderer;
import io.cvet.editor.gfx.RenderBackend;
import io.cvet.editor.gfx.RenderContext;

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
		RenderContext.colour(255, 0, 255);
		RenderContext.rect(start.x + caret.padding, start.y + caret.padding, end.x, RenderBackend.EDITING_FONT.getHeight());
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
			return x / ImmediateRenderer.EDITING_FONT.getWidth(" ");
		}
		
		public int getY() {
			// FIXME fml
			return y / ImmediateRenderer.EDITING_FONT.getHeight();
		}
	}

}
