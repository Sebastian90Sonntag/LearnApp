package com.graphicdesigncoding.learnapp.forms;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.graphicdesigncoding.learnapp.MainActivity;
import com.graphicdesigncoding.learnapp.R;
import com.graphicdesigncoding.learnapp.databinding.MainmenuFormBinding;

/////////////////////////////////////
//COPYRIGHT BY GraphicDesignCoding///
/////////////////////////////////////

public class MainMenuForm extends Fragment {

    private MainmenuFormBinding binding;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = MainmenuFormBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.button_quiz).animate().setDuration(0).alpha(0).start();
        view.findViewById(R.id.button_scoreboard).animate().setDuration(0).alpha(0).start();
        view.findViewById(R.id.button_profil).animate().setDuration(0).alpha(0).start();
        view.findViewById(R.id.button_quit).animate().setDuration(0).alpha(0).start();
        view.findViewById(R.id.button_quiz).animate().setStartDelay(200).setDuration(400).alpha(1).start();
        view.findViewById(R.id.button_scoreboard).animate().setStartDelay(400).setDuration(600).alpha(1).start();
        view.findViewById(R.id.button_profil).animate().setStartDelay(600).setDuration(800).alpha(1).start();
        view.findViewById(R.id.button_quit).animate().setStartDelay(800).setDuration(1000).alpha(1).start();

        //Quiz Button Binding -> Open Quiz
        binding.buttonQuiz.setOnClickListener(btn_view -> {
            NavHostFragment.findNavController(MainMenuForm.this).navigate(R.id.action_MainMenuForm_to_QuizForm);
            System.out.println("Login QuizForm...");
        });

        //Scoreboard Button Binding -> Open Scoreboard
        binding.buttonScoreboard.setOnClickListener(btn_view -> {
            NavHostFragment.findNavController(MainMenuForm.this).navigate(R.id.action_MainMenuForm_to_ScoreboardForm);
            System.out.println("Nav to ScoreboardForm...");
        });

        //Profile Button Binding -> Open Profile
        binding.buttonProfil.setOnClickListener(btn_view -> {
            NavHostFragment.findNavController(MainMenuForm.this).navigate(R.id.action_MainMenuForm_to_ProfileForm);
            System.out.println("Nav to ProfileForm...");
        });

        //Quit Button Binding -> Quit Application
        binding.buttonQuit.setOnClickListener(btn_view -> {
            System.out.println("Quit Application...");
            getActivity().finish();
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).showExtendedBar(true,"Main Menu",false);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
