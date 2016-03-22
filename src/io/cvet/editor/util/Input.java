package io.cvet.editor.util;

import io.cvet.editor.gfx.Colour;
import io.cvet.editor.gfx.Render;
import io.cvet.editor.gui.Component;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class Input {

	public static int x, y;
	public static int w = 16, h = 16;

	private static boolean[] lastKeys = new boolean[256];
	
	public static void update() {
		x = Mouse.getX();
		y = Display.getHeight() - Mouse.getY();
		
		for (int i = 0; i < lastKeys.length; i++) {
			lastKeys[i] = getKey(i);
		}
	}
	
	public static boolean getKey(int keyCode) {
		return Keyboard.isKeyDown(keyCode);
	}
	
	public static boolean getKeyPressed(int keyCode) {
		return getKey(keyCode) && !lastKeys[keyCode];
	}
	
	public static void render() {
		Render.colour(Colour.YELLOW);
		Render.rect(x, y, w, h);
	}

	public static boolean intersects(Component c) {
		return x < c.x + c.w 
				&& x + w > c.x 
				&& y < c.y + c.h 
				&& y + h > c.y;
	}
	
}
