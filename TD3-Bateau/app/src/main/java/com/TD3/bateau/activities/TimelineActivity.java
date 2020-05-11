package com.TD3.bateau.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.TD3.bateau.CustomListAdapter;
import com.TD3.bateau.Post;
import com.TD3.bateau.R;
import com.TD3.bateau.fragments.PostDisplayFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TimelineActivity extends AppCompatActivity {

    ListView listView;
    List<Post> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline_layout);

        loadList();

        final Spinner spinner = findViewById(R.id.spinner_post_sort);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.sort));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (spinner.getItemAtPosition(position).toString()){
                    case "plus récent":
                        listView.setAdapter(new CustomListAdapter(getApplicationContext(), list));
                        break;
                    case "moins récent":
                        List inv = new ArrayList(){{addAll(list);}};
                        Collections.reverse(inv);
                        listView.setAdapter(new CustomListAdapter(getApplicationContext(), inv));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putDouble("lat", ((Post)listView.getItemAtPosition(position)).getLocation().getLatitude());
                bundle.putDouble("lon", ((Post)listView.getItemAtPosition(position)).getLocation().getLongitude());
                if (findViewById(R.id.post_frame) != null) {
                    PostDisplayFragment postDisplayFragment = new PostDisplayFragment();
                    postDisplayFragment.setArguments(bundle);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.post_frame, postDisplayFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    findViewById(R.id.post_frame).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void loadList(){
        Gson gson = new Gson();
        String json = OpenStreetViewActivity.mPrefs.getString(getResources().getString(R.string.postListKey), "");
        Type type = new TypeToken<ArrayList<Post>>() {
        }.getType();
        list = gson.fromJson(json, type);

        listView = findViewById(R.id.post_listview);
        listView.setAdapter(new CustomListAdapter(this, list));
    }
}
