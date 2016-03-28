package io.cvet.editor.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JFileChooser;

import org.lwjgl.input.Keyboard;

import io.cvet.editor.Editor;
import io.cvet.editor.config.Settings;
import io.cvet.editor.gui.layers.Layer;
import io.cvet.editor.gui.menu.Menu;
import io.cvet.editor.gui.menu.MenuAction;
import io.cvet.editor.gui.menu.MenuItem;
import io.cvet.editor.gui.text.Line;
import io.cvet.editor.gui.text.TextArea;
import io.cvet.editor.util.Input;

public class Buffer extends TextArea implements CursorAction {

	private String name;
	private File file;
	private boolean saved;
	private long timer;

	private boolean autoSave;
	private int saveRateMS;

	public Buffer(String name) {
		loadSettings();
		this.name = name;
		this.timer = System.currentTimeMillis();
		this.getCaret().setCursorAction(this);

		context = new Menu();
		context.setLayer(Layer.TOP);
		context.setVisible(false);
		context.addItem(new MenuItem(context, "Paste", new MenuAction() {
			@Override
			public void perform() {
				System.out.println("paste pls");
			}
		}));
		context.setMouseTrigger(Input.MOUSE_RIGHT);
		addChild(context);
	}

	public Buffer(String name, String contents) {
		this(name);
		buffer.clear();
		for (String s : contents.split("\n")) {
			buffer.add(new Line(s));
		}
	}

	public Buffer(File file) {
		this(file.getName());
		this.saved = true;
		this.file = file;
		buffer.clear();
		this.loadFile(file);
	}
	
	public void loadSettings() {
		super.loadSettings();
		autoSave = (boolean) Settings.getSetting("auto_save");
		saveRateMS = (int) Settings.getSetting("save_rate");
	}

	public void update() {
		super.update();
		
		if (!saved && autoSave && System.currentTimeMillis() - timer > saveRateMS) {
			save();
			timer += saveRateMS;
		}
		
		context.loseFocus();
	}

	public void render() {
		renderChildren(children);
		super.render();
	}

	public void save() {
		// we haven't before, so we need
		// to set where to save.
		if (!hasBeenSaved()) {
			// TODO: store the last directory we saved
			// in, and set the default location to that
			JFileChooser chooser = new JFileChooser(System.getenv("HOME"));
			chooser.setDialogTitle("Save File \"" + name + "\"");
			chooser.setSelectedFile(new File(name));
			if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
				file = chooser.getSelectedFile();
			} else {
				System.err.println("choose a file next time fuckboy");
				return;
			}
		}

		if (file == null) {
			System.err.println("this shouldn't happen\n");
			return;
		}

		// write contents of buffer
		// into the file
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (Line builtString : buffer) {
				String actualString = builtString.toString() + '\n';
				bw.write(actualString);
			}
			bw.close();
			saved = true;
		} catch (Exception e) {
			System.err.println("failed 2 write :(");
		}
		
		if (file == Settings.getUserConfigFile()) {
			Editor.loadEverything();
		}

		saved = true;
	}

	public boolean hasBeenSaved() {
		return file != null;
	}

	public boolean isSaved() {
		return saved && file != null;
	}

	public String getName() {
		return name;
	}

	public void rename(String newName) {
		this.name = newName;
		File oldFile = file;
		
		// TODO: oldFile is null we can't
		// rename a non-created file
		
		String newFilePath = oldFile.getAbsolutePath().replace(oldFile.getName(), "") + newName;
		File newFile = new File(newFilePath);
		try {
			file.renameTo(newFile);
			this.file = newFile;
			oldFile.delete();
		} catch (Exception e) {
			// :(
			System.err.println("Couldn't move file!");
			this.file = oldFile;
			this.name = file.getName();
		}
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public boolean keyPress(int keyCode) {
		// FIXME
		switch (keyCode) {
		case Keyboard.KEY_DELETE:
		case Keyboard.KEY_BACK:
		default:
			if (Character.isAlphabetic(Keyboard.getEventCharacter())
					|| Character.isDigit(Keyboard.getEventCharacter())) {
				saved = false;
			}
			break;
		}
		return false;
	}

	public File getFile() {
		return file;
	}

}
