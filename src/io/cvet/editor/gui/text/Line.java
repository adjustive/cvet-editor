package io.cvet.editor.gui.text;

import java.util.ArrayList;
import java.util.List;

import io.cvet.editor.gfx.Colour;

public class Line {

	public List<Glyph> value;
	public Colour defaultColouring = Colour.WHITE;
	
	// TODO: current colour?
	public static Glyph WRAP_GLYPH = new Glyph(Colour.WHITE, '\n');
	
	public Line() {
		this(0);
	}
	
	public Line(int length) {
		this.value = new ArrayList<Glyph>(length);
	}
	
	public Line(String value) {
		this(value.length());
		append(defaultColouring, value);
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder(value.size());
		for (Glyph g : value) {
			s.append(g.value);
		}
		return s.toString();
	}
	
	public void set(Colour colouring, char c, int where) {
		value.set(where, new Glyph(colouring, c)); 
	}

	public void insert(int where, char c) {
		insert(defaultColouring, c, where);
	}
	
	public void insert(Colour colouring, char c, int where) {
		value.add(where, new Glyph(colouring, c)); 
	}
	
	public void append(char c) {
		append(defaultColouring, c);
	}
	
	public void append(Glyph g) {
		value.add(g);
	}
	
	public void append(Colour colouring, char c) {
		value.add(new Glyph(colouring, c));
	}
	
	public void append(String s) {
		append(defaultColouring, s);
	}
	
	public void append(Colour colouring, String s) {
		for (char c : s.toCharArray()) {
			append(colouring, c);
		}
	}
	
	public void setColouringAt(Colour c, int idx) {
		value.get(idx).colouring = c;
	}
	
	public void setCharAt(int idx, char c) {
		value.get(idx).value = c;
	}
	
	/**
	 * Set the colouring for 
	 * every glyph in the line.
	 */
	public void colour(Colour c) {
		for (int i = 0; i < value.size(); i++) {
			value.get(i).colouring = c;
		}
	}
	
	/**
	 * A glyph is synonymous with
	 * a character, it's named differently
	 * to avoid conflicting with Java's
	 * Character.
	 */
	public static class Glyph {
		public Colour colouring;
		public char value;
		
		public Glyph() {}
		
		public Glyph(Colour colouring, char value) {
			this.colouring = colouring;
			this.value = value;
		}
	}

	public int length() {
		return value.size();
	}

	public Line substring(int s, int e) {
		Line result = new Line();
		for (int i = s; i < e; i++) {
			result.value.add(value.get(i));
		}
		return result;
	}
	
	public Line substring(int s) {
		return substring(s, value.size());
	}

	public void deleteCharAt(int i) {
		if (i < 0 || i >= value.size()) {
			System.err.println("not sure how I feel about this");
			return;
		}
		value.remove(i);
	}

	public char charAt(int ix) {
		return value.get(ix).value;
	}
	
}
