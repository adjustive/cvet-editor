package io.cvet.editor.gui;

import java.util.ArrayList;
import java.util.Set;

import io.cvet.editor.gui.tab.Tab;
import io.cvet.editor.gui.tab.TabPanel;

public class View extends Component {

	private TabPanel pane;
	
	public View() {
		pane = new TabPanel();
		addChild(pane);
	}
	
	public void init() {
		
	}
	
	public void update() {
		updateChildren(children);
	}
	
	public void render() {
		renderChildren(children);
	}

	public void addTab(Buffer buffer) {
		pane.addTab(buffer);
	}
	
	public Tab getTab(String tabName) {
		return pane.findTab(tabName);
	}

	public Buffer getBuffer(String key) {
		return pane.tabs.peek().buff;
	}

	public ArrayList<String> getBufferNames() {
		ArrayList<String> buffers = new ArrayList<String>(pane.tabs.size());
		for (int i = 0; i < pane.tabs.size(); i++) {
			buffers.add(pane.tabs.get(i).name);
		}
		return buffers;
	}

	public void closeCurrentTab() {
		System.out.println("Closing tab " + pane.getCurrentTab().buff.getName());
		pane.removeTab(pane.getCurrentTab());
	}

	public Tab getCurrentTab() {
		return pane.getCurrentTab();
	}
	
}
