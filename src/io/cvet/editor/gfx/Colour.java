package io.cvet.editor.gfx;

public class Colour {


	public float r, g, b, a;
	
	public static Colour PINK = new Colour(255, 0, 255);
	public static Colour WHITE = new Colour(255, 255, 255);
	public static Colour BLACK = new Colour(0, 0, 0);
	public static final Colour YELLOW = new Colour(255, 255, 0);
	
	public Colour(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public Colour(float r, float g, float b) {
		this(r, g, b, 255);
	}
	
	public Colour(int hex) {
        this(((hex >> 16) & 0xFF), ((hex >> 8) & 0xFF), (hex & 0xFF), 255);
	}
	
}
