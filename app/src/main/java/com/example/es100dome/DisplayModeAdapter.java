package com.example.es100dome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.es100.e.DisplayMode;

import java.util.Arrays;
import java.util.List;

/**
 * @Description : TODO
 * @Author : BruceChen
 * @Date : 2018/1/20 14:30
 */

public final class DisplayModeAdapter extends BaseAdapter {
    private Context context;
    private List<DisplayMode> list;

    public DisplayModeAdapter(Context context) {
        this.context = context;
        list = Arrays.asList(DisplayMode.values());
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public DisplayMode getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VH vh;
        if(convertView == null) {
            vh = new VH();
            convertView = LayoutInflater.from(context).inflate(R.layout.display_mode_item, null, false);
            vh.icon = (TextView) convertView.findViewById(R.id.icon);
            vh.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(vh);
        } else {
            vh = (VH) convertView.getTag();
        }
        DisplayMode mode = getItem(position);
        vh.icon.setText(mode.getImageRes());
        vh.title.setText(mode.getTextRes());
        return convertView;
    }

    private class VH {
        TextView icon, title;
    }
}
