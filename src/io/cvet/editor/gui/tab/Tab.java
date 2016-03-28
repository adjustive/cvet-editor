package io.cvet.editor.gui.tab;

import io.cvet.editor.Editor;
import io.cvet.editor.gfx.Colour;
import io.cvet.editor.gfx.RenderBackend;
import io.cvet.editor.gfx.RenderContext;
import io.cvet.editor.gui.Buffer;
import io.cvet.editor.gui.Component;
import io.cvet.editor.gui.layers.Layer;
import io.cvet.editor.gui.menu.Menu;
import io.cvet.editor.gui.menu.MenuAction;
import io.cvet.editor.gui.menu.MenuItem;
import io.cvet.editor.util.Input;
import io.cvet.editor.util.Theme;

public class Tab extends Component {

	public String name;
	public Buffer buff;
	
	private int padding = 10;
	
	public Tab(String name, Buffer buff) {
		setFont(RenderBackend.EDITING_FONT);
		
		this.name = name;
		this.buff = buff;
		this.w = font.getWidth(name) + (padding * 2);
		this.h = font.getHeight() + (padding);
		
		addChild(buff);
		buff.setPosition(buff.x, this.h);

		// context menu
		this.context = new Menu();
		context.setLayer(Layer.TOP);
		context.setVisible(false);
		addChild(context);
		
		// because we can't do this inside of the
		// interfaces :(
		final Tab t = this;
		
		context.setMouseTrigger(Input.MOUSE_LEFT);
		
		context.addItem(new MenuItem(context, "Close", new MenuAction() {
			public void perform() {
				Editor.getMainView().pane.removeTab(t);
			}
		}));
		
		// close every tab other than this one.
		context.addItem(new MenuItem(context, "Close Others", new MenuAction() {
			@Override
			public void perform() {
				Editor.getMainView().removeTabOtherThan(t);
			}
		}));
		
		// close every tab, including this one.
		context.addItem(new MenuItem(context, "Close All", new MenuAction() {
			@Override
			public void perform() {
				Editor.getMainView().clearTabs();
			}
		}));
	}

	@Override
	public void init() {
		
	}

	@Override
	public void update() {
		updateChildren(children);
	}
	
	public void renderTab(int x, int y) {
		RenderContext.font(font);
		RenderContext.colour(Theme.ACCENT);
		RenderContext.rect(x, y, w, h);
		
		// render tab name in a center alignment
		int tabNameWidth = font.getWidth(name) + (padding / 2);
		RenderContext.colour(Colour.WHITE);
		RenderContext.drawString(name, x + ((w / 2) - (tabNameWidth / 2)), y + ((h / 2) - (font.getHeight() / 2)));
	}

	@Override
	public void render() {
		renderChildren(children);
	}

}
