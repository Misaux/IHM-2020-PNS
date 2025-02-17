package com.TD3.bateau.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.ContentResolver;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.TD3.bateau.Post;
import com.TD3.bateau.R;
import com.TD3.bateau.activities.MainActivity;
import com.TD3.bateau.activities.OpenStreetViewActivity;
import com.google.gson.Gson;

import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.TD3.bateau.activities.ApplicationBateau.CHANNEL_ID;

public class NewPostFragment extends Fragment {
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private Spinner spinner;
    SharedPreferences mPrefs;
    private int notificationId = 0;
    Post post = new Post();
    private ViewGroup container;
    private boolean ajouterAgenda = false;


    private void sendNotificationOnChannel(String title, String content, Bitmap myBitmap, Post post, String channelId, int priority) {
        if (myBitmap != null) {
            NotificationCompat.Builder notification = new NotificationCompat.Builder(this.getContext(), channelId)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(title + " a été posté !")
                    .setContentText("Votre post est en ligne, avec cette photo")
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(myBitmap)
                            .bigLargeIcon(myBitmap))
                    .setUsesChronometer(true)
                    .setPriority(priority);
            NotificationManagerCompat.from(this.getContext()).notify(notificationId, notification.build());
        } else {
            NotificationCompat.Builder notification = new NotificationCompat.Builder(this.getContext(), channelId)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(title + " a été posté !")
                    .setContentText("Votre post est en ligne")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Rappel de votre post \n" +
                                    "Titre : " + post.getTitle() +
                                    "\nTheme : " + post.getTheme() +
                                    "\nDétail : " + post.getComment())
                    )
                    .setUsesChronometer(true)
                    .setPriority(priority);
            NotificationManagerCompat.from(this.getContext()).notify(notificationId, notification.build());
        }


    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        this.container = container;
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
        final ToggleButton boutonAgenda = view.findViewById(R.id.button);
        Button bt_valid = view.findViewById(R.id.bt_valid);
        boutonAgenda.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ajouterAgenda = isChecked;
            }
        });
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
                    post.setDate(Calendar.getInstance().getTime());


                    if (ajouterAgenda) {
                        Calendar beginTime = Calendar.getInstance();
                        Calendar endTime = Calendar.getInstance();
                        endTime.set(Calendar.DAY_OF_MONTH, endTime.get(Calendar.DAY_OF_MONTH) + 1);

                        Intent intent = new Intent(Intent.ACTION_INSERT)
                                .setData(CalendarContract.Events.CONTENT_URI)
                                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                                .putExtra(CalendarContract.Events.TITLE, post.getTitle())
                                .putExtra(CalendarContract.Events.DESCRIPTION, post.getComment())
                                .putExtra(CalendarContract.Events.EVENT_LOCATION, String.valueOf(post.getLocation()))
                                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
                        startActivity(intent);
                    }

                    post.setUserID(getResources().getInteger(R.integer.userId));

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
                    getActivity().getSupportFragmentManager().popBackStack();
                    if (getActivity().getClass() == OpenStreetViewActivity.class) {
                        ((OpenStreetViewActivity) getActivity()).displayAllPosts();
                        ((OpenStreetViewActivity) getActivity()).displayAllBeacons();
                    }
                }
                if (post.getBitmapName() != null) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(new File(post.getBitmapName())));
                        sendNotificationOnChannel(post.getTitle(), "", bitmap, post, CHANNEL_ID, NotificationCompat.PRIORITY_DEFAULT);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    sendNotificationOnChannel(post.getTitle(), "", null, post, CHANNEL_ID, NotificationCompat.PRIORITY_DEFAULT);
                }

            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
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

            ContextWrapper cw = new ContextWrapper(getContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            // Create imageDir
            String bitmapName = Calendar.getInstance().getTime().getTime() + ".jpg";
            File mypath = new File(directory, bitmapName);


            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(mypath);
                // Use the compress method on the BitMap object to write image to the OutputStream
                photo.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            post.setBitmapName(directory.getAbsolutePath() + "/" + bitmapName);

            ((Button) getView().findViewById(R.id.addPhoto)).setText(R.string.replacePhoto);
        }
    }
}