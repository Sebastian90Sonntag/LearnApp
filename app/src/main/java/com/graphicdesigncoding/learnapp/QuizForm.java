package com.graphicdesigncoding.learnapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.graphicdesigncoding.learnapp.databinding.QuizFormBinding;

public class QuizForm extends Fragment {
    private QuizFormBinding binding;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = QuizFormBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Show Button Binding -> Show Answer
        binding.buttonShow.setOnClickListener((View btn_view) -> {
            LinearLayout ll = view.findViewById(R.id.cardview_content);
            view.findViewById(R.id.button_show).setVisibility(View.GONE);
            ll.animate().setDuration(500).rotationY(90).alpha(0).withEndAction(() -> {
                ll.animate().rotationY(-90).withEndAction(() -> {
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
        LinearLayout ll = view.findViewById(R.id.cardview_content);
        view.findViewById(R.id.button_show).setVisibility(View.GONE);
        ll.animate().setDuration(500).rotationY(90).alpha(0).withEndAction(() -> {
            ll.animate().rotationY(-90).withEndAction(() -> {
                ll.animate().setDuration(500).rotationY(0).alpha(1).withEndAction(() -> {
                    view.findViewById(R.id.button_known).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.button_maybe_known).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.button_dont_known).setVisibility(View.VISIBLE);
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
