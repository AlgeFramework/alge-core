package poc;

import com.ning.http.client.*;

/**
 * Created with IntelliJ IDEA.
 * User: psrinivasan
 * Date: 8/23/12
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyAsyncCompletionHandler implements AsyncHandler {

    public Object onCompleted(Response response) throws Exception {
        Response r = response;
        System.out.println("onCompleted called :)");
        byte[] bytes = r.getResponseBodyAsBytes();
        System.out.println("status code = " + r.getStatusCode());
        System.out.println("status text = " + r.getStatusText());
        System.out.println("output bytes = " + r.getResponseBody());
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onThrowable(Throwable throwable) {
        System.out.println("onThrowable called");
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public STATE onBodyPartReceived(HttpResponseBodyPart httpResponseBodyPart) throws Exception {
        System.out.println("onBodyPartReceived called");
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public STATE onStatusReceived(HttpResponseStatus httpResponseStatus) throws Exception {
        System.out.println("onStatusReceived called:  the status is " + httpResponseStatus.getStatusCode()
                + " " + httpResponseStatus.getStatusText());

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public STATE onHeadersReceived(HttpResponseHeaders httpResponseHeaders) throws Exception {
        System.out.println("onHeadersReceived called");
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public Object onCompleted() throws Exception {
        System.out.println("onCompleted called");
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
