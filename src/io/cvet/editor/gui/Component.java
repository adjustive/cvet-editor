package io.cvet.editor.gui;

import io.cvet.editor.Layout;
import io.cvet.editor.util.Input;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public abstract class Component {

	// components dimensions
	public int x, y, w, h;
	
	// if it has focus
	protected boolean focus = false;
	
	// if the focus can be changed
	protected boolean focusable;
	
	protected List<Component> children = new ArrayList<Component>();
	
	public abstract void init();
	public abstract void update();
	public abstract void render();

	public Component() {
		
	}
	
	public Component(int x, int y, int w, int h) {
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
		default:
			children.add(c);
			break;
		}
	}
	
	public void checkFocus() {
		for (Component c : children) {
			if (Input.intersects(c)) {
				if (Mouse.isButtonDown(0)) {
					clearFocus();
					c.setFocus(true);
				}
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
	
}
