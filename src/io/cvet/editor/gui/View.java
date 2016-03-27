package io.cvet.editor.gui;

import java.util.ArrayList;

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
		return pane.findTab(key).buff;
	}

	public ArrayList<Tab> getTabList() {
		return pane.tabs;
	}

	public void closeCurrentTab() {
		System.out.println("Closing tab " + pane.getCurrentTab().buff.getName());
		pane.removeTab(pane.getCurrentTab());
	}

	public Tab getCurrentTab() {
		return pane.getCurrentTab();
	}

	public void focusTab(Buffer buff) {
		int t = pane.findTabIndex(buff.getName());
		pane.currentTabIdx = t;
	}
	
}
