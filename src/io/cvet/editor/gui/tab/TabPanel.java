package io.cvet.editor.gui.tab;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;

import io.cvet.editor.gfx.RenderBackend;
import io.cvet.editor.gfx.RenderContext;
import io.cvet.editor.gui.Buffer;
import io.cvet.editor.gui.Component;
import io.cvet.editor.util.Input;
import io.cvet.editor.util.Theme;

public class TabPanel extends Component {

	public ArrayList<Tab> tabs = new ArrayList<Tab>();
	public int currentTabIdx = 0;
	private int padding = 5;
	
	public TabPanel() {
		this.h = RenderBackend.CHARACTER_HEIGHT + (padding * 3);
	}
	
	@Override
	public void init() {
		
	}

	@Override
	public void update() {
		if (getCurrentTab() != null) {
			getCurrentTab().update();
		}
		
		if (currentTabIdx < 0) {
			currentTabIdx = 0;
		}
	}

	@Override
	public void render() {
		RenderContext.colour(Theme.DARK_ACCENT);
		RenderContext.rect(x, y, Display.getWidth(), this.h);
		
		if (getCurrentTab() == null) {
			return;
		}
		
		Tab tab = getCurrentTab();
		tab.renderTab();
		tab.render();
	}
	
	public Tab addTab(Buffer buff) {
		System.out.println("adding " + buff.getName());
		buff.setPosition(x, y + this.h);
		
		Tab t = new Tab(buff.getName(), buff);
		tabs.add(t);
		currentTabIdx = tabs.size() - 1;
		return t;
	}
	
	public Tab getCurrentTab() {
		if (tabs.size() == 0) {
			return null;
		}
		return tabs.get(currentTabIdx);
	}
	
	public void removeTab(Tab t) {
		// FIXME: this is hacky?
		if (t == null) {
			return;
		}
		
		tabs.remove(t);
		currentTabIdx -= 1;
	}

	public int findTabIndex(String tabName) {
		for (int i = 0; i < tabs.size(); i++) {
			if (tabs.get(i).name.equals(tabName)) {
				return i;
			}
		}
		return -1;
	}
	
	public Tab findTab(String tabName) {
		for (int i = 0; i < tabs.size(); i++) {
			if (tabs.get(i).name.equals(tabName)) {
				return tabs.get(i);
			}
		}
		return null;
	}
	
}
