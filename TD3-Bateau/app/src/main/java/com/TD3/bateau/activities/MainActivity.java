package com.TD3.bateau.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.TD3.bateau.Post;
import com.TD3.bateau.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class    MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
