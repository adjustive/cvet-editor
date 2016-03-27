package io.cvet.editor.gui.tab;

import java.util.Stack;

import org.lwjgl.opengl.Display;

import io.cvet.editor.gfx.Colour;
import io.cvet.editor.gfx.RenderBackend;
import io.cvet.editor.gfx.RenderContext;
import io.cvet.editor.gui.Buffer;
import io.cvet.editor.gui.Component;
import io.cvet.editor.util.Theme;

public class TabPanel extends Component {

	public Stack<Tab> tabs = new Stack<Tab>();
	private int lastTabIndex = 0;
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
			if (getCurrentTab().name.equals(tab.name)) {
				RenderContext.colour(230, 201, 115);
				RenderContext.rect(size, this.h - 1, tab.w, 2);
			}
			size += tab.w + 1;
		}
		
		if (getCurrentTab() != null) {
			getCurrentTab().render();
		}
	}
	
	public void addTab(Buffer buff) {
		System.out.println("adding " + buff.getName());
		
		Tab t = new Tab(lastTabIndex++, buff.getName(), buff);
		tabs.push(t);
	}
	
	public Tab getCurrentTab() {
		if (tabs.size() == 0) {
			return null;
		}
		return tabs.peek();
	}
	
	public void removeTab(Tab t) {
		tabs.remove(t);
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
