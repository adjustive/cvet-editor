package io.cvet.editor.gfx;

import io.cvet.editor.util.RNG;

import org.newdawn.slick.Color;

public class Colour {


	public float r, g, b, a;
	
	public static Colour PINK = new Colour(255, 0, 255);
	public static Colour WHITE = new Colour(255, 255, 255);
	public static Colour BLACK = new Colour(0, 0, 0);
	public static final Colour YELLOW = new Colour(255, 255, 0);
	public static final Colour RED = new Colour(255, 0, 0);
	
	// how do you spell gray?
	public static final Colour GRAY = new Colour(30, 30, 30);
	
	private Color fuckingSlick;
	
	public Colour() {
		this(RNG.range(0, 255), RNG.range(0, 255), RNG.range(0, 255));
	}
	
	public Colour(Colour o) {
		this(o.r, o.g, o.b);
	}
	
	public Colour(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		this.fuckingSlick = new Color(r / 255, g / 255, b / 255, a / 255); 
	}
	
	public Colour(float r, float g, float b) {
		this(r, g, b, 255);
	}
	
	public Colour(int hex) {
        this(((hex >> 16) & 0xFF), ((hex >> 8) & 0xFF), (hex & 0xFF), 255);
	}
	
	public Colour darker() {
		this.r *= (1 - 0.1);
		this.g *= (1 - 0.1);
		this.b *= (1 - 0.1);
		return this;
	}

	public Color getStupidFuckingColour() {
		return fuckingSlick;
	}
	
	public String toString() {
		return r + ", " + g + ", " + b + ", " + a;
	}
	
}
