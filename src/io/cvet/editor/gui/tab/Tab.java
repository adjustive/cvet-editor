package io.cvet.editor.gui.tab;

import io.cvet.editor.gfx.Colour;
import io.cvet.editor.gfx.RenderBackend;
import io.cvet.editor.gfx.RenderContext;
import io.cvet.editor.gui.Buffer;
import io.cvet.editor.gui.Component;
import io.cvet.editor.util.Theme;

public class Tab extends Component {

	public String name;
	public Buffer buff;
	
	private final int padding = 5;
	
	public Tab(String name, Buffer buff) {
		setFont(RenderBackend.INTERFACE_FONT);
		
		this.name = name;
		this.buff = buff;
		this.w = font.getWidth(name) + (padding * 4);
		this.h = font.getHeight() + (padding * 3);
		
		addChild(buff);
		buff.setPosition(buff.x, this.h);
	}

	@Override
	public void init() {
		
	}

	@Override
	public void update() {
		updateChildren(children);
	}
	
	public void renderTab() {
		RenderContext.font(font);
		RenderContext.colour(Theme.SECONDARY);
		RenderContext.rect(x, y, w, h);
		
		// render tab name in a center alignment
		int tabNameWidth = font.getWidth(name);
		RenderContext.colour(Colour.WHITE);
		RenderContext.drawString(name, x + ((w / 2) - (tabNameWidth / 2)), y + ((h / 2) - (font.getHeight() / 2)));
	}

	@Override
	public void render() {
		renderChildren(children);
	}

}
