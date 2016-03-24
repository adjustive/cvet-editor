package io.cvet.editor.gui;

import io.cvet.editor.gfx.Colour;
import io.cvet.editor.gfx.Render;

import org.newdawn.slick.TrueTypeFont;

public class Label extends Component {

	private String value;
	private TrueTypeFont font = Render.INTERFACE_FONT;
	private Colour background = null;
	private Colour foreground = Colour.WHITE;
	private int xPad = 15, yPad = 10;
	
	public Label(String value, int w, int h) {
		this.value = value;
		this.w = w + xPad;
		this.h = h + yPad;
	}

	public Label(String value, TrueTypeFont font) {
		this(value, font.getWidth(value), font.getHeight());
		setFont(font);
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
			Render.rect(x - xPad, y + yPad, w + 2, h + 2);
			Render.colour(background);
			Render.rect(x - xPad, y + yPad, w, h);
		}
		
		Render.colour(foreground);
		Render.font(font);
		
		int xx = (w / 2) - (font.getWidth(value) / 2);
		Render.drawString(value, x + xx - xPad, y + (int) (yPad * 1.5));
	}

	public void setBackground(Colour background) {
		this.background = background;
	}

	public void setForeground(Colour foreground) {
		this.foreground = foreground;
	}

	public void setValue(String value) {
		this.w = font.getWidth(value) + xPad;
		this.value = value;
	}

	public void setFont(TrueTypeFont font) {
		this.font = font;
	}

}
