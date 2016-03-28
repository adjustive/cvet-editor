package io.cvet.editor.gui.menu;

import io.cvet.editor.gfx.Colour;
import io.cvet.editor.gfx.RenderBackend;
import io.cvet.editor.gfx.RenderContext;
import io.cvet.editor.gui.Component;

public class MenuItem extends Component {

	public String name;
	public String tooltip;
	public MenuAction action;
	
	private Menu parent;
	private Colour hoverColour = new Colour(0x0087FF);
	private Colour defaultColour = Colour.WHITE;
	private Colour currentColour = defaultColour;
	private int padding = 10;
	
	public MenuItem(Menu parent, String name, MenuAction action) {
		this.parent = parent;
		this.name = name;
		this.action = action;
		this.w = RenderBackend.CURRENT_FONT.getWidth(name) + (padding * 2);
		this.h = RenderBackend.CHARACTER_HEIGHT + (padding); // TODO: do this properly
	}
	
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	@Override
	public void init() {
		
	}

	@Override
	public void update() {
		currentColour = defaultColour;
	}

	@Override
	public void render() {
		// TODO: render rectangle size of the menu itself
		// not the item.
		
		RenderContext.colour(currentColour);
		RenderContext.rect(x, y, parent.w, h);

		int xo = ((this.w / 2) - (RenderBackend.CURRENT_FONT.getWidth(name) / 2));
				
		// TODO: label and center align?
		// Rendering bug..
		RenderContext.colour(currentColour == hoverColour ? Colour.WHITE : Colour.BLACK);
		RenderContext.drawString(name, x + xo, y + (padding / 2));
	}

	public void hover() {
		currentColour = hoverColour;
	}
	
}
