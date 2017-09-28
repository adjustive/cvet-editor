package io.cvet.editor.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.lwjgl.input.Keyboard;

import io.cvet.editor.Editor;
import io.cvet.editor.config.Settings;
import io.cvet.editor.gui.commands.CommandPalette;
import io.cvet.editor.gui.text.Line;
import io.cvet.editor.gui.text.TextArea;

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
		
	}

	public void render() {
		renderChildren(children);
		super.render();
	}

	public void save() {
		// we haven't before, so we need
		// to set where to save.
		if (!hasBeenSaved()) {
			CommandPalette palette = Editor.getInstance().palette;
			palette.setText(System.getProperty("user.home") + File.separatorChar);
			palette.show();
			return;
		}

		if (file == null) {
			System.err.println("this shouldn't happen\n");
			return;
		}

		// write contents of buffer
		// into the file
		// FIXME:
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
