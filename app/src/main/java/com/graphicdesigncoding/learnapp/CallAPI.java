package com.graphicdesigncoding.learnapp;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Base64;
import android.util.Pair;

import androidx.annotation.Nullable;
import androidx.annotation.StringDef;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class CallAPI implements Callback{

    final Handler main_Handler = new Handler();

    public void finished(Object obj){}
    public void canceled(Object obj) {}

    public CallAPI(String url_str,@Nullable String params,ContentType contentType, TransferMethod method, Callback callback){

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
                    boolean error = s.toString().trim().toLowerCase().contains("error") || s.toString().trim().toLowerCase().contains("mysql");
                    boolean isJson = ((s.toString().trim().startsWith("{") && s.toString().trim().endsWith("}")) ||
                            (s.toString().trim().startsWith("[") && s.toString().trim().endsWith("]")) || s.toString().trim().isEmpty());

                    if(error || isJson){

                        str = s.toString();

                        if(error){
                            main_Handler.post(() -> callback.canceled(str));
                        }else{
                            main_Handler.post(() -> callback.finished(str));
                        }

                    }else{

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


