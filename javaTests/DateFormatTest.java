import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

public class DateFormatTest {

    public static final String testDate = "1-Mar-2013";

    public static void main(String args[]) throws ParseException{

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

        Date dateFromString = sdf.parse(testDate);
        System.out.println("Date right now = "+sdf.format(new Date()));
        System.out.println("Date from  String = "+sdf.format(dateFromString));
    }

}
