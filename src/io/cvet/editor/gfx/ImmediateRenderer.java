package io.cvet.editor.gfx;

import org.lwjgl.opengl.GL11;

/*
 * An abstraction over OpenGL to simplify
 * supporting different versions, backends,
 * etc..
 */
public class ImmediateRenderer extends RenderBackend {

	@Override
	public void init(final int width, final int height) {
		System.out.println("Initializing OpenGL Renderer");
		
//		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glOrtho(0, width, height, 0, 1, -1);
	}
	
	public void type(GeometricType type) {
		super.type(type);
		GL11.glBegin(type.getRawType());
	}
	
	public void reset() {
		super.reset();
		GL11.glEnd();
	}
	
	@Override
	public void vertex(float x, float y) {
		GL11.glVertex2f(x, y);
	}

	@Override
	public void tex(float x, float y) {
		GL11.glTexCoord2f(x, y);
	}

	@Override
	public void colour(float r, float g, float b, float a) {
		this.r = r / 255;
		this.g = g / 255;
		this.b = b / 255;
		this.a = a / 255;
		GL11.glColor4f(this.r, this.g, this.b, this.a);
	}

	@Override
	public void colour(float r, float g, float b) {
		colour(r, g, b, 255);
	}

}
