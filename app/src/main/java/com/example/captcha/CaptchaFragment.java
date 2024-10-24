package com.example.captcha;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.example.captcha.databinding.FragmentCaptchaBinding;
import com.example.captcha.databinding.FragmentFirstBinding;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CaptchaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CaptchaFragment extends Fragment {

        private FragmentCaptchaBinding binding;
        private String hashStr;
        private RequestQueue queue;
        private String TAG = CaptchaFragment.class.getSimpleName();

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentCaptchaBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

        @Override
        public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            queue = Volley.newRequestQueue(requireContext());

            // Initially hide input field and verify button, show progress bar
            binding.inputText.setVisibility(View.GONE);
            binding.verifyButton.setVisibility(View.GONE);
            binding.refreshButton.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);

            getCaptcha();

            binding.verifyButton.setOnClickListener(view1 -> verifyCaptca());

            // Retry API call on refresh button click
            binding.refreshButton.setOnClickListener(view12 -> {
                binding.refreshButton.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.VISIBLE);
                getCaptcha();
            });


        }

        private void getCaptcha() {
            String url = "https://captcha-generator-verifier-api.onrender.com/captcha";
            StringRequest request = new StringRequest(Request.Method.GET, url,
                    response -> {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String imageUrl = jsonObject.getString("image");
                            hashStr = jsonObject.getString("hash");

                            // Load captcha image using Glide
                            Glide.with(requireContext())
                                    .load(imageUrl)
                                    .placeholder(R.drawable.ic_launcher_background)  // Optional placeholder
                                    .error(R.drawable.ic_launcher_foreground)       // Optional error image
                                    .into(binding.captchaImgView);

                            // Once image is loaded, show input field and verify button, hide progress bar
                            binding.inputText.setVisibility(View.VISIBLE);
                            binding.verifyButton.setVisibility(View.VISIBLE);
                            binding.progressBar.setVisibility(View.GONE);
                        } catch (Exception ex) {
                            showError();
                            Log.d(TAG, "JSON exception: " + ex.getMessage());
                        }
                    },
                    error -> {
                        showError();
                        Log.d(TAG, "Error message: " + error.getMessage());
                    });

            queue.add(request);
        }

        private void verifyCaptca() {
            String url = "https://captcha-generator-verifier-api.onrender.com/captcha";
            String enteredCode = binding.inputText.getText().toString();

            // Create JSON body
            JSONObject body = new JSONObject();
            try {
                body.put("captcha", enteredCode);
                body.put("hash", hashStr);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Error in Json Formatting", Toast.LENGTH_LONG).show();
                return;
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body,
                    response -> {
                        try {
                            boolean verified = response.getBoolean("match");
                            if (verified) {
                                Toast.makeText(requireContext(), "Verified", Toast.LENGTH_LONG).show();
                                // Initially hide input field and verify button, show progress bar
                                binding.inputText.setText("");
                                binding.inputText.setVisibility(View.GONE);
                                binding.verifyButton.setVisibility(View.GONE);
                                binding.refreshButton.setVisibility(View.GONE);
                                binding.progressBar.setVisibility(View.VISIBLE);

                                getCaptcha();
                            } else {
                                Toast.makeText(requireContext(), "Wrong Captcha", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException ex) {
                            Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "JSON exception: " + ex.getMessage());
                        }
                    },
                    error -> {
                        Toast.makeText(requireContext(), "Backend is Down", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Error message: " + error.getMessage());
                    });

            queue.add(request);
        }

        private void showError() {
            // Show refresh button, hide other views and progress bar
            binding.inputText.setVisibility(View.GONE);
            binding.verifyButton.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
            binding.refreshButton.setVisibility(View.VISIBLE);
            Toast.makeText(requireContext(), "Failed to load captcha", Toast.LENGTH_LONG).show();
        }

}