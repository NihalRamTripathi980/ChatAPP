package com.example.nrtchatsapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.nrtchatsapp.databinding.ActivityViewStoryBinding;

public class ViewStory extends AppCompatActivity {
    ActivityViewStoryBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewStoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}