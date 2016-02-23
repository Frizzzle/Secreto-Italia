package mdtu.com.secretoitalia.WebViews;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import mdtu.com.secretoitalia.R;

/**
 * Created by koctyabondar on 9/14/15.
 */
public class ReadPolicy extends Activity {
    private ProgressDialog activityDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_policy);
        activityDialog = new ProgressDialog(this);
        activityDialog.setMessage("Loading page...");
        activityDialog.setCancelable(false);
        activityDialog.show();
        WebView webView = (WebView) findViewById(R.id.readPolicy);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://secretoitalia.com/product-changes");
        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                activityDialog.hide();
            }
        });
    }
}
