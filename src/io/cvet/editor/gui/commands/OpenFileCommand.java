package io.cvet.editor.gui.commands;

import io.cvet.editor.Editor;
import io.cvet.editor.gui.Buffer;

import java.io.File;

import javax.swing.JFileChooser;

public class OpenFileCommand extends Command {

	public OpenFileCommand() {
		super("open", 1);
	}

	@Override
	public void action(String[] arguments) {
		// If we pass a question mark
		// this means to open a file with
		// the file viewer
		if (arguments[0].equals("?")) {
			JFileChooser chooser = new JFileChooser();
			chooser.setVisible(true);
			if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				Editor.getInstance().setCurrentBuffer(new Buffer(file));
			}
			return;
		}
		
		File potentialFile = new File(arguments[0]);
		if (potentialFile.isFile()) {
			Editor.getInstance().setCurrentBuffer(new Buffer(potentialFile));
			return;
		}
		
		// TODO: check for file in the
		// editors view
	}
	
	@Override
	public String getHelp() {
		return "Opens the given file.\n " +
				"If a ? is specified,\n" +
				"a file chooser will be shown. If a non-path\n" +
				"is given, an existing file in the editor will be\n" +
				"opened if it exists. (open <path|?|name>)";
	}

}
