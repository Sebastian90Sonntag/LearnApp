package com.graphicdesigncoding.learnapp.forms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.graphicdesigncoding.learnapp.MainActivity;
import com.graphicdesigncoding.learnapp.R;
import com.graphicdesigncoding.learnapp.databinding.ScoreboardFormBinding;
import com.graphicdesigncoding.learnapp.repository.Resource;
import com.graphicdesigncoding.learnapp.user.CustomArrayAdapter;
import com.graphicdesigncoding.learnapp.user.User;
import com.graphicdesigncoding.learnapp.viewmodel.ScoreboardViewModel;

//COPYRIGHT BY GraphicDesignCoding
public class ScoreboardForm extends Fragment {

    private ScoreboardFormBinding binding;
    private CustomArrayAdapter userArrayAdapter;
    private ScoreboardViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ScoreboardFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = view.findViewById(R.id.listView_scoreboard);
        userArrayAdapter = new CustomArrayAdapter(getContext(), R.layout.listview_row_layout);
        listView.setAdapter(userArrayAdapter);

        viewModel = new ViewModelProvider(this).get(ScoreboardViewModel.class);

        viewModel.getScoreboardResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                userArrayAdapter.clear();
                for (User item : resource.data) {
                    if (item.getUserImg() == null) {
                        item.setUserImg(((MainActivity) requireActivity()).getBitmapFromVectorDrawable(getContext(), R.drawable.ic_account_avatar));
                    }
                    userArrayAdapter.add(item);
                }
            } else if (resource.status == Resource.Status.ERROR) {
                Toast.makeText(view.getContext(), resource.message != null ? resource.message : "Error loading scoreboard", Toast.LENGTH_LONG).show();
            }
        });

        viewModel.loadScoreboard();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mA = ((MainActivity) requireActivity());
        mA.showExtendedBar(true, "Score Board", true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
