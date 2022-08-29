package com.graphicdesigncoding.learnapp.image;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.Set;


//COPYRIGHT BY GraphicDesignCoding
public class IMG_Resize {
    private Bitmap bmp;
    private Bitmap resized;
    private ByteArrayOutputStream stream;
    private QUALITY_PERCENT qp;
    private int width;
    private int height;

    public IMG_Resize(Bitmap _bmp, PIXEL size,@Nullable QUALITY_PERCENT qual){
        bmp = _bmp;
        width = size.pixel_value;
        height = size.pixel_value;
        SetDefQuality(qual);
        CompressResize();
    }

    public IMG_Resize(Bitmap _bmp, int _width,int _height,@Nullable QUALITY_PERCENT qual){
        bmp = _bmp;
        this.width = _width;
        this.height = _height;
        SetDefQuality(qual);
        CompressResize();
    }
    public ByteArrayOutputStream GetStream(){
        return stream;
    }

    public Bitmap GetBitmap(){
        return resized;
    }

    private void CompressResize(){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        stream = bytes;
        Bitmap newBmp = Bitmap.createScaledBitmap(bmp,width,height,true);
        //newBmp.compress(GetMimeType(_context,GetUri(_context,bmp)), GetQuality(qp), bytes);
        newBmp.compress(Bitmap.CompressFormat.PNG, qp.quality_value, bytes);
        resized = newBmp;
    }

    private void SetDefQuality(QUALITY_PERCENT _qual){
        if (_qual == null){
            qp = QUALITY_PERCENT.P100;
        }
        else{qp = _qual;}
    }
    public enum PIXEL{
        X16(16),X32(32),X64(64),X128(128),X256(256),X512(512),X1024(1024),X2048(2048),X4096(4096);
        private final int pixel_value;
        PIXEL(int pixel_value) {
            this.pixel_value = pixel_value;
        }
    }
    public enum QUALITY_PERCENT{
        P10(10),P20(20), P30(30), P40(40), P50(50), P60(60), P70(70), P80(80), P90(90), P100(100);
        private final int quality_value;
        QUALITY_PERCENT(int value) {
            this.quality_value = value;
        }
    }
}

