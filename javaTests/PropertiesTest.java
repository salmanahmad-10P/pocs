import java.util.*;

public class PropertiesTest {
    public static void main(String args[]) {
        Properties props = System.getProperties();
        Enumeration names = props.propertyNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            System.out.println("Name = " + name +"    :    value = "+props.get(name));
        }
    }
}
