package io.cvet.editor;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.Set;

import javax.swing.UIManager;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import io.cvet.editor.config.Settings;
import io.cvet.editor.gfx.Colour;
import io.cvet.editor.gfx.RenderBackend;
import io.cvet.editor.gfx.RenderContext;
import io.cvet.editor.gui.Buffer;
import io.cvet.editor.gui.Component;
import io.cvet.editor.gui.commands.CommandPalette;
import io.cvet.editor.gui.layers.Layer;
import io.cvet.editor.util.Input;
import io.cvet.editor.util.RNG;
import io.cvet.editor.util.Theme;

public class Editor extends Component implements Runnable {

	public static boolean DEBUG_MODE = false;
	private static Editor instance;
	private String OS = System.getProperty("os.name");
	public static String commands = "";
	
	private HashMap<String, Buffer> buffers;
	private String currentBufferName;

	private Thread thread;
	private CommandPalette palette;
	private int frameRate = 0;

	public Editor() {
		instance = this;
	}

	public void init() {
		java.awt.DisplayMode mode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDisplayMode();
		final int width = mode.getWidth() / 12 * 9;
		final int height = mode.getHeight() / 12 * 9;

		// setup the display
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setTitle("nate");

			int offset = OS.startsWith("Windows") ? 40 : 0;
			Display.setLocation((mode.getWidth() / 2) - (width / 2), (mode.getHeight() / 2) - (height / 2) - offset);

			Display.create();
		} catch (Exception e) {
			System.err.println("Well this is awkward.");
			return;
		}

		this.w = Display.getWidth();
		this.h = Display.getHeight();

		RenderContext.init(w, h);

		// pick a random child to focus lol
		if (children.size() != 0) {
			children.get(RNG.cap(children.size())).setFocus(true);
		}

		buffers = new HashMap<String, Buffer>();

		palette = new CommandPalette();
		palette.setVisible(false);
		palette.setKeyboardTrigger(Modifier.Super, Keyboard.KEY_P);
		palette.setLayer(Layer.TOP);
		addChild(palette);
	}

	public void update() {
		if (Display.wasResized()) {
			resize();
		}

		updateChildren(children);

		Input.update();
		Display.update();
	}

	public void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		RenderContext.colour(Theme.BASE);
		RenderContext.rect(0, 0, Display.getWidth(), Display.getHeight());

		renderChildren(children);

		RenderContext.colour(Colour.YELLOW);
		String framerate = "fps: " + frameRate;
		int padding = 60;
		RenderContext.drawString(framerate,
				Display.getWidth() - RenderBackend.INTERFACE_FONT.getWidth(framerate) - padding,
				Display.getHeight() - RenderBackend.INTERFACE_FONT.getHeight() - padding);
		
		RenderContext.drawString("[" + commands + "]", 40, Display.getHeight() - padding);
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

			render();
			frames++;

			if (delta >= 1) {
				update();
				delta--;
			}

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				this.frameRate = frames;
				frames = 0;
			}

			try {
				Thread.sleep(2);
			} catch (Exception e) {
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
		System.exit(0);
	}

	public void closeCurrentBuffer() {
		// nothing to do
		if (buffers.size() == 0) {
			return;
		}

		children.remove(getCurrentBuffer());

		// no areas left to focus
		if (buffers.isEmpty()) {
			return;
		}

		// give the last textarea focus
		// if it exists
		if (buffers.size() != 0) {
			buffers.get(currentBufferName).setFocus(true);
		}
	}

	public boolean bufferWithNameExists(String name) {
		return buffers.containsKey(name);
	}

	public void loadBuffer(String name) {
		if (bufferWithNameExists(name)) {
			Buffer buff = buffers.get(name);
			addChild(buff);
			currentBufferName = name;
		} else {
			System.err.println("shit!! we dont handle buffer collisions yet!");
			return;
		}
	}

	// New buffer that is given focus
	public void pushBuffer(Buffer buff) {
		clearFocus();
		// TODO: dont add it multiple times
		// remove other buffers that aren't in view?
		addChild(buff);

		String name = buff.getName();
		if (!bufferWithNameExists(name)) {
			buffers.put(buff.getName(), buff);
			currentBufferName = name;
		} else {
			System.err.println("shit!! we dont handle buffer collisions yet!");
			return;
		}

		buff.setFocus(true);
	}

	public Buffer getCurrentBuffer() {
		return buffers.get(currentBufferName);
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
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

	public Set<String> getBufferNames() {
		return buffers.keySet();
	}

	public HashMap<String, Buffer> getBuffers() {
		return buffers;
	}

}
