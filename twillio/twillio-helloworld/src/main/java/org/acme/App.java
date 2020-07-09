package org.acme;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class App  {

    public static String accountSID = null;
    public static String authToken = null;
    public static String toPhoneNumber = null;
    public static String fromPhoneNumber = null;

    public static void main( String[] args ) {

        accountSID = System.getProperty("ACCOUNT_SID");
        authToken = System.getProperty("AUTH_TOKEN");
        toPhoneNumber = System.getProperty("TO_PHONE_NUMBER");
        fromPhoneNumber = System.getProperty("FROM_PHONE_NUMBER");

        Twilio.init(accountSID, authToken);

        Message message = Message.creator(
            new PhoneNumber(toPhoneNumber),
            new PhoneNumber(fromPhoneNumber),
            "This is the ship that made the Kessel Run in fourteen parsecs?"
        ).create();
        System.out.println(message.getSid());
    }

}
