package io.cvet.editor.gui;

import io.cvet.editor.gfx.Render;

public class Label extends Component {

	private String value;
	
	public Label(String value) {
		this.value = value;
		this.w = Render.MONOSPACED_FONT.getWidth(value);
		this.h = Render.MONOSPACED_FONT.getHeight() * 2;
	}
	
	@Override
	public void init() {
		
	}

	@Override
	public void update() {
		
	}

	@Override
	public void render() {
		Render.colour(255, 255, 255);
		Render.drawString(value, x, y);
	}

	public void setValue(String value) {
		this.value = value;
	}

}
