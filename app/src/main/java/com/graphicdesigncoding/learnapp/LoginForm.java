package com.graphicdesigncoding.learnapp;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.graphicdesigncoding.learnapp.databinding.LoginFormBinding;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginForm extends Fragment {

    private LoginFormBinding binding;
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = LoginFormBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        ///////////////////////////////////////////////////////////////////////////////////////////
        // Update View
        super.onViewCreated(view, savedInstanceState);

        ///////////////////////////////////////////////////////////////////////////////////////////
        // Get TextChangedListener from Email TextInput
        binding.editTextEmailAddress.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                // Set TintColor to Email TextInput
                EditText et = view.findViewById(R.id.editText_EmailAddress);
                et.getBackground().setTint(Color.TRANSPARENT);

                // Get Input String from email
                String email = s.toString();

                //General Email Regex (RFC 5322 Official Standard)
                if (!email.equals("") && new RegExPattern().Email(email)){

                    et.getBackground().setTint(Color.GREEN);

                }

                else{

                    et.requestFocus();
                    et.getBackground().setTint(Color.RED);

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });


        ///////////////////////////////////////////////////////////////////////////////////////////
        // Get TextChangedListener from Email TextInput
        binding.editTextPassword.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                // Set TintColor to Password TextInput
                EditText et = view.findViewById(R.id.editText_Password);
                et.getBackground().setTint(Color.TRANSPARENT);

                // Get Input String from Password
                String password = s.toString();

                if(!password.equals("") && new RegExPattern().Password(password)){

                    // Set TintColor when RegEx succeed
                    et.getBackground().setTint(Color.GREEN);

                }else{

                    // Set TintColor when RegEx failed
                    et.requestFocus();
                    et.getBackground().setTint(Color.RED);

                }
            }

            ///////////////////////////////////////////////////////////////////////////////////////////
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            ///////////////////////////////////////////////////////////////////////////////////////////
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

        });


        ///////////////////////////////////////////////////////////////////////////////////////////
        //Login Button Binding -> Send Login Data To Server
        binding.buttonLogin.setOnClickListener(btn_view -> {

            //Set Input tint color
            view.findViewById(R.id.editText_EmailAddress).getBackground().setTint(Color.TRANSPARENT);
            view.findViewById(R.id.editText_Password).getBackground().setTint(Color.TRANSPARENT);

            //Get Input data
            String email = ((EditText) view.findViewById(R.id.editText_EmailAddress)).getText().toString();
            String password = ((EditText) view.findViewById(R.id.editText_Password)).getText().toString();

            //Check if email data matches requirements
            if(new RegExPattern().Email(email)){

                //Check if password matches requirements
                if(new RegExPattern().Password(password)){
                    // Send data to server
                    new CallAPI().Post(
                            "https://api.graphic-design-coding.de/login",
                            "{\"e\":\"" + email + "\",\"p\":\"" + password + "\"}",
                            new Callback() {
                                @Override
                                public void finished(Object _obj) {
                                    JSONObject obj;

                                    try {

                                        obj = new JSONObject(_obj.toString());
                                        String token = new Crypt().md5("token");
                                        String imgLink = new Crypt().md5("image_link");
                                        String firstname = new Crypt().md5("firstname");
                                        String lastname = new Crypt().md5("lastname");
                                        String username = new Crypt().md5("username");
                                        String error = new Crypt().md5("error");

                                        try {

                                            if (obj.has(token) && obj.has(imgLink)) {

                                                // Set data to variables from JSONObj
                                                token = obj.get(token).toString();
                                                imgLink = obj.get(imgLink).toString();
                                                firstname = obj.get(firstname).toString();
                                                lastname = obj.get(lastname).toString();
                                                username = obj.get(username).toString();

                                                // Check for login data
                                                Context context = getActivity();
                                                SharedPreferences sharedPref;

                                                if (context != null) {

                                                    // Write shared Preferences
                                                    sharedPref = context.getSharedPreferences("LoginData", Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPref.edit();
                                                    editor.putString("UEmail", email);
                                                    editor.putString("UPwd", password);
                                                    editor.putString("UFirstname", firstname);
                                                    editor.putString("ULastname", lastname);
                                                    editor.putString("UUsername", username);
                                                    editor.putString("UToken", token);
                                                    editor.putString("UImage", imgLink);
                                                    editor.apply();
                                                    System.out.println("SharedPreferences -> written");


                                                    // Go to MainMenu
                                                    NavHostFragment.findNavController(LoginForm.this).navigate(R.id.action_global_nav_main);
                                                    System.out.println("Login -> performed...");


                                                } else {

                                                    System.out.println("SharedPreferences -> failed to write data.");

                                                }

                                            } else if (obj.has(error)) {

                                                error = obj.getString(error);
                                                System.out.println(error);
                                                System.out.println("Login -> Server Error");
                                                Toast.makeText(view.getContext(), "Server Error", Toast.LENGTH_LONG).show();

                                            } else {

                                                System.out.println("Login -> Wrong login data");
                                                Toast.makeText(view.getContext(), "Wrong login data", Toast.LENGTH_LONG).show();

                                            }

                                        } catch (JSONException e) {

                                            System.out.println("Login -> JSONObject didn't match required");
                                            Toast.makeText(view.getContext(), "Server Error", Toast.LENGTH_LONG).show();

                                        }

                                    } catch (Throwable e) {

                                        Toast.makeText(view.getContext(), "Device Service error", Toast.LENGTH_LONG).show();

                                    }
                                }

                                @Override
                                public void canceled() {
                                    // No connection to server || No internet
                                    Toast.makeText(view.getContext(),"No Connection to Server",Toast.LENGTH_LONG).show();
                                }
                            }
                    );

                }else{
                    //Set ViewInput to red
                    view.findViewById(R.id.editText_Password).requestFocus();
                    view.findViewById(R.id.editText_Password).getBackground().setTint(Color.RED);
                    Toast.makeText(view.getContext(),"Password must match",Toast.LENGTH_LONG).show();
                }
            }else{
                //Set ViewInput to red
                view.findViewById(R.id.editText_EmailAddress).requestFocus();
                view.findViewById(R.id.editText_EmailAddress).getBackground().setTint(Color.RED);
                Toast.makeText(view.getContext(),"Email must match",Toast.LENGTH_LONG).show();
            }
        });

        ///////////////////////////////////////////////////////////////////////////////////////////
        //Register Link Binding -> Show Register Form
        binding.textViewRegister.setOnClickListener(view1 -> {
            Log.i("Login Window","Register Pressed");
            NavHostFragment.findNavController(LoginForm.this).navigate(R.id.action_LoginForm_to_RegisterForm);

        });

    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = ((MainActivity)getActivity());
        if (activity != null){
            activity.showExtendedBar(true,"Login",false);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}