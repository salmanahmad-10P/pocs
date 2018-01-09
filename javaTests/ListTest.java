import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ListTest {
    static int numElements = 50;
    static int dsCapacity = 20;
    public static void main (String args[]) {
        equalsTest();
    }

    public static void removeListTest() {
        try {
            LinkedList<String> newList = new LinkedList();
	    for(int t = 0; t <= numElements; t++) {
		newList.addFirst("Azra "+t);
		if(newList.size() > dsCapacity)
		  newList.removeLast();
	    }
	    for(int t = 0; t < newList.size(); t++) {
	      System.out.println("element = "+newList.get(t));
	    }
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static void equalsTest() {
        CopyOnWriteArrayList<Integer> availableSessions = new CopyOnWriteArrayList<Integer>();
        availableSessions.add(new Integer(1));
        if(availableSessions.contains(new Integer(1))) {
            System.out.println("it sees it");
        } else {
            System.out.println("does not see it");
        }
        
    }
}
