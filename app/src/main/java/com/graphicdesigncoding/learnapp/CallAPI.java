package com.graphicdesigncoding.learnapp;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Base64;

import androidx.annotation.Nullable;

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

////////////////////////////////ERROR !!!! WENN FEHLER IN PHP DANN ABSTURZ
public class CallAPI implements Callback{
    final Handler main_Handler = new Handler();
    enum Method{ POST,GET}

    public void finished(Object obj){}
    public void canceled() {}

    // POST WITH SENDING JSON AND RETURN
    public void Post(String urlstr, String json, Callback callback){
        new Thread(() -> {
            StringBuilder s = new StringBuilder();
            HttpURLConnection urlConnection = ServerCon(urlstr, Method.POST , true,true);
            try {
                OutputStream out_stream;
                InputStream in_stream;
                if (urlConnection != null) {
                    out_stream = urlConnection.getOutputStream();
                    OutputStreamWriter out = new OutputStreamWriter(new BufferedOutputStream(out_stream));
                    out.write("data=" + json);
                    out.close();
                    in_stream = urlConnection.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(in_stream));
                    String response;
                    while ((response = in.readLine()) != null) {
                        s.append(response);
                    }
                    in.close();
                    urlConnection.disconnect();
                }else {
                    main_Handler.post(callback::canceled);
                }
            } catch (IOException e) {
                e.printStackTrace();
                main_Handler.post(callback::canceled);
            }
            String finalS = s.toString();
            main_Handler.post(() -> callback.finished(finalS));
        }).start();
    }
    // Executed in an Activity, so 'this' is the Context
// The fileUrl is a string URL, such as "http://www.example.com/image.png"
//    Intent downloadIntent = new Intent(this, DownloadService.class);
//downloadIntent.setData(Uri.parse(fileUrl));
//    startService(downloadIntent);

    public void GetImage(String urlstr, Callback callback) {
        new Thread(() -> {
                Bitmap bmp;
                HttpURLConnection urlConnection = ServerCon(urlstr, Method.POST , false,true);
                try {
                    if (urlConnection != null) {
                        StringBuilder s = new StringBuilder();
                        InputStream in_stream = urlConnection.getInputStream();
                        BufferedReader in = new BufferedReader(new InputStreamReader(in_stream));
                        String response;
                        while ((response = in.readLine()) != null) {
                            s.append(response);
                        }
                        byte[] decoded = Base64.decode(s.toString(),0);
                        bmp = BitmapFactory.decodeByteArray(decoded,0, decoded.length);
                        in.close();
                        urlConnection.disconnect();
                    }else {
                        main_Handler.post(callback::canceled);
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    main_Handler.post(callback::canceled);
                    return;
                }
            Bitmap finalBmp = bmp;
            main_Handler.post(() -> callback.finished(finalBmp));
        }).start();
    }
    //Stream
    public void SendImage(String urlstr,String userToken,IMG_Resize bitmap, Callback callback) {
        new Thread(() -> {
            HttpURLConnection urlConnection = ServerCon(urlstr, Method.POST , true,true);
            StringBuilder s = new StringBuilder();
            try {
                InputStream in_stream;
                if (urlConnection != null) {
                    urlConnection.setRequestProperty("Content-Type","image/png");
                    byte[] fileContents = bitmap.GetStream().toByteArray();
                    String fileContent = android.util.Base64.encodeToString(fileContents, android.util.Base64.DEFAULT);
                    String params = new Crypt().md5("token") + "=" + userToken + "&" + new Crypt().md5("image")  + "=" + fileContent;
                    urlConnection.connect();
                    OutputStreamWriter image = new OutputStreamWriter(urlConnection.getOutputStream()); //Stream
                    image.write(params);
                    image.flush();
                    in_stream = urlConnection.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(in_stream));
                    String response;
                    while ((response = in.readLine()) != null) {
                        s.append(response);
                    }
                    in.close();
                    urlConnection.disconnect();
                }else {
                    main_Handler.post(callback::canceled);
                    System.out.println("no connection");
                }
            } catch (IOException e) {
                e.printStackTrace();
                main_Handler.post(callback::canceled);
            }
            main_Handler.post(() -> callback.finished(s));
        }).start();
    }

    private HttpURLConnection ServerCon(String _url,Method _method,boolean allow_out,boolean allow_in){
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


