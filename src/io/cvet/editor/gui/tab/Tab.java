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
	
	private int padding = 10;
	
	public Tab(String name, Buffer buff) {
		setFont(RenderBackend.INTERFACE_FONT);
		
		this.name = name;
		this.buff = buff;
		this.w = font.getWidth(name) + (padding * 2);
		this.h = font.getHeight() + (padding);
		
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
	
	public void renderTab(int x, int y) {
		RenderContext.font(font);
		RenderContext.colour(Theme.ACCENT);
		RenderContext.rect(x, y, w, h - 1);
		
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
