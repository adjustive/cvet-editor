package io.cvet.editor.gui;

import io.cvet.editor.config.Settings;
import io.cvet.editor.gui.tab.TabPanel;

public class View extends Component {

	private TabPanel pane;
	
	public View() {
		pane = new TabPanel();
		addChild(pane);

		// testing tabs
		pane.addTab(new Buffer(Settings.defaultEditorConfig));
	}
	
	public void init() {
		
	}
	
	public void update() {
		updateChildren(children);
	}
	
	public void render() {
		renderChildren(children);
	}
	
}
