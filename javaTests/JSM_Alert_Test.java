import java.io.*;

import mil.navy.spawar.c4idev.domain.alert.Alert;

public class JSM_Alert_Test {

    public static void main(String[] args) {
        Alert alertObj = new Alert();
        System.out.println("main() alertObj = "+alertObj.getMessageID());
    }

}
