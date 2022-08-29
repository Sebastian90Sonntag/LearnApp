package com.graphicdesigncoding.learnapp.api;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
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

    final Handler main_Handler = new Handler();

    public void finished(Object obj){}
    public void canceled(Object obj) {}

    public CallAPI(String url_str, @Nullable String params, ContentType contentType, TransferMethod method, Callback callback){

        new Thread(() -> {

            StringBuilder s = new StringBuilder();
            Bitmap bmp;
            String str;
            HttpURLConnection urlConnection;

            if(params == null){

                urlConnection = ServerCon(url_str, method, false, true);

            }else{

                urlConnection = ServerCon(url_str, method, true, true);

            }

            try {

                OutputStream out_stream;
                InputStream in_stream;

                if (urlConnection != null) {
                    Crypt crypt = new Crypt();
                    urlConnection.setRequestProperty("Content-Type",contentType.getAction());
                    out_stream = urlConnection.getOutputStream();
                    if(params != null) {
                        OutputStreamWriter out = new OutputStreamWriter(new BufferedOutputStream(out_stream));
                        out.write(params);
                        out.close();
                    }
                    in_stream = urlConnection.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(in_stream));
                    String response;
                    while ((response = in.readLine()) != null) {
                        s.append(response);
                    }
                    boolean error = s.toString().trim().toLowerCase().contains(crypt.md5("error"));
                    boolean isJson = ((s.toString().trim().startsWith("{") && s.toString().trim().endsWith("}")) ||
                            (s.toString().trim().startsWith("[") && s.toString().trim().endsWith("]")) || s.toString().trim().isEmpty());

                    if(error || isJson){
                        System.out.println("DEF");
                        System.out.println(s.toString());
                        str = s.toString();

                        if(error && isJson){

                            try {

                                JSONObject jsonObject = new JSONObject(str);

                                if(jsonObject.has(crypt.md5("error"))){
                                    main_Handler.post(() -> callback.canceled(str));
                                }else{
                                    main_Handler.post(() -> callback.finished(str));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                main_Handler.post(() -> callback.canceled(str));
                            }

                        }else{
                            main_Handler.post(() -> callback.finished(str));
                        }

                    }else{
                        System.out.println(s.toString());
                        byte[] decoded = Base64.decode(s.toString(),0);
                        bmp = BitmapFactory.decodeByteArray(decoded,0, decoded.length);

                        if(bmp != null){

                            main_Handler.post(() -> callback.finished(bmp));
                        }else{

                            main_Handler.post(() -> callback.finished(null));
                        }
                    }
                }else {
                    main_Handler.post(() -> callback.canceled("{\"" + new Crypt().md5("error") + "\":\"NoConnection\"}"));
                }
            } catch (IOException e) {
                e.printStackTrace();
                main_Handler.post(() -> callback.canceled("{\"" + new Crypt().md5("error") + "\":\"NoConnection\"}"));
            }
        }).start();
    }

    private HttpURLConnection ServerCon(String _url,TransferMethod _method,boolean allow_out,boolean allow_in){
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
            if(allow_out) {
                urlConnection.setDoOutput(true);
            }
            if(allow_in) {
                urlConnection.setDoInput(true);
            }
            urlConnection.setRequestMethod(_method.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return urlConnection;
    }
}


