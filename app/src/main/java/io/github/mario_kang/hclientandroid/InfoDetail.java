package io.github.mario_kang.hclientandroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InfoDetail extends AppCompatActivity {

    String viewerURL;
    String djURL;
    String anime;
    StringE s = new StringE();
    final List<Target> targets = new ArrayList<>();
    String number;
    String viewerTitle;
    RequestQueue queue;
    StringRequest request;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        djURL = intent.getExtras().getString("djURL");
        number = intent.getExtras().getString("number");
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();
        setContentView(R.layout.activity_info_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        queue = Volley.newRequestQueue(this);
        request = new StringRequest(Request.Method.GET, djURL,
                new Response.Listener<String>() {
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
                            viewerURL = s.SplitString(s.SplitString(response, "file: \"")[1], "\",")[0];
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
                            viewerURL = s.SplitString(s.SplitString(response, "<div class=\"cover\"><a href=\"")[1], "\"><img src=")[0];
                        }
                        String artist1 = s.SplitString(s.SplitString(response, "</h2>")[0], "<h2>")[1];
                        String artist = getString(R.string.artist);
                        String artistlist[] = s.SplitString(artist1, "</a></li>");
                        if (artist1.contains("N/A"))
                            artist += "N/A";
                        else
                            for (int i = 0; i <= artistlist.length - 2; i++) {
                                if (Build.VERSION.SDK_INT >= 24)
                                    artist += Html.fromHtml(s.SplitString(artistlist[i], ".html\">")[1], Html.FROM_HTML_MODE_LEGACY).toString();
                                else
                                    artist += Html.fromHtml(s.SplitString(artistlist[i], ".html\">")[1]).toString();
                                if (i != artistlist.length - 2)
                                    artist += ", ";
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
                        String type1 = s.SplitString(s.SplitString(response, "<td>Type")[1], "</a></td>")[0];
                        String type = getString(R.string.type);
                        if (type1.contains("N/A"))
                            type += "N/A";
                        else
                            if (Build.VERSION.SDK_INT >= 24)
                                type += Html.fromHtml(s.SplitString(type1, ".html\">")[1].replace(" ", "").replace("CG", " CG").replace("\n", ""), Html.FROM_HTML_MODE_LEGACY).toString();
                            else
                                type += Html.fromHtml(s.SplitString(type1, ".html\">")[1].replace(" ", "").replace("CG", " CG").replace("\n", "")).toString();
                        String series1 = s.SplitString(s.SplitString(response, "<td>Series</td>")[1], "</ul>")[0];
                        String series = getString(R.string.series);
                        String series2[] = s.SplitString(series1, "</a></li>");
                        if (series1.contains("N/A"))
                            series += "N/A";
                        else
                            for (int i = 0; i <= series2.length - 2; i++) {
                                if (Build.VERSION.SDK_INT >= 24)
                                    series += Html.fromHtml(s.SplitString(series2[i], ".html\">")[1], Html.FROM_HTML_MODE_LEGACY).toString();
                                else
                                    series += Html.fromHtml(s.SplitString(series2[i], ".html\">")[1]).toString();
                                if (i != series2.length - 2)
                                    series += ", ";
                            }
                        String tags1 = s.SplitString(s.SplitString(response, "Tags")[1], "</td>")[1];
                        String tags = getString(R.string.tag);
                        String tags2[] = s.SplitString(tags1, "</a></li>");
                        if (tags2.length == 1)
                            tags += "N/A";
                        else
                            for (int i = 0; i <= tags2.length - 2; i++) {
                                if (Build.VERSION.SDK_INT >= 24)
                                    tags += Html.fromHtml(s.SplitString(tags2[i], ".html\">")[1], Html.FROM_HTML_MODE_LEGACY).toString();
                                else
                                    tags += Html.fromHtml(s.SplitString(tags2[i], ".html\">")[1]).toString();
                                if (i != tags2.length - 2)
                                    tags += ", ";
                            }
                        String characters1 = s.SplitString(s.SplitString(response, "Characters")[1], "</td>")[1];
                        String characters = getString(R.string.characters);
                        String characters2[] = s.SplitString(characters1, "</a></li>");
                        if (characters2.length == 1)
                            characters += "N/A";
                        else
                            for (int i = 0; i <= characters2.length - 2; i++) {
                                if (Build.VERSION.SDK_INT >= 24)
                                    characters += Html.fromHtml(s.SplitString(characters2[i], ".html\">")[1], Html.FROM_HTML_MODE_LEGACY).toString();
                                else
                                    characters += Html.fromHtml(s.SplitString(characters2[i], ".html\">")[1]).toString();
                                if (i != characters2.length - 2)
                                    characters += ", ";
                            }
                        String groups1 = s.SplitString(s.SplitString(response, "<td>Group")[1], "</td>")[1];
                        String groups = getString(R.string.groups);
                        String groups2[] = s.SplitString(groups1, "</a></li>");
                        if (groups2.length == 1)
                            groups += "N/A";
                        else {
                            for (int i = 0; i <= groups2.length - 2; i++) {
                                if (Build.VERSION.SDK_INT >= 24)
                                    groups += Html.fromHtml(s.SplitString(groups2[i], ".html\">")[1], Html.FROM_HTML_MODE_LEGACY).toString();
                                else
                                    groups += Html.fromHtml(s.SplitString(groups2[i], ".html\">")[1]).toString();
                                if (i != groups2.length - 2)
                                    groups += ", ";
                            }
                        }
                        String dates = getString(R.string.date);
                        String dates1 = s.SplitString(s.SplitString(response, "\"date\">")[1], "</span>")[0] + "00";
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZZ", Locale.US);
                        try {
                            Date date = format.parse(dates1);
                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                            String dates3 = format1.format(date);
                            dates += dates3;
                        } catch (ParseException e) {
                            dates += "N/A";
                        }
                        String picurl = "https:" + Pic;
                        Target target = new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                ImageView imageView = (ImageView)findViewById(R.id.detailImage);
                                imageView.setImageBitmap(bitmap);
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        };
                        targets.add(target);
                        Picasso.with(getBaseContext()).load(picurl).tag("InfoTag").into(target);
                        String text = artist + "\n" + groups + "\n" + type + "\n" + language + "\n" + series + "\n" + characters + "\n" + tags + "\n" + dates;
                        TextView titleView = (TextView)findViewById(R.id.detailTitle);
                        titleView.setText(Title);
                        TextView textView = (TextView)findViewById(R.id.detailText);
                        textView.setText(text);
                        getSupportActionBar().setTitle(Title);
                        viewerTitle = Title;
                        dialog.hide();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String text = error.getLocalizedMessage();
                dialog.hide();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getApplicationContext(),text,duration);
                toast.show();
            }

        });
        queue.add(request);
    }

    @Override
    protected void onStop() {
        super.onStop();
        request.cancel();
        Picasso.with(this).cancelTag("InfoTag");
        dialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.infodetail, menu);
        MenuItem item = menu.findItem(R.id.bookmarkTitle);
        SharedPreferences shared = getSharedPreferences("pref", MODE_PRIVATE);
        String json = shared.getString("bookmarkTitle","[]");
        String numberA = number.replace("/galleries/", "");
        boolean isFound = false;
        try {
            JSONArray jsonArray = new JSONArray(json);
            int len = jsonArray.length();
            for (int a = 0; a < len; a++) {
                if (jsonArray.get(a).equals(numberA)) {
                    isFound = true;
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        item.setChecked(isFound);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.openViewer:
                if (!(anime.contains("anime"))) {
                Intent viewer = new Intent(this, Viewer.class);
                viewer.putExtra("viewerURL", viewerURL);
                viewer.putExtra("viewerTitle", viewerTitle);
                startActivity(viewer);
                }
                else {
                    String a = "https:" + viewerURL;
                    a = a.replace("\\u002d", "-");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(a), "video/mp4");
                    startActivity(Intent.createChooser(intent, ""));
                }
                break;
            case R.id.openBrowser:
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(djURL));
                startActivity(browser);
                break;
            case R.id.bookmarkTitle:
                SharedPreferences shared = getSharedPreferences("pref", MODE_PRIVATE);
                String json = shared.getString("bookmarkTitle","[]");
                String numberA = number.replace("/galleries/", "");
                try {
                    JSONArray jsonArray = new JSONArray(json);
                    int len = jsonArray.length();
                    boolean isFound = false;
                    for (int a = 0; a < len; a++)
                        if (jsonArray.get(a).equals(numberA)) {
                            jsonArray = StringE.remove(a, jsonArray);
                            isFound = true;
                            item.setChecked(false);
                            break;
                        }
                    if (!isFound) {
                        jsonArray.put(numberA);
                        item.setChecked(true);
                    }
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putString("bookmarkTitle",jsonArray.toString());
                    editor.apply();
                } catch (JSONException ignored) {

                }
                break;
            case R.id.shareURL:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, djURL);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, getString(R.string.share_with)));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
