package io.cvet.editor.gui;

import java.util.ArrayList;

import io.cvet.editor.gui.tab.Tab;
import io.cvet.editor.gui.tab.TabPanel;

public class View extends Component {

	public TabPanel pane;
	
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
		pane.removeTab(pane.getCurrentTab());
	}

	public Tab getCurrentTab() {
		return pane.getCurrentTab();
	}

	public void setTab(Buffer buff) {
		setTab(buff.getName());
	}

	public void setTab(String key) {
		pane.currentTabIdx = pane.findTabIndex(key);
	}

	public void removeTabOtherThan(Tab t) {
		pane.currentTabIdx = pane.findTabIndex(t.name);
		
		ArrayList<Tab> tabs = getTabList();
		for (int i = 0; i < tabs.size(); i++) {
			if (!tabs.get(i).name.equals(t.name)) {
				pane.removeTab(tabs.get(i));
			}
		}
	}

	public void clearTabs() {
		ArrayList<Tab> tabs = getTabList();
		for (int i = 0; i < tabs.size(); i++) {
			pane.removeTab(tabs.get(i));
		}
		pane.currentTabIdx = -1;
	}
	
}
