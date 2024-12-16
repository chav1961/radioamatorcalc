package chav1961.calc.references.tubes.images;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class Loader {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		final File		f = new File("c:/tmp/lamp.png");
		
		final String	val = Base64.getEncoder().encodeToString(Files.readAllBytes(f.toPath()));
		System.err.println(val);
	}

}
