import javax.imageio.*;
import java.io.*;
import java.net.*;
import java.awt.image.*;
import java.awt.*;

public class ImageIOTest {
	public static void main(String[] args) {
		Image image = null;
		try {
			URL url = new URL("http://localhost/rfidimages/image.gif");
			image = ImageIO.read(url);

			BufferedWriter out = new BufferedWriter(new FileWriter(
					"testImage.jpg"));
			out.write(image);
			out.close();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
