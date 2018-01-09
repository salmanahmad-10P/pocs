import java.io.*;

public class ClassLoaderTest {

	public static void main(String[] args) {
		String fileName = "ClassLoaderTest.java";
		InputStream is = ClassLoader.getSystemResourceAsStream(fileName);
		if (is == null)
			System.out.println("fuck");
		else
			System.out.println("woo-hoo");
	}
}
