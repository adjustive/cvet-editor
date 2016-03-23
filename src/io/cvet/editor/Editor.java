package io.cvet.editor;

import static org.lwjgl.opengl.GL11.*;

import io.cvet.editor.config.Settings;
import io.cvet.editor.gfx.Render;
import io.cvet.editor.gui.Buffer;
import io.cvet.editor.gui.Component;
import io.cvet.editor.gui.commands.CommandPalette;
import io.cvet.editor.util.FileUtil;
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
	
	private static final String WELCOME_STRING;
	
	static {
		WELCOME_STRING = FileUtil.LoadFromUrl("http://loripsum.net/api/10/long/plaintext");
	}

	private static Editor instance;
	private Thread thread;
	private CommandPalette palette;
	private int frames = 0;
	private final String intro = "Press `ESC` to get started. If you are stuck, type `help`.";
	
	private Stack<Buffer> buffers;
	
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

		// pick a random child to focus lol
		if (children.size() != 0) {
			children.get(RNG.cap(children.size())).setFocus(true);
		}
		
		buffers = new Stack<Buffer>();
		
		palette = new CommandPalette();
		palette.setVisible(false);
	}
	
	public void update() {
		if (Input.getKeyPressed(Keyboard.KEY_ESCAPE)) {
			showCommandPalette("");
		}
		
		if (palette.isVisible() && palette.getFocus()) {
			palette.update();
		} else {
			updateChildren(children);
		}
		
		// this is here to eat up input if we have nothing focused.
		while (Keyboard.next()) {}
		
		Input.update();
		Display.update();
	}
	
	public void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		Render.colour(Theme.BASE);
		Render.rect(0, 0, Display.getWidth(), Display.getHeight());
		
		renderChildren(children);

		if (children.size() == 0) {
			int x = Render.CURRENT_FONT.getWidth(intro);
			Render.drawString(intro, (Display.getWidth() / 2) - (x / 2), (Display.getHeight() / 2) - (Render.EDITING_FONT.getHeight() / 2));
		}
		
		if (palette.isVisible()) {
			palette.render();
		}
		
		Input.render();

		Render.colour(255, 0, 0);
		Render.drawString("fps: " + frames, Display.getWidth() - 100, Display.getHeight() - 40);
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
				render();
				update();
				frames++;
				delta--;
			}
			
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				this.frames = frames;
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

	public Buffer getCurrentBuffer() {
		return buffers.peek();
	}

	public void showCommandPalette(String input) {
		palette.setVisible(true);
		palette.setFocus(true);
		palette.setText(input);
	}
	
}
