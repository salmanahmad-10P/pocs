import java.lang.String;

public class HelloWorld {
	public static void main(String args[]) {
	    try {
                TestClass tClass = new TestClass();
                for(int t = 0; t < 10; t++ ) {
                   tClass.add();
                   System.out.println(" counter = "+tClass.counter+"  :  hashCode = "+tClass.hashCode());
                }
	    } catch(Exception x) {
		x.printStackTrace();
	    }
	}
}

class TestClass {
    int counter = 0;

    public void add() {
        counter++;
    }
}
