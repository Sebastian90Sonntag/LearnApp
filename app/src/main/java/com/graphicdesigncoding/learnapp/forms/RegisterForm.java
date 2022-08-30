package com.graphicdesigncoding.learnapp.forms;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.graphicdesigncoding.learnapp.api.RegExPattern;
import com.graphicdesigncoding.learnapp.api.TransferMethod;
import com.graphicdesigncoding.learnapp.databinding.RegisterFormBinding;

/////////////////////////////////////
//COPYRIGHT BY GraphicDesignCoding///
/////////////////////////////////////

public class RegisterForm  extends Fragment {

    private RegisterFormBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = RegisterFormBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // Username Text EVENT
        binding.editTextUsername.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                EditText et = view.findViewById(R.id.editText_Username);
                new InputChecker().editText(et,s.toString(),RegExPattern.Name);

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        // Firstname Text EVENT
        binding.editTextFirstName.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                EditText et = view.findViewById(R.id.editText_FirstName);
                new InputChecker().editText(et,s.toString(),RegExPattern.Name);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}

        });

        // LastName Text EVENT
        binding.editTextLastName.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                EditText et = view.findViewById(R.id.editText_LastName);
                new InputChecker().editText(et,s.toString(),RegExPattern.Name);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}

        });

        // Email Text EVENT
        binding.editTextEmailAddress.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                EditText et = view.findViewById(R.id.editText_EmailAddress);
                new InputChecker().editText(et,s.toString(),RegExPattern.Email);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        // Password Text EVENT
        binding.editTextPassword.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                EditText et = view.findViewById(R.id.editText_Password);
                new InputChecker().editText(et,s.toString(),RegExPattern.Password);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        // RepeatPassword Text EVENT
        binding.editTextRepeatPassword.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                EditText et = view.findViewById(R.id.editText_RepeatPassword);
                new InputChecker().editText(et,s.toString(),RegExPattern.Password);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        // Send Btn EVENT
        binding.buttonSend.setOnClickListener((View btn_view) -> {

            String Username = ((EditText)view.findViewById(R.id.editText_Username)).getText().toString();
            String FirstName = ((EditText)view.findViewById(R.id.editText_FirstName)).getText().toString();
            String LastName = ((EditText)view.findViewById(R.id.editText_LastName)).getText().toString();
            String EmailAddress = ((EditText)view.findViewById(R.id.editText_EmailAddress)).getText().toString();
            String Password = ((EditText)view.findViewById(R.id.editText_Password)).getText().toString();
            String RepeatPassword = ((EditText)view.findViewById(R.id.editText_RepeatPassword)).getText().toString();

            if(!Username.isEmpty() && !FirstName.isEmpty() && !LastName.isEmpty() && !EmailAddress.isEmpty() && !Password.isEmpty() && !RepeatPassword.isEmpty() && (Password.compareTo(RepeatPassword) == 0)){
                // Send data to server
                new CallAPI(
                        "https://api.graphic-design-coding.de/register",
                        "{  \"u\":\"" + Username + "\"," +
                                "\"f\":\"" + FirstName + "\"," +
                                "\"l\":\"" + LastName + "\"," +
                                "\"e\":\"" + EmailAddress + "\"," +
                                "\"p\":\"" + Password + "\","+
                                "\"rp\":\"" + RepeatPassword + "\"" +
                                "}",
                        ContentType.APPLICATION_JSON,
                        TransferMethod.POST,

                        new Callback() {

                            @Override
                            public void finished(Object obj) {
                                MainActivity mA = (MainActivity) requireActivity();
                                mA.Debug("RegisterForm",obj.toString());
                                NavHostFragment.findNavController(RegisterForm.this).navigate(R.id.action_RegisterForm_to_LoginForm);
                                Toast.makeText(view.getContext(),"Registered",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void canceled(Object obj) {
                                Toast.makeText(view.getContext(),"Register Error",Toast.LENGTH_LONG).show();
                            }
                        }
                );
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)requireActivity()).showExtendedBar(true,"Register",true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
