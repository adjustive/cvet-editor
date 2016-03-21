package io.cvet.editor.gui;

import io.cvet.editor.gfx.Render;
import io.cvet.editor.util.RNG;

public class Button extends Component {

	private int r, g, b;
	
	public Button(int w, int h) {
		this.w = w;
		this.h = h;
		this.r = RNG.cap(255);
		this.g = RNG.cap(255);
		this.b = RNG.cap(255);
	}
	
	public Button(int x, int y, int w, int h) {
		super(x, y, w, h);
	}
	
	@Override
	public void init() {
		
	}

	@Override
	public void update() {
		
	}

	@Override
	public void render() {
		Render.colour(r, g, b);
		Render.rect(x, y, w, h);
	}

}
