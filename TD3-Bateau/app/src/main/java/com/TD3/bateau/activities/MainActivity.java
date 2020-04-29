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

public class MainActivity extends AppCompatActivity {
    List<Post> postList = new ArrayList<>();
    public static SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPrefs = getPreferences(MODE_PRIVATE);

        Gson gson = new Gson();
        String json = mPrefs.getString(getResources().getString(R.string.postListKey), "");
        List list = gson.fromJson(json, List.class);
        if(list == null){
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            gson = new Gson();
            json = gson.toJson(postList);
            prefsEditor.putString(getResources().getString(R.string.postListKey), json);
            prefsEditor.apply();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(getApplicationContext(), OpenStreetViewActivity.class);
        startActivity(intent);
    }
}
