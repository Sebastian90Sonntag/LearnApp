package com.graphicdesigncoding.learnapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.graphicdesigncoding.learnapp.databinding.ProfileFormBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ProfileForm extends Fragment {
    private ProfileFormBinding binding;
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ){
        // Get ControlBindings
        binding = ProfileFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPref = GetSharedPrefs();
        String token = sharedPref.getString("UToken",null);
        new CallAPI().Post(
                "https://api.graphic-design-coding.de/profile/",
                "{\"t\":\"" + token + "\"}",
                new Callback() {

                    @Override
                    public void finished(Object obj) {

                        JSONObject jobj;

                        try {

                            jobj = new JSONObject(obj.toString());
                            String token = new Crypt().md5("token");
                            String username = new Crypt().md5("username");
                            String lastname = new Crypt().md5("lastname");
                            String firstname = new Crypt().md5("firstname");
                            String email = new Crypt().md5("email");

                            if (    jobj.has(token) &&
                                    jobj.has(username) &&
                                    jobj.has(lastname) &&
                                    jobj.has(firstname) &&
                                    jobj.has(email)){

                                try{

                                    SetTextViewText(view,R.id.textView_username,jobj.get(username).toString());
                                    SetTextViewText(view,R.id.textView_firstname,jobj.get(firstname).toString());
                                    SetTextViewText(view,R.id.textView_lastname,jobj.get(lastname).toString());
                                    SetTextViewText(view,R.id.textView_email,jobj.get(email).toString());
                                }catch (JSONException e){

                                    System.out.println("JSON Exception thrown");
                                }

                            }else{
                                System.out.println("ProfileLoading -> JSONObject doesn't match");
                            }
                        } catch (JSONException e) {

                            System.out.println("ProfileLoading -> JSONObject failed");
                            e.printStackTrace();
                        }
                        SharedPreferences sharedPref = GetSharedPrefs();
                        String key = sharedPref.getString("UImage",null);
                        System.out.println("ImageUpload -> image key: " + key);
                        if (!key.equals("")){
                            // Check if Bitmap is in Memory
                            if(((MainActivity) getActivity()).isBitmapInMemoryCache(key)){

                                ((ImageView) view.findViewById(R.id.imageView_profil_image)).setImageBitmap(((MainActivity) getActivity()).getBitmapFromMemCache(key));
                            }else{

                                new CallAPI().GetImage(key, new Callback() {
                                    @Override
                                    public void finished(Object obj) {
                                        SharedPreferences sharedPref = GetSharedPrefs();
                                        String key = sharedPref.getString("UImage",null);
                                        if (!key.equals("")){
                                            // Load new Image into the ImageView
                                            ((MainActivity) getActivity()).addBitmapToMemoryCache(key,(Bitmap) obj);
                                            ((ImageView) view.findViewById(R.id.imageView_profil_image)).setImageBitmap(((MainActivity) getActivity()).getBitmapFromMemCache(key));
                                        }else{
                                            System.out.println("ImageUpload -> unknown image key => default image loaded");
                                        }
                                    }
                                    @Override
                                    public void canceled() {
                                        // Load default Image into the ImageView
                                        Bitmap bitmap = ((MainActivity)getContext()).getBitmapFromVectorDrawable(getContext(),R.drawable.ic_account_avatar);
                                        ((ImageView) view.findViewById(R.id.imageView_profil_image)).setImageBitmap(bitmap);
                                        System.out.println("ImageUpload -> load default image");
                                    }
                                });
                            }
                        }else {

                            Bitmap bitmap = ((MainActivity)getContext()).getBitmapFromVectorDrawable(getContext(),R.drawable.ic_account_avatar);
                            ((ImageView) view.findViewById(R.id.imageView_profil_image)).setImageBitmap(bitmap);
                            System.out.println("ImageUpload -> load default image");
                        }
                    }
                    @Override
                    public void canceled() {
                    }
                }
        );

        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // Get image data out of google galery
                Intent data = result.getData();
                Bitmap bitmap = null;
                // try to decode Image
                try {

                    bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(view.getContext().getContentResolver(), data.getData()));
                } // if Image decoding fails
                catch (IOException e) {
                    System.out.println("ImageUpload -> image decoding failed...");
                    e.printStackTrace();
                }
                // Resize IMG
                IMG_Resize resizedBMP = new IMG_Resize(bitmap, IMG_Resize.PIXEL.X128, IMG_Resize.QUALITY_PERCENT.X100);
                // Set IMG -> ImageView
                ((ImageView) view.findViewById(R.id.imageView_profil_image)).setImageBitmap(resizedBMP.GetBitmap());
                // Send data to server
                SharedPreferences sharedPref1 = GetSharedPrefs();
                    // Get memorized 'UToken' from sharedPreferences
                String token1 = sharedPref1.getString("UToken",null);
                    // Get Editor from sharedPreferences
                SharedPreferences.Editor editor = sharedPref1.edit();
                // Send Image to Server
                new CallAPI().SendImage("https://api.graphic-design-coding.de/profile/",
                        token1,
                    resizedBMP,
                    new Callback() {

                        @Override
                        public void finished(Object obj) {

                            try {
                                // Get the returned Object from Server request
                                JSONObject jobj = new JSONObject(obj.toString());
                                String _token = jobj.getString(new Crypt().md5("token"));
                                String imgLink = jobj.getString(new Crypt().md5("image_link"));
                                // replace new Image in Memory Cache if some exist
                                if(((MainActivity) getActivity()).isBitmapInMemoryCache(imgLink) && token1.equals(_token)){

                                    ((MainActivity) getActivity()).removeBitmapFromMemCache(imgLink);
                                    ((MainActivity) getActivity()).addBitmapToMemoryCache(imgLink, resizedBMP.GetBitmap());
                                    //Set received imgLink to sharedPreferences
                                    editor.putString("UImage", imgLink);
                                    editor.apply();
                                }else{
                                    // if non memory Image exist just add it...
                                    ((MainActivity) getActivity()).addBitmapToMemoryCache(imgLink, resizedBMP.GetBitmap());
                                }
                            } catch (JSONException e) {
                                System.out.println("ImageUpload -> JSON obj didn't match...");
                                e.printStackTrace();
                            }
                            SetUploadButtonVisibility(view,true);
                        }
                        @Override
                        public void canceled() {
                            SetUploadButtonVisibility(view,true);
                        }
                    }
                );
            }else {
                SetUploadButtonVisibility(view,true);
            }
        });

        // Send Btn EVENT
        binding.buttonSend.setOnClickListener((View btn_view) -> {
            SetUploadButtonVisibility(view,false);
            someActivityResultLauncher.launch(pickPhoto);
        });
    }
    private void SetUploadButtonVisibility(View _view,boolean _bool){

        if (_bool){

            _view.findViewById(R.id.button_send).setVisibility(View.VISIBLE);
            _view.findViewById(R.id.button_send).setEnabled(true);
        }else{

            _view.findViewById(R.id.button_send).setVisibility(View.INVISIBLE);
            _view.findViewById(R.id.button_send).setEnabled(false);
        }
    }
    private void SetTextViewText(View _view,int _id,String _txt){

        TextView txtview = _view.findViewById(_id);
        txtview.setText(_txt);
    }
    private SharedPreferences GetSharedPrefs(){
        SharedPreferences sharedPref;
        sharedPref = getContext().getSharedPreferences("LoginData",getContext().MODE_PRIVATE);
        return sharedPref;
    }
    @Override
    public void onResume() {

        super.onResume();
        ((MainActivity)getActivity()).showExtendedBar(true,"Profile",true);
    }
    @Override
    public void onDestroyView() {

        super.onDestroyView();
        binding = null;
    }
}
