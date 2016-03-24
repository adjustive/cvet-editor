package io.cvet.editor.gfx;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_POLYGON_MODE;
import static org.lwjgl.opengl.GL11.GL_SCISSOR_BOX;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScissor;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;
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
	private static Colour colour = Colour.WHITE;
	
	public static TrueTypeFont EDITING_FONT;
	public static TrueTypeFont INTERFACE_FONT;
	public static TrueTypeFont CURRENT_FONT;
	
	public static int CHARACTER_WIDTH, CHARACTER_HEIGHT;
	private static boolean ANTI_ALIAS;
	private static int FONT_SIZE;
	private static String FONT_FACE;
	
	static {
		loadFont();
	}
	
	public static void loadFont() {
		ANTI_ALIAS = (boolean) Settings.getSetting("anti_alias");
		FONT_SIZE = (int) Settings.getSetting("font_size");
		FONT_FACE = (String) Settings.getSetting("font_face");
		EDITING_FONT = new TrueTypeFont(new Font(FONT_FACE, Font.PLAIN, FONT_SIZE).deriveFont(FONT_SIZE), ANTI_ALIAS);
		INTERFACE_FONT = new TrueTypeFont(new Font("Arial", Font.PLAIN, 14), ANTI_ALIAS);
		CHARACTER_WIDTH = EDITING_FONT.getWidth("a");
		CHARACTER_HEIGHT = EDITING_FONT.getHeight();
		CURRENT_FONT = EDITING_FONT;
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
		Render.colour = c;
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
		CURRENT_FONT.drawString(x, y, s, colour.getStupidFuckingColour());
		glPopMatrix();
		glDisable(GL_BLEND);
	}
	
	public static void drawStringln(String s, int x, int y) {
		String[] lines = s.split("\n");
		int idx = 0;
		for (String line : lines) {
			drawString(line, x, y + (idx * CURRENT_FONT.getHeight()));
			idx++;
		}
	}

	public static void startClip(int x, int y, int w, int h) {
		glEnable(GL_SCISSOR_BOX);
		glScissor(x, y, w, h);
	}

	public static void endClip() {
		glDisable(GL_SCISSOR_BOX);
	}

	public static void font(TrueTypeFont face) {
		CURRENT_FONT = face;
	}

}
