package io.github.mario_kang.hclientandroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class SearchView extends AppCompatActivity {

    final List<Target> targets = new ArrayList<>();
    List<String> arr = new ArrayList<>();
    List<String> arr2 = new ArrayList<>();
    ListViewAdapter Adapter;
    StringE s = new StringE();
    int list = 1;
    boolean loadingMore = false;
    int itemCount = 0;
    StringRequest request;
    RequestQueue requestQueue;
    String name;
    String type;
    boolean isNumber;
    int number;
    int langIndex = 0;
    String anime;
    String taga;
    String language;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        Adapter = new ListViewAdapter();
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        Adapter = new ListViewAdapter();
        ListView listView = (ListView)findViewById(R.id.listview3);
        listView.setAdapter(Adapter);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        type = intent.getStringExtra("type");
        isNumber = intent.getBooleanExtra("isNumber", false);
        number = intent.getIntExtra("number", 0);
        if (isNumber) {
            getSupportActionBar().setTitle(getString(R.string.hitomi_number) + Integer.toString(number));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(view.getContext(), InfoDetail.class);
                    intent.putExtra("number", Integer.toString(number));
                    intent.putExtra("djURL", "https://hitomi.la/galleries/" + Integer.toString(number) + ".html");
                    view.getContext().startActivity(intent);
                }
            });
            CellViewNumber();
        }
        else {
            getSupportActionBar().setTitle(type + ":" + name.replace("_", " "));
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (totalItemCount != 0) {
                        itemCount = totalItemCount;
                        int lastInScreen = firstVisibleItem + visibleItemCount;
                        if ((lastInScreen == totalItemCount)) {
                            if (!loadingMore) {
                                list += 1;
                                downloadTask(list, langIndex);
                                loadingMore = true;
                            }
                        }
                    }
                }
            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(view.getContext(), InfoDetail.class);
                    String title = s.SplitString(s.SplitString(arr.get(position), ".html\">")[0], "<a href=\"")[1];
                    intent.putExtra("number", title);
                    intent.putExtra("djURL", "https://hitomi.la" + title + ".html");
                    view.getContext().startActivity(intent);
                }
            });
            downloadTask(list, langIndex);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Picasso.with(this).cancelTag("MainDownload");
        request.cancel();
        dialog.dismiss();
    }

    void downloadTask(int page, int lang) {
        dialog.show();
        taga = name.replace("_", "%20");
        String URL;
        String[] langArray = getResources().getStringArray(R.array.LanguageName);
        String[] localizedLangArray = getResources().getStringArray(R.array.LanguageName_Localized);
        ArrayList<Point> arrayList = new ArrayList<>();
        for (int a = 0; a < langArray.length; a++) {
            Point b = new Point(langArray[a], localizedLangArray[a]);
            arrayList.add(b);
        }
        Collections.sort(arrayList, new Descending());
        Point all = new Point("all", getString(R.string.all));
        arrayList.add(0, all);
        language = arrayList.get(lang).getName();
        switch (type) {
            case "male":
            case "female":
                URL = "https://hitomi.la/tag/" + type + ":" + taga + "-" + language + "-" + Integer.toString(page) + ".html";
                break;
            case "language":
                URL = "https://hitomi.la/index-" + taga + "-" + Integer.toString(page) + ".html";
                break;
            default:
                URL = "https://hitomi.la/" + type + "/" + taga + "-" + language + "-" + Integer.toString(page) + ".html";
                break;
        }
        requestQueue = Volley.newRequestQueue(this);
        request = new StringRequest(URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String str = s.replacingOccurrences(response);
                String temp[] = s.SplitString(str, "<div class=\"dj\">");
                for (int i = 1; i < temp.length; i++) {
                    String img = s.SplitString(temp[i], "\"> </div>")[0];
                    String imga = s.SplitString(img, "<img src=\"")[1];
                    String urlString = String.format(Locale.ENGLISH, "https:%s", imga);
                    arr.add(temp[i]);
                    arr2.add(urlString);
                }
                CellView();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
                NetworkResponse response = error.networkResponse;
                if (response != null && response.statusCode == 404)
                    loadingMore = true;
                else {
                    String text = error.getLocalizedMessage();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                    toast.show();
                }
            }
        });
        requestQueue.add(request);
    }

    public void CellView() {
        for (int b = itemCount; b < arr.size(); b++) {
            Adapter.setCount(arr.size());
            String a = arr.get(b);
            String ar[] = s.SplitString(a, "</a></h1>");
            final String title;
            String title1;
            try {
                if (Build.VERSION.SDK_INT >= 24)
                    title1 = Html.fromHtml(s.SplitString(ar[0], ".html\">")[2], Html.FROM_HTML_MODE_LEGACY).toString();
                else
                    title1 = Html.fromHtml(s.SplitString(ar[0], ".html\">")[2]).toString();
            }
            catch (ArrayIndexOutOfBoundsException e) {
                title1 = "";
            }
            title = title1;
            String etc[] = s.SplitString(ar[1], "</div>");
            String artlist = s.SplitString(etc[0], "list\">")[1];
            final String artist;
            String artist1;
            if (artlist.contains("N/A"))
                artist1 = "N/A";
            else {
                String c[] = s.SplitString(artlist, "</a></li>");
                int d = c.length - 2;
                String f = "";
                for (int e = 0; e <= d; e++) {
                    f += s.SplitString(c[e], ".html\">")[1];
                    if (e != d)
                        f += ", ";
                }
                if (Build.VERSION.SDK_INT >= 24)
                    artist1 = Html.fromHtml(f, Html.FROM_HTML_MODE_LEGACY).toString();
                else
                    artist1 = Html.fromHtml(f).toString();
            }
            artist = artist1;
            String etc1[] = s.SplitString(etc[1], "</td>");
            final String series;
            String series1;
            if (etc1[1].contains("N/A"))
                series1 = getString(R.string.series) + "N/A";
            else {
                String c[] = s.SplitString(etc1[1], "</a></li>");
                int d = c.length - 2;
                String f = "";
                for (int e = 0; e <= d; e++) {
                    f += s.SplitString(c[e], ".html\">")[1];
                    if (e != d)
                        f += ", ";
                }
                f = getString(R.string.series) + f;
                if (Build.VERSION.SDK_INT >= 24)
                    series1 = Html.fromHtml(f, Html.FROM_HTML_MODE_LEGACY).toString();
                else
                    series1 = Html.fromHtml(f).toString();
            }
            series = series1;
            final String language;
            String language1;
            if (etc1[5].contains("N/A"))
                language1 = getString(R.string.language1) + "N/A";
            else
            if (Build.VERSION.SDK_INT >= 24)
                language1 = Html.fromHtml(getString(R.string.language1) + s.SplitString(s.SplitString(etc1[5], ".html\">")[1], "</a>")[0], Html.FROM_HTML_MODE_LEGACY).toString();
            else
                language1 = Html.fromHtml(getString(R.string.language1) + s.SplitString(s.SplitString(etc1[5], ".html\">")[1], "</a>")[0]).toString();
            language = language1;
            final String tag1;
            String tag;
            String taga[] = s.SplitString(etc1[7], "</a></li>");
            int tagb = taga.length;
            if (tagb == 1)
                tag = getString(R.string.tag) + "N/A";
            else {
                String c = "";
                for (int i = 0; i <= tagb - 2; i++) {
                    c += s.SplitString(taga[i], ".html\">")[1];
                    if (i != tagb - 2)
                        c += ", ";
                }
                if (Build.VERSION.SDK_INT >= 24)
                    tag = Html.fromHtml(getString(R.string.tag) + c, Html.FROM_HTML_MODE_LEGACY).toString();
                else
                    tag = Html.fromHtml(getString(R.string.tag) + c).toString();
            }
            tag1 = tag;
            String url = arr2.get(b);
            final int finalB = b;
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Drawable drawable;
                    drawable = new BitmapDrawable(bitmap);
                    Adapter.addItem(finalB, drawable, title, artist, series, language, tag1);
                    Adapter.notifyDataSetChanged();
                    dialog.hide();
                    if (finalB == arr.size() - 1)
                        loadingMore = false;
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Adapter.addItem(finalB, null, title, artist, series, language, tag1);
                    Adapter.notifyDataSetChanged();
                    dialog.hide();
                    if (finalB == arr.size() - 1)
                        loadingMore = false;
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    Adapter.addItem(finalB, null, title, artist, series, language, tag1);
                    Adapter.notifyDataSetChanged();
                }
            };
            targets.add(target);
            Picasso.with(this).load(url).tag("MainDownload").into(target);
        }
    }

    public void CellViewNumber() {
        dialog.show();
        String URL = "https://hitomi.la/galleries/" + Integer.toString(number) + ".html";
        requestQueue = Volley.newRequestQueue(this);
        request = new StringRequest(URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                anime = s.SplitString(s.SplitString(response,"<td>Type</td><td>")[1], "</a></td>")[0];
                String Title, Pic;
                if (anime.contains("anime")) {
                    if (Build.VERSION.SDK_INT >= 24)
                        Title = Html.fromHtml(s.SplitString(s.SplitString(response, "<h1>")[2], "</h1>")[0], Html.FROM_HTML_MODE_LEGACY).toString();
                    else
                        Title = Html.fromHtml(s.SplitString(s.SplitString(response, "<h1>")[2], "</h1>")[0]).toString();
                    Pic = s.SplitString(s.SplitString(response, "\"cover\"><img src=\"")[1], "\"></div>")[0];
                }
                else {
                    try {
                        if (Build.VERSION.SDK_INT >= 24)
                            Title = Html.fromHtml(s.SplitString(s.SplitString(s.SplitString(response, "</a></h1>")[0], "<h1>")[3], ".html\">")[1], Html.FROM_HTML_MODE_LEGACY).toString();
                        else
                            Title = Html.fromHtml(s.SplitString(s.SplitString(s.SplitString(response, "</a></h1>")[0], "<h1>")[3], ".html\">")[1]).toString();
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Title = null;
                    }
                    Pic = s.SplitString(s.SplitString(response, ".html\"><img src=\"")[1], "\"></a></div>")[0];
                }
                String artist1 = s.SplitString(s.SplitString(response, "</h2>")[0], "<h2>")[1];
                String artist = "";
                String artistlist[] = s.SplitString(artist1, "</a></li>");
                if (artist1.contains("N/A"))
                    artist = "N/A";
                else {
                    for (int i = 0; i <= artistlist.length - 2; i++) {
                        if (Build.VERSION.SDK_INT >= 24)
                            artist = Html.fromHtml(s.SplitString(artistlist[i], ".html\">")[1], Html.FROM_HTML_MODE_LEGACY).toString();
                        else
                            artist = Html.fromHtml(s.SplitString(artistlist[i], ".html\">")[1]).toString();
                        if (i != artistlist.length - 2)
                            artist += ", ";
                    }
                }
                String lang = s.SplitString(s.SplitString(response, "Language")[1], "</a></td>")[0];
                String language = getString(R.string.language1);
                if (lang.contains("N/A"))
                    language += "N/A";
                else
                    if (Build.VERSION.SDK_INT >= 24)
                        language += Html.fromHtml(s.SplitString(lang, ".html\">")[1], Html.FROM_HTML_MODE_LEGACY).toString();
                    else
                        language += Html.fromHtml(s.SplitString(lang, ".html\">")[1]).toString();
                String series1 = s.SplitString(s.SplitString(response, "<td>Series</td>")[1], "</ul>")[0];
                String series = getString(R.string.series);
                String series2[] = s.SplitString(series1, "</a></li>");
                if (series1.contains("N/A"))
                    series += "N/A";
                else {
                    for (int i = 0; i <= series2.length - 2; i++) {
                        if (Build.VERSION.SDK_INT >= 24)
                            series += Html.fromHtml(s.SplitString(series2[i], ".html\">")[1], Html.FROM_HTML_MODE_LEGACY).toString();
                        else
                            series += Html.fromHtml(s.SplitString(series2[i], ".html\">")[1]).toString();
                        if (i != series2.length - 2)
                            series += ", ";
                    }
                }
                String tags1 = s.SplitString(s.SplitString(response, "Tags")[1], "</td>")[1];
                String tags = getString(R.string.tag);
                String tags2[] = s.SplitString(tags1, "</a></li>");
                if (tags2.length == 1)
                    tags += "N/A";
                else {
                    for (int i = 0; i <= tags2.length - 2; i++) {
                        if (Build.VERSION.SDK_INT >= 24)
                            tags += Html.fromHtml(s.SplitString(tags2[i], ".html\">")[1], Html.FROM_HTML_MODE_LEGACY).toString();
                        else
                            tags += Html.fromHtml(s.SplitString(tags2[i], ".html\">")[1]).toString();
                        if (i != tags2.length - 2)
                            tags += ", ";
                    }
                }
                String picurl = "https:" + Pic;
                final String finalTitle = Title;
                final String finalArtist = artist;
                final String finalSeries = series;
                final String finalLanguage = language;
                final String finalTags = tags;
                Adapter.setCount(1);
                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Drawable drawable;
                        drawable = new BitmapDrawable(bitmap);
                        Adapter.addItem(0, drawable, finalTitle, finalArtist, finalSeries, finalLanguage, finalTags);
                        Adapter.notifyDataSetChanged();
                        dialog.hide();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        Adapter.addItem(0, null, finalTitle, finalArtist, finalSeries, finalLanguage, finalTags);
                        Adapter.notifyDataSetChanged();
                        dialog.hide();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        Adapter.addItem(0, null, finalTitle, finalArtist, finalSeries, finalLanguage, finalTags);
                        Adapter.notifyDataSetChanged();
                    }
                };
                targets.add(target);
                Picasso.with(getBaseContext()).load(picurl).tag("MainDownload").into(target);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
                String text;
                NetworkResponse response = error.networkResponse;
                if (response != null) {
                    text = getString(R.string.could_not1) + Integer.toString(number) + getString(R.string.could_not2);
                }
                else {
                    text = error.getLocalizedMessage();
                }
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                toast.show();
            }
        });
        requestQueue.add(request);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        type = intent.getStringExtra("type");
        isNumber = intent.getBooleanExtra("isNumber", false);
        number = intent.getIntExtra("number", 0);
        if (isNumber || (!isNumber && type.equals("language"))) {
            getMenuInflater().inflate(R.menu.search_number, menu);
            if (isNumber)
                findItem(menu, "bookmarkTitle");
            else
                findItem(menu, "bookmarkType");
        }
        else {
            getMenuInflater().inflate(R.menu.search, menu);
            findItem(menu, "bookmarkType");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.openBrowser:
                String URL;
                if (isNumber) {
                    URL = "https://hitomi.la/galleries/" + Integer.toString(number) + ".html";
                }
                else {
                    switch (type) {
                        case "male":
                        case "female":
                            URL = "https://hitomi.la/tag/" + type + ":" + taga + "-" + language + "-1.html";
                            break;
                        case "language":
                            URL = "https://hitomi.la/index-" + taga + "-1.html";
                            break;
                        default:
                            URL = "https://hitomi.la/" + type + "/" + taga + "-" + language + "-1.html";
                            break;
                    }
                }
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
                startActivity(browser);
                break;
            case R.id.bookmarkTitle:
                Intent intent = getIntent();
                name = intent.getStringExtra("name");
                type = intent.getStringExtra("type");
                isNumber = intent.getBooleanExtra("isNumber", false);
                number = intent.getIntExtra("number", 0);
                if (isNumber || (!isNumber && type.equals("language"))) {
                    if (isNumber)
                        checkItem(item, "bookmarkTitle");
                    else
                        checkItem(item, "bookmarkType");
                }
                else {
                    checkItem(item, "bookmarkType");
                }
                break;
            case R.id.shareURL:
                intent = new Intent(Intent.ACTION_SEND);
                if (isNumber) {
                    URL = "https://hitomi.la/galleries/" + Integer.toString(number) + ".html";
                }
                else {
                    switch (type) {
                        case "male":
                        case "female":
                            URL = "https://hitomi.la/tag/" + type + ":" + taga + "-" + language + "-1.html";
                            break;
                        case "language":
                            URL = "https://hitomi.la/index-" + taga + "-1.html";
                            break;
                        default:
                            URL = "https://hitomi.la/" + type + "/" + taga + "-" + language + "-1.html";
                            break;
                    }
                }
                intent.putExtra(Intent.EXTRA_TEXT, URL);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, getString(R.string.share_with)));
                break;
            case R.id.language:
                View v = findViewById(R.id.language);
                PopupMenu popup = new PopupMenu(this, v);
                final String[] langArray = getResources().getStringArray(R.array.LanguageName_Localized);
                Arrays.sort(langArray, String.CASE_INSENSITIVE_ORDER);
                final ArrayList<String> listArray = new ArrayList<>();
                Collections.addAll(listArray, langArray);
                listArray.add(0, getString(R.string.all));
                for (String a:listArray)
                    popup.getMenu().add(a);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String LanguageName = item.getTitle().toString();
                        for (int i = 0; i < listArray.size(); i++)
                            if (LanguageName.equals(listArray.get(i))) {
                                langIndex = i;
                                break;
                            }
                        ListView listView = (ListView)findViewById(R.id.listview3);
                        Adapter.removeAll();
                        listView.setAdapter(Adapter);
                        arr.clear();
                        arr2.clear();
                        targets.clear();
                        Adapter.notifyDataSetChanged();
                        list = 1;
                        itemCount = 0;
                        loadingMore = false;
                        Picasso.with(getApplicationContext()).cancelTag("MainDownload");
                        request.cancel();
                        downloadTask(list, langIndex);
                        return false;
                    }
                });
                popup.show();
        }
        return super.onOptionsItemSelected(item);
    }

    void findItem(Menu menu, String string) {
        MenuItem item = menu.findItem(R.id.bookmarkTitle);
        SharedPreferences shared = getSharedPreferences("pref", MODE_PRIVATE);
        String json = shared.getString(string,"[]");
        boolean isFound = false;
        try {
            JSONArray jsonArray = new JSONArray(json);
            int len = jsonArray.length();
            for (int a = 0; a < len; a++)
                switch (string) {
                    case "bookmarkTitle":
                        if (jsonArray.get(a).equals(Integer.toString(number))) {
                            isFound = true;
                            break;
                        }
                        break;
                    default:
                        if (jsonArray.get(a).equals(type + ":" + name)) {
                            isFound = true;
                            break;
                        }
                        break;
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        item.setChecked(isFound);
    }

    void checkItem(MenuItem item, String string) {
        SharedPreferences shared = getSharedPreferences("pref", MODE_PRIVATE);
        String json = shared.getString(string,"[]");
        try {
            JSONArray jsonArray = new JSONArray(json);
            int len = jsonArray.length();
            boolean isFound = false;
            for (int a = 0; a < len; a++)
                switch (string) {
                    case "bookmarkTitle":
                        if (jsonArray.get(a).equals(Integer.toString(number))) {
                            jsonArray = StringE.remove(a, jsonArray);
                            isFound = true;
                            item.setChecked(false);
                            break;
                        }
                        break;
                    default:
                        if (jsonArray.get(a).equals(type + ":" + name)) {
                            jsonArray = StringE.remove(a, jsonArray);
                            isFound = true;
                            item.setChecked(false);
                            break;
                        }
                        break;
                }
            if (!isFound) {
                switch (string) {
                    case "bookmarkTitle":
                        jsonArray.put(Integer.toString(number));
                        item.setChecked(true);
                        break;
                    default:
                        jsonArray.put(type + ":" + name);
                        item.setChecked(true);
                        break;
                }
            }
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(string,jsonArray.toString());
            editor.apply();
        } catch (JSONException ignored) {
        }
    }
}

class Point {
    private String name;
    private String localizedName;

    Point(String name, String localizedName) {
        this.name = name;
        this.localizedName = localizedName;
    }

    public String getName() {
        return name;
    }

    String getLocalizedName() {
        return localizedName;
    }

}

class Descending implements Comparator<Point> {

    @Override
    public int compare(Point o1, Point o2) {
        return o1.getLocalizedName().compareTo(o2.getLocalizedName());
    }

}
