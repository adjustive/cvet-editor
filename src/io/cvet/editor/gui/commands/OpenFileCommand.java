package io.cvet.editor.gui.commands;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import io.cvet.editor.Editor;
import io.cvet.editor.gui.Buffer;
import io.cvet.editor.util.FileUtil;

public class OpenFileCommand extends Command {

	public OpenFileCommand() {
		super("open", 0);
	}

	@Override
	public void action(String[] arguments) {
		if (arguments.length == 0) {
			try {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JFileChooser chooser = new JFileChooser();
						if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
							File file = chooser.getSelectedFile();
							Editor.getInstance().pushBuffer(new Buffer(file));
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		} 
		
		File potentialFile = new File(arguments[0]);
		if (potentialFile.isFile()) {
			Editor.getInstance().pushBuffer(new Buffer(potentialFile));
			return;
		}
		
		// check if its a URL _last_
		// it's more likely the user
		// will try to open a file than a url
		if (FileUtil.isValidURL(arguments[0])) {
			// TODO: check actual scheme
			String source = FileUtil.LoadFromUrl(arguments[0]);
			File file = new File(arguments[0]);
			Editor.getInstance().pushBuffer(new Buffer(file.getName(), source));
			return;
		}
		
		// TODO: check for file in the
		// editors view
	}
	
	@Override
	public String getHelp() {
		return "Opens the given file.\n" +
				"If a ? is specified,\n" +
				"a file chooser will be shown. If a non-path\n" +
				"is given, an existing file in the editor will be\n" +
				"opened if it exists. (open <path|?|name>)";
	}

}
