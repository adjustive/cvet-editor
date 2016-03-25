package io.cvet.editor.gfx;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.TrueTypeFont;

import io.cvet.editor.gfx.RenderBackend.GeometricType;

public class RenderContext {

	private static RenderBackend backend;
	
	public void setRenderBackend(RenderBackend backend) {
		RenderContext.backend = backend;
	}
	
	public static void rect(int x, int y, int w, int h) {
		// colour bind
		backend.type(GeometricType.Quad);
		backend.vertex(x, y);
		backend.vertex(x + w, y);
		backend.vertex(x + w, y + h);
		backend.vertex(x, y + h);
		backend.reset();
	}

	public static void startClip(int x, int y, int w, int h) {
		
	}
	
	public static void endClip() {
		
	}

	public static void colour(int r, int g, int b) {
		backend.colour(r, g, b);
	}

	public static void colour(Colour colour) {
		backend.colour(colour.r, colour.g, colour.b);
	}

	public static void font(TrueTypeFont font) {
		// TODO
	}

	public static void drawString(String suggName, int i, int j) {
		// TODO
	}

	public static void init(int w, int h) {
		backend = new BatchRenderer();
		backend.init(w, h);
	}

	public static void flush() {
		backend.flush();
	}

	public static void loadIdentity() {
		GL11.glLoadIdentity();
	}
	
}
