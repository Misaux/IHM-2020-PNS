package com.TD3.bateau.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.TD3.bateau.Post;
import com.TD3.bateau.R;
import com.TD3.bateau.activities.OpenStreetViewActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PostDisplayFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post_display_layout, container, false);

        Gson gson = new Gson();
        String json = OpenStreetViewActivity.mPrefs.getString(getResources().getString(R.string.postListKey), "");
        Type type = new TypeToken<ArrayList<Post>>() {
        }.getType();
        List<Post> list = gson.fromJson(json, type);

        for (Post post : list) {
            if (post.getLocation().getLatitude() == getArguments().getDouble("lat") && post.getLocation().getLongitude() == getArguments().getDouble("lon")) {
                ((TextView)view.findViewById(R.id.textView_title)).setText(post.getTitle());
                ((TextView)view.findViewById(R.id.textView_theme)).setText(post.getTheme());
                ((TextView)view.findViewById(R.id.textView_detail)).setText(post.getComment());
                ((TextView)view.findViewById(R.id.textView_Date)).setText(post.getDate().toString().split("G")[0]);
                try {
                    if(post.getBitmapName() != null) {
                        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(new File(post.getBitmapName())));
                        if (bitmap != null) {
                            ((ImageView) view.findViewById(R.id.post_image)).setImageBitmap(bitmap);
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

        return view;
    }
}
