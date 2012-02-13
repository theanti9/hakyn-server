package com.hakyn.config;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class HakynConfigLoader {

	// Load the hakyn config
	public static void LoadConfig() throws IOException, FileNotFoundException {
		HashMap<String, Object> attrs = new HashMap<String, Object>();
		// Open config file
		FileInputStream finstream = new FileInputStream("hakyn.config");
		// Get the buffered reader
		BufferedReader bf = new BufferedReader(new InputStreamReader(finstream));
		
		String line;
		// While we have lines
		while ((line = bf.readLine()) != null) {
			// See if the current line is a comment, signified by starting with a ';'
			if (line.charAt(0) == ';') {
				// If so, pass on this line
				continue;
			}
			// Split on the equal sign
			String[] spl = line.split("/=/");
			// If we don't have two elements, something's up so pass
			if (spl.length != 2) {
				continue;
			}
			// Add the trimmed results to the hashmap
			attrs.put(spl[0].trim(), spl[1].trim());
		}
		// Return the new HakynConfig object with the appropriate attributes
		HakynConfig.Setup(attrs);
	}
	
}
