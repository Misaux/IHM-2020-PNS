package com.TD3.bateau;

import com.TD3.bateau.activities.OpenStreetViewActivity;

import java.util.Comparator;

public class SortByDistance implements Comparator<Post> {
    @Override
    public int compare(Post o1, Post o2) {
        return (int)(o1.getLocation().distanceToAsDouble(OpenStreetViewActivity.mLocationOverlay.getMyLocation()) - o2.getLocation().distanceToAsDouble(OpenStreetViewActivity.mLocationOverlay.getMyLocation()));
    }
}
