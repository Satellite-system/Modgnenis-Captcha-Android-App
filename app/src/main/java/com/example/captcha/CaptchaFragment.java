package com.example.captcha;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private  String hashStr;

    private FragmentCaptchaBinding binding;
    ImageView imageView ;
    String TAG = FirstFragment.class.getSimpleName();
    RequestQueue queue; //

    public CaptchaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CaptchaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CaptchaFragment newInstance(String param1, String param2) {
        CaptchaFragment fragment = new CaptchaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCaptchaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        queue = Volley.newRequestQueue(requireContext());
        imageView =  (ImageView) getView().findViewById(R.id.captchaImgView);

        getCaptcha();
        binding.verifyButton.setOnClickListener(view1 -> {
            verifyCaptca();
        });

    }


    private void getCaptcha(){
        String url = "https://captcha-generator-verifier-api.onrender.com/captcha";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
//                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
//                            Toast.makeText(requireContext(), "Success", Toast.LENGTH_LONG).show();

                            String imageUrl = jsonObject.getString("image");
                            hashStr = jsonObject.getString("hash");

                            Glide.with(requireContext())
                                    .load(imageUrl)
                                    .placeholder(R.drawable.ic_launcher_background)  // Optional placeholder
                                    .error(R.drawable.ic_launcher_foreground)       // Optional error image
                                    .into(imageView);
                        } catch (Exception ex) {
                            Toast.makeText(requireContext(), "Some Error Occured", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "JSON exception: " + ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(requireContext(), "Backend is Down", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Error message: " + error.getMessage());
                    }
                }) ;
        queue.add(request);
    }

    private void verifyCaptca(){
        String url = "https://captcha-generator-verifier-api.onrender.com/captcha";
        String enteredCode = binding.inputText.getText().toString();  // Convert to String

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

//        Toast.makeText(requireContext(), body.toString(), Toast.LENGTH_LONG).show();

        // Create JsonObjectRequest
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
        // Process the JSON response
        boolean verified = response.getBoolean("match");
        if (verified) {
            Toast.makeText(requireContext(), "Verified", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(requireContext(), "Wrong Captcha", Toast.LENGTH_LONG).show();
        }

        Log.d(TAG, response.toString());

    } catch (JSONException ex) {
        // Handle the JSON parsing error
        Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_LONG).show();
        Log.d(TAG, "JSON exception: " + ex.getMessage());
    }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(requireContext(), "Backend is Down", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Error message: " + error.getMessage());
                    }
                });

// Add the request to the queue
        queue.add(request);
    }


}