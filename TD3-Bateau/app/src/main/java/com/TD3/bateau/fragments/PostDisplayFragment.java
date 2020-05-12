package com.TD3.bateau.fragments;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;

import com.TD3.bateau.Post;
import com.TD3.bateau.R;
import com.TD3.bateau.activities.OpenStreetViewActivity;
import com.TD3.bateau.activities.TimelineActivity;
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
        final View view = inflater.inflate(R.layout.post_display_layout, container, false);

        final Gson gson = new Gson();
        final String json = OpenStreetViewActivity.mPrefs.getString(getResources().getString(R.string.postListKey), "");
        Type type = new TypeToken<ArrayList<Post>>() {
        }.getType();
        final List<Post> list = gson.fromJson(json, type);

        for (final Post post : list) {
            if (post.getLocation().getLatitude() == getArguments().getDouble("lat") && post.getLocation().getLongitude() == getArguments().getDouble("lon")) {
                Button button_suppr = view.findViewById(R.id.button_suppr);
                if (post.getUserID() != getResources().getInteger(R.integer.userId)) {
                    view.findViewById(R.id.button_suppr).setVisibility(View.INVISIBLE);
                } else {
                    view.findViewById(R.id.toggleButton_like).setVisibility(View.INVISIBLE);
                    button_suppr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharedPreferences.Editor prefsEditor = OpenStreetViewActivity.mPrefs.edit();

                            list.remove(post);

                            Gson gson = new Gson();
                            String json = gson.toJson(list);
                            prefsEditor.putString(getResources().getString(R.string.postListKey), json);
                            prefsEditor.apply();

                            container.setVisibility(View.INVISIBLE);
                            getActivity().getSupportFragmentManager().popBackStack();
                            if (getActivity().getClass() == OpenStreetViewActivity.class) {
                                ((OpenStreetViewActivity) getActivity()).displayAllPosts();
                            }
                            if (getActivity().getClass() == TimelineActivity.class) {
                                ((TimelineActivity) getActivity()).loadList();
                            }
                        }
                    });
                }

                ToggleButton button_like = view.findViewById(R.id.toggleButton_like);
                button_like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            ((TextView) view.findViewById(R.id.textView_like)).setText(Integer.toString(post.getLikeCount() + 1));
                        } else {
                            ((TextView) view.findViewById(R.id.textView_like)).setText(Integer.toString(post.getLikeCount()));
                        }
                    }
                });

                ((TextView) view.findViewById(R.id.textView_title_list)).setText(post.getTitle());
                ((TextView) view.findViewById(R.id.textView_theme)).setText(post.getTheme());
                ((TextView) view.findViewById(R.id.textView_detail)).setText(post.getComment());
                ((TextView) view.findViewById(R.id.textView_like)).setText(Integer.toString(post.getLikeCount()));
                ((TextView) view.findViewById(R.id.textView_Date)).setText(post.getDate().toString().split("G")[0]);
                try {
                    if (post.getBitmapName() != null) {
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
