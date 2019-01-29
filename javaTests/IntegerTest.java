public class IntegerTest {
    public static void main(String[] args) {
        Integer intObj = new Integer("02451");
        System.out.println("intObj = "+intObj.intValue());
        System.out.println("number of leading zeros = "+Integer.numberOfLeadingZeros(intObj.intValue()));

        intObj = new Integer(02451);
        System.out.println("intObj = "+intObj.intValue() +" intObj = "+intObj);
        System.out.println("number of leading zeros = "+Integer.numberOfLeadingZeros(intObj.intValue()));

        System.out.println("intValue of 02451  = "+new Integer(02451).intValue());
    }
}
