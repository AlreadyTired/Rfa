package com.example.kimhyunwoo.runwithme.MainActivity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kimhyunwoo.runwithme.R;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter{
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();

    public ListViewAdapter()
    {
    }

    public void clear()
    {
        listViewItemList.clear();
    }

    @Override
    public int getCount()
    {
        return listViewItemList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final int pos = position;
        final Context context = parent.getContext();

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item,parent,false);
        }

        ImageView iconImageView = (ImageView)convertView.findViewById(R.id.UserImageView);
        TextView titleTextView = (TextView)convertView.findViewById(R.id.TextView1);
        TextView descTextView = (TextView)convertView.findViewById(R.id.TextView2);

        ListViewItem listViewItem = listViewItemList.get(position);

        iconImageView.setImageDrawable((listViewItem.getIcon()));
        titleTextView.setText(listViewItem.getNickname());
        descTextView.setText(listViewItem.getEmail());

        return convertView;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public Object getItem(int position)
    {
        return listViewItemList.get(position);
    }

    public void addItem(Drawable icon,String nickname,String email)
    {
        ListViewItem item = new ListViewItem();

        item.setIcon(icon);
        item.setNickname(nickname);
        item.setEmail(email);

        listViewItemList.add(item);
    }
}
