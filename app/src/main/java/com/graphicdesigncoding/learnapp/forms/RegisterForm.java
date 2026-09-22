package com.graphicdesigncoding.learnapp.forms;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.graphicdesigncoding.learnapp.MainActivity;
import com.graphicdesigncoding.learnapp.R;
import com.graphicdesigncoding.learnapp.api.RegExPattern;
import com.graphicdesigncoding.learnapp.databinding.RegisterFormBinding;
import com.graphicdesigncoding.learnapp.repository.Resource;
import com.graphicdesigncoding.learnapp.viewmodel.RegisterViewModel;

//COPYRIGHT BY GraphicDesignCoding
public class RegisterForm extends Fragment {

    private RegisterFormBinding binding;
    private RegisterViewModel viewModel;

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

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        binding.editTextUsername.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                EditText et = view.findViewById(R.id.editText_Username);
                new InputChecker().editText(et, s.toString(), RegExPattern.Name);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        binding.editTextEmailAddress.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                EditText et = view.findViewById(R.id.editText_EmailAddress);
                new InputChecker().editText(et, s.toString(), RegExPattern.Email);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        binding.editTextPassword.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                EditText et = view.findViewById(R.id.editText_Password);
                new InputChecker().editText(et, s.toString(), RegExPattern.Password);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        binding.editTextRepeatPassword.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                EditText et = view.findViewById(R.id.editText_RepeatPassword);
                new InputChecker().editText(et, s.toString(), RegExPattern.Password);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        viewModel.getRegisterResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.SUCCESS) {
                MainActivity mA = (MainActivity) requireActivity();
                mA.Debug("RegisterForm", "Registration Successful");
                NavHostFragment.findNavController(RegisterForm.this).navigate(R.id.action_RegisterForm_to_LoginForm);
                Toast.makeText(view.getContext(), "Registered Successfully", Toast.LENGTH_LONG).show();
            } else if (resource.status == Resource.Status.ERROR) {
                Toast.makeText(view.getContext(), resource.message != null ? resource.message : "Register Error", Toast.LENGTH_LONG).show();
            }
        });

        binding.buttonSend.setOnClickListener((View btn_view) -> {
            String Username = ((EditText) view.findViewById(R.id.editText_Username)).getText().toString();
            String EmailAddress = ((EditText) view.findViewById(R.id.editText_EmailAddress)).getText().toString();
            String Password = ((EditText) view.findViewById(R.id.editText_Password)).getText().toString();
            String RepeatPassword = ((EditText) view.findViewById(R.id.editText_RepeatPassword)).getText().toString();

            if (!Username.isEmpty() && !EmailAddress.isEmpty() && !Password.isEmpty() && !RepeatPassword.isEmpty() && (Password.compareTo(RepeatPassword) == 0)) {
                viewModel.register(Username, EmailAddress, Password, RepeatPassword);
            } else {
                Toast.makeText(view.getContext(), "Please check input fields", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).showExtendedBar(true, "Register", true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
