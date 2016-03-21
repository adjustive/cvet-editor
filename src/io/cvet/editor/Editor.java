package io.cvet.editor;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import io.cvet.editor.gfx.Render;
import io.cvet.editor.gui.Component;
import io.cvet.editor.gui.Label;
import io.cvet.editor.gui.Panel;
import io.cvet.editor.gui.TextArea;
import io.cvet.editor.util.Input;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Editor extends Component implements Runnable {
	
	private Thread thread;

	public static Panel DEBUG_INTERFACE;
	private Label fps;
	
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

		addChild(new TextArea(Display.getWidth(), Display.getHeight()), Layout.Halves);
		addChild(new TextArea(Display.getWidth(), Display.getHeight()), Layout.Halves);
		
		// this is the debug user interface...
		// eventually toggle this from keypress or a setting
		DEBUG_INTERFACE = new Panel(Display.getWidth() - 150, 15);
		addChild(DEBUG_INTERFACE, Layout.Free);
		DEBUG_INTERFACE.setFocusable(false);

		fps = new Label("fps: ????????");
		DEBUG_INTERFACE.addChild(fps, Layout.Child);
	}
	
	public void update() {
		Input.update();
		
		checkFocus();
		for (Component c : children) {
			c.checkFocus();

			// only update if
			// the component is focused on
			if (!c.getFocusable() || c.getFocus()) {
				c.update();
			}
		}
			
		Display.update();
	}
	
	public void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		Render.colour(255, 255, 255, 255);
		Render.rect(0, 0, Display.getWidth(), Display.getHeight());
		
		for (Component c : children) {
			Render.startClip(c.x, c.y, c.w, c.h);
			c.render();
			Render.endClip();
			
			if (c.getFocusable() && c.getFocus()) {
				Render.colour(125, 255, 50);
				Render.rect(c.x, c.y + c.h - 2, c.w, 2);
			}
		}
		
		Input.render();
	}
	
	public void run() {
		init();
		
		long timer = System.currentTimeMillis();
		int frames = 0;
		
		while (!Display.isCloseRequested()) {
			render();
			update();
			frames++;
			
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				fps.setValue("fps: " + frames);
				frames = 0;
			}
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
