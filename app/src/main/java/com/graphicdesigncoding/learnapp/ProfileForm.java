package com.graphicdesigncoding.learnapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.graphicdesigncoding.learnapp.databinding.ProfileFormBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ProfileForm extends Fragment
{
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

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        MainActivity mA = ((MainActivity)getContext());
        SharedPreferences sPref = mA.GetSharedPrefs("LoginData");
        String token = sPref.getString("UToken",null);
        String username = sPref.getString("UUsername",null);
        String lastname = sPref.getString("ULastname",null);
        String firstname = sPref.getString("UFirstname",null);
        String email = sPref.getString("UEmail",null);
        String ImageURL = sPref.getString("UImage",null);

        if (!(token.isEmpty() && username.isEmpty() && lastname.isEmpty() &&
                firstname.isEmpty() && email.isEmpty()))
        {
            ((TextView) view.findViewById( R.id.textView_email )).setText(username);
            ((TextView) view.findViewById(R.id.textView_firstname)).setText(firstname);
            ((TextView) view.findViewById(R.id.textView_lastname)).setText(lastname);
            ((TextView) view.findViewById(R.id.textView_username)).setText(username);

            if (!ImageURL.isEmpty()){
                // Check if Bitmap is in Memory
                if(mA.isBitmapInMemoryCache(ImageURL)){

                    ((ImageView) view.findViewById(R.id.imageView_profil_image)).setImageBitmap(
                            mA.getBitmapFromMemCache(ImageURL)
                    );

                }else{

                    new CallAPI().GetImage(ImageURL, new Callback() {
                        @Override
                        public void finished(Object obj) {

                            MainActivity mA = (MainActivity)getContext();
                            SharedPreferences sPref = mA.GetSharedPrefs("LoginData");
                            String ImageURL = sPref.getString("UImage",null);

                            if (!ImageURL.isEmpty()){

                                // Load new Image into the ImageView
                                mA.addBitmapToMemoryCache(ImageURL,(Bitmap) obj);
                                ((ImageView) view.findViewById(R.id.imageView_profil_image))
                                .setImageBitmap( mA.getBitmapFromMemCache(ImageURL) );

                            }else{

                                System.out.println("ImageUpload -> unknown image key => default image loaded");
                            }
                        }
                        @Override
                        public void canceled() {
                            MainActivity mA = ((MainActivity)getContext());
                            // Load default Image into the ImageView
                            Bitmap bitmap = mA.getBitmapFromVectorDrawable(
                                                getContext(),
                                                R.drawable.ic_account_avatar
                            );
                            ((ImageView) view.findViewById(R.id.imageView_profil_image))
                                .setImageBitmap(bitmap);
                            System.out.println("ImageUpload -> load default image");
                        }
                    });
                }

            }else {

                Bitmap bitmap = mA.getBitmapFromVectorDrawable(
                        getContext(),
                        R.drawable.ic_account_avatar
                );

                ((ImageView) view.findViewById(R.id.imageView_profil_image))
                        .setImageBitmap(bitmap);

                System.out.println("ImageUpload -> load default image");

            }

        }else{

            System.out.println("ProfileLoading -> Objects doesn't match");

        }
        //Define Intent for Image picking:
        // An Intent is a messaging object you can use to request an action from another app component.
        Intent pickPhoto = new Intent(Intent.ACTION_PICK);
        pickPhoto.setType("image/*");

        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {

            if (result.getResultCode() == Activity.RESULT_OK) {

                // Get image data out of google gallery
                Intent data = result.getData();
                Bitmap bitmap = null;
                Uri fullPhotoUri = data.getData();
                // try to decode Image
                try {
                    bitmap = ImageDecoder.decodeBitmap(
                            ImageDecoder.createSource(getContext().getContentResolver(), data.getData())
                    );
                } // if Image decoding fails
                catch (IOException e) {
                    System.out.println("ImageUpload -> image decoding failed...");
                    e.printStackTrace();
                }

                // Resize IMG
                IMG_Resize resizedBMP = new IMG_Resize(
                        bitmap,
                        IMG_Resize.PIXEL.X128,
                        IMG_Resize.QUALITY_PERCENT.X100
                );
                // Set IMG -> ImageView
                ((ImageView) view.findViewById(R.id.imageView_profil_image)).setImageBitmap(resizedBMP.GetBitmap());
                // Send data to server
                SharedPreferences sharedPref1 = ((MainActivity)getContext()).GetSharedPrefs("LoginData");
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
                                System.out.println("ImageUpload -> Done");

                                // replace new Image in Memory Cache if some exist
                                if(((MainActivity) getActivity()).isBitmapInMemoryCache(imgLink) && token1.equals(_token)){
                                    ((MainActivity) getActivity()).removeBitmapFromMemCache(imgLink);
                                    ((MainActivity) getActivity()).addBitmapToMemoryCache(imgLink, resizedBMP.GetBitmap());
                                    System.out.println("ImageUpload -> updated Image to Profile");
                                    //Set received imgLink to sharedPreferences
                                    editor.putString("UImage", imgLink);
                                    editor.apply();
                                    System.out.println("SharedPreferences -> updated Image");
                                }else{
                                    // if no memory Image exist just add it...
                                    ((MainActivity) getActivity()).addBitmapToMemoryCache(imgLink, resizedBMP.GetBitmap());
                                    System.out.println("SharedPreferences -> added Image");
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
