package io.github.mario_kang.hclientandroid;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

class searchList implements Comparable {

    private int count;
    private String name;
    private String type;

    searchList() {
        count = 0;
        name = "";
        type = "";
    }

    void setData(int _count, String _name, String _type) {
        setCount(_count);
        setName(_name);
        setType(_type);
    }

    private void setCount(int _count) {
        count = _count;
    }

    private void setName(String _name) {
        name = _name;
    }

    private void setType(String _type) {
        type = _type;
    }

    public searchList getData() {
        return this;
    }

    int getCount() {
        return count;
    }

    String getName() {
        return name;
    }

    String getType() {
        return type;
    }

    public int compareTo(@NonNull Object obj) {
        searchList other = (searchList)obj;
        if (count > other.count)
            return -1;
        else if (count < other.count)
            return 1;
        else
            return 0;
    }
}

public class SearchFragment extends Fragment {

    ArrayList<searchList> list = new ArrayList<>();
    SearchViewAdapter Adapter;
    StringE s = new StringE();
    String searchText = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, null);
        SharedPreferences shared = getContext().getSharedPreferences("pref", MODE_PRIVATE);
        String tags = shared.getString("tags","");
        Adapter = new SearchViewAdapter();
        ListView listView = view.findViewById(R.id.listview2);
        listView.setAdapter(Adapter);
        try {
            JSONObject jsonObject = new JSONObject(tags);
            addAll("male", jsonObject);
            addAll("female", jsonObject);
            addAll("tag", jsonObject);
            addAll("series", jsonObject);
            addAll("group", jsonObject);
            addAll("character", jsonObject);
            addAll("language", jsonObject);
            searchList a[] = new searchList[list.size()];
            a = list.toArray(a);
            Arrays.sort(a);
            for (searchList anA : a)
                Adapter.addItem(anA.getName(), anA.getType() + ", " + Integer.toString(anA.getCount()) + getString(R.string.items));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Adapter.backup();
        Adapter.notifyDataSetChanged();
        SearchView searchView = view.findViewById(R.id.searchBar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Adapter.getFilter().filter(newText);
                int a;
                try {
                    a = Integer.parseInt(newText);
                }
                catch (NumberFormatException e) {
                    a = 0;
                }
                Adapter.addItem(getResources().getString(R.string.find_number1) + Integer.toString(a) + getString(R.string.find_number2), null);
                searchText = newText;
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), io.github.mario_kang.hclientandroid.SearchView.class);
                SearchViewItem qlist = Adapter.getItem(position);
                String tname = qlist.getStr().replace(" ", "_");
                String ttype = qlist.getStr2();
                boolean ifNumber;
                String type;
                int number = 0;
                if (ttype == null) {
                    ifNumber = true;
                    type = "";
                    try {
                        number = Integer.parseInt(searchText);
                    }
                    catch (NumberFormatException e) {
                        number = 0;
                    }
                }
                else {
                    ifNumber = false;
                    type = s.SplitString(ttype, ", ")[0];
                }
                intent.putExtra("name", tname);
                intent.putExtra("type", type);
                intent.putExtra("isNumber", ifNumber);
                intent.putExtra("number", number);
                view.getContext().startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    void addAll(String type, JSONObject object) {
        try {
            JSONArray array = object.getJSONArray(type);
            int len = array.length();
            for (int i = 0; i < len; i++) {
                JSONObject a = array.getJSONObject(i);
                searchList searchlist = new searchList();
                searchlist.setData(Integer.parseInt(a.get("t").toString()), a.get("s").toString(), type);
                list.add(searchlist);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("HClient");
    }
}
