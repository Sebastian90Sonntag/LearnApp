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
import com.graphicdesigncoding.learnapp.databinding.RecoverFormBinding;
import com.graphicdesigncoding.learnapp.repository.Resource;
import com.graphicdesigncoding.learnapp.viewmodel.RecoverViewModel;

//COPYRIGHT BY GraphicDesignCoding
public class RecoverForm extends Fragment {

    private RecoverFormBinding binding;
    private RecoverViewModel viewModel;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = RecoverFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(RecoverViewModel.class);

        binding.recoverEditEmail.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                EditText et = view.findViewById(R.id.recover_edit_code);
                new InputChecker().editText(et, s.toString(), RegExPattern.Email);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        binding.recoverEditNewpassword.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                EditText et = view.findViewById(R.id.recover_edit_newpassword);
                new InputChecker().editText(et, s.toString(), RegExPattern.Password);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        binding.recoverEditRepeatpassword.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                EditText et = view.findViewById(R.id.recover_edit_repeatpassword);
                new InputChecker().editText(et, s.toString(), RegExPattern.Password);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        viewModel.getResetRequestResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            MainActivity mA = ((MainActivity) requireActivity());
            if (resource.status == Resource.Status.SUCCESS) {
                mA.showExtendedBar(true, "Password Recovery", false);
                mA.SetControlVisibility(view, R.id.recover_button_codecheck, true);
                mA.SetControlVisibility(view, R.id.recover_edit_newpassword, true);
                mA.SetControlVisibility(view, R.id.recover_edit_repeatpassword, true);
                mA.SetControlVisibility(view, R.id.recover_edit_code, true);
                Toast.makeText(view.getContext(), "Recovery code sent", Toast.LENGTH_SHORT).show();
            } else if (resource.status == Resource.Status.ERROR) {
                Toast.makeText(view.getContext(), resource.message != null ? resource.message : "No valid email", Toast.LENGTH_LONG).show();
                mA.SetControlVisibility(view, R.id.recover_edit_email, true);
                mA.SetControlVisibility(view, R.id.recover_button_sendcode, true);
            }
        });

        viewModel.getResetConfirmResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            MainActivity mA = ((MainActivity) requireActivity());
            if (resource.status == Resource.Status.SUCCESS) {
                Toast.makeText(view.getContext(), "Password reset success", Toast.LENGTH_LONG).show();
                NavHostFragment.findNavController(RecoverForm.this).navigate(R.id.action_recoverForm_to_LoginForm);
            } else if (resource.status == Resource.Status.ERROR) {
                mA.SetControlVisibility(view, R.id.recover_edit_code, true);
                mA.SetControlVisibility(view, R.id.recover_button_codecheck, true);
                mA.SetControlVisibility(view, R.id.recover_edit_newpassword, true);
                mA.SetControlVisibility(view, R.id.recover_edit_repeatpassword, true);
                Toast.makeText(view.getContext(), resource.message != null ? resource.message : "Wrong code", Toast.LENGTH_LONG).show();
            }
        });

        binding.recoverButtonSendcode.setOnClickListener(btn_view -> {
            MainActivity mA = ((MainActivity) requireContext());
            mA.SetControlVisibility(view, R.id.recover_edit_email, false);
            mA.SetControlVisibility(view, R.id.recover_button_sendcode, false);
            String email = ((EditText) view.findViewById(R.id.recover_edit_email)).getText().toString();

            viewModel.requestReset(email);
        });

        binding.recoverButtonCodecheck.setOnClickListener(btn_view -> {
            MainActivity mA = ((MainActivity) requireContext());
            String code = ((EditText) view.findViewById(R.id.recover_edit_code)).getText().toString();
            String password = ((EditText) view.findViewById(R.id.recover_edit_newpassword)).getText().toString();
            String repeatpassword = ((EditText) view.findViewById(R.id.recover_edit_repeatpassword)).getText().toString();

            if (!code.isEmpty() && !password.isEmpty() && !repeatpassword.isEmpty() && password.equals(repeatpassword)) {
                mA.SetControlVisibility(view, R.id.recover_button_codecheck, false);
                mA.SetControlVisibility(view, R.id.recover_edit_newpassword, false);
                mA.SetControlVisibility(view, R.id.recover_edit_repeatpassword, false);
                mA.SetControlVisibility(view, R.id.recover_edit_code, false);

                viewModel.confirmReset(code, password, repeatpassword);
            } else {
                Toast.makeText(view.getContext(), "Check input fields", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = ((MainActivity) getActivity());
        if (activity != null) {
            activity.showExtendedBar(true, "Password Recovery", true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
