package io.github.mario_kang.hclientandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SearchViewAdapter extends BaseAdapter implements Filterable{
    private ArrayList<SearchViewItem> SearchViewItemList = new ArrayList<>() ;
    private ArrayList<SearchViewItem> mStringFilterList;
    private ValueFilter filter;

    SearchViewAdapter() {
    }

    @Override
    public int getCount() {
        return SearchViewItemList.size() ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.searchview_item, parent, false);
        }
        TextView titleTextView = convertView.findViewById(R.id.SearchtextView1);
        TextView TextView2 = convertView.findViewById(R.id.SearchtextView2);
        SearchViewItem listViewItem = SearchViewItemList.get(position);
        titleTextView.setText(listViewItem.getStr());
        TextView2.setText(listViewItem.getStr2());
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public SearchViewItem getItem(int position) {
        return SearchViewItemList.get(position);
    }

    void addItem(String title, String text2) {
        SearchViewItem item = new SearchViewItem();
        item.setStr(title);
        item.setStr2(text2);
        SearchViewItemList.add(item);
    }

    void backup() {
        mStringFilterList = SearchViewItemList;
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new ValueFilter();
        return filter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<SearchViewItem>  filterList = new ArrayList<>();
                Pattern p = Pattern.compile("(.*[a-zA-Z]):(.*[a-zA-Z0-9-. ])");
                Matcher m = p.matcher(constraint);
                if (m.matches()) {
                    String type = m.group(1);
                    String name = m.group(2);
                    StringE s = new StringE();
                    for (int i = 0; i < mStringFilterList.size(); i++)
                        if ((mStringFilterList.get(i).getStr().toUpperCase()).contains(name.toUpperCase()))
                            if (s.SplitString(mStringFilterList.get(i).getStr2(), ", ")[0].toUpperCase().equals(type.toUpperCase()))
                                filterList.add(mStringFilterList.get(i));
                    results.count = filterList.size();
                    results.values = filterList;
                }
                else {
                    for (int i = 0; i < mStringFilterList.size(); i++)
                        if ((mStringFilterList.get(i).getStr().toUpperCase()).contains(constraint.toString().toUpperCase()))
                            filterList.add(mStringFilterList.get(i));
                    results.count = filterList.size();
                    results.values = filterList;
                }
            }
            else {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            @SuppressWarnings(value = "unchecked")
            ArrayList<SearchViewItem> mList = (ArrayList<SearchViewItem>) results.values;
            SearchViewItemList = mList;
            notifyDataSetChanged();
        }
    }
}

