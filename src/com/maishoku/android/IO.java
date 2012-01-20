package com.maishoku.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;


public class IO {

	public static final Charset charset = Charset.forName("utf-8");
	
	public static String readString(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is, charset), 4096);
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line).append('\n');
		}
		return sb.toString();
	}

}
