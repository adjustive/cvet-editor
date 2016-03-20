package io.cvet.editor.util;

import java.util.Random;

public class RNG {

	private static Random r = new Random();
	
	public static int range(int min, int max) {
		return r.nextInt((max - min) + 1) + min;
	}
	
	public static int any() {
		return r.nextInt(Integer.MAX_VALUE);
	}
	
	public static int cap(int cap) {
		return r.nextInt(cap);
	}
	
}
