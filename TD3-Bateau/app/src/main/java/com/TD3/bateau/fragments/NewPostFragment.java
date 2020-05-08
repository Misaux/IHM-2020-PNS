package com.TD3.bateau.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.TD3.bateau.Post;
import com.TD3.bateau.R;
import com.TD3.bateau.activities.MainActivity;
import com.TD3.bateau.activities.OpenStreetViewActivity;
import com.google.gson.Gson;

import org.osmdroid.util.GeoPoint;

import java.util.List;
import java.util.Objects;

public class NewPostFragment extends Fragment {
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private Spinner spinner;
    SharedPreferences mPrefs;
    Post post = new Post();

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_post_layout, container, false);
        spinner = view.findViewById(R.id.spinnerTheme);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.theme));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);

        this.imageView = view.findViewById(R.id.imageView1);
        Button photoButton = view.findViewById(R.id.addPhoto);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        Button bt_valid = view.findViewById(R.id.bt_valid);
        bt_valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((EditText) getView().findViewById(R.id.titleBox)).getText() == null) {
                    Toast.makeText(getActivity().getBaseContext(), "Ajouter un titre", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    post.setTitle(((EditText) getView().findViewById(R.id.titleBox)).getText().toString());
                    post.setComment(((EditText) getView().findViewById(R.id.detailBox)).getText().toString());
                    if (getArguments() != null) {
                        post.setLocation(new GeoPoint(getArguments().getDouble("lat", 0), getArguments().getDouble("lon", 0)));
                    }
                    post.setTheme(spinner.getSelectedItem().toString());

                    Gson gson = new Gson();
                    String json = mPrefs.getString(getResources().getString(R.string.postListKey), "");
                    List list = gson.fromJson(json, List.class);

                    list.add(post);

                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                    gson = new Gson();
                    json = gson.toJson(list);
                    prefsEditor.putString(getResources().getString(R.string.postListKey), json);
                    prefsEditor.apply();

                    container.setVisibility(View.INVISIBLE);
                    if (getActivity().getClass() == OpenStreetViewActivity.class){
                        ((OpenStreetViewActivity) getActivity()).displayAllPosts();
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getActivity().getPreferences(getActivity().MODE_PRIVATE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(getActivity(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            imageView.setImageBitmap(photo);
            post.setBitmap(photo);
            ((Button) getView().findViewById(R.id.addPhoto)).setText(R.string.replacePhoto);
        }
    }
}