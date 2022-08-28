package com.graphicdesigncoding.learnapp.forms;

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

import com.graphicdesigncoding.learnapp.MainActivity;
import com.graphicdesigncoding.learnapp.R;
import com.graphicdesigncoding.learnapp.api.CallAPI;
import com.graphicdesigncoding.learnapp.api.Callback;
import com.graphicdesigncoding.learnapp.api.ContentType;
import com.graphicdesigncoding.learnapp.api.Crypt;
import com.graphicdesigncoding.learnapp.api.RegExPattern;
import com.graphicdesigncoding.learnapp.api.SimpleJson;
import com.graphicdesigncoding.learnapp.api.TransferMethod;
import com.graphicdesigncoding.learnapp.databinding.LoginFormBinding;
import org.json.JSONObject;

//COPYRIGHT BY GraphicDesignCoding
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
                if (!email.isEmpty() && new RegExPattern().Email(email)){

                    et.getBackground().setTint(Color.GREEN);

                }else{

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

                if(!password.isEmpty() && new RegExPattern().Password(password)){

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
                    new CallAPI(
                            "https://api.graphic-design-coding.de/login",
                            "{\"e\":\"" + email + "\",\"p\":\"" + password + "\"}",
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

                                    if (obj.has(token) && obj.has(imgLink) && obj.has(firstname) && obj.has(lastname) && obj.has(username)) {

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
                                        editor.putString("UEmail", email);
                                        editor.putString("UPwd", password);
                                        editor.putString("UFirstname", firstname);
                                        editor.putString("ULastname", lastname);
                                        editor.putString("UUsername", username);
                                        editor.putString("UToken", token);
                                        editor.putString("UImage", imgLink);
                                        editor.apply();
                                        ((MainActivity) context).Debug("LoginForm", "SharedPreferences -> written");

                                        // Go to MainMenu
                                        NavHostFragment.findNavController(LoginForm.this).navigate(R.id.action_global_nav_main);
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

                                        ((MainActivity) requireActivity()).Debug("LoginForm", "Server Error");
                                        Toast.makeText(view.getContext(), "Server Error", Toast.LENGTH_LONG).show();

                                    }
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