package com.graphicdesigncoding.learnapp;

import android.graphics.Bitmap;

public class PrepareImageToBase64 {
    //new Crypt().md5("token") + "=" + userToken + "&" + new Crypt().md5("image")  + "=" + fileContent;
    public String Convert(IMG_Resize bitmap){
        byte[] fileContents = bitmap.GetStream().toByteArray();
        return android.util.Base64.encodeToString(fileContents, android.util.Base64.DEFAULT);
    }
}
