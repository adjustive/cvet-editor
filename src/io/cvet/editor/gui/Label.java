package io.cvet.editor.gui;

import io.cvet.editor.gfx.Colour;
import io.cvet.editor.gfx.Render;

public class Label extends Component {

	private String value;
	private Colour background = null;
	private Colour foreground = Colour.WHITE;
	
	public Label(String value, int w, int h) {
		this.value = value;
		this.w = w;
		this.h = h;
	}

	public Label(String value) {
		this(value, Render.MONOSPACED_FONT.getWidth(value), Render.MONOSPACED_FONT.getHeight() * 2);
	}
	
	@Override
	public void init() {
		
	}

	@Override
	public void update() {
		
	}

	@Override
	public void render() {
		if (background != null) {
			Render.colour(background);
			Render.rect(x, y, w, h);
		}
		
		Render.colour(foreground);
		Render.drawString(value, x + 5, y + 1);
	}

	public void setBackground(Colour background) {
		this.background = background;
	}

	public void setForeground(Colour foreground) {
		this.foreground = foreground;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
