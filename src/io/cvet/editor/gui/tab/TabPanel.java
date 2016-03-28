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
		this.h = RenderBackend.CHARACTER_HEIGHT + (padding * 2);
	}
	
	@Override
	public void init() {
		
	}

	@Override
	public void update() {
		if (getCurrentTab() != null) {
			getCurrentTab().update();
		}
		for (int i = 0; i < tabs.size(); i++) {
			Tab t = tabs.get(i);
			if (Input.intersects(t)) {
				if (Input.getMouseClicked(0)) {
					currentTabIdx = i;
				}
			}
		}
		
		if (currentTabIdx < 0) {
			currentTabIdx = 0;
		}
	}

	@Override
	public void render() {
		RenderContext.colour(Theme.DARK_BASE);
		RenderContext.rect(x, y, Display.getWidth() + 2, this.h + 2);
		
		RenderContext.colour(Theme.DARK_ACCENT);
		RenderContext.rect(x, y, Display.getWidth(), this.h);
		
		int size = 0;
		for (int i = 0; i < tabs.size(); i++) {
			Tab tab = tabs.get(i);
			tab.renderTab(size, y);
			
			// underline
			if (getCurrentTab() != null && getCurrentTab().name.equals(tab.name)) {
				RenderContext.colour(230, 201, 115);
				RenderContext.rect(size, this.h - 1, tab.w, 2);
				tab.x = size;
				tab.y = y;
			}
			
			size += tab.w + 1;
		}
		
		if (getCurrentTab() != null) {
			getCurrentTab().render();
		}
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
