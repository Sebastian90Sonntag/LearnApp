package com.graphicdesigncoding.learnapp.forms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.graphicdesigncoding.learnapp.MainActivity;
import com.graphicdesigncoding.learnapp.R;
import com.graphicdesigncoding.learnapp.databinding.QuizFormBinding;
import com.graphicdesigncoding.learnapp.repository.QuizRepository;
import com.graphicdesigncoding.learnapp.repository.Resource;
import com.graphicdesigncoding.learnapp.viewmodel.QuizViewModel;

//COPYRIGHT BY GraphicDesignCoding
public class QuizForm extends Fragment {

    private QuizFormBinding binding;
    private QuizViewModel viewModel;
    private QuizRepository.QuizQuestion currentQuestionData;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = QuizFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.cardview_content).setVisibility(View.GONE);
        viewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        viewModel.getCurrentQuestion().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                currentQuestionData = resource.data;
                ((TextView) view.findViewById(R.id.textView_card_title)).setText(currentQuestionData.title);
                ((TextView) view.findViewById(R.id.textView_card_content)).setText(currentQuestionData.question);

                LinearLayout ll = view.findViewById(R.id.cardview_content);
                ll.setVisibility(View.VISIBLE);
                view.findViewById(R.id.button_show).setVisibility(View.VISIBLE);

                // Complete the fade-in and rotation reset for the next question card
                ll.animate().setDuration(400).rotationY(0).alpha(1.0f).start();

            } else if (resource.status == Resource.Status.ERROR) {
                MainActivity mA = (MainActivity) requireActivity();
                mA.Debug("QuizForm", resource.message != null ? resource.message : "Error fetching question");
            }
        });

        viewModel.loadNextQuestion();

        binding.buttonShow.setOnClickListener((View btn_view) -> {
            if (currentQuestionData == null) return;
            LinearLayout ll = view.findViewById(R.id.cardview_content);
            view.findViewById(R.id.button_show).setVisibility(View.GONE);
            ll.animate().setDuration(500).rotationY(90).alpha(0).withEndAction(() -> {
                ll.animate().rotationY(-90).withEndAction(() -> {
                    ((TextView) view.findViewById(R.id.textView_card_content)).setText(currentQuestionData.answer);
                    ll.animate().setDuration(500).rotationY(0).alpha(1).withEndAction(() -> {
                        view.findViewById(R.id.button_known).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.button_maybe_known).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.button_dont_known).setVisibility(View.VISIBLE);
                    });
                });
            }).start();
        });

        binding.buttonKnown.setOnClickListener((View btn_view) -> NextQuestion(view, 1));
        binding.buttonMaybeKnown.setOnClickListener((View btn_view) -> NextQuestion(view, 2));
        binding.buttonDontKnown.setOnClickListener((View btn_view) -> NextQuestion(view, 3));
    }

    private void NextQuestion(View view, int statusId) {
        if (currentQuestionData == null) return;

        view.findViewById(R.id.button_known).setVisibility(View.GONE);
        view.findViewById(R.id.button_maybe_known).setVisibility(View.GONE);
        view.findViewById(R.id.button_dont_known).setVisibility(View.GONE);

        LinearLayout ll = view.findViewById(R.id.cardview_content);
        ll.animate().setDuration(500).rotationY(90).alpha(0).withEndAction(() -> {
            ll.animate().rotationY(-90).withEndAction(() -> {
                viewModel.submitRating(currentQuestionData.id, statusId);
            });
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).showExtendedBar(true, "Quiz", true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
