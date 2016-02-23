package mdtu.com.secretoitalia;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Pair;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by koctyabondar on 9/7/15.
 */
public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

    private String url;
    private ImageView imageView;
    public static ArrayList<Pair<String,Bitmap>> cashImage = new ArrayList<Pair<String, Bitmap>>();

    public static Boolean isCahsed(String url) {
        for (Pair<String, Bitmap> item :
                cashImage) {
            if (item.first == url) {
                return true;
            }
        }
        return false;
    }

    public static Bitmap getBitmap(String url) {
        for (Pair<String, Bitmap> item :
                cashImage) {
            if (item.first == url) {
                return item.second;
            }
        }
        return null;
    }
    public ImageLoadTask(String url, ImageView imageView) {
        this.url = url;
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            URL urlConnection = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);

        cashImage.add(new Pair<String, Bitmap>(url,result));

        imageView.setImageBitmap(result);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);


    }

}
