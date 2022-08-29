package com.graphicdesigncoding.learnapp.forms;

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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.graphicdesigncoding.learnapp.MainActivity;
import com.graphicdesigncoding.learnapp.image.ImageResize;
import com.graphicdesigncoding.learnapp.image.PrepareImageToBase64;
import com.graphicdesigncoding.learnapp.R;
import com.graphicdesigncoding.learnapp.api.CallAPI;
import com.graphicdesigncoding.learnapp.api.Callback;
import com.graphicdesigncoding.learnapp.api.ContentType;
import com.graphicdesigncoding.learnapp.api.Crypt;
import com.graphicdesigncoding.learnapp.api.TransferMethod;
import com.graphicdesigncoding.learnapp.databinding.ProfileFormBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

//COPYRIGHT BY GraphicDesignCoding
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
        MainActivity mA = ((MainActivity)requireContext());
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

            if (!ImageURL.isEmpty() && !ImageURL.contains("null")){
                // Check if Bitmap is in Memory
                if(mA.isBitmapInMemoryCache(ImageURL)){

                    ((ImageView) view.findViewById(R.id.imageView_profil_image)).setImageBitmap(
                            mA.getBitmapFromMemCache(ImageURL)
                    );

                }else{

                    new CallAPI(ImageURL,
                            null,
                            ContentType.TEXT_PLAIN,
                            TransferMethod.POST,
                            new Callback() {
                        @Override
                        public void finished(Object obj) {

                            MainActivity mA = (MainActivity)requireContext();

                            // Load new Image into the Image cache
                            mA.addBitmapToMemoryCache(ImageURL,(Bitmap) obj);
                            // Load new Image into the ImageView
                            ((ImageView) view.findViewById(R.id.imageView_profil_image))
                                .setImageBitmap( mA.getBitmapFromMemCache(ImageURL) );
                            mA.Debug("ProfileForm","Upload Finished");
                        }
                        @Override
                        public void canceled(Object obj) {
                            MainActivity mA = ((MainActivity)requireContext());
                            // Load default Image into the ImageView
                            Bitmap bitmap = mA.getBitmapFromVectorDrawable(
                                                requireContext(),
                                                R.drawable.ic_account_avatar
                            );
                            ((ImageView) view.findViewById(R.id.imageView_profil_image))
                                .setImageBitmap(bitmap);
                            mA.Debug("ProfileForm","Upload Canceled: " + obj.toString());
                        }
                    });
                }

            }else {

                Bitmap bitmap = mA.getBitmapFromVectorDrawable(
                        requireContext(),
                        R.drawable.ic_account_avatar
                );

                ((ImageView) view.findViewById(R.id.imageView_profil_image))
                        .setImageBitmap(bitmap);
                mA.Debug("ProfileForm","Load default image");

            }

        }else{

            mA.Debug("ProfileForm","Profile data empty");

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
                //Uri fullPhotoUri = data.getData();
                // try to decode Image
                try {
                    if (data != null) {
                        bitmap = ImageDecoder.decodeBitmap(
                                ImageDecoder.createSource(requireContext().getContentResolver(), data.getData())
                        );
                    }else{
                        mA.Debug("ProfileForm","Image data is null");
                    }
                } // if Image decoding fails
                catch (IOException e) {
                    e.printStackTrace();
                    mA.Debug("ProfileForm","Image decoding failed");
                }

                // Resize IMG
                ImageResize resizedBMP = new ImageResize(
                        bitmap,
                        ImageResize.PIXEL.X128,
                        ImageResize.QUALITY_PERCENT.P100
                );
                // Set IMG -> ImageView
                ((ImageView) view.findViewById(R.id.imageView_profil_image)).setImageBitmap(resizedBMP.GetBitmap());
                // Send data to server
                SharedPreferences sharedPref1 = ((MainActivity)requireContext()).GetSharedPrefs("LoginData");
                    // Get memorized 'UToken' from sharedPreferences
                String token1 = sharedPref1.getString("UToken",null);
                    // Get Editor from sharedPreferences
                SharedPreferences.Editor editor = sharedPref1.edit();
                // Send Image to Server
                new CallAPI("https://api.graphic-design-coding.de/profile/",
                        new Crypt().md5("token") + "=" + token1 + "&" + new Crypt().md5("image")  + "=" + new PrepareImageToBase64().Convert(resizedBMP),
                        ContentType.TEXT_PLAIN,
                        TransferMethod.POST,
                        new Callback() {

                        @Override
                        public void finished(Object obj) {
                            MainActivity mA = ((MainActivity)requireContext());
                            try {

                                // Get the returned Object from Server request
                                JSONObject jobj = new JSONObject(obj.toString());
                                String _token = jobj.getString(new Crypt().md5("token"));
                                String imgLink = jobj.getString(new Crypt().md5("image_link"));
                                mA.Debug("ProfileForm","Image Upload finished");

                                // replace new Image in Memory Cache if some exist
                                if(mA.isBitmapInMemoryCache(imgLink) && token1.equals(_token)){

                                    mA.removeBitmapFromMemCache(imgLink);
                                    mA.addBitmapToMemoryCache(imgLink, resizedBMP.GetBitmap());
                                    mA.Debug("ProfileForm","updated Image to Memory");
                                    //Set received imgLink to sharedPreferences
                                    editor.putString("UImage", imgLink);
                                    editor.apply();
                                    mA.Debug("ProfileForm","updated Image to shared prefs");

                                }else{

                                    // if no memory Image exist just add it...
                                    mA.addBitmapToMemoryCache(imgLink, resizedBMP.GetBitmap());
                                    mA.Debug("ProfileForm","Inserted Image to Memory");
                                }

                            } catch (JSONException e) {

                                e.printStackTrace();
                                mA.Debug("ProfileForm","JSON Object don't match");
                            }

                            mA.SetControlVisibility(view,R.id.button_send,true);

                        }
                        @Override
                        public void canceled(Object obj) {
                            MainActivity mA = ((MainActivity)requireContext());
                            mA.SetControlVisibility(view,R.id.button_send,true);
                            mA.Debug("ProfileForm",obj.toString());

                        }
                    }
                );

            }else ((MainActivity)requireContext()).SetControlVisibility(view,R.id.button_send, true);

        });

        // Send Btn EVENT
        binding.buttonSend.setOnClickListener((View btn_view) -> {

            ((MainActivity)requireContext()).SetControlVisibility(view,R.id.button_send,false);
            someActivityResultLauncher.launch(pickPhoto);

        });
    }



    @Override
    public void onResume() {

        super.onResume();
        MainActivity mA = ((MainActivity)requireContext());
        mA.showExtendedBar(true,"Profile",true);
    }
    @Override
    public void onDestroyView() {

        super.onDestroyView();
        binding = null;

    }
}
