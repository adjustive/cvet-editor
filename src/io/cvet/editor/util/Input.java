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
		Render.colour(Mouse.isButtonDown(0) ? Colour.RED : Colour.YELLOW);
		Render.rect(x, 0, 1, Display.getHeight());
		Render.rect(0, y, Display.getWidth(), 1);
	}

	public static boolean intersects(Component c) {
		return x < c.x + c.w 
				&& x + w > c.x 
				&& y < c.y + c.h 
				&& y + h > c.y;
	}
	
	public static boolean isControlModifierDown() {
		return getKey(Keyboard.KEY_LCONTROL) || getKey(Keyboard.KEY_RCONTROL);
	}
	
	public static boolean isShiftModifierDown() {
		return getKey(Keyboard.KEY_LSHIFT) || getKey(Keyboard.KEY_RSHIFT);
	}
	
	public static boolean isControlModifier(int key) {
		return key == Keyboard.KEY_LCONTROL || key == Keyboard.KEY_RCONTROL;
	}
	
}
