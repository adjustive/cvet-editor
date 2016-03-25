package io.cvet.editor.gui;

import org.lwjgl.opengl.Display;

import io.cvet.editor.gfx.RenderContext;

public class Panel extends Component {

	public Panel(int x, int y) {
		this.x = x;
		this.y = y;
		this.w = Math.abs(Display.getWidth() - x) - y;
		this.setFocus(true);
	}
	
	@Override
	public void init() {
		
	}

	@Override
	public void update() {
		// expand height
		this.h = children.size() * 28;
	}

	@Override
	public void render() {
		RenderContext.colour(255, 0, 255);
		RenderContext.rect(x, y, w, h);
		
		for (Component c : children) {
			c.render();
		}
	}
	
}
