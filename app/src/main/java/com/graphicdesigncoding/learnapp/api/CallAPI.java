package com.graphicdesigncoding.learnapp.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//COPYRIGHT BY GraphicDesignCoding
public class CallAPI implements Callback {

    final Handler main_Handler = new Handler(Looper.getMainLooper());

    public void finished(Object obj){}
    public void canceled(Object obj) {}

    public CallAPI(String url_str, @Nullable String params, ContentType contentType, TransferMethod method, Callback callback) {
        this(url_str, params, contentType, method, null, callback);
    }

    public CallAPI(String url_str, @Nullable String params, ContentType contentType, TransferMethod method, @Nullable String jwtToken, Callback callback) {

        new Thread(() -> {

            StringBuilder s = new StringBuilder();
            Bitmap bmp;
            String str;
            HttpURLConnection urlConnection;

            if (params == null && method == TransferMethod.GET) {
                urlConnection = ServerCon(url_str, method, false, true, jwtToken);
            } else {
                urlConnection = ServerCon(url_str, method, true, true, jwtToken);
            }

            try {
                OutputStream out_stream;
                InputStream in_stream;

                if (urlConnection != null) {
                    if (contentType != null) {
                        urlConnection.setRequestProperty("Content-Type", contentType.getAction());
                    }
                    if (jwtToken != null && !jwtToken.isEmpty()) {
                        urlConnection.setRequestProperty("Authorization", "Bearer " + jwtToken);
                    }

                    if (params != null && method != TransferMethod.GET) {
                        byte[] postData = params.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                        urlConnection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                        out_stream = urlConnection.getOutputStream();
                        out_stream.write(postData, 0, postData.length);
                        out_stream.flush();
                        out_stream.close();
                    }

                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode >= 200 && responseCode < 300) {
                        in_stream = urlConnection.getInputStream();
                    } else {
                        in_stream = urlConnection.getErrorStream();
                    }

                    if (in_stream != null) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(in_stream, java.nio.charset.StandardCharsets.UTF_8));
                        String response;
                        while ((response = in.readLine()) != null) {
                            s.append(response);
                        }
                        in.close();
                    }

                    String rawResponse = s.toString().trim();
                    boolean isErrorStatus = (responseCode >= 400);
                    boolean isJson = ((rawResponse.startsWith("{") && rawResponse.endsWith("}")) ||
                            (rawResponse.startsWith("[") && rawResponse.endsWith("]")) || rawResponse.isEmpty());

                    if (isErrorStatus || isJson) {
                        str = rawResponse;

                        if (isErrorStatus) {
                            main_Handler.post(() -> callback.canceled(str));
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(str);
                                if (jsonObject.has("error")) {
                                    main_Handler.post(() -> callback.canceled(str));
                                } else {
                                    main_Handler.post(() -> callback.finished(str));
                                }
                            } catch (JSONException e) {
                                main_Handler.post(() -> callback.finished(str));
                            }
                        }

                    } else {
                        try {
                            byte[] decoded = Base64.decode(rawResponse, Base64.DEFAULT);
                            bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);

                            if (bmp != null) {
                                main_Handler.post(() -> callback.finished(bmp));
                            } else {
                                main_Handler.post(() -> callback.finished(null));
                            }
                        } catch (Exception e) {
                            main_Handler.post(() -> callback.finished(rawResponse));
                        }
                    }
                } else {
                    main_Handler.post(() -> callback.canceled("{\"error\":\"No connection to server (" + url_str + ")\"}"));
                }
            } catch (IOException e) {
                e.printStackTrace();
                main_Handler.post(() -> callback.canceled("{\"error\":\"No connection to server (" + url_str + ")\"}"));
            }
        }).start();
    }

    private HttpURLConnection ServerCon(String _url, TransferMethod _method, boolean allow_out, boolean allow_in, @Nullable String jwtToken) {
        URL url;
        try {
            url = new URL(_url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        HttpURLConnection urlConnection;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            if (allow_out && _method != TransferMethod.GET) {
                urlConnection.setDoOutput(true);
            }
            if (allow_in) {
                urlConnection.setDoInput(true);
            }
            urlConnection.setRequestMethod(_method.toString());
            if (jwtToken != null && !jwtToken.isEmpty()) {
                urlConnection.setRequestProperty("Authorization", "Bearer " + jwtToken);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return urlConnection;
    }
}
