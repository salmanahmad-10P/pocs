import java.io.*;
import java.net.*;

public class SystemTest {
  public static void main(String args[]) {
  try {
      String msg = "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'><env:Header></env:Header><env:Body><ns2:AddTrack xmlns:ns2=\"http://northgrum.com/C2PC/webservices\"><ns2:trk xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:Unit\"><ns2:Threat>HOS</ns2:Threat><ns2:TrackType>UNITS</ns2:TrackType><ns2:MilStd2525Symbol><ns2:MilStd2525SymbolId>SHGP----------G</ns2:MilStd2525SymbolId></ns2:MilStd2525Symbol><ns2:Events><ns2:PositionalReport><ns2:Location><ns2:Position><ns2:Latitude>5.0587481307412947018065096926875412464141845703125</ns2:Latitude><ns2:Longitude>-5.33167516098513782907275526667945086956024169921875</ns2:Longitude></ns2:Position></ns2:Location></ns2:PositionalReport></ns2:Events></ns2:trk></ns2:AddTrack></env:Body></env:Envelope>";
      URL url = new URL("http://150.125.48.68/C2PCWeb/C2PCWebService.asmx");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setDoOutput(true);
      conn.setDoInput(true);
      byte[] requestBytes = msg.getBytes();
      OutputStream os = conn.getOutputStream();
      os.write(requestBytes);
      String contentType = conn.getContentType();
      System.out.println("content-type: " + contentType);
      InputStream is = conn.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      String response = reader.readLine();
      System.out.println("response: " + response);
   } catch(Exception x) {
	x.printStackTrace();
   }

  }
}
