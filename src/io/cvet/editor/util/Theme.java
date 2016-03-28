package io.cvet.editor.util;

import io.cvet.editor.gfx.Colour;

public class Theme {

	public static final Colour BASE = new Colour(0x272822);
	public static final Colour DARK_BASE = new Colour(BASE).darker();
	
	public static final Colour ACCENT = new Colour(0x61A598);
	public static final Colour DARK_ACCENT = new Colour(ACCENT).darker();

	// SYNTAX HIGHLIGHTING
	
	public static final Colour TYPE = new Colour(0x66D9EF);
	public static final Colour IDENTIFIER = new Colour(0xA6E22E);
	public static final Colour KEYWORD = new Colour(0xF92672);
	public static final Colour LITERAL = new Colour(0x8779FF);
	
}
