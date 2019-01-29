import com.ning.http.client.*;
import java.util.concurrent.Future;

public class AsyncHttpClientTest {

    public static final String ASYNC = "ASYNC";
    private static AsyncHttpClient asyncHttpClient = null;
    //private static String urlString = "http://ratwater4:8080/rest/cclt/inVmMessage/1";
    private static String urlString = "http://www.google.com";
    private static boolean printResponseBody = true;

    public static void main(String args[]) {
        boolean asynch = true;
        try {
            if(System.getProperty(ASYNC) != null)
                asynch = Boolean.parseBoolean(System.getProperty(ASYNC));

            asyncHttpClient = new AsyncHttpClient();

            if(asynch)
                executeAsynchTest(printResponseBody);
            else
                executeSynchTest();

            Thread.sleep(5000);
           // asyncHttpClient.close();

        }catch(Exception x) {
            x.printStackTrace();
        }
    }

    private static void executeAsynchTest(final boolean printResponseBody) throws Exception {
        Future<String> f = asyncHttpClient.prepareGet(urlString).execute(new AsyncCompletionHandler<String>(){
            private StringBuilder builder = new StringBuilder();

            @Override
            public STATE onStatusReceived(HttpResponseStatus status) throws Exception {
                int statusCode = status.getStatusCode();
                System.out.println("onStatusReceived() statusCode = "+statusCode);
                // The Status have been read
                // If you don't want to read the headers,body or stop processing the response
                if(statusCode == 200)
                    return STATE.CONTINUE;
                else
                    return STATE.ABORT;
            }

            @Override
            public STATE onHeadersReceived(HttpResponseHeaders h) throws Exception {
                FluentCaseInsensitiveStringsMap hMap = h.getHeaders();
                return STATE.CONTINUE;
            }

            @Override
            public STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
               builder.append(new String(bodyPart.getBodyPartBytes()));
               return STATE.CONTINUE;
            }

            @Override
            public String onCompleted(Response response) throws Exception{
                // Will be invoked once the response has been fully read or a ResponseComplete exception
                // has been thrown.
                if(printResponseBody)
                    System.out.println("onCompleted() responseBody = "+builder.toString());
                return builder.toString();
            }
        
            @Override
            public void onThrowable(Throwable t){
                t.printStackTrace();
            }
        });
        
    }

    private static void executeSynchTest() throws Exception {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        Future<Response> f = asyncHttpClient.prepareGet("http://www.ning.com/").execute();
        Response rObj = f.get();
        System.out.println("main() response code = "+rObj.getStatusCode());
    }
}
