package io.cvet.editor.gui.tab;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;

import io.cvet.editor.gfx.RenderContext;
import io.cvet.editor.gui.Buffer;
import io.cvet.editor.gui.Component;

public class TabPanel extends Component {

	private List<Tab> tabs = new ArrayList<Tab>();
	private Tab currentTab = null;
	private int lastTabIndex = 0;
	
	@Override
	public void init() {
		
	}

	@Override
	public void update() {
		if (currentTab != null) {
			currentTab.update();
		}
	}

	@Override
	public void render() {
		RenderContext.colour(225, 0, 220);
		RenderContext.rect(x, y, Display.getWidth(), 24);
		
		int size = 0;
		for (int i = 0; i < tabs.size(); i++) {
			Tab tab = tabs.get(i);
			tab.renderTab(size, y);
			size += tab.w;
		}
		
		if (currentTab != null) {
			currentTab.render();
		}
	}
	
	public void addTab(Buffer buff) {
		Tab t = new Tab(lastTabIndex++, buff.getName(), buff);
		tabs.add(t);
		currentTab = t;
	}
	
}
