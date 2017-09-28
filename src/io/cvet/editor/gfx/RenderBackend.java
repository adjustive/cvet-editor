package io.cvet.editor.gfx;

import java.awt.Font;

import org.lwjgl.opengl.GL11;

import org.newdawn.slick.TrueTypeFont;

import io.cvet.editor.config.Settings;

public abstract class RenderBackend {
	
	protected float r, g, b, a;
	protected Colour currentColour;
	protected GeometricType type;

	public static TrueTypeFont EDITING_FONT;
	public static TrueTypeFont INTERFACE_FONT;
	public static TrueTypeFont CURRENT_FONT;
	public static int CHARACTER_WIDTH, CHARACTER_HEIGHT;
	public static boolean ANTI_ALIAS;
	public static int CODE_FONT_SIZE, UI_FONT_SIZE;
	public static String CODE_FONT_FACE, UI_FONT_FACE;
	
	public static void loadFont() {
		ANTI_ALIAS = (boolean) Settings.getSetting("anti_alias");

		UI_FONT_SIZE = (int) Settings.getSetting("ui_font_size");
		UI_FONT_FACE = (String) Settings.getSetting("ui_font_face");
		
		CODE_FONT_SIZE = (int) Settings.getSetting("font_size");
		CODE_FONT_FACE = (String) Settings.getSetting("font_face");

		EDITING_FONT = new TrueTypeFont(new Font(CODE_FONT_FACE, Font.PLAIN, CODE_FONT_SIZE).deriveFont(CODE_FONT_SIZE), ANTI_ALIAS);
		INTERFACE_FONT = new TrueTypeFont(new Font(UI_FONT_FACE, Font.PLAIN, UI_FONT_SIZE).deriveFont(UI_FONT_SIZE), ANTI_ALIAS);
		
		// this is ONE OF THE reasons why non monospaced fonts
		// dont work!
		CHARACTER_WIDTH = EDITING_FONT.getWidth("a");
		CHARACTER_HEIGHT = EDITING_FONT.getHeight();
		CURRENT_FONT = EDITING_FONT;
	}
	
	public abstract void init(final int width, final int height);
	
	public abstract void vertex(float x, float y);
	public abstract void tex(float x, float y);

	public abstract void colour(float r, float g, float b, float a);
	public abstract void colour(float r, float g, float b);
	
	public void type(GeometricType type) {
		this.type = type;
	}
	
	public void reset() {
		this.type = GeometricType.NoType;
	}
	
	public static enum GeometricType {
		Quad(GL11.GL_QUADS),
		Triangle(GL11.GL_TRIANGLES),
		Line(GL11.GL_LINE),
		NoType(-69);
		
		private int glType;
		GeometricType(int glType) {
			this.glType = glType;
		}
		
		public int getRawType() {
			return glType;
		}
	};

}
