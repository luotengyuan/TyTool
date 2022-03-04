package com.lois.unclassifieda.http;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Description: Http请求(依赖库org.apache.http.legacy.jar)
 * User: Luo.T.Y
 * Date: 2017-09-23
 * Time: 15:19
 */
public class HttpRequester {
    private static final String TAG = HttpRequester.class.getSimpleName();
    /**
     * 异步请求客户端
     */
    private static AsyncHttpClient client = new AsyncHttpClient();
    /**
     * 同步请求客户端
     */
    private static SyncHttpClient syncHttpClient = new SyncHttpClient();

    /**
     * 异步post请求（需要在主线程中执行）
     * @param context
     * @param url
     * @param object
     * @param responseHandler
     * @throws IOException
     * @throws JSONException
     */
    public static void asyncPost(Context context, String url, JSONObject object, ResponseHandlerInterface responseHandler) throws IOException, JSONException {
        ByteArrayEntity entity = new ByteArrayEntity(object.toString().getBytes("UTF-8"));
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        client.post(context, url, entity, "application/json", responseHandler);
    }

    /**
     * 同步post请求（需要在子线程中执行）
     * @param context
     * @param url
     * @param object
     * @param responseHandler
     * @throws IOException
     * @throws JSONException
     */
    public static void syncPost(Context context, String url, JSONObject object, ResponseHandlerInterface responseHandler) throws IOException, JSONException {
        ByteArrayEntity entity = new ByteArrayEntity(object.toString().getBytes("UTF-8"));
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        syncHttpClient.post(context, url, entity, "application/json", responseHandler);
    }
}
