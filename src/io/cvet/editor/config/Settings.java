package io.cvet.editor.config;

import io.cvet.editor.gfx.ImmediateRenderer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import me.grison.jtoml.impl.Toml;

public class Settings {

	private static Toml toml;
	private static File editorDir = new File(defaultDirectory() + "/.nate-editor");
	private static File defaultEditorConfig = new File(editorDir + "/config.toml");
	private static File userEditorConfig = new File(editorDir + "/user_config.toml");
	
	// this is the default config file
	private static String defaultConfig = "[editor]\n" +
			"font_face = \"Monospaced\"\n" +
			"font_size = 14\n" +
			"hungry_backspace = false\n" +
			"tab_size = 4\n" +
			"match_braces = false\n" +
			"anti_alias = true\n"
			+ "blink_cursor = false\n"
			+ "cursor_blink_latency = 1000";
	
	private static HashMap<String, Object> DEFAULT_SETTING_LOOKUP;
	private static HashMap<String, Object> USER_SETTING_LOOKUP;
	
	public static void reload() {
		loadSettings();
		ImmediateRenderer.loadFont();
	}
	
	public static void newFile(File file, String contents) {
		if (!file.isFile()) {
			try {
				file.createNewFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				writer.write(contents);
				writer.close();
			} 
			catch (IOException e) {
				System.err.println("Failed to create and write file " + file.getAbsolutePath());
			}
		}
	}
	
	public static Object getSetting(String key) {
		if (USER_SETTING_LOOKUP != null && USER_SETTING_LOOKUP.containsKey(key)) {
			Object res = USER_SETTING_LOOKUP.get(key);
			System.out.println("notice: found setting " + key + " : " + res.toString());
			if (res instanceof Long) {
				return (int) safeLongToInt((long) res);
			}
			return res;
		}
		
		if (DEFAULT_SETTING_LOOKUP == null) {
			System.err.println("> mfw the backup fails");
			System.exit(1969);
		}
		
		System.err.println("Failed to load " + key + " loading default from backup");
		Object res = DEFAULT_SETTING_LOOKUP.get(key);
		if (res instanceof Long) {
			return (int) safeLongToInt((long) res);
		}
		return res;
	}
	
	public static void setupEditor() {
		if (!editorDir.isDirectory()) {
			if (!editorDir.mkdir()) {
				// TODO: proper error handling
				System.err.println("failed to create config folder at " + editorDir.getAbsolutePath());
			}
		}
		
		newFile(defaultEditorConfig, defaultConfig);
		newFile(userEditorConfig, defaultConfig);
	}
	
	public static void loadSettings() {
		try {
			toml = Toml.parse(defaultEditorConfig);
			DEFAULT_SETTING_LOOKUP = (HashMap<String, Object>) toml.getMap("editor");
			if (DEFAULT_SETTING_LOOKUP == null) {
				System.err.println("No editor entry!");
			}
			
			toml = Toml.parse(userEditorConfig);
			USER_SETTING_LOOKUP = (HashMap<String, Object>) toml.getMap("editor");
			if (USER_SETTING_LOOKUP == null) {
				System.err.println("No editor entry!");
			}
		}
		catch (IOException e) {
			System.err.println("failed to parse editor config");
		}
	}
	
	public static int safeLongToInt(long l) {
	    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
	        throw new IllegalArgumentException
	            (l + " cannot be cast to int without changing its value.");
	    }
	    return (int) l;
	}

	public static File getUserConfigFile() {
		return userEditorConfig;
	}
	
	private static String defaultDirectory() {
	    String OS = System.getProperty("os.name").toUpperCase();
	    if (OS.contains("WIN"))
	        return System.getenv("APPDATA");
	    else if (OS.contains("MAC"))
	        return System.getProperty("user.home") + "/Library/Application "
	                + "Support";
	    else if (OS.contains("NUX"))
	        return System.getProperty("user.home");
	    return System.getProperty("user.dir");
	}
	
}
