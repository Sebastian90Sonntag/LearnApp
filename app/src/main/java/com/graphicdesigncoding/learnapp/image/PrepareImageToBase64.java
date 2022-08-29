package com.graphicdesigncoding.learnapp.image;

//COPYRIGHT BY GraphicDesignCoding
public class PrepareImageToBase64 {
    //new Crypt().md5("token") + "=" + userToken + "&" + new Crypt().md5("image")  + "=" + fileContent;
    public String Convert(ImageResize bitmap){
        byte[] fileContents = bitmap.GetStream().toByteArray();
        return android.util.Base64.encodeToString(fileContents, android.util.Base64.DEFAULT);
    }
}
