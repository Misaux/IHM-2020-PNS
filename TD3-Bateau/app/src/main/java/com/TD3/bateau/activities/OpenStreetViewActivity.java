package com.TD3.bateau.activities;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.TD3.bateau.Post;
import com.TD3.bateau.R;
import com.TD3.bateau.fragments.NewPostFragment;
import com.TD3.bateau.fragments.PostDisplayFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
// essai de suivre le tuto : https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library
// et https://stackoverflow.com/questions/18302603/where-do-i-place-the-assets-folder-in-android-studio?rq=1

public class OpenStreetViewActivity extends AppCompatActivity {
    List<Post> postList = new ArrayList<>();
    public static SharedPreferences mPrefs;

    private MapView map;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MyLocationNewOverlay mLocationOverlay;
    private boolean newPostLocFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPrefs = getPreferences(MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(postList);
        prefsEditor.putString(getResources().getString(R.string.postListKey), json);
        prefsEditor.apply();

        IMapController mapController;

        super.onCreate(savedInstanceState);
        //load/initialize the osmdroid configuration, this can be done
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        //inflate and create the map
        setContentView(R.layout.open_street_view_activity);

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);    //render
        map.setBuiltInZoomControls(true);               // zoomable
        map.setMultiTouchControls(true);                //  zoom with 2 fingers

        mapController = map.getController();
        mapController.setZoom(18.0);

        requestPermissionsIfNecessary(new String[]{
                // if you need to show the current location, uncomment the line below
                Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), map);
        this.mLocationOverlay.enableMyLocation();
        this.mLocationOverlay.enableFollowLocation();
        map.getOverlays().add(this.mLocationOverlay);


        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                //Toast.makeText(getBaseContext(),p.getLatitude() + " - "+p.getLongitude(),Toast.LENGTH_LONG).show();
                if (newPostLocFlag) {
                    newPostLocFlag = false;
                    findViewById(R.id.addButton).setBackgroundColor(Color.rgb(213, 213, 213));
                    Bundle bundle = new Bundle();
                    bundle.putDouble("lat", p.getLatitude());
                    bundle.putDouble("lon", p.getLongitude());
                    if (findViewById(R.id.post_fragment_container) != null) {
                        NewPostFragment newPostFragment = new NewPostFragment();
                        newPostFragment.setArguments(bundle);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.post_fragment_container, newPostFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        findViewById(R.id.post_fragment_container).setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };


        MapEventsOverlay OverlayEvents = new MapEventsOverlay(getBaseContext(), mReceive);
        map.getOverlays().add(OverlayEvents);

        mapController.setCenter(mLocationOverlay.getMyLocation());
        displayAllPosts();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayAllPosts();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    public void buttonClick(View view) {
        //view.setBackgroundColor(Color.rgb(200, 200, 200));
        findViewById(R.id.addButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.addButton).setEnabled(false);
        newPostLocFlag = !newPostLocFlag;
        if (newPostLocFlag) {
            newPostLocFlag = false;
            //findViewById(R.id.addButton).setBackgroundColor(Color.rgb(213, 213, 213));
            Bundle bundle = new Bundle();
            bundle.putDouble("lat", mLocationOverlay.getMyLocation().getLatitude());
            bundle.putDouble("lon", mLocationOverlay.getMyLocation().getLongitude());
            if (findViewById(R.id.post_fragment_container) != null) {
                NewPostFragment newPostFragment = new NewPostFragment();
                newPostFragment.setArguments(bundle);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.post_fragment_container, newPostFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                findViewById(R.id.post_fragment_container).setVisibility(View.VISIBLE);
            }
        }
        else view.setBackgroundColor(Color.rgb(213, 213, 213));
    }

    public void displayAllPosts() {
        Gson gson = new Gson();
        String json = this.mPrefs.getString(getResources().getString(R.string.postListKey), "");
        Type type = new TypeToken<ArrayList<Post>>() {
        }.getType();
        final List<Post> list = gson.fromJson(json, type);


        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        for (Post post : list) {
            OverlayItem item = new OverlayItem(post.getTitle() + " (" + post.getTheme() + ")", post.getComment(), post.getLocation());
            switch (post.getTheme()){
                case "Nageur":
                    item.setMarker(getDrawable(R.drawable.nageur32x32));
                    break;
                case "Bateau":
                    item.setMarker(getDrawable(R.drawable.bateau32x32));
                    break;
                case "Poisson":
                    item.setMarker(getDrawable(R.drawable.poisson32x32));
                    break;
                case "Température":
                    item.setMarker(getDrawable(R.drawable.temperature32x32));
                    break;
            }
            items.add(item);
        }

        //the Place icons on the map with a click listener
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(this, items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //Toast.makeText(getBaseContext(), "Restez appuyé pour afficher les détails", Toast.LENGTH_LONG).show();

                        for (Post post : list) {
                            if (post.getLocation().getLatitude() == item.getPoint().getLatitude() && post.getLocation().getLongitude() == item.getPoint().getLongitude()) {
                                Bundle bundle = new Bundle();
                                bundle.putDouble("lat", item.getPoint().getLatitude());
                                bundle.putDouble("lon", item.getPoint().getLongitude());
                                if (findViewById(R.id.post_fragment_container) != null) {
                                    PostDisplayFragment postDisplayFragment = new PostDisplayFragment();
                                    postDisplayFragment.setArguments(bundle);
                                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.post_fragment_container, postDisplayFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                    findViewById(R.id.post_fragment_container).setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {

                        return true;
                    }
                });

        mOverlay.setFocusItemsOnTap(true);
        for (Overlay overlay : map.getOverlays()){
            if (overlay.getClass() == ItemizedOverlayWithFocus.class){
                map.getOverlays().remove(overlay);
            }
        }
        map.getOverlays().add(mOverlay);
    }
}
