package io.cvet.editor;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;

import io.cvet.editor.gui.Button;
import io.cvet.editor.gui.Component;
import io.cvet.editor.util.Input;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Editor extends Component implements Runnable {
	
	private Thread thread;
	
	public void init() {
		// setup the display
		try {
			Display.setDisplayMode(new DisplayMode(1280, 720));
			Display.create();
		} catch (Exception e) {
			System.err.println("shit");
		}
		
		this.w = Display.getWidth();
		this.h = Display.getHeight();
		
		// setup OpenGL
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glMatrixMode(GL_MODELVIEW);
		glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		
		addChild(new Button(Display.getWidth(), Display.getHeight()), Layout.Halves);
		addChild(new Button(Display.getWidth(), Display.getHeight()), Layout.Halves);
	}
	
	public void update() {
		Input.update();
		
		checkFocus();
		for (Component c : children) {
			c.checkFocus();
			c.update();
		}
		
		Display.update();
	}
	
	public void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		for (Component c : children) {
			c.render();
		}
		
		Input.render();
	}
	
	public void run() {
		init();
		
		while (!Display.isCloseRequested()) {
			update();
			render();
		}
		
		Display.destroy();
		stop();
	}
	
	public void start() {
		thread = new Thread(this);
		thread.start();
	}
	
	public void stop() {
		thread.interrupt();
	}
	
	public static void main(String[] args) {
		new Editor().start();
	}
	
}
