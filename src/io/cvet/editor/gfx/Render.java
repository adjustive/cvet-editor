package io.cvet.editor.gfx;

import static org.lwjgl.opengl.GL11.*;

import io.cvet.editor.config.Settings;

import java.awt.Font;

import org.newdawn.slick.TrueTypeFont;

/*
 * An abstraction over OpenGL to simplify
 * supporting different versions, backends,
 * etc..
 */
public class Render {

	private static float r, g, b, a;
	public static TrueTypeFont MONOSPACED_FONT;
	
	private static boolean ANTI_ALIAS;
	private static int FONT_SIZE;
	
	static {
		loadFont();
	}
	
	public static void loadFont() {
		ANTI_ALIAS = (boolean) Settings.getSetting("anti_alias");
		FONT_SIZE = (int) Settings.getSetting("font_size");
		MONOSPACED_FONT = new TrueTypeFont(new Font("Monospaced", Font.PLAIN, FONT_SIZE), ANTI_ALIAS);
	}

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
	
	public static void colour(Colour c) {
		colour(c.r, c.g, c.b, c.a);
	}

	public static void colour(float r, float g, float b) {
		colour(r, g, b, 255);
	}

	private static void applyColour() {
		glColor4f(r, g, b, a);
	}

	public static void rect(int x, int y, int w, int h) {
		glPushMatrix();
		applyColour();
		glBegin(GL_TRIANGLES);
		glVertex2f(x, y);
		glVertex2f(x + w, y);
		glVertex2f(x + w, y + h);

		glVertex2f(x, y);
		glVertex2f(x + w, y + h);
		glVertex2f(x, y + h);
		glEnd();
		glPopMatrix();
	}

	public static void texture(Texture t, int x, int y) {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		int w = t.getWidth();
		int h = t.getHeight();

		glPushMatrix();
		glBindTexture(GL_TEXTURE_2D, t.getHandle());

		glBegin(GL_TRIANGLES);
		glTexCoord2f(0, 0);
		glVertex2f(x, y);

		glTexCoord2f(1, 0);
		glVertex2f(x + w, y);

		glTexCoord2f(1, 1);
		glVertex2f(x + w, y + h);

		glTexCoord2f(0, 0);
		glVertex2f(x, y);

		glTexCoord2f(1, 1);
		glVertex2f(x + w, y + h);

		glTexCoord2f(0, 1);
		glVertex2f(x, y + h);
		glEnd();

		glPopMatrix();
		glDisable(GL_BLEND);
	}

	public static void drawString(String s, int x, int y) {
		applyColour();
		glEnable(GL_BLEND);
		glPushMatrix();
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		MONOSPACED_FONT.drawString(x, y, s);
		glPopMatrix();
		glDisable(GL_BLEND);
	}

	public static void startClip(int x, int y, int w, int h) {
		glEnable(GL_SCISSOR_BOX);
		glScissor(x, y, w, h);
	}

	public static void endClip() {
		glDisable(GL_SCISSOR_BOX);
	}

}
