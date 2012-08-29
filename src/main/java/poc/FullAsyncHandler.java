package poc;

import com.ning.http.client.*;

import java.util.HashSet;

public class FullAsyncHandler implements AsyncHandler {

    private final Response.ResponseBuilder builder = new Response.ResponseBuilder();
    public static final HashSet<Integer> list = new HashSet<Integer>();

    @Override
    public void onThrowable(Throwable throwable) {
    }

    @Override
    public STATE onBodyPartReceived(HttpResponseBodyPart httpResponseBodyPart) throws Exception {
        builder.accumulate(httpResponseBodyPart);
        return STATE.CONTINUE;

    }

    @Override
    public STATE onStatusReceived(HttpResponseStatus status) throws Exception {
        builder.reset();
        builder.accumulate(status);
        return STATE.CONTINUE;
    }

    @Override
    public STATE onHeadersReceived(HttpResponseHeaders headers) throws Exception {
        builder.accumulate(headers);
        return STATE.CONTINUE;

    }

    @Override
    public Object onCompleted() throws Exception {
        Response r = builder.build();
        System.out.println("onCompleted called on FullAsyncHandler :)");
        byte[] bytes = r.getResponseBodyAsBytes();
        System.out.println("status code = " + r.getStatusCode());
        System.out.println("status text = " + r.getStatusText());
        System.out.println("output bytes = " + r.getResponseBody());
        System.out.println("thread name: " + Thread.currentThread().getName());
//        Exception exception = new Exception();
//        exception.printStackTrace();
        list.add(Thread.currentThread().hashCode());
        return r;
    }
}
