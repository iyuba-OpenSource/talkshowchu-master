package com.iyuba.wordtest.ui.test;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.iyuba.wordtest.databinding.ActivityWordLineBinding;

/**
 * 单词连线
 */
public class WordLineActivity extends AppCompatActivity {

    private ViewBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWordLineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
