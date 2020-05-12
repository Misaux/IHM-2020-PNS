package com.TD3.bateau.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.*;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.TD3.bateau.Beacon;
import com.TD3.bateau.Post;
import com.TD3.bateau.R;
import com.TD3.bateau.Wind;
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
import java.util.Date;
import java.util.List;

import static com.TD3.bateau.activities.ApplicationBateau.CHANNEL_ID;
// essai de suivre le tuto : https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library
// et https://stackoverflow.com/questions/18302603/where-do-i-place-the-assets-folder-in-android-studio?rq=1

public class OpenStreetViewActivity extends AppCompatActivity implements LocationListener {
    List<Post> postList = new ArrayList<>();
    List<Beacon> beaconList = new ArrayList<>();
    public static SharedPreferences mPrefs;

    private MapView map;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    public static MyLocationNewOverlay mLocationOverlay;
    private boolean newPostLocFlag = false;
    private Location myCurrentLocation;
    private int notificationId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPrefs = getPreferences(MODE_PRIVATE);

        Gson gson = new Gson();
        String json = mPrefs.getString(getResources().getString(R.string.postListKey), "");
        Type type = new TypeToken<ArrayList<Post>>() {
        }.getType();
        List<Post> list = gson.fromJson(json, type);
        if (list == null || list.size() == 0) {
            postList.add(new Post("Banc de poisson", "La pêche est bonne par ici!", new GeoPoint(43.100105499354115, 5.977206230163574), "Poisson", new Date(2020, 4, 12, 10, 5, 32), 132, 266241));

            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            gson = new Gson();
            json = gson.toJson(postList);
            prefsEditor.putString(getResources().getString(R.string.postListKey), json);
            prefsEditor.apply();
        }

        this.addBeaconsOnMap();

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
        mapController.setZoom(13.0);

        requestPermissionsIfNecessary(new String[]{
                // if you need to show the current location, uncomment the line below
                Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, this);
        }


        mLocationOverlay = new MyLocationNewOverlay( new GpsMyLocationProvider(getApplicationContext()), map);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        map.getOverlays().add(mLocationOverlay);
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                //Toast.makeText(getBaseContext(),p.getLatitude() + " - "+p.getLongitude(),Toast.LENGTH_LONG).show();
                if (newPostLocFlag) {
                    newPostLocFlag = false;
                    findViewById(R.id.addButton).setBackgroundColor(Color.rgb(159, 159, 159));
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
        displayAllBeacons();

    }


    @Override
    public void onResume() {
        super.onResume();
        displayAllPosts();
        displayAllBeacons();
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



    private void addBeaconsOnMap() {
        Gson gson = new Gson();
        String json = mPrefs.getString(getResources().getString(R.string.beaconListKey), "");
        Type type = new TypeToken<ArrayList<Beacon>>() {
        }.getType();
        List<Beacon> list = gson.fromJson(json, type);
        if (list == null || list.size() == 0) {
            beaconList.add(new Beacon("La Rochelle",  new GeoPoint(46.05555, -1.28170),  18.2,21.4, new Wind("Nord-Est", 8), 34.6));
            beaconList.add(new Beacon("Ile de Ré",  new GeoPoint(46.257398, -1.327062),  15.7,19.1, new Wind("Sud", 3), 74.6));

            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            gson = new Gson();
            json = gson.toJson(beaconList);
            prefsEditor.putString(getResources().getString(R.string.beaconListKey), json);
            prefsEditor.apply();
        }else{
            beaconList = list;
        }
    }

    public void displayAllBeacons() {
        Gson gson = new Gson();
        String json = mPrefs.getString(getResources().getString(R.string.beaconListKey), "");
        Type type = new TypeToken<ArrayList<Beacon>>() {
        }.getType();
        final List<Beacon> list = gson.fromJson(json, type);

        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        for (Beacon beacon : list) {
            OverlayItem item = new OverlayItem(beacon.getName(), "", beacon.getLocation());
            item.setMarker(getDrawable(R.drawable.balise32x32));
            items.add(item);
        }

        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(this, items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //Toast.makeText(getBaseContext(), "Restez appuyé pour afficher les détails", Toast.LENGTH_LONG).show();
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return true;
                    }
                });
        map.getOverlays().add(mOverlay);
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

    public void buttonPlusClick(View view) {
        view.setBackgroundColor(Color.rgb(159, 159, 159));
        newPostLocFlag = !newPostLocFlag;
        if (newPostLocFlag) {
            Toast.makeText(getBaseContext(), "Cliquer sur la carte pour placer le poste", Toast.LENGTH_LONG).show();
            view.setBackgroundColor(Color.rgb(80, 80, 80));
        } else
            view.setBackgroundColor(Color.rgb(159, 159, 159));
    }

    public void buttonTimelineClick(View view){
        Intent intent = new Intent(this, TimelineActivity.class);
        startActivity(intent);
    }

    public void displayAllPosts() {
        Gson gson = new Gson();
        String json = mPrefs.getString(getResources().getString(R.string.postListKey), "");
        Type type = new TypeToken<ArrayList<Post>>() {
        }.getType();
        final List<Post> list = gson.fromJson(json, type);


        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        for (Post post : list) {
            OverlayItem item = new OverlayItem(post.getTitle() + " (" + post.getTheme() + ")", "", post.getLocation());
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

    private double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180;
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println( beaconList.toString());
        if(location != null){
            if(myCurrentLocation == null || location.getLatitude() != myCurrentLocation.getLatitude() || location.getLongitude() != myCurrentLocation.getLongitude() ){
                myCurrentLocation = location;
                System.out.println("NOUVELLE POSITION");

                for(Beacon b : beaconList){
                    double dLat = degreesToRadians(myCurrentLocation.getLatitude()-b.getLocation().getLatitude());
                    double dLon = degreesToRadians(myCurrentLocation.getLongitude()-b.getLocation().getLongitude());

                    double lat1 = degreesToRadians(b.getLocation().getLatitude());
                    double lat2 = degreesToRadians(myCurrentLocation.getLatitude());
                    double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
                    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
                    double distance =  6371 * c;

                    System.out.println("Disance en KM : " + distance);
                    if(distance < 3.0 ){
                        sendNotificationOnChannel( b);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }

    private void sendNotificationOnChannel(Beacon beacon) {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Vous êtes à proximité de la balise " + beacon.getName() + ".")
            .setContentText("Voici les informations recueillies : ")
            .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText("T° Air : "+ beacon.getAirTemperature() +
                            "\nT° Eau  : " + beacon.getWaterTemperature() +
                            "\nProfondeur : " + beacon.getDepth() + " mètres"+
                            "\nVent : " + beacon.getWind().toString()))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

         NotificationManagerCompat.from(getApplicationContext()).notify(notificationId++, notification.build());
    }
}
