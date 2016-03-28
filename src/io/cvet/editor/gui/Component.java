package io.cvet.editor.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.TrueTypeFont;

import io.cvet.editor.Layout;
import io.cvet.editor.gfx.RenderBackend;
import io.cvet.editor.gfx.RenderContext;
import io.cvet.editor.gui.layers.Layer;
import io.cvet.editor.gui.menu.Menu;
import io.cvet.editor.util.Input;

public abstract class Component {

	public static enum Modifier {
		Super,
		Shift,
		Alt,
	}
	
	// components dimensions
	public int x, y, w, h;
	protected Layer layer = Layer.BOTTOM;
	
	protected Menu context;
	protected TrueTypeFont font = RenderBackend.EDITING_FONT;
	protected boolean focus = true;
	protected boolean visible = true;
	
	protected Modifier mod = Modifier.Super;
	public int trigger = -1;
	protected int mouseTrigger = -1;
	
	// if the focus can be changed
	protected boolean focusable;
	
	protected List<Component> children = new ArrayList<Component>();
	
	public abstract void init();
	public abstract void update();
	public abstract void render();

	public Component() {
	}
	
	public Component(int x, int y, int w, int h) {
		this.focusable = true;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	public void setFocus(boolean focus) {
		this.focus = focus;
	}
	
	public void setFocusable(boolean focusable) {
		this.focusable = focusable;
	}
	
	public boolean getFocus() {
		return focus;
	}
	
	public boolean getFocusable() {
		return focusable;
	}
	
	private Component getLastComponent() {
		if (children.size() > 2) {
			return children.get(children.size() - 1);
		}
		return children.get(0);
	}
	
	// todo: modular layout?
	public void addChild(Component c, Layout layout) {
		switch (layout) {
		case Child:
			c.x = this.x;
			c.y = children.size() != 0 ? c.y += getLastComponent().h : this.y;
			children.add(c);
			break;
		default: 
			children.add(c);
			break;
		}
	}
	
	public void checkFocus() {
		for (Component c : children) {
			if (Input.intersects(c)) {
				if (isVisible() && getFocusable() && Mouse.isButtonDown(0)) {
					clearFocus();
					c.setFocus(true);
				}
			}
		}
	}

	public void processTrigger() {
		if (context != null && !context.isVisible() && mouseTrigger != -1) {
			System.out.println("listening for context");
			if (Input.intersects(this) && Input.getMouseClicked(mouseTrigger)) {
				context.setPosition(Input.x, Input.y);
				context.setVisible(true);
				context.setFocus(true);
				return;
			}
		}
		
		if (!isVisible() && trigger != -1) {
			switch (mod) {
			case Super:
				if (Input.isControlModifierDown()) {
					boolean trig = Input.getKeyPressed(trigger);
					setVisible(trig);
					setFocus(trig);
				}
				break;
			case Shift:
				if (Input.isShiftModifierDown()) {
					setVisible(Input.getKeyPressed(trigger));
				}
				break;
			case Alt:
				System.out.println("todo");
				break;
			}
		}
	}
	
	public void updateChildren(List<Component> children) {
		processTrigger();
		
		checkFocus();
		for (int i = 0; i < children.size(); i++) {
			Component c = children.get(i);

			c.checkFocus();
			c.processTrigger();
			
			// only update if
			// the component is focused on
			if (c.isVisible() && c.getFocus()) {
				c.update();
			}
		}
	}
	
	public void renderLayer(List<Component> children, Layer target) {
		for (int i = 0; i < children.size(); i++) {
			Component c = children.get(i);
			if (c.layer != target) {
				continue;
			}

			RenderContext.startClip(c.x, c.y, c.w, c.h);
			if (c.isVisible()) {
				c.render();
			}
			RenderContext.endClip();
			
			// render a strip at the bottom of focused components
			if (c.getFocusable() && c.getFocus()) {
				RenderContext.colour(125, 255, 50);
				RenderContext.rect(c.x, c.y + c.h - 2, c.w, 2);
			}
		}
	}
	
	public void renderChildren(List<Component> children) {
		renderLayer(children, Layer.BOTTOM);
		renderLayer(children, Layer.MIDDLE);
		renderLayer(children, Layer.TOP);
	}
	
	public void clearFocus() {
		for (Component c : children) {
			c.setFocus(false);
		}
	}
	
	public void setKeyboardTrigger(Modifier mod, int trigger) {
		this.mod = mod;
		this.trigger = trigger;
	}
	
	public void addChild(Component c) {
		addChild(c, Layout.Free);
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setLayer(Layer layer) {
		this.layer = layer;
	}
	
	public void setFont(TrueTypeFont font) {
		this.font = font;
	}
	
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setBounds(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	public void setMouseTrigger(int mouseTrigger) {
		this.mouseTrigger = mouseTrigger;
	}
	
	public TrueTypeFont getFont() {
		return font;
	}
	
}
