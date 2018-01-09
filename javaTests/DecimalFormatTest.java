import java.text.DecimalFormat;

public class DecimalFormatTest {
	public static void main(String args[]) {
		DecimalFormat dfo = new DecimalFormat("00000");
		try {
			System.out.println("decimal = " + dfo.format(1));
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
