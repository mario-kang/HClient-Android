package io.github.mario_kang.hclientandroid;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONArray;
import org.json.JSONException;

import static android.content.Context.MODE_PRIVATE;

public class BookmarkFragment extends Fragment {

    RequestQueue queue;
    StringRequest request;
    StringE s = new StringE();
    ListViewAdapter Adapter;
    int size = 0;
    ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, null);
        final SharedPreferences shared = getContext().getSharedPreferences("pref", MODE_PRIVATE);
        dialog = new ProgressDialog(getContext());
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        queue = Volley.newRequestQueue(getContext());
        Adapter = new ListViewAdapter();
        ListView listView = view.findViewById(R.id.listview4);
        listView.setAdapter(Adapter);
        String jsonTitle = shared.getString("bookmarkTitle", "[]");
        String jsonTag = shared.getString("bookmarkType", "[]");
        try {
            final JSONArray jsonArrayTitle = new JSONArray(jsonTitle);
            final JSONArray jsonArrayTag = new JSONArray(jsonTag);
            int i = 0;
            for (;i < jsonArrayTitle.length(); i++) {
                String num = jsonArrayTitle.getString(i);
                DownloadTitle(i, num);
            }
            for (; i < jsonArrayTitle.length() + jsonArrayTag.length(); i++) {
                String tag = jsonArrayTag.getString(i - jsonArrayTitle.length());
                DownloadTag(i, tag);
            }
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                    int i = 0;
                    String t = "";
                    boolean a = false;
                    for (;i < jsonArrayTitle.length(); i++)
                        if (position == i) {
                            t = jsonArrayTitle.optString(i);
                            a = true;
                            break;
                        }
                    for (; i < jsonArrayTitle.length() + jsonArrayTag.length() && !a; i++)
                        if (position == i) {
                            t = jsonArrayTag.optString(i - jsonArrayTitle.length());
                            break;
                        }
                    final boolean finalA = a;
                    final int finalI = i;
                    alertBuilder.setTitle(t).setItems(R.array.longClick, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences.Editor editor = shared.edit();
                            JSONArray jsonArray;
                            if (finalA) {
                                jsonArray = StringE.remove(finalI, jsonArrayTitle);
                                editor.putString("bookmarkTitle",jsonArray.toString());
                                Adapter.remove(finalI);
                                Adapter.notifyDataSetChanged();
                            }
                            else {
                                jsonArray = StringE.remove(finalI - jsonArrayTitle.length(), jsonArrayTag);
                                editor.putString("bookmarkType",jsonArray.toString());
                                Adapter.remove(finalI);
                                Adapter.notifyDataSetChanged();
                            }
                            editor.apply();
                        }
                    });
                    alertBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertBuilder.show();
                    return true;
                }
            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int i = 0;
                    String t = "";
                    boolean a = false;
                    for (;i < jsonArrayTitle.length(); i++)
                        if (position == i) {
                            t = jsonArrayTitle.optString(i);
                            a = true;
                            break;
                        }
                    for (; i < jsonArrayTitle.length() + jsonArrayTag.length() && !a; i++)
                        if (position == i) {
                            t = jsonArrayTag.optString(i - jsonArrayTitle.length());
                            break;
                        }
                    if (a) {
                        t = "/galleries/" + t;
                        Intent intent = new Intent(view.getContext(), InfoDetail.class);
                        intent.putExtra("number", t);
                        intent.putExtra("djURL", "https://hitomi.la" + t + ".html");
                        view.getContext().startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(view.getContext(), io.github.mario_kang.hclientandroid.SearchView.class);
                        String type = s.SplitString(t, ":")[0];
                        String name = s.SplitString(t, ":")[1].replace(" ", "_");
                        intent.putExtra("name", name);
                        intent.putExtra("type", type);
                        intent.putExtra("isNumber", false);
                        view.getContext().startActivity(intent);
                    }
                }
            });
        }
        catch (JSONException ignored) {

        }
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        Picasso.with(getContext()).cancelTag("InfoTag");
        dialog.dismiss();
        try {
            request.cancel();
        }
        catch (NullPointerException ignored) {

        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("HClient");
    }

    void DownloadTitle(final int i, String num) {
        dialog.show();
        size++;
        Adapter.setCount(size);
        String URL = "https://hitomi.la/galleries/" + num + ".html";
        queue = Volley.newRequestQueue(getContext());
        request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String anime = s.SplitString(s.SplitString(response,"<td>Type</td><td>")[1], "</a></td>")[0];
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
                                Title = "";
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
                                    artist += Html.fromHtml(s.SplitString(artistlist[i], ".html\">")[1], Html.FROM_HTML_MODE_LEGACY).toString();
                                else
                                    artist += Html.fromHtml(s.SplitString(artistlist[i], ".html\">")[1]).toString();
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
                        Target target = new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                Drawable drawable;
                                drawable = new BitmapDrawable(bitmap);
                                Adapter.addItem(i, drawable, finalTitle, finalArtist, finalSeries, finalLanguage, finalTags);
                                dialog.hide();
                                Adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                Adapter.addItem(i, null, finalTitle, finalArtist, finalSeries, finalLanguage, finalTags);
                                dialog.hide();
                                Adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                                Adapter.addItem(i, null, finalTitle, finalArtist, finalSeries, finalLanguage, finalTags);
                                Adapter.notifyDataSetChanged();
                            }
                        };
                        Picasso.with(getContext()).load(picurl).tag("InfoTag").into(target);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
                String text = error.getLocalizedMessage();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getContext(),text,duration);
                toast.show();
            }

        });
        queue.add(request);
    }

    void DownloadTag(int j, String tag) {
        size++;
        Adapter.setCount(size);
        Adapter.addItem(j, tag);
        Adapter.notifyDataSetChanged();
    }
}