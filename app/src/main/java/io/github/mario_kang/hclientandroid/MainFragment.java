package io.github.mario_kang.hclientandroid;

import android.app.ProgressDialog;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainFragment extends Fragment {

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
    ProgressDialog dialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, null);
        setHasOptionsMenu(true);
        Adapter = new ListViewAdapter();
        dialog = new ProgressDialog(getContext());
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        downloadTask(list);
        ListView listView = view.findViewById(R.id.listview1);
        listView.setAdapter(Adapter);
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
                            downloadTask(list);
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
        return view;
    }

    public void downloadTask(int page) {
        dialog.show();
        String url = String.format(Locale.ENGLISH, "https://hitomi.la/index-all-%d.html",page);
        requestQueue = Volley.newRequestQueue(getContext());
        request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
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
                String text = error.getLocalizedMessage();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getContext().getApplicationContext(),text,duration);
                toast.show();
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
            Picasso.with(getContext()).load(url).tag("MainDownload").into(target);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().invalidateOptionsMenu();
        Picasso.with(getContext()).cancelTag("MainDownload");
        request.cancel();
        dialog.dismiss();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("HClient");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String url = "https://hitomi.la/";
        switch (item.getItemId()) {
            case R.id.openBrowser:
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browser);
                break;
            case R.id.shareURL:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, url);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, getString(R.string.share_with)));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
