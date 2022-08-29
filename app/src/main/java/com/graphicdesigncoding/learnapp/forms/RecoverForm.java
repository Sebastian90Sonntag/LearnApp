package com.graphicdesigncoding.learnapp.forms;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.graphicdesigncoding.learnapp.MainActivity;
import com.graphicdesigncoding.learnapp.R;
import com.graphicdesigncoding.learnapp.api.RegExPattern;
import com.graphicdesigncoding.learnapp.databinding.RecoverFormBinding;

//COPYRIGHT BY GraphicDesignCoding
public class RecoverForm extends Fragment {

    private RecoverFormBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = RecoverFormBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        ///////////////////////////////////////////////////////////////////////////////////////////
        // Update View
        super.onViewCreated(view, savedInstanceState);

        ///////////////////////////////////////////////////////////////////////////////////////////
        // Get TextChangedListener from Email TextInput
        binding.recoverEditEmail.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                // Set TintColor to Password TextInput
                EditText et = view.findViewById(R.id.recover_edit_code);
                et.getBackground().setTint(Color.TRANSPARENT);

                // Get Input String from Password
                String email = s.toString();

                if (!email.isEmpty() && new RegExPattern().Email(email)) {

                    // Set TintColor when RegEx succeed
                    et.getBackground().setTint(Color.GREEN);

                } else {

                    // Set TintColor when RegEx failed
                    et.requestFocus();
                    et.getBackground().setTint(Color.RED);

                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        ///////////////////////////////////////////////////////////////////////////////////////////
        binding.recoverButtonSendcode.setOnClickListener(btn_view -> {
        });

    }
    ///////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = ((MainActivity)getActivity());
        if (activity != null){
            activity.showExtendedBar(true,"Recover",true);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
