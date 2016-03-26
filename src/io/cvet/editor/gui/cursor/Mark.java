package io.cvet.editor.gui.cursor;

import io.cvet.editor.gfx.ImmediateRenderer;
import io.cvet.editor.gfx.RenderBackend;
import io.cvet.editor.gfx.RenderContext;

public class Mark {

	private Cursor caret;
	public Span start, end;
	
	public Mark(Cursor caret, Span start) {
		this.start = start;
		this.end = new Span(start);
		this.caret = caret;
	}
	
	public void update() {
		
	}
	
	public void render() {
		RenderContext.colour(255, 0, 255);
		RenderContext.rect(start.x + caret.padding, start.y + caret.padding, end.x - start.x, RenderBackend.CHARACTER_HEIGHT);
	}
	
	public static class Span {
		public int x, y;
		
		public Span(int x, int y) {
			this.x = x * RenderBackend.CHARACTER_WIDTH;
			this.y = y * RenderBackend.CHARACTER_HEIGHT;
		}
		
		public Span(Span end) {
			this(end.getX(), end.getY());
		}
		
		public Span() {
			
		}
		
		public int getX() {
			// FIXME fml
			return x / RenderBackend.CHARACTER_WIDTH;
		}
		
		public int getY() {
			// FIXME fml
			return y / RenderBackend.CHARACTER_HEIGHT;
		}
	}

}
