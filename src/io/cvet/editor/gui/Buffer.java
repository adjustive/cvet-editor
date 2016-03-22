package io.cvet.editor.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JFileChooser;

public class Buffer extends TextArea {

	private String name;
	private File file;
	private boolean saved;
	
	public Buffer(String name) {
		this.name = name;
	}
	
	public Buffer(String name, String contents) {
		this.name = name;
		buffer.clear();
		for (String s : contents.split("\n")) {
			buffer.add(new StringBuilder(s));
		}
	}
	
	public Buffer(File file) {
		this.name = file.getName();
		this.saved = true;
		this.file = file;
		buffer.clear();
		this.loadFile(file);
	}

	public void save() {
		// we haven't before, so we need
		// to set where to save.
		if (!isSaved()) {
			// TODO: store the last directory we saved
			// in, and set the default location to that
			JFileChooser chooser = new JFileChooser(System.getenv("user.home"));
			if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
				file = chooser.getSelectedFile();
			}
		}
		
		// write contents of buffer
		// into the file
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (StringBuilder builtString : buffer) {
				String actualString = builtString.toString() + '\n';
				bw.write(actualString);
			}
			bw.close();
		} 
		catch (Exception e) {
			System.err.println(":(");
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
