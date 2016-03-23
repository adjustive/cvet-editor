package io.cvet.editor.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class FileUtil {

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

}