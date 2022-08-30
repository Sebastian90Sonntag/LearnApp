package com.graphicdesigncoding.learnapp.forms;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.graphicdesigncoding.learnapp.MainActivity;
import com.graphicdesigncoding.learnapp.R;
import com.graphicdesigncoding.learnapp.api.CallAPI;
import com.graphicdesigncoding.learnapp.api.Callback;
import com.graphicdesigncoding.learnapp.api.ContentType;
import com.graphicdesigncoding.learnapp.api.Crypt;
import com.graphicdesigncoding.learnapp.api.SimpleJson;
import com.graphicdesigncoding.learnapp.api.TransferMethod;
import com.graphicdesigncoding.learnapp.databinding.StartupFormBinding;

import org.json.JSONObject;

/////////////////////////////////////
//COPYRIGHT BY GraphicDesignCoding///
/////////////////////////////////////

public class StartUpForm extends Fragment {
    private StartupFormBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = StartupFormBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Animation
        view.findViewById(R.id.imageView_app_logo).animate().setDuration(0).alpha(0).start();
        view.findViewById(R.id.imageView_app_logo).animate().setDuration(3000).alpha(1).start();
        view.findViewById(R.id.textview_app_title).animate().setDuration(0).alpha(0).start();
        view.findViewById(R.id.textview_app_title).animate().setStartDelay(1500).setDuration(4000).alpha(1).start();

        view.findViewById(R.id.imageView_app_logo).animate().setStartDelay(2500).setDuration(3000).scaleX((float) 1.15).start();
        view.findViewById(R.id.imageView_app_logo).animate().setStartDelay(2500).setDuration(3000).scaleY((float) 1.15).start();

        view.findViewById(R.id.imageView_gdc).animate().setDuration(5000).rotation(720.0f).start();

        view.findViewById(R.id.imageView_gdc).animate().setStartDelay(1500).setDuration(2500).scaleX((float) 1.5).start();
        view.findViewById(R.id.imageView_gdc).animate().setStartDelay(1500).setDuration(2500).scaleY((float) 1.5).start();
        // Check for login data
        Context context = requireActivity();
        SharedPreferences sharedPref = context.getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        String unm = sharedPref.getString("UEmail", null);
        String pass = sharedPref.getString("UPassword", null);

        if (unm != null && pass != null) {
            if (!unm.isEmpty() && !pass.isEmpty()) {
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        // Do the task...
                        new CallAPI(
                                "https://api.graphic-design-coding.de/login",
                                "{\"e\":\"" + unm + "\",\"p\":\"" + pass + "\"}",
                                ContentType.APPLICATION_JSON,
                                TransferMethod.POST,
                                new Callback() {
                                    @Override
                                    public void finished(Object _obj) {

                                        SimpleJson simpleJson = new SimpleJson();
                                        JSONObject obj = simpleJson.Decode(_obj.toString());
                                        Crypt crypt = new Crypt();
                                        String token = crypt.md5("token");
                                        String imgLink = crypt.md5("image_link");
                                        String firstname = crypt.md5("firstname");
                                        String lastname = crypt.md5("lastname");
                                        String username = crypt.md5("username");

                                        if (obj.has(token) && obj.has(imgLink) && obj.has(firstname) &&
                                                obj.has(lastname) && obj.has(username)) {

                                            // Set data to variables from JSONObj
                                            token = simpleJson.Get(obj, token).toString();
                                            imgLink = simpleJson.Get(obj, imgLink).toString();
                                            firstname = simpleJson.Get(obj, firstname).toString();
                                            lastname = simpleJson.Get(obj, lastname).toString();
                                            username = simpleJson.Get(obj, username).toString();

                                            // Check for login data
                                            Context context = requireContext();
                                            SharedPreferences sharedPref;

                                            // Write shared Preferences
                                            sharedPref = context.getSharedPreferences("LoginData", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putString("UFirstname", firstname);
                                            editor.putString("ULastname", lastname);
                                            editor.putString("UUsername", username);
                                            editor.putString("UToken", token);
                                            editor.putString("UImage", imgLink);
                                            editor.apply();

                                            ((MainActivity) context).Debug("LoginForm", "SharedPreferences -> written");

                                            // Go to MainMenu
                                            NavHostFragment.findNavController(StartUpForm.this).navigate(R.id.action_StartUpForm_to_nav_main);

                                            ((MainActivity) context).Debug("LoginForm", "Login -> performed");


                                        }
                                    }

                                    @Override
                                    public void canceled(Object _obj) {
                                        // No connection to server || No internet
                                        SimpleJson simpleJson = new SimpleJson();
                                        JSONObject obj = simpleJson.Decode(_obj.toString());
                                        Crypt crypt = new Crypt();
                                        String error = crypt.md5("error");

                                        if (obj.has(error)) {

                                            String the_error = simpleJson.Get(obj,error).toString();
                                            ((MainActivity) requireActivity()).Debug("LoginForm", the_error);
                                            Toast.makeText(view.getContext(), "Wrong login data", Toast.LENGTH_LONG).show();

                                        } else {

                                            NavHostFragment.findNavController(StartUpForm.this).navigate(R.id.action_StartUpForm_to_nav_login_logout);
                                            ((MainActivity) requireActivity()).Debug("LoginForm", "Server Error");
                                            Toast.makeText(view.getContext(), "Server Error", Toast.LENGTH_LONG).show();

                                        }
                                    }
                                }
                        );
                        handler.removeCallbacks(this);
                    }
                };
                handler.postDelayed(runnable, 5000);
            }} else {
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        // Do the task...
                        NavHostFragment.findNavController(StartUpForm.this).navigate(R.id.action_StartUpForm_to_nav_login_logout);
                        handler.removeCallbacks(this);
                    }
                };
                handler.postDelayed(runnable, 5000);
            }

        }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)requireActivity()).showExtendedBar(false,"", false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
