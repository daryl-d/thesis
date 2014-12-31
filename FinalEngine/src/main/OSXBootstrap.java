package main;

import java.io.IOException;

public class OSXBootstrap {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Runtime.getRuntime().exec(
				"java -XstartOnFirstThread -cp etes.jar gui.Root");
	}

}
