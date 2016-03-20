package io.cvet.editor.util;

import io.cvet.editor.gfx.Render;
import io.cvet.editor.gui.Component;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class Input {

	public static int x, y;
	public static int w = 16, h = 16;

	public static void update() {
		x = Mouse.getX();
		y = Display.getHeight() - Mouse.getY();
	}
	
	public static void render() {
		Render.colour(255, 255, 0);
		Render.rect(x, y, w, h);
	}

	public static boolean intersects(Component c) {
		return x < c.x + c.w 
				&& x + w > c.x 
				&& y < c.y + c.h 
				&& y + h > c.y;
	}
	
}
