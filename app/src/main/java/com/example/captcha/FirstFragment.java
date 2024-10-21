package com.example.captcha;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.captcha.databinding.FragmentFirstBinding;

import com.android.volley.RequestQueue;

import org.json.JSONObject;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    String TAG = FirstFragment.class.getSimpleName();
    RequestQueue queue; // = Volley.newRequestQueue(requireContext());


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(v ->
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment)
        );

        binding.buttonCaptcha.setOnClickListener(v ->
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_CaptchaFragment)
        );

        queue = Volley.newRequestQueue(requireContext());

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}