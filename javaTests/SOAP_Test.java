import java.net.*;
import java.util.*;
import java.io.*;

public class SOAP_Test {

  static String Server = "150.125.48.68";
  static String WebServicePath = "/C2PCWeb/C2PCWebService.asmx";
  static String SoapAction = "http://northgrum.com/C2PC/webservices/AddTrack";
  static String MethodName = "";
  static String XmlNamespace = "";
  static private Vector ParamNames = new Vector();
  static private Vector ParamData = new Vector();

 public static void main(String args[]) {
	sendRequest();
 }

  public static String sendRequest() {
    String retval = "";
    Socket socket = null;
    try {
      socket = new Socket(Server, 80);
    }
    catch (Exception ex1) {
      return ("Error: "+ex1.getMessage());
    }

    try {
      OutputStream os = socket.getOutputStream();
      boolean autoflush = true;
      PrintWriter out = new PrintWriter(socket.getOutputStream(), autoflush);
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

	StringBuffer bodyString = new StringBuffer();
	bodyString.append("<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'><env:Header></env:Header><env:Body><ns2:AddTrack xmlns:ns2=\"http://northgrum.com/C2PC/webservices\"><ns2:trk xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:Unit\"><ns2:Threat>HOS</ns2:Threat><ns2:TrackType>UNITS</ns2:TrackType><ns2:MilStd2525Symbol><ns2:MilStd2525SymbolId>SHGP----------G</ns2:MilStd2525SymbolId></ns2:MilStd2525Symbol><ns2:Events><ns2:PositionalReport><ns2:Location><ns2:Position><ns2:Latitude>5.0587481307412947018065096926875412464141845703125</ns2:Latitude><ns2:Longitude>-5.33167516098513782907275526667945086956024169921875</ns2:Longitude></ns2:Position></ns2:Location></ns2:PositionalReport></ns2:Events></ns2:trk></ns2:AddTrack></env:Body></env:Envelope>");

      // send an HTTP request to the web service
      out.println("POST " + WebServicePath + " HTTP/1.1");
      out.println("Host: 150.125.48.68:80");
      out.println("Content-Type: text/xml; charset=utf-8");
      out.println("Content-Length: " + String.valueOf(bodyString.length()));
      out.println("SOAPAction: \"" + SoapAction + "\"");
      out.println("Connection: keep-alive");
      out.println();

      out.println(bodyString.toString());
      out.println();

      // Read the response from the server ... times out if the response takes
      // more than 3 seconds
      String inputLine;
      StringBuffer sb = new StringBuffer(1000);

      int wait_seconds = 3;
      boolean timeout = false;
      long m = System.currentTimeMillis();
      while ( (inputLine = in.readLine()) != null && !timeout) {
        sb.append(inputLine + "\n");
        if ( (System.currentTimeMillis() - m) > (1000 * wait_seconds)) timeout = true;
      }
      in.close();

	System.out.println("response = "+sb.toString());

      socket.close();
    }
    catch (Exception x) {
	x.printStackTrace();
    }

    return retval;
  }
}
