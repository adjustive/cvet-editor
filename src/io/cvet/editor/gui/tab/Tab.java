package io.cvet.editor.gui.tab;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import io.cvet.editor.gfx.Colour;
import io.cvet.editor.gfx.RenderBackend;
import io.cvet.editor.gfx.RenderContext;
import io.cvet.editor.gui.Buffer;
import io.cvet.editor.gui.Component;
import io.cvet.editor.util.Input;
import io.cvet.editor.util.Theme;

public class Tab extends Component {

	public int index;
	public String name;
	public Buffer buff;
	
	private int padding = 10;
	
	public Tab(int index, String name, Buffer buff) {
		setFont(RenderBackend.EDITING_FONT);
		
		this.index = index;
		this.name = name;
		this.buff = buff;
		this.w = font.getWidth(name) + (padding * 2);
		this.h = font.getHeight() + (padding);
		addChild(buff);
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
		RenderContext.rect(x, y, w, h);
		
		// render tab name in a center alignment
		int tabNameWidth = font.getWidth(name) + (padding / 2);
		RenderContext.colour(Colour.WHITE);
		RenderContext.drawString(name, x + ((w / 2) - (tabNameWidth / 2)), y + ((h / 2) - (font.getHeight() / 2)));
	}

	@Override
	public void render() {
		GL11.glPushMatrix();
		GL11.glTranslatef(0, this.h, 0);
		renderChildren(children);
		GL11.glPopMatrix();
	}
	
}
