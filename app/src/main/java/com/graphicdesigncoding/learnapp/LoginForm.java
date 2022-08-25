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
        Context context = view.getContext().getApplicationContext();
        super.onViewCreated(view, savedInstanceState);
        //binding.getRoot().findViewById(R.id.imageButton_Account).setVisibility(View.VISIBLE);
        binding.editTextEmailAddress.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                EditText et = view.findViewById(R.id.editText_EmailAddress);
                et.getBackground().setTint(Color.TRANSPARENT);
                String email = s.toString();
                //General Email Regex (RFC 5322 Official Standard)
                if(!email.equals("") && new RegExPattern().Email(email)){
                    et.getBackground().setTint(Color.GREEN);
                }else{
                    et.requestFocus();
                    et.getBackground().setTint(Color.RED);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        binding.editTextPassword.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                EditText et = view.findViewById(R.id.editText_Password);
                et.getBackground().setTint(Color.TRANSPARENT);
                String password = s.toString();
                if(!password.equals("") && new RegExPattern().Password(password)){
                    et.getBackground().setTint(Color.GREEN);
                }else{
                    et.requestFocus();
                    et.getBackground().setTint(Color.RED);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });


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
                                    String token;
                                    String imgLink;
                                    if(obj.has(new Crypt().md5("token")) && obj.has(new Crypt().md5("image_link"))) {
                                        try {
                                            token = obj.get(new Crypt().md5("token")).toString();
                                            imgLink = obj.get(new Crypt().md5("image_link")).toString();
                                            System.out.println(token);
                                            // Check for login data
                                            Context context = getActivity();
                                            SharedPreferences sharedPref;
                                            if (context != null) {
                                                System.out.println("WRITE TO sharedprefs");
                                                // Write shared Preferences
                                                sharedPref = context.getSharedPreferences("LoginData", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPref.edit();
                                                editor.putString("UEmail", email);
                                                editor.putString("UPwd", password);
                                                editor.putString("UToken", token);
                                                editor.putString("UImage", imgLink);
                                                editor.apply();

                                                System.out.println("Nav forwarding...");
                                                // Go to MainMenu
                                                NavHostFragment.findNavController(LoginForm.this).navigate(R.id.action_global_nav_main);
                                            }else{
                                                System.out.println("NO WRITE TO sharedprefs");
                                                // System.out.println("sharedprefs:");
                                                // System.out.println(sharedPref.getString("UEmail",null));
                                                // System.out.println(sharedPref.getString("UPwd",null));
                                                //System.out.println(sharedPref.getString("UToken",null));
                                            }
                                        } catch (JSONException e) {
                                            //User not exist :D
                                            Toast.makeText(view.getContext(),"Wrong login data",Toast.LENGTH_LONG).show();
                                        }
                                    }else if(obj.has(new Crypt().md5("error"))){
                                        String error;
                                        try {
                                            error = obj.getString(new Crypt().md5("error"));
                                            System.out.println("idk1");
                                            System.out.println(error);
                                            Toast.makeText(view.getContext(),"Wrong login data",Toast.LENGTH_LONG).show();
                                        } catch (JSONException e) {
                                            Toast.makeText(view.getContext(),"Service error",Toast.LENGTH_LONG).show();
                                        }
                                    }else{
                                        //User not exist :D
                                        System.out.println("idk3");
                                        Toast.makeText(view.getContext(),"Wrong login data",Toast.LENGTH_LONG).show();
                                    }
                                } catch (Throwable e) {
                                    Toast.makeText(view.getContext(),"Device Service error",Toast.LENGTH_LONG).show();
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

        //Register Link Binding -> Show Register Form
        binding.textViewRegister.setOnClickListener(view1 -> {
            Log.i("Login Window","Register Pressed");
            NavHostFragment.findNavController(LoginForm.this).navigate(R.id.action_LoginForm_to_RegisterForm);
        });

    }
    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).showExtendedBar(true,"Login",false);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}