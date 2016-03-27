package io.cvet.editor.gui;

import org.newdawn.slick.TrueTypeFont;

import io.cvet.editor.gfx.Colour;
import io.cvet.editor.gfx.ImmediateRenderer;
import io.cvet.editor.gfx.RenderContext;
import io.cvet.editor.util.Theme;

public class Label extends Component {

	private String value;
	private TrueTypeFont font = ImmediateRenderer.INTERFACE_FONT;
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
			// drop shadow
			RenderContext.colour(Theme.DARK_BASE);
			RenderContext.rect(x - xPad, y + yPad, w + 2, h + 2);

			RenderContext.colour(background);
			RenderContext.rect(x - xPad, y + yPad, w, h);
		}
		
		RenderContext.colour(foreground);
		RenderContext.font(font);
		
		int xx = (w / 2) - (font.getWidth(value) / 2);
		RenderContext.drawString(value, x + xx - xPad, y + (int) (yPad * 1.5));
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

	public String getValue() {
		return value;
	}

}
