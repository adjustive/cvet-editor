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

		/* immediaet 
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glOrtho(0, width, height, 0, 1, -1);
*/
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);        
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);                    
        
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);                
        GL11.glClearDepth(1);                                       
        
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        GL11.glViewport(0,0,width,height);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
	
	public void flush() {}
	
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

	@Override
	public void colour(float[] col) {
		assert (col.length < 0 || col.length > 3);
		colour(col[0], col[1], col[2]);
	}

}
