import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.MGRSRef;

public class mgrsTest {
    private static int utmZoneNumber = 41;
    private static char utmZoneChar = 'R';
    private static char eastingId = 'P';
    private static char northingId = 'R';
    private static int easting = 48544;
    private static int northing = 22978;
    private static int precision = MGRSRef.PRECISION_1M;
    private static boolean isBessel = true;

    public static void main(String args[]) {
        MGRSRef mgrsRef  = new MGRSRef(utmZoneNumber, utmZoneChar, eastingId, northingId, easting, northing, precision, isBessel);
        LatLng latLng = mgrsRef.toLatLng();
        System.out.println("MGRS = "+utmZoneNumber+utmZoneChar+" "+eastingId+northingId+" "+easting+" "+northing);
        System.out.println("lat = "+latLng.getLatitude() +"	:	long = " + latLng.getLongitude());

        CoordinateConversion cc = new CoordinateConversion();
        StringBuffer mgrBuf = new StringBuffer();
        mgrBuf.append(utmZoneNumber);
        mgrBuf.append(utmZoneChar);
        mgrBuf.append(eastingId);
        mgrBuf.append(northingId);
        mgrBuf.append(easting);
        mgrBuf.append(northing);
        double[] mgrArray = cc.mgrutm2LatLon(mgrBuf.toString());
        System.out.println("lat = "+mgrArray[0] +"	:	long = " + mgrArray[1]);

    }
}
