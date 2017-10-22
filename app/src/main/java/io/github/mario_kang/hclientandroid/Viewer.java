package io.github.mario_kang.hclientandroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Viewer extends AppCompatActivity {

    String viewerURL;
    String title;
    StringE s = new StringE();
    StringRequest request;
    ProgressBar bar;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();
        bar = (ProgressBar)findViewById(R.id.progressBar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Intent intent = getIntent();
        viewerURL = intent.getExtras().getString("viewerURL");
        title = intent.getExtras().getString("viewerTitle");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://hitomi.la" + viewerURL;
        request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.hide();
                String HTMLString = "<!DOCTYPE HTML><style>img{width:100%;}</style>";
                String list[] = s.SplitString(response, "<div class=\"img-url\">//");
                for (int i = 0; i <= list.length - 2; i++) {
                    String galleries = s.SplitString(list[i + 1], "</div>")[0];
                    String num = s.SplitString(s.SplitString(galleries, "/galleries/")[1], "/")[0];
                    String numb = s.SplitString(galleries, "/")[3];
                    int a = 97 + Integer.parseInt(num) % 2;
                    char t = Character.toChars(a)[0];
                    HTMLString += "<img src=\"https://" + Character.toString(t) + "a.hitomi.la/galleries/" + num + "/" + numb + "\" >";
                }
                WebView webView = (WebView) findViewById(R.id.webView);
                webView.getSettings().setSupportZoom(true);
                webView.getSettings().setBuiltInZoomControls(true);
                webView.getSettings().setDisplayZoomControls(!(getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH)));
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        bar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        bar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        super.onReceivedError(view, request, error);
                        bar.setVisibility(View.INVISIBLE);
                    }
                });
                webView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        super.onProgressChanged(view, newProgress);
                        bar.setProgress(newProgress);
                    }
                });
                webView.loadData(HTMLString, "text/html", null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
                String text = error.getLocalizedMessage();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                toast.show();
            }
        });
        queue.add(request);
    }

    @Override
    protected void onStop() {
        super.onStop();
        dialog.dismiss();
        if (request != null)
            request.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.openBrowser:
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://hitomi.la" + viewerURL));
                startActivity(browser);
                break;
            case R.id.shareURL:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "https://hitomi.la" + viewerURL);
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
