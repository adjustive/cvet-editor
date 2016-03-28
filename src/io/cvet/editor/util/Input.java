package io.cvet.editor.util;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import io.cvet.editor.gfx.Colour;
import io.cvet.editor.gfx.RenderContext;
import io.cvet.editor.gui.Component;

public class Input {

	public static int x, y;
	public static int w = 1, h = 1;
	public static final int MOUSE_MIDDLE = 2;
	public static final int MOUSE_LEFT = 1;
	public static final int MOUSE_RIGHT = 0;
	
	private static boolean[] lastMouse = new boolean[3];
	private static boolean[] lastKeys = new boolean[256];
	
	public static void update() {
		x = Mouse.getX();
		y = Display.getHeight() - Mouse.getY();
		
		for (int i = 0; i < lastKeys.length; i++) {
			lastKeys[i] = getKey(i);
		}
		
		for (int i = 0; i < lastMouse.length; i++) {
			lastMouse[i] = getMouse(i);
		}
	}
	
	public static boolean controlCombo(int keyCode) {
		return isControlModifierDown() && Input.getKey(keyCode);
	}
	
	public static boolean getMouse(int mouseCode) {
		return Mouse.isButtonDown(mouseCode);
	}
	
	public static boolean getKey(int keyCode) {
		return Keyboard.isKeyDown(keyCode);
	}
	
	public static boolean getMouseClicked(int mouseCode) {
		return getMouse(mouseCode) && !lastMouse[mouseCode];
	}
	
	public static boolean getKeyPressed(int keyCode) {
		return getKey(keyCode) && !lastKeys[keyCode];
	}
	
	public static void render(	) {
		RenderContext.colour(Mouse.isButtonDown(0) ? Colour.RED : Colour.YELLOW);
		RenderContext.rect(x, 0, 1, Display.getHeight());
		RenderContext.rect(0, y, Display.getWidth(), 1);
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
	
	public static boolean isAltModifierDown() {
		return getKey(Keyboard.KEY_LMETA) || getKey(Keyboard.KEY_RMETA);
	}
	
	public static boolean isControlModifier(int key) {
		return key == Keyboard.KEY_LCONTROL || key == Keyboard.KEY_RCONTROL;
	}
	
}
