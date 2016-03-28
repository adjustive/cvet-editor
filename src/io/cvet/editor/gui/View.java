package io.cvet.editor.gui;

import java.util.ArrayList;
import java.util.Calendar;

import io.cvet.editor.gui.tab.Tab;
import io.cvet.editor.gui.tab.TabPanel;

public class View extends Component {

	public TabPanel pane;
	
	private static String getWhen() {
		Calendar c = Calendar.getInstance();
		int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
		if (timeOfDay >= 0 && timeOfDay < 12) {
			return "Good Morning!";
		} else if (timeOfDay >= 12 && timeOfDay < 16) {
			return "Good Afternoon!";
		} else if (timeOfDay >= 16 && timeOfDay < 21) {
			return "Good Evening!";
		} else if (timeOfDay >= 21 && timeOfDay < 24) {
			return "Good Night!";
		}
		return "Hello!";
	}
	
	private String DEFAULT_BUFFER_MESSAGE = "Todo List: \n"
			+ "* Fix blinking cursor\n";
	
	private boolean showDefaultMessage = true;
	
	public View() {
		pane = new TabPanel();
		addChild(pane);
		
		if (showDefaultMessage) {
			addTab(new Buffer(getWhen(), DEFAULT_BUFFER_MESSAGE));
		}
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
