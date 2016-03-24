package io.cvet.editor;

import static org.lwjgl.opengl.GL11.*;

import io.cvet.editor.config.Settings;
import io.cvet.editor.gfx.Colour;
import io.cvet.editor.gfx.Render;
import io.cvet.editor.gui.Buffer;
import io.cvet.editor.gui.Component;
import io.cvet.editor.gui.commands.CommandPalette;
import io.cvet.editor.util.Input;
import io.cvet.editor.util.RNG;
import io.cvet.editor.util.Theme;

import java.awt.GraphicsEnvironment;
import java.util.Stack;

import javax.swing.UIManager;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Editor extends Component implements Runnable {
	
	public static boolean DEBUG_MODE = false;
	private static Editor instance;
	private final String MOTD = "Press `CLTR+P` to open the command palette.\n" +
			"If you are stuck, type `help` for a buffer of commands.\n" +
			"P.S. You can change this message in the configuration file.\n" +
			"Well, not yet... but you will be able to soon!\n";
	private String OS = System.getProperty("os.name");

	private Stack<Buffer> buffers;
	private Thread thread;
	private CommandPalette palette;
	private int frameRate = 0;
	
	public Editor() {
		instance = this;
	}
	
	public void init() {
		java.awt.DisplayMode mode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
		final int width = mode.getWidth() / 12 * 9;
		final int height = mode.getHeight() / 12 * 9;
		
		// setup the display
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setTitle("nate");
			
			// TODO: probably a nicer way to do this
			int offset = OS.startsWith("Windows") ? 40 : 0;
			Display.setLocation((mode.getWidth() / 2) - (width / 2), (mode.getHeight() / 2) - (height / 2) - offset);
			
			Display.create();
		} catch (Exception e) {
			System.err.println("Well this is awkward.");
			return;
		}
		
		this.w = Display.getWidth();
		this.h = Display.getHeight();
		
		// setup OpenGL
		glEnable(GL_TEXTURE_2D);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glMatrixMode(GL_MODELVIEW);
		glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		resize();
		
		// pick a random child to focus lol
		if (children.size() != 0) {
			children.get(RNG.cap(children.size())).setFocus(true);
		}
		
		buffers = new Stack<Buffer>();
		
		palette = new CommandPalette();
		palette.setVisible(false);
	}
	
	public void update() {
		if (Display.wasResized()) {
			resize();
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Input.getKeyPressed(Keyboard.KEY_P)) {
			showCommandPalette("");
		}
		
		if (Input.getKeyPressed(Keyboard.KEY_F1)) {
			DEBUG_MODE = !DEBUG_MODE;
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
		
		Render.colour(Theme.BASE);
		Render.rect(0, 0, Display.getWidth(), Display.getHeight());
		
		renderChildren(children);
		
		// everything after we render in a nice
		// sans-serif font... for now this mostly
		// means the welcome motd and the fps
		Render.font(Render.INTERFACE_FONT);
		if (children.size() == 0) {
			String[] splitMOTD = MOTD.split("\n");
			int blockHeight = splitMOTD.length * Render.EDITING_FONT.getHeight();
			int blockOffset = (Display.getHeight() / 2) - (blockHeight / 2);
			int idx = 0;
			for (String line : splitMOTD) {
				int lineWidth = Render.CURRENT_FONT.getWidth(line);
				Render.colour(Colour.WHITE);
				Render.drawString(line, (Display.getWidth() / 2) - (lineWidth / 2), blockOffset + (idx * Render.CURRENT_FONT.getHeight()));
				idx++;
			}
		}
		
		if (palette.isVisible()) {
			palette.render();
		}
		
		if (DEBUG_MODE) {
			Input.render();
		}

		if (DEBUG_MODE) {
			Render.colour(Colour.YELLOW);
			String framerate = "fps: " + frameRate;
			int padding = 10;
			Render.drawString(framerate, Display.getWidth() - Render.INTERFACE_FONT.getWidth(framerate) - padding, Display.getHeight() - Render.INTERFACE_FONT.getHeight() - padding);
		}
	}
	
	public void run() {
		init();
		
		long timer = System.currentTimeMillis();
		int frames = 0;
		double ns = 1000000000.0 / 60.0;
		double delta = 0;
		long last = System.nanoTime();
		
		while (!Display.isCloseRequested()) {
			long now = System.nanoTime();
			delta += (now - last) / ns;
			last = now;
			
			if (delta >= 1) {
				update();
				render();
				frames++;
				delta--;
			}
			
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				this.frameRate = frames;
				frames = 0;
			}
			
			try {
				Thread.sleep(2);
			} catch (Exception e) {}
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
		System.exit(0);
	}
	
	// TODO: hashmap for this for them O(1)s...
	public void closeCurrentBuffer() {
		// nothing to do
		if (buffers.empty()) {
			return;
		}
		
		Buffer buff = buffers.pop();
		children.remove(buff);
		
		// no areas left to focus
		if (buffers.isEmpty()) {
			return;
		}
		
		// give the last textarea focus
		// if it exists
		if (!buffers.empty()) {
			buffers.peek().setFocus(true);
		}
	}
	
	public void setCurrentBuffer(Buffer buff) {
		clearFocus();
		addChild(buff);
		buffers.push(buff);
		buff.setFocus(true);
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) { }
		Keyboard.enableRepeatEvents(true);

		Settings.setupEditor();
		Settings.loadSettings();
		new Editor().start();
	}
	
	public static Editor getInstance() {
		return instance;
	}

	public void exit() {
		Display.destroy();
		stop();	
	}

	public void resize() {
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}
	
	public Buffer getCurrentBuffer() {
		return buffers.peek();
	}

	public void showCommandPalette(String input) {
		palette.setVisible(true);
		palette.setFocus(true);
		
		// add a cheeky space in there
		palette.setText(input);
	}
	
}
