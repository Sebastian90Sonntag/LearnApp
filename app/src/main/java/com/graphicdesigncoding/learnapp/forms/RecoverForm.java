package com.graphicdesigncoding.learnapp.forms;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.digest.Md5Crypt;
import com.graphicdesigncoding.learnapp.MainActivity;
import com.graphicdesigncoding.learnapp.R;
import com.graphicdesigncoding.learnapp.api.CallAPI;
import com.graphicdesigncoding.learnapp.api.Callback;
import com.graphicdesigncoding.learnapp.api.ContentType;
import com.graphicdesigncoding.learnapp.api.Crypt;
import com.graphicdesigncoding.learnapp.api.RegExPattern;
import com.graphicdesigncoding.learnapp.api.SimpleJson;
import com.graphicdesigncoding.learnapp.api.TransferMethod;
import com.graphicdesigncoding.learnapp.databinding.RecoverFormBinding;

import org.json.JSONObject;

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

                // Get Input String from Email
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
                    MainActivity mA = ((MainActivity)requireContext());
                    mA.SetControlVisibility(view,R.id.recover_edit_email,false);
                    mA.SetControlVisibility(view,R.id.recover_button_sendcode,false);
                    String email = ((EditText) view.findViewById(R.id.recover_edit_email)).getText().toString();

                    new CallAPI(
                            "https://api.graphic-design-coding.de/login",
                            "{\"e\":\"" + email + "\",\"r\":\"" + new Crypt().md5("recover") + "\"}",
                            ContentType.APPLICATION_JSON,
                            TransferMethod.POST,
                            new Callback() {
                                @Override
                                public void finished(Object _obj) {
                                    SimpleJson simpleJson = new SimpleJson();
                                    JSONObject jobj = simpleJson.Decode(_obj.toString());
                                    if(jobj.has(new Crypt().md5("ok"))) {
                                        mA.SetControlVisibility(view, R.id.recover_button_codecheck, true);
                                        mA.SetControlVisibility(view, R.id.recover_edit_newpassword, true);
                                        mA.SetControlVisibility(view, R.id.recover_edit_repeatpassword, true);
                                        mA.SetControlVisibility(view, R.id.recover_edit_code, true);
                                    }
                                }

                                @Override
                                public void canceled(Object _obj) {

                                    Toast.makeText(view.getContext(), "no valid email", Toast.LENGTH_LONG).show();
                                    mA.SetControlVisibility(view,R.id.recover_edit_email,true);
                                    mA.SetControlVisibility(view,R.id.recover_button_sendcode,true);

                                }
                            }
                    );
                });
        binding.recoverButtonCodecheck.setOnClickListener(btn_view->{
            MainActivity mA = ((MainActivity)requireContext());
            String code = ((EditText) view.findViewById(R.id.recover_edit_code)).getText().toString();
            String password = ((EditText) view.findViewById(R.id.recover_edit_newpassword)).getText().toString();
            String repeatpassword = ((EditText) view.findViewById(R.id.recover_edit_repeatpassword)).getText().toString();

            System.out.println(code);
            System.out.println(password);
            System.out.println(repeatpassword);
            System.out.println("{\"rt\":\"" + code + "\",\"r\":\"" + new Crypt().md5("recoverToken")+"\",\"p\":\"" + password + "\",\"rp\":\"" + repeatpassword +"\"}");
            if (!code.isEmpty() && !password.isEmpty() && !repeatpassword.isEmpty() && password.equals(repeatpassword)){
                mA.SetControlVisibility(view, R.id.recover_button_codecheck, false);
                mA.SetControlVisibility(view, R.id.recover_edit_newpassword, false);
                mA.SetControlVisibility(view, R.id.recover_edit_repeatpassword, false);
                mA.SetControlVisibility(view, R.id.recover_edit_code, false);
                new CallAPI(
                        "https://api.graphic-design-coding.de/login",
                        "{\"rt\":\"" + code + "\",\"r\":\"" + new Crypt().md5("recoverToken")+"\",\"p\":\"" + password + "\",\"rp\":\"" + repeatpassword +"\"}",
                        ContentType.APPLICATION_JSON,
                        TransferMethod.POST,
                        new Callback() {
                            @Override
                            public void finished(Object _obj) {
                                SimpleJson simpleJson = new SimpleJson();
                                JSONObject jobj = simpleJson.Decode(_obj.toString());
                                if (jobj.has(new Crypt().md5("ok"))) {
                                    Toast.makeText(view.getContext(), "password reset success", Toast.LENGTH_LONG).show();
                                    NavHostFragment.findNavController(RecoverForm.this).navigate(R.id.action_recoverForm_to_LoginForm);
                                }
                            }


                            @Override
                            public void canceled(Object _obj) {

                                mA.SetControlVisibility(view,R.id.recover_edit_code,true);
                                mA.SetControlVisibility(view,R.id.recover_button_codecheck,true);
                                mA.SetControlVisibility(view, R.id.recover_edit_newpassword, true);
                                mA.SetControlVisibility(view, R.id.recover_edit_repeatpassword, true);
                                Toast.makeText(view.getContext(), "wrong code", Toast.LENGTH_LONG).show();

                            }
                        }
                );}
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
