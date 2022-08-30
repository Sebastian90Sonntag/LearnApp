package com.graphicdesigncoding.learnapp.forms;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.graphicdesigncoding.learnapp.MainActivity;
import com.graphicdesigncoding.learnapp.R;
import com.graphicdesigncoding.learnapp.databinding.StartupFormBinding;

/////////////////////////////////////
//COPYRIGHT BY GraphicDesignCoding///
/////////////////////////////////////

public class StartUpForm extends Fragment {
    private StartupFormBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = StartupFormBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Check for login data
        Context context = requireActivity();
        SharedPreferences sharedPref = context.getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        view.findViewById(R.id.imageView_app_logo).animate().setDuration(0).alpha(0).start();
        view.findViewById(R.id.imageView_app_logo).animate().setDuration(3000).alpha(1).start();
        view.findViewById(R.id.textview_app_title).animate().setDuration(0).alpha(0).start();
        view.findViewById(R.id.textview_app_title).animate().setStartDelay(1500).setDuration(4000).alpha(1).start();

        view.findViewById(R.id.imageView_app_logo).animate().setStartDelay(2500).setDuration(3000).scaleX((float) 1.15).start();
        view.findViewById(R.id.imageView_app_logo).animate().setStartDelay(2500).setDuration(3000).scaleY((float) 1.15).start();

        view.findViewById(R.id.imageView_gdc).animate().setDuration(5000).rotation(720.0f).start();

        view.findViewById(R.id.imageView_gdc).animate().setStartDelay(1500).setDuration(2500).scaleX((float) 1.5).start();
        view.findViewById(R.id.imageView_gdc).animate().setStartDelay(1500).setDuration(2500).scaleY((float) 1.5).start();

        String unm = sharedPref.getString("UEmail", null);
        String pass = sharedPref.getString("UPassword", null);

        if(unm != null && pass != null) {
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    // Do the task...
                    NavHostFragment.findNavController(StartUpForm.this).navigate(R.id.action_StartUpForm_to_nav_main);
                    handler.removeCallbacks(this);
                }
            };
            handler.postDelayed(runnable, 5000);
        }else{
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    // Do the task...
                    NavHostFragment.findNavController(StartUpForm.this).navigate(R.id.action_StartUpForm_to_nav_login_logout);
                    handler.removeCallbacks(this);
                }
            };
            handler.postDelayed(runnable, 5000);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)requireActivity()).showExtendedBar(false,"", false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
