package com.TD3.bateau.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.TD3.bateau.Post;
import com.TD3.bateau.R;
import com.google.gson.Gson;

import org.osmdroid.util.GeoPoint;

import java.util.List;
import java.util.Objects;

public class NewPostActivity extends Activity {
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    SharedPreferences mPrefs;
    Post post = new Post();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = getPreferences(MODE_PRIVATE);
        setContentView(R.layout.new_post_layout);
        this.imageView = this.findViewById(R.id.imageView1);
        Button photoButton = this.findViewById(R.id.addPhoto);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            imageView.setImageBitmap(photo);
            post.setBitmap(photo);
            ((Button) findViewById(R.id.addPhoto)).setText(R.string.replacePhoto);
        }
    }

    public void bt_valid_click(View view) {
        if (((EditText) findViewById(R.id.titleBox)).getText() == null) {
            Toast.makeText(getBaseContext(), "Ajouter un titre", Toast.LENGTH_LONG).show();
            return;
        } else {
            post.setTitle(((EditText) findViewById(R.id.titleBox)).getText().toString());
            post.setComment(((EditText) findViewById(R.id.detailBox)).getText().toString());
            post.setLocation(new GeoPoint(getIntent().getDoubleExtra("lat", 0), getIntent().getDoubleExtra("lon", 0)));

            Gson gson = new Gson();
            String json = MainActivity.mPrefs.getString(getResources().getString(R.string.postListKey), "");
            List list = gson.fromJson(json, List.class);

            list.add(post);

            SharedPreferences.Editor prefsEditor = MainActivity.mPrefs.edit();
            gson = new Gson();
            json = gson.toJson(list);
            prefsEditor.putString(getResources().getString(R.string.postListKey), json);
            prefsEditor.apply();
            finish();
        }
    }
}