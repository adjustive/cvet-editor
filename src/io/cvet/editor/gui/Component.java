package io.cvet.editor.gui;

import io.cvet.editor.Layout;
import io.cvet.editor.gfx.Render;
import io.cvet.editor.util.Input;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public abstract class Component {

	// components dimensions
	public int x, y, w, h;
	
	protected boolean focus = true;
	protected boolean visible = true;
	
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
	
	// LAYOUT
	
	private int nextX = 0;
	private int nextY = 0;
	
	private int findTallestComponent() {
		int tallest = 0;
		for (Component c : children) {
			if (c.h > tallest) {
				tallest = c.h;
			}
		}
		return tallest;
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
		case Left:
			// if our row will be greater than
			// the width, reset row width
			// and shift down
			if (nextX + c.w >= this.w) {
				nextX = 0;
				nextY += findTallestComponent();
			} else if (children.size() != 0) {
				nextX += getLastComponent().w;
			}
			c.y = nextY;
			c.x = nextX;
			children.add(c);
		case Halves:
			children.add(c);
			if (children.size() != 0) {
				// before we add it, we half the previous
				int idx = 0;
				for (Component child : children) {
					child.w = Display.getWidth() / children.size();
					child.x = idx * child.w;
					idx += 1;
				}
			} else {
				c.w = Display.getWidth();
			}
			break;
		case VerticalHalves:
			children.add(c);
			// FIXME
			if (children.size() > 2) {
				int newH = getLastComponent().h / 2;
				getLastComponent().h = newH;
			}
			break;
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
	
	public void updateChildren(List<Component> children) {
		checkFocus();
		for (int i = 0; i < children.size(); i++) {
			Component c = children.get(i);

			c.checkFocus();

			// only update if
			// the component is focused on
			if (c.isVisible() && c.getFocus()) {
				c.update();
			}
		}
	}
	
	public void renderChildren(List<Component> children) {
		for (int i = 0; i < children.size(); i++) {
			Component c = children.get(i);

			Render.startClip(c.x, c.y, c.w, c.h);
			if (c.isVisible()) {
				c.render();
			}
			Render.endClip();
			
			// render a strip at the bottom of focused components
			if (c.getFocusable() && c.getFocus()) {
				Render.colour(125, 255, 50);
				Render.rect(c.x, c.y + c.h - 2, c.w, 2);
			}
		}
	}
	
	public void clearFocus() {
		for (Component c : children) {
			c.setFocus(false);
		}
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
	
	public void setPosition(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
}
