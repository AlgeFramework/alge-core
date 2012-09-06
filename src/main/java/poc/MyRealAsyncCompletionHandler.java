package poc;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.HttpResponseBodyPart;
import com.ning.http.client.Response;

/**
 * Created with IntelliJ IDEA.
 * User: psrinivasan
 * Date: 8/23/12
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyRealAsyncCompletionHandler extends AsyncCompletionHandler {
    @Override
    public Object onCompleted(Response response) throws Exception {
        Response r = response;
        System.out.println("onCompleted called :)");
        byte[] bytes = r.getResponseBodyAsBytes();
        System.out.println("status code = " + r.getStatusCode());
        System.out.println("status text = " + r.getStatusText());
        //System.out.println("output bytes = " + r.getResponseBody());
        System.out.println("thread name: " + Thread.currentThread().getName());
        return STATE.CONTINUE;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public STATE onBodyPartReceived(final HttpResponseBodyPart content) throws Exception {
        //System.out.println("body part received!");
        System.out.println("thread name: " + Thread.currentThread().getName());
        return super.onBodyPartReceived(content);
    }
}
