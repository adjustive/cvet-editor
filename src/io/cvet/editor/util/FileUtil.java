package io.cvet.editor.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;

import org.lwjgl.Sys;

public class FileUtil {

	public static String getClipboardContents() {
		try {
			return Sys.getClipboard();
		} catch (Exception e) {
			System.err.println("Failed to get clipboard contents, todo error\n" + e.getMessage());
			return "";
		}
	}
	
	public static String LoadFromUrl(String location) {
		StringBuilder result = new StringBuilder();
		try {
			URL where = new URL(location);
			URLConnection conn = where.openConnection();
	
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				result.append(line);
			}
			br.close();
		}
		catch (Exception e) {
			return "";
		}
		return result.toString();
	}
	
	public static boolean isValidURL(String what) {
		try {
		    URL url = new URL(what);
		    URLConnection conn = url.openConnection();
		    conn.connect();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public static ByteBuffer loadIcon(String path) {
		InputStream inputStream = FileUtil.class.getResourceAsStream(path);
		PNGDecoder decoder = null;
		ByteBuffer bytebuf = null;

		try {
			decoder = new PNGDecoder(inputStream);
			bytebuf = ByteBuffer.allocateDirect(decoder.getWidth() * decoder.getHeight() * 4);
			decoder.decode(bytebuf, decoder.getWidth() * 4, PNGDecoder.RGBA);
			bytebuf.flip();
			return bytebuf;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bytebuf;
	}

}
