package com.graphicdesigncoding.learnapp.forms;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.graphicdesigncoding.learnapp.MainActivity;
import com.graphicdesigncoding.learnapp.R;
import com.graphicdesigncoding.learnapp.api.RegExPattern;
import com.graphicdesigncoding.learnapp.databinding.LoginFormBinding;
import com.graphicdesigncoding.learnapp.repository.Resource;
import com.graphicdesigncoding.learnapp.viewmodel.LoginViewModel;

//COPYRIGHT BY GraphicDesignCoding
public class LoginForm extends Fragment {

    private LoginFormBinding binding;
    private LoginViewModel viewModel;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = LoginFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        binding.editTextPassword.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                EditText et = view.findViewById(R.id.editText_Password);
                new InputChecker().editText(et, s.toString(), RegExPattern.Password);
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

        viewModel.getLoginResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.SUCCESS) {
                ((MainActivity) requireContext()).Debug("LoginForm", "Login -> performed");
                NavHostFragment.findNavController(LoginForm.this).navigate(R.id.action_global_nav_main);
            } else if (resource.status == Resource.Status.ERROR) {
                ((MainActivity) requireActivity()).Debug("LoginForm", resource.message);
                Toast.makeText(view.getContext(), resource.message != null ? resource.message : "Server Error", Toast.LENGTH_LONG).show();
            }
        });

        binding.buttonLogin.setOnClickListener(btn_view -> {
            view.findViewById(R.id.editText_EmailAddress).getBackground().setTint(Color.TRANSPARENT);
            view.findViewById(R.id.editText_Password).getBackground().setTint(Color.TRANSPARENT);

            EditText et_email = view.findViewById(R.id.editText_EmailAddress);
            EditText et_password = view.findViewById(R.id.editText_Password);

            String email = et_email.getText().toString();
            String password = et_password.getText().toString();

            if (new InputChecker().editText(et_email, email, RegExPattern.Email)) {
                if (new InputChecker().editText(et_password, password, RegExPattern.Password)) {
                    viewModel.login(email, password);
                } else {
                    view.findViewById(R.id.editText_Password).requestFocus();
                    Toast.makeText(view.getContext(), "Password must match requirements", Toast.LENGTH_LONG).show();
                }
            } else {
                view.findViewById(R.id.editText_EmailAddress).requestFocus();
                Toast.makeText(view.getContext(), "Email must match requirements", Toast.LENGTH_LONG).show();
            }
        });

        binding.textViewRegister.setOnClickListener(view1 -> {
            Log.i("Login Window", "Register Pressed");
            NavHostFragment.findNavController(LoginForm.this).navigate(R.id.action_LoginForm_to_RegisterForm);
        });

        binding.textViewRecover.setOnClickListener(view1 -> {
            Log.i("Login Window", "Recover Pressed");
            NavHostFragment.findNavController(LoginForm.this).navigate(R.id.action_LoginForm_to_RecoverForm);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = ((MainActivity) getActivity());
        if (activity != null) {
            activity.showExtendedBar(true, "Login", false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}