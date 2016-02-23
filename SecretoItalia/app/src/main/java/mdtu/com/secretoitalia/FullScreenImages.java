package mdtu.com.secretoitalia;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by koctyabondar on 9/18/15.
 */
public class FullScreenImages extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image);

        String url = getIntent().getStringExtra("url");

        ImageView mImageView = (ImageView) findViewById(R.id.fullImage);

        Picasso.with(this).load(url).into(mImageView);

        PhotoViewAttacher mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.update();
    }
}
