package io.cvet.editor;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import java.awt.GraphicsEnvironment;
import java.nio.ByteBuffer;
import java.util.ArrayList;

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
import io.cvet.editor.gui.View;
import io.cvet.editor.gui.commands.CommandPalette;
import io.cvet.editor.gui.layers.Layer;
import io.cvet.editor.gui.tab.Tab;
import io.cvet.editor.util.FileUtil;
import io.cvet.editor.util.Input;
import io.cvet.editor.util.Theme;

public class Editor extends Component implements Runnable {

	public static boolean DEBUG_MODE = false;
	private static Editor instance;
	public CommandPalette palette;
	private String OS = System.getProperty("os.name");
	public static String commands = "";
	
	public View mainView;

	private Thread thread;
	private int frameRate = 0;
	
	public Editor() {
		instance = this;
	}

	public void init() {
		java.awt.DisplayMode mode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
		this.w = mode.getWidth() / 12 * 9;
		this.h = mode.getHeight() / 12 * 9;

		// setup the display
		try {
			Display.setDisplayMode(new DisplayMode(w, h));
			Display.setTitle("nate");
			int offset = OS.startsWith("Windows") ? 40 : 0;
			Display.setLocation((mode.getWidth() / 2) - (w / 2), (mode.getHeight() / 2) - (h / 2) - offset);
			Display.setResizable(true);
			
//			Display.setIcon(new ByteBuffer[] {
//				FileUtil.loadIcon("/icon16.png"),					
//				FileUtil.loadIcon("/icon32.png"),					
//				FileUtil.loadIcon("/icon64.png"),					
//				FileUtil.loadIcon("/icon128.png"),					
//				FileUtil.loadIcon("/icon256.png"),					
//				FileUtil.loadIcon("/icon512.png"),					
//			});

			Display.create();
		} catch (Exception e) {
			System.err.println("Well this is awkward.");
			return;
		}

		RenderBackend.loadFont();
		RenderContext.init(w, h);

		palette = new CommandPalette();
		palette.setVisible(false);
		palette.setKeyboardTrigger(Modifier.Super, Keyboard.KEY_P);
		palette.setLayer(Layer.TOP);
		addChild(palette);
		
		mainView = new View();
		addChild(mainView);
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

	public void exit() {
		Display.destroy();
		stop();
	}

	public void resize() {

	}

	public static void loadEverything() {
		RenderBackend.loadFont();
		Settings.loadSettings();
		
		ArrayList<Tab> tabs = Editor.getMainView().getTabList();
		for (Tab t : tabs) {
			Buffer buff = t.buff;
			buff.loadSettings();
			buff.getCaret().loadSettings();
		}
	}
	
	public static View getMainView() {
		return instance.mainView;
	}
	
	public static Editor getInstance() {
		return instance;
	}
	
}
