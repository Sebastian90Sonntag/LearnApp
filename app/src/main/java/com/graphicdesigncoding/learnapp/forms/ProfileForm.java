package com.graphicdesigncoding.learnapp.forms;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.graphicdesigncoding.learnapp.MainActivity;
import com.graphicdesigncoding.learnapp.R;
import com.graphicdesigncoding.learnapp.api.SessionManager;
import com.graphicdesigncoding.learnapp.databinding.ProfileFormBinding;
import com.graphicdesigncoding.learnapp.image.ImageResize;
import com.graphicdesigncoding.learnapp.repository.Resource;
import com.graphicdesigncoding.learnapp.viewmodel.ProfileViewModel;

import java.io.IOException;

//COPYRIGHT BY GraphicDesignCoding
public class ProfileForm extends Fragment {

    private ProfileFormBinding binding;
    private ProfileViewModel viewModel;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = ProfileFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        MainActivity mA = ((MainActivity) requireContext());
        SessionManager session = viewModel.getSessionManager();

        String username = session.getUsername();
        String email = session.getEmail();
        String ImageURL = session.getImage();

        if (username != null) {
            ((TextView) view.findViewById(R.id.textView_username)).setText(username);
        }
        if (email != null) {
            ((TextView) view.findViewById(R.id.textView_email)).setText(email);
        }

        if (ImageURL != null && !ImageURL.isEmpty() && !ImageURL.contains("null")) {
            if (mA.isBitmapInMemoryCache(ImageURL)) {
                ((ImageView) view.findViewById(R.id.imageView_profil_image)).setImageBitmap(
                        mA.getBitmapFromMemCache(ImageURL)
                );
            }
        } else {
            Bitmap bitmap = mA.getBitmapFromVectorDrawable(requireContext(), R.drawable.ic_account_avatar);
            ((ImageView) view.findViewById(R.id.imageView_profil_image)).setImageBitmap(bitmap);
        }

        viewModel.fetchProfile().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                SessionManager updatedSession = resource.data;
                if (updatedSession.getUsername() != null) {
                    ((TextView) view.findViewById(R.id.textView_username)).setText(updatedSession.getUsername());
                }
                if (updatedSession.getEmail() != null) {
                    ((TextView) view.findViewById(R.id.textView_email)).setText(updatedSession.getEmail());
                }
            }
        });

        Intent pickPhoto = new Intent(Intent.ACTION_PICK);
        pickPhoto.setType("image/*");

        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Bitmap bitmap = null;
                        try {
                            if (data != null && data.getData() != null) {
                                bitmap = ImageDecoder.decodeBitmap(
                                        ImageDecoder.createSource(requireContext().getContentResolver(), data.getData())
                                );
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (bitmap != null) {
                            ImageResize resizedBMP = new ImageResize(bitmap, ImageResize.PIXEL.X128, ImageResize.QUALITY_PERCENT.P100);
                            ((ImageView) view.findViewById(R.id.imageView_profil_image)).setImageBitmap(resizedBMP.GetBitmap());

                            viewModel.uploadAvatar(resizedBMP);
                        }
                    } else {
                        mA.SetControlVisibility(view, R.id.button_send, true);
                    }
                }
        );

        viewModel.getUploadResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            mA.SetControlVisibility(view, R.id.button_send, true);

            if (resource.status == Resource.Status.SUCCESS) {
                mA.Debug("ProfileForm", "Upload finished successfully");
                Toast.makeText(view.getContext(), "Avatar updated", Toast.LENGTH_SHORT).show();
            } else if (resource.status == Resource.Status.ERROR) {
                mA.Debug("ProfileForm", resource.message);
                Toast.makeText(view.getContext(), resource.message != null ? resource.message : "Upload error", Toast.LENGTH_SHORT).show();
            }
        });

        binding.buttonSend.setOnClickListener((View btn_view) -> {
            mA.SetControlVisibility(view, R.id.button_send, false);
            someActivityResultLauncher.launch(pickPhoto);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mA = ((MainActivity) requireContext());
        mA.showExtendedBar(true, "Profile", true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
