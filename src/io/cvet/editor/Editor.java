package io.cvet.editor;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import io.cvet.editor.gfx.Colour;
import io.cvet.editor.gfx.Render;
import io.cvet.editor.gui.Component;
import io.cvet.editor.gui.TextArea;
import io.cvet.editor.gui.commands.CommandPalette;
import io.cvet.editor.util.Input;
import io.cvet.editor.util.RNG;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Editor extends Component implements Runnable {
	
	private static Editor instance;
	private Thread thread;
	private CommandPalette palette;
	private int frames = 0;
	private TextArea currentArea;
	
	public Editor() {
		instance = this;
	}
	
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

		// pick a random child to focus lol
		if (children.size() != 0) {
			children.get(RNG.cap(children.size())).setFocus(true);
		}
		
		palette = new CommandPalette();
		palette.setVisible(false);
	}
	
	public void update() {
		if (Input.getKeyPressed(Keyboard.KEY_ESCAPE)) {
			palette.setVisible(true);
			palette.setFocus(true);
		} else if (Input.getKeyPressed(Keyboard.KEY_F2)) {
			JFileChooser chooser = new JFileChooser();
			chooser.setVisible(true);
			if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				TextArea buff = new TextArea(Display.getWidth(), Display.getHeight());
				buff.loadFile(file);
				addChild(buff);
			}
		}
		
		
		if (palette.isVisible() && palette.getFocus()) {
			palette.update();
		} else {
			updateChildren(children);
		}
		
		Input.update();
		Display.update();
	}
	
	public void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		Render.colour(Colour.BLACK);
		Render.rect(0, 0, Display.getWidth(), Display.getHeight());
		
		renderChildren(children);

		if (palette.isVisible()) {
			palette.render();
		}
		
		Input.render();

		Render.colour(255, 0, 0);
		Render.drawString("fps: " + frames, Display.getWidth() - 100, 20);
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
				this.frames = frames;
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
	
	// TODO: hashmap for this for them O(1)s...
	public void closeCurrentBuffer() {
		children.remove(currentArea);
	}
	
	public void setCurrentTextArea(TextArea area) {
		addChild(area);
		this.currentArea = area;
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) { }
		new Editor().start();
	}
	
	public static Editor getInstance() {
		return instance;
	}
	
}
