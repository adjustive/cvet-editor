package io.cvet.editor.gui.commands;

import io.cvet.editor.Editor;
import io.cvet.editor.gui.TextArea;

import java.io.File;

import javax.swing.JFileChooser;

public class OpenFileCommand extends Command {

	public OpenFileCommand() {
		super("open", 1);
	}

	@Override
	public void action(String[] arguments) {
		System.out.println(arguments.toString());
		if (arguments[0].equals("?")) {
			JFileChooser chooser = new JFileChooser();
			chooser.setVisible(true);
			if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				TextArea buff = new TextArea(file.getName());
				buff.loadFile(file);
				Editor.getInstance().setCurrentTextArea(buff);
			}
		}
	}

}
