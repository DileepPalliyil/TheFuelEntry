package in.thefleet.thefuelentry;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by DILEEP on 08-01-2017.
 */

public class ServerRequestQueue {
    private static ServerRequestQueue serverRequestQueue;
    private RequestQueue volleyRequestQueue;

    private ServerRequestQueue(Context context) {
        volleyRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static ServerRequestQueue getInstance(Context context) {
        if (serverRequestQueue == null) {
            synchronized (ServerRequestQueue.class) {
                if (serverRequestQueue == null) {
                    serverRequestQueue = new ServerRequestQueue(context);
                }
            }
        }
        return serverRequestQueue;
    }

    public void addToQueue(Request request, String tag) {
        request.setTag(tag);
        volleyRequestQueue.add(request);
    }

    public void cancelAll(String tag) {
        volleyRequestQueue.cancelAll(tag);
    }


}
