package com.graphicdesigncoding.learnapp;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
//COPYRIGHT BY GraphicDesignCoding
public class CustomArrayAdapter  extends ArrayAdapter<User> {
    private static final String TAG = "FruitArrayAdapter";
    private List<User> userList = new ArrayList<User>();

    static class UserViewHolder {
        ImageView userImg;
        TextView userName;
        TextView score;
    }

    public CustomArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public void add(User object) {
        userList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.userList.size();
    }

    @Override
    public User getItem(int index) {
        return this.userList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        UserViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.listview_row_layout, parent, false);
            viewHolder = new UserViewHolder();
            viewHolder.userImg = (ImageView) row.findViewById(R.id.userImg);
            viewHolder.userName = (TextView) row.findViewById(R.id.userName);
            viewHolder.score = (TextView) row.findViewById(R.id.score);
            row.setTag(viewHolder);
        } else {
            viewHolder = (UserViewHolder)row.getTag();
        }
        User user = getItem(position);
        viewHolder.userImg.setImageBitmap(user.getUserImg());
        viewHolder.userName.setText(user.getUserName());
        viewHolder.score.setText(user.getScore());
        return row;
    }
}
