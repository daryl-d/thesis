package com.daryl.thesis.loader.helper;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonSerialiser {

	@SuppressWarnings("resource")
	public static void main(String args[]) throws FileNotFoundException {
		Gson gson = new GsonBuilder().serializeNulls().create();
		
		File f = new File("out.txt");
		Scanner scanner = new Scanner(f);

		while (scanner.hasNext()) {
			String l = scanner.nextLine();
			Scanner line = new Scanner(l).useDelimiter(",");
			Tick t = new Tick(line, l);
			line.close();
			System.out.println(gson.toJson(t));
		}
		
		scanner.close();
	}
}
