package com.TD3.bateau;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.TD3.bateau.activities.OpenStreetViewActivity;

import java.util.List;

public class CustomListAdapter extends BaseAdapter {
    private List<Post> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public CustomListAdapter(Context aContext,  List<Post> listData) {
        this.context = aContext;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.post_list_template, null);
            holder = new ViewHolder();
            holder.themeImage = convertView.findViewById(R.id.theme_image);
            holder.postTitle = convertView.findViewById(R.id.textView_title_list);
            holder.postDate = convertView.findViewById(R.id.textView_date_list);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Post post = this.listData.get(position);
        holder.postTitle.setText(post.getTitle() + "(" + (int)post.getLocation().distanceToAsDouble(OpenStreetViewActivity.mLocationOverlay.getMyLocation()) + "m)");
        holder.postDate.setText(post.getDate().toString().split("G")[0]);

        switch (post.getTheme()){
            case "Nageur":
                holder.themeImage.setImageResource(R.drawable.nageur32x32);
                break;
            case "Bateau":
                holder.themeImage.setImageResource(R.drawable.bateau32x32);
                break;
            case "Poisson":
                holder.themeImage.setImageResource(R.drawable.poisson32x32);
                break;
            case "Autres":
                holder.themeImage.setImageResource(R.drawable.marker_default);
                break;
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView themeImage;
        TextView postTitle;
        TextView postDate;
    }
}
