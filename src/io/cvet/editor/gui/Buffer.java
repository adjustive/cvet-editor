package io.cvet.editor.gui;

import io.cvet.editor.gfx.Render;
import io.cvet.editor.gui.text.Line;
import io.cvet.editor.gui.text.TextArea;
import io.cvet.editor.util.Theme;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JFileChooser;

import org.lwjgl.opengl.Display;

public class Buffer extends TextArea {

	private String name;
	private File file;
	private boolean saved;
	private int lineNum = 1;
	private Label title;
	private int padding = 10;
	
	public Buffer(String name) {
		this.name = name;
		
		String bufferInformation = "#" + lineNum + " " + name;
		this.title = new Label(bufferInformation, Render.EDITING_FONT);
		title.setPosition(Display.getWidth() - title.w - padding, padding, title.w, title.h);
		title.setBackground(Theme.ACCENT);
		addChild(title);
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
	
	public void update() {
		super.update();
		title.setValue((saved ? name : "*" + name) + " #" + (getCaret().iy + 1));
	}
	
	public void render() {
		renderChildren(children);
		super.render();
	}

	public void save() {
		// we haven't before, so we need
		// to set where to save.
		if (!isSaved()) {
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
		} 
		catch (Exception e) {
			System.err.println("failed 2 write :(");
		}
		
		saved = true;
	}
	
	public boolean isSaved() {
		return saved && file != null;
	}
	
	public String getName() {
		return name;
	}

	public void setFile(File file) {
		this.file = file;
	}

}
