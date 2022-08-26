package com.graphicdesigncoding.learnapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.graphicdesigncoding.learnapp.databinding.QuizFormBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class QuizForm extends Fragment {
    private QuizFormBinding binding;
    private String CurrentTitle;
    private String CurrentQuestion;
    private String CurrentAnswer;
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = QuizFormBinding.inflate(inflater, container, false);


        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.cardview_content).setVisibility(View.GONE);
        JSONObject jsonObject = ((MainActivity)getActivity()).getUserPreferences();
        try {
            String token = jsonObject.getString("UToken");
            new CallAPI().Post(
                "https://api.graphic-design-coding.de/quiz",
                "{\"t\":\"" + token + "\"\"}",
                new Callback() {
                    @Override
                    public void finished(Object obj) {
                        try {
                            JSONObject jobj = new JSONObject(obj.toString());
                            CurrentTitle = jobj.getString(new Crypt().md5("title"));
                            CurrentQuestion = jobj.getString(new Crypt().md5("question"));
                            CurrentAnswer = jobj.getString(new Crypt().md5("answer"));
                            // Set Strings to Views
                            ((TextView)view.findViewById(R.id.textView_card_title)).setText(CurrentTitle);
                            ((TextView)view.findViewById(R.id.textView_card_content)).setText(CurrentQuestion);
                            view.findViewById(R.id.cardview_content).setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //No Connection Information
                        }
                    }

                    @Override
                    public void canceled() {
                        //No Connection Information
                    }
                }
            );
        } catch (JSONException e) {
            e.printStackTrace();
            //Error Handling User Response
        }
        //Show Button Binding -> Show Answer
        binding.buttonShow.setOnClickListener((View btn_view) -> {
            LinearLayout ll = view.findViewById(R.id.cardview_content);
            view.findViewById(R.id.button_show).setVisibility(View.GONE);
            ll.animate().setDuration(500).rotationY(90).alpha(0).withEndAction(() -> {
                ll.animate().rotationY(-90).withEndAction(() -> {
                    ((TextView)view.findViewById(R.id.textView_card_content)).setText(CurrentAnswer);
                    ll.animate().setDuration(500).rotationY(0).alpha(1).withEndAction(() -> {
                        view.findViewById(R.id.button_known).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.button_maybe_known).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.button_dont_known).setVisibility(View.VISIBLE);
                    });
                });
            }).start();
        });

        //Known Button Binding -> Save and show next Answer
        binding.buttonKnown.setOnClickListener((View btn_view) -> {
            NextQuestion(view);
        });

        //Maybe Known Button Binding -> Save and show next Answer
        binding.buttonMaybeKnown.setOnClickListener((View btn_view) -> {
            NextQuestion(view);
        });

        //Don't Known Button Binding -> Save and show next Answer
        binding.buttonDontKnown.setOnClickListener((View btn_view) -> {
            NextQuestion(view);
        });
    }

    private void NextQuestion(View view){
        view.findViewById(R.id.button_known).setVisibility(View.GONE);
        view.findViewById(R.id.button_maybe_known).setVisibility(View.GONE);
        view.findViewById(R.id.button_dont_known).setVisibility(View.GONE);
        LinearLayout ll = view.findViewById(R.id.cardview_content);
        ll.animate().setDuration(500).rotationY(90).alpha(0).withEndAction(() -> {
            ll.animate().rotationY(-90).withEndAction(() -> {
                ((TextView)view.findViewById(R.id.textView_card_title)).setText(CurrentTitle);
                ((TextView)view.findViewById(R.id.textView_card_content)).setText(CurrentQuestion);
                ll.animate().setDuration(500).rotationY(0).alpha(1).withEndAction(() -> {
                    view.findViewById(R.id.button_show).setVisibility(View.VISIBLE);
                });
            });
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).showExtendedBar(true,"Quiz",true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
