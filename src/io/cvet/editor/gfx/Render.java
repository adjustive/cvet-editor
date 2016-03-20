package io.cvet.editor.gfx;

import static org.lwjgl.opengl.GL11.*;

/*
 * An abstraction over OpenGL to simplify
 * supporting different versions, backends,
 * etc..
 */
public class Render {
	
	private static float r, g, b, a;

	public static void showPolygons() {
		glEnable(GL_POLYGON_MODE);
		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
	}
	
	public static void hidePolygons() {
		glDisable(GL_POLYGON_MODE);
		// reset old colours
		glColor4f(r, g, b, a);
	}
	
	public static void colour(float r, float g, float b, float a) {
		Render.r = r / 255;
		Render.g = g / 255;
		Render.b = b / 255;
		Render.a = a / 255;
	}
	
	public static void colour(float r, float g, float b) {
		colour(r, g, b, 255);
	}
	
	private static void applyColour() {
		glColor4f(r, g, b, a);
	}
	
	public static void rect(int x, int y, int w, int h) {
		applyColour();
		glBegin(GL_TRIANGLES);
			glVertex2f(x, y);
			glVertex2f(x + w, y);
			glVertex2f(x + w, y + h);
			
			glVertex2f(x, y);
			glVertex2f(x + w, y + h);
			glVertex2f(x, y + h);
		glEnd();
	}
	
}
