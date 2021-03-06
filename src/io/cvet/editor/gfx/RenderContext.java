package io.cvet.editor.gfx;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.TextureImpl;

import io.cvet.editor.gfx.RenderBackend.GeometricType;

public class RenderContext {

	public static RenderBackend backend;
	
	public void setRenderBackend(RenderBackend backend) {
		RenderContext.backend = backend;
	}
	
	public static void rect(int x, int y, int w, int h) {
		backend.type(GeometricType.Triangle);
		
		backend.vertex(x, y);
		backend.vertex(x + w, y);
		backend.vertex(x + w, y + h);

		backend.vertex(x, y);
		backend.vertex(x + w, y + h);
		backend.vertex(x, y + h);
		
		backend.reset();
	}

	public static void startClip(int x, int y, int w, int h) {
		
	}
	
	public static void endClip() {
		
	}

	public static void colour(int r, int g, int b) {
		if (backend.currentColour == null) {
			backend.currentColour = new Colour(r, g, b);
			System.out.println("set");
		}
		backend.colour(r, g, b);
	}

	public static void colour(Colour colour) {
		backend.currentColour = colour;
		backend.colour(colour.r, colour.g, colour.b);
	}

	public static void font(TrueTypeFont font) {
		RenderBackend.CURRENT_FONT = font;
	}

	public static void drawString(String suggName, int x, int y) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		RenderBackend.CURRENT_FONT.drawString(x, y, suggName, backend.currentColour.getStupidFuckingColour());
		GL11.glDisable(GL11.GL_BLEND);
		TextureImpl.bindNone();
	}

	public static void init(int w, int h) {
		backend = new ImmediateRenderer();
		backend.init(w, h);
	}
	
}
