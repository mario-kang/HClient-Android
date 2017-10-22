package io.github.mario_kang.hclientandroid;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class ListViewAdapter extends BaseAdapter{

    private static final int ITEM_TITLE = 0;
    private static final int ITEM_TAGS = 1;
    private static final int ITEM_MAX = 2;
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<>();

    ListViewAdapter() {
    }

    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_MAX;
    }

    @Override
    public int getItemViewType(int position) {
        try {
            return listViewItemList.get(position).getType();
        }
        catch (NullPointerException e) {
            return 1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        int viewType = getItemViewType(position);
        ListViewItem listViewItem = listViewItemList.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            switch (viewType) {
                case ITEM_TITLE:
                    convertView = inflater.inflate(R.layout.listview_item, parent, false);
                    break;
                case ITEM_TAGS:
                    convertView = inflater.inflate(R.layout.listview_item2, parent, false);
            }
        }
        switch (viewType) {
            case ITEM_TITLE:
                ImageView iconImageView = convertView.findViewById(R.id.imageView1);
                TextView titleTextView = convertView.findViewById(R.id.textView1);
                TextView TextView2 = convertView.findViewById(R.id.textView2);
                TextView TextView3 = convertView.findViewById(R.id.textView3);
                TextView TextView4 = convertView.findViewById(R.id.textView4);
                TextView TextView5 = convertView.findViewById(R.id.textView5);
                try {
                    iconImageView.setImageDrawable(listViewItem.getIcon());
                }
                catch (NullPointerException ignored) {}
                try {
                    titleTextView.setText(listViewItem.getStr());
                }
                catch (NullPointerException ignored) {}
                try {
                    TextView2.setText(listViewItem.getStr2());
                }
                catch (NullPointerException ignored) {}
                try {
                    TextView3.setText(listViewItem.getStr3());
                }
                catch (NullPointerException ignored) {}
                try {
                    TextView4.setText(listViewItem.getStr4());
                }
                catch (NullPointerException ignored) {}
                try {
                    TextView5.setText(listViewItem.getStr5());
                }
                catch (NullPointerException ignored) {}
                break;
            case ITEM_TAGS:
                TextView tagView = convertView.findViewById(R.id.textView6);
                try {
                    tagView.setText(listViewItem.getStr6());
                }
                catch (NullPointerException ignored) {}
                break;
        }
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    void setCount(int count) {
        int a = listViewItemList.size();
        for (int b = 0; b < count - a; b++) {
            ListViewItem item = new ListViewItem();
            item.setType(ITEM_TAGS);
            item.setStr6(null);
            listViewItemList.add(null);
            listViewItemList.set(a + b, item);
        }
    }

    void addItem(int position, Drawable icon, String title, String text2, String text3, String text4, String text5) {
        ListViewItem item = new ListViewItem();
        item.setType(ITEM_TITLE);
        item.setIcon(icon);
        item.setStr(title);
        item.setStr2(text2);
        item.setStr3(text3);
        item.setStr4(text4);
        item.setStr5(text5);
        listViewItemList.set(position, item);
    }

    void addItem(int position, String title) {
        ListViewItem item = new ListViewItem();
        item.setType(ITEM_TAGS);
        item.setStr6(title);
        listViewItemList.set(position, item);
    }

    void removeAll() {
        listViewItemList.clear();
    }

    void remove(int a) {
        listViewItemList.remove(a);
    }
}

