package io.cvet.editor.gui;

import io.cvet.editor.gfx.Colour;
import io.cvet.editor.gfx.Render;

import org.newdawn.slick.TrueTypeFont;

public class Label extends Component {

	private String value;
	private static TrueTypeFont face = Render.INTERFACE_FONT;
	private Colour background = null;
	private Colour foreground = Colour.WHITE;
	
	public Label(String value, int w, int h) {
		this.value = value;
		this.w = w;
		this.h = h;
	}

	public Label(String value) {
		this(value, face.getWidth(value), face.getHeight() * 2);
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
			Render.colour(Colour.BLACK);
			Render.rect(x, y, w + 2, h + 2);
			Render.colour(background);
			Render.rect(x, y, w, h);
		}
		
		Render.colour(foreground);
		Render.font(face);
		
		int xx = (w / 2) - (face.getWidth(value) / 2);
		Render.drawString(value, x + xx, y + 3);
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
