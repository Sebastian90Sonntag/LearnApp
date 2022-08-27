package com.graphicdesigncoding.learnapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.AccessControlContext;
import java.security.AccessController;


//COPYRIGHT BY GraphicDesignCoding
public class IMG_Resize {
    Bitmap bmp;
    Bitmap resized;
    ByteArrayOutputStream stream;
    QUALITY_PERCENT qp;
    int width;
    int height;
    public enum QUALITY_PERCENT{X10,X20,X30,X40,X50,X60,X70,X80,X90,X100}
    public enum PIXEL{X16,X32,X64,X128,X256,X512,X1024}

    public IMG_Resize(Bitmap _bmp, PIXEL size,@Nullable QUALITY_PERCENT qual){
        SetContext();
        bmp = _bmp;
        width = GetPixel(size);
        height = GetPixel(size);
        if (qual == null){
            qual = QUALITY_PERCENT.X100;
        }
        qp = qual;
        CompressResize(width,height);
    }
    public IMG_Resize(Bitmap _bmp, int _width,int _height,@Nullable QUALITY_PERCENT qual){
        SetContext();
        bmp = _bmp;
        if (qual == null){
            qual = QUALITY_PERCENT.X100;
        }
        qp = qual;
        CompressResize(_width,_height);
    }
    public ByteArrayOutputStream GetStream(){
        return stream;
    }
    public Bitmap GetBitmap(){
        return resized;
    }
    private void SetContext(){

    }
    private void CompressResize( int width,int height){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        stream = bytes;
        Bitmap newBmp = Bitmap.createScaledBitmap(bmp,width,height,true);
        //newBmp.compress(GetMimeType(_context,GetUri(_context,bmp)), GetQuality(qp), bytes);
        newBmp.compress(Bitmap.CompressFormat.PNG, GetQuality(qp), bytes);
        resized = newBmp;
    }
    private int GetQuality(QUALITY_PERCENT qual) {
        switch(qual){
            case X10:
                return 10;
            case X20:
                return 20;
            case X30:
                return 30;
            case X40:
                return 40;
            case X50:
                return 50;
            case X60:
                return 60;
            case X70:
                return 70;
            case X80:
                return 80;
            case X90:
                return 90;
            case X100:
                return 100;
        }
        throw new RuntimeException("Stub!");
    }

    private int GetPixel(PIXEL _in){
        switch (_in) {
            case X16:
                return 16;
            case X32:
                return 32;
            case X64:
                return 64;
            case X128:
                return 128;
            case X256:
                return 256;
            case X512:
                return 512;
            case X1024:
                return 1024;
        }
        throw new RuntimeException("Stub!");
    }
/*
public Bitmap.CompressFormat GetMimeType(Context _context, Uri _uriImage)
{
    String strMimeType;
    Cursor cursor = _context.getContentResolver().query(_uriImage, new String[] { MediaStore.MediaColumns.MIME_TYPE }, null, null, null);

    if (cursor != null && cursor.moveToNext()) {
        strMimeType = cursor.getString(0);
        switch (strMimeType) {
            case "JPEG":
                return Bitmap.CompressFormat.JPEG;
            case "PNG":
                return Bitmap.CompressFormat.PNG;
            case "WEBP":
                return Bitmap.CompressFormat.WEBP;
        }
    }else {
        throw new RuntimeException("Stub!");
    }
    throw new RuntimeException("Stub!");
}
    public Uri GetUri(Context context, Bitmap inImage) {

        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);

        return Uri.parse(path);
    }
    public static Uri GetUri(Context inContext, Object ResID ) {
        String pkgName = inContext.getApplicationContext().getPackageName();
        Uri path = Uri.parse("android.resource://"+pkgName+"/" + ResID);
        return path;
    }
    public static Uri GetUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }*/
}

