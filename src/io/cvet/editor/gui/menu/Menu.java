package io.cvet.editor.gui.menu;

import java.util.ArrayList;

import io.cvet.editor.gfx.RenderContext;
import io.cvet.editor.gui.Component;
import io.cvet.editor.util.Input;

public class Menu extends Component {

	private ArrayList<MenuItem> items = new ArrayList<MenuItem>();
	
	public Menu() {
		
	}
	
	public void addItem(MenuItem item) { 
		item.x = 0;
		item.y = items.size() * item.h;
		items.add(item);
		if (item.w > this.w) {
			this.w = item.w;
		}
		this.h += item.h;
	}

	@Override
	public void init() {
		
	}

	@Override
	public void update() {
		for (int i = 0; i < items.size(); i++) {
			MenuItem item = items.get(i);
			item.update();
			if (Input.intersects(item)) {
				item.hover();
				if (Input.getMouseClicked(0)) {
					item.action.perform();
					hide();
					return;
				}
			}
		}
	}

	@Override
	public void render() {
		RenderContext.colour(255, 255, 255);
		RenderContext.rect(x, y, this.w, this.h);
		
		for (int i = 0; i < items.size(); i++) {
			MenuItem item = items.get(i);
			item.x = x;
			item.y = y + (i * item.h); // hmm..
			item.render();
		}
	}
	
	public void hide() {
		setFocus(false);
		setVisible(false);
	}

	public void show(int x, int y) {
		setPosition(Input.x, Input.y);
		setVisible(true);
		setFocus(true);
	}

}
