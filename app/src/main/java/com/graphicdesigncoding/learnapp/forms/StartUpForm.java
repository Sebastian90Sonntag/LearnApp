package com.graphicdesigncoding.learnapp.forms;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.graphicdesigncoding.learnapp.MainActivity;
import com.graphicdesigncoding.learnapp.R;
import com.graphicdesigncoding.learnapp.api.SessionManager;
import com.graphicdesigncoding.learnapp.databinding.StartupFormBinding;
import com.graphicdesigncoding.learnapp.repository.AuthRepository;
import com.graphicdesigncoding.learnapp.repository.Resource;

//COPYRIGHT BY GraphicDesignCoding
public class StartUpForm extends Fragment {
    private StartupFormBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = StartupFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Animations
        view.findViewById(R.id.imageView_app_logo).animate().setDuration(0).alpha(0).start();
        view.findViewById(R.id.imageView_app_logo).animate().setDuration(3000).alpha(1).start();
        view.findViewById(R.id.textview_app_title).animate().setDuration(0).alpha(0).start();
        view.findViewById(R.id.textview_app_title).animate().setStartDelay(1500).setDuration(4000).alpha(1).start();

        view.findViewById(R.id.imageView_app_logo).animate().setStartDelay(2500).setDuration(3000).scaleX((float) 1.15).start();
        view.findViewById(R.id.imageView_app_logo).animate().setStartDelay(2500).setDuration(3000).scaleY((float) 1.15).start();

        view.findViewById(R.id.imageView_gdc).animate().setDuration(5000).rotation(720.0f).start();

        view.findViewById(R.id.imageView_gdc).animate().setStartDelay(1500).setDuration(2500).scaleX((float) 1.5).start();
        view.findViewById(R.id.imageView_gdc).animate().setStartDelay(1500).setDuration(2500).scaleY((float) 1.5).start();

        // Check for login data
        SessionManager sessionManager = new SessionManager(requireContext());
        String unm = sessionManager.getEmail();
        String pass = sessionManager.getPassword();

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable;

        if (unm != null && pass != null) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    AuthRepository authRepository = new AuthRepository(requireContext());
                    authRepository.login(unm, pass).observe(getViewLifecycleOwner(), resource -> {
                        if (resource == null) return;

                        if (resource.status == Resource.Status.SUCCESS) {
                            ((MainActivity) requireContext()).Debug("StartUpForm", "Auto Login -> success");
                            NavHostFragment.findNavController(StartUpForm.this).navigate(R.id.action_StartUpForm_to_nav_main);
                        } else if (resource.status == Resource.Status.ERROR) {
                            ((MainActivity) requireActivity()).Debug("StartUpForm", "Auto Login -> failed");
                            NavHostFragment.findNavController(StartUpForm.this).navigate(R.id.action_StartUpForm_to_nav_login_logout);
                        }
                    });
                    handler.removeCallbacks(this);
                }
            };
        } else {
            runnable = new Runnable() {
                @Override
                public void run() {
                    NavHostFragment.findNavController(StartUpForm.this).navigate(R.id.action_StartUpForm_to_nav_login_logout);
                    handler.removeCallbacks(this);
                }
            };
        }
        handler.postDelayed(runnable, 4000);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).showExtendedBar(false, "", false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
