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
import com.TD3.bateau.SortByDistance;
import com.TD3.bateau.fragments.PostDisplayFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TimelineActivity extends AppCompatActivity {

    ListView listView;
    List<Post> list;
    List<Post> currentList;
    int triSelected =0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline_layout);

        loadList();
        currentList = list;
        final Spinner spinner = findViewById(R.id.spinner_post_sort);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.sort));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (spinner.getItemAtPosition(position).toString()){
                    case "moins récent":
                        triSelected = 0;
                        listView.setAdapter(new CustomListAdapter(getApplicationContext(), currentList));
                        break;
                    case "plus récent":
                        triSelected = 1;
                        List inv = new ArrayList(){{addAll(currentList);}};
                        Collections.reverse(inv);
                        listView.setAdapter(new CustomListAdapter(getApplicationContext(), inv));
                        break;
                    case "plus proche":
                        triSelected = 2;
                        List temp = new ArrayList(){{addAll(currentList);}};
                        Collections.sort(temp, new SortByDistance());
                        listView.setAdapter(new CustomListAdapter(getApplicationContext(), temp));
                        break;
                    case "moins proche":
                        triSelected = 3;
                        List temp2 = new ArrayList(){{addAll(currentList);}};
                        Collections.sort(temp2, new SortByDistance().reversed());
                        listView.setAdapter(new CustomListAdapter(getApplicationContext(), temp2));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String[] tabTheme = {"Tous", getResources().getStringArray(R.array.theme)[1], getResources().getStringArray(R.array.theme)[2] , getResources().getStringArray(R.array.theme)[3],getResources().getStringArray(R.array.theme)[0]};
        final Spinner spinnerType = findViewById(R.id.spinner_post_sort_type);
        ArrayAdapter<String> myAdapterType = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, tabTheme);
        myAdapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(myAdapterType);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (spinnerType.getItemAtPosition(position).toString()){
                    case "Tous":
                        currentList = list;
                        switch (triSelected){
                            case 0:
                                listView.setAdapter(new CustomListAdapter(getApplicationContext(), currentList));
                                break;
                            case 1:
                                List inverse = new ArrayList(){{addAll(currentList);}};
                                Collections.reverse(inverse);
                                listView.setAdapter(new CustomListAdapter(getApplicationContext(), inverse));
                                break;
                            case 2:
                                List proche = new ArrayList(){{addAll(currentList);}};
                                Collections.sort(proche, new SortByDistance());
                                listView.setAdapter(new CustomListAdapter(getApplicationContext(), proche));
                                break;
                            case 3:
                                List loin = new ArrayList(){{addAll(currentList);}};
                                Collections.sort(loin, new SortByDistance().reversed());
                                listView.setAdapter(new CustomListAdapter(getApplicationContext(), loin));
                                break;
                        }
                        break;
                    case "Autres":
                        List temp3 = new ArrayList(){{addAll(list);}};
                        Predicate<Post> streamsPredicate3 = new Predicate<Post>() {
                            @Override
                            public boolean test(Post item) {
                                return item.getTheme().equals("Autres");
                            }
                        };
                        temp3 = (List) temp3.stream()
                                .filter(streamsPredicate3)
                                .collect(Collectors.toList());
                        currentList = temp3;
                        switch (triSelected){
                            case 0:
                                listView.setAdapter(new CustomListAdapter(getApplicationContext(), currentList));
                                break;
                            case 1:
                                List inverse = new ArrayList(){{addAll(currentList);}};
                                Collections.reverse(inverse);
                                listView.setAdapter(new CustomListAdapter(getApplicationContext(), inverse));
                                break;
                            case 2:
                                List proche = new ArrayList(){{addAll(currentList);}};
                                Collections.sort(proche, new SortByDistance());
                                listView.setAdapter(new CustomListAdapter(getApplicationContext(), proche));
                                break;
                            case 3:
                                List loin = new ArrayList(){{addAll(currentList);}};
                                Collections.sort(loin, new SortByDistance().reversed());
                                listView.setAdapter(new CustomListAdapter(getApplicationContext(), loin));
                                break;
                        }
                        break;
                    case "Bateau":
                        List inv = new ArrayList(){{addAll(list);}};
                        Predicate<Post> streamsPredicatebat = new Predicate<Post>() {
                            @Override
                            public boolean test(Post item) {
                                return item.getTheme().equals("Bateau");
                            }
                        };
                        inv = (List) inv.stream()
                                .filter(streamsPredicatebat)
                                .collect(Collectors.toList());
                        currentList = inv;
                        switch (triSelected){
                            case 0:
                                listView.setAdapter(new CustomListAdapter(getApplicationContext(), currentList));
                                break;
                            case 1:
                                List inverse = new ArrayList(){{addAll(currentList);}};
                                Collections.reverse(inverse);
                                listView.setAdapter(new CustomListAdapter(getApplicationContext(), inverse));
                                break;
                            case 2:
                                List proche = new ArrayList(){{addAll(currentList);}};
                                Collections.sort(proche, new SortByDistance());
                                listView.setAdapter(new CustomListAdapter(getApplicationContext(), proche));
                                break;
                            case 3:
                                List loin = new ArrayList(){{addAll(currentList);}};
                                Collections.sort(loin, new SortByDistance().reversed());
                                listView.setAdapter(new CustomListAdapter(getApplicationContext(), loin));
                                break;
                        }
                        break;
                    case "Nageur":
                        List temp = new ArrayList(){{addAll(list);}};
                        Predicate<Post> streamsPredicate1 = new Predicate<Post>() {
                            @Override
                            public boolean test(Post item) {
                                return item.getTheme().equals("Nageur");
                            }
                        };
                        temp = (List) temp.stream()
                                .filter(streamsPredicate1)
                                .collect(Collectors.toList());
                        currentList = temp;
                        switch (triSelected){
                            case 0:
                                listView.setAdapter(new CustomListAdapter(getApplicationContext(), currentList));
                                break;
                            case 1:
                                List inverse = new ArrayList(){{addAll(currentList);}};
                                Collections.reverse(inverse);
                                listView.setAdapter(new CustomListAdapter(getApplicationContext(), inverse));
                                break;
                            case 2:
                                List proche = new ArrayList(){{addAll(currentList);}};
                                Collections.sort(proche, new SortByDistance());
                                listView.setAdapter(new CustomListAdapter(getApplicationContext(), proche));
                                break;
                            case 3:
                                List loin = new ArrayList(){{addAll(currentList);}};
                                Collections.sort(loin, new SortByDistance().reversed());
                                listView.setAdapter(new CustomListAdapter(getApplicationContext(), loin));
                                break;
                        }
                        break;
                    case "Poisson":
                        List temp2 = new ArrayList(){{addAll(list);}};
                        Predicate<Post> streamsPredicate2 = new Predicate<Post>() {
                            @Override
                            public boolean test(Post item) {
                                return item.getTheme().equals("Poisson");
                            }
                        };
                        temp2 = (List) temp2.stream()
                                .filter(streamsPredicate2)
                                .collect(Collectors.toList());
                        currentList = temp2;
                        switch (triSelected){
                            case 0:
                                listView.setAdapter(new CustomListAdapter(getApplicationContext(), currentList));
                                break;
                            case 1:
                                List inverse = new ArrayList(){{addAll(currentList);}};
                                Collections.reverse(inverse);
                                listView.setAdapter(new CustomListAdapter(getApplicationContext(), inverse));
                                break;
                            case 2:
                                List proche = new ArrayList(){{addAll(currentList);}};
                                Collections.sort(proche, new SortByDistance());
                                listView.setAdapter(new CustomListAdapter(getApplicationContext(), proche));
                                break;
                            case 3:
                                List loin = new ArrayList(){{addAll(currentList);}};
                                Collections.sort(loin, new SortByDistance().reversed());
                                listView.setAdapter(new CustomListAdapter(getApplicationContext(), loin));
                                break;
                        }
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

        ((Spinner)findViewById(R.id.spinner_post_sort)).setSelection(0);
        listView = findViewById(R.id.post_listview);
        listView.setAdapter(new CustomListAdapter(this, list));
    }
}
