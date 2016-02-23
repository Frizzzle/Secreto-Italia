package mdtu.com.secretoitalia.ImageSlideLib;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mdtu.com.secretoitalia.R;

public class ViewPagerExampleActivity extends Activity {
	
	/**
	 * Step 1: Download and set up v4 support library: http://developer.android.com/tools/support-library/setup.html
	 * Step 2: Create ExtendedViewPager wrapper which calls TouchImageView.canScrollHorizontallyFroyo
	 * Step 3: ExtendedViewPager is a custom view and must be referred to by its full package name in XML
	 * Step 4: Write TouchImageAdapter, located below
	 * Step 5. The ViewPager in the XML should be ExtendedViewPager
	 */
    public static ViewPagerExampleActivity viewInstanse;
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewInstanse = this;
        setContentView(R.layout.activity_viewpager_fullscreen);
        ExtendedViewPager mViewPager = (ExtendedViewPager) findViewById(R.id.view_pager);
        TouchImageAdapter adapter = new TouchImageAdapter();
        String url = getIntent().getStringExtra("url");
        String url1 = getIntent().getStringExtra("url1");
        String url2 = getIntent().getStringExtra("url2");
        ArrayList<String> urls = new ArrayList<String>();
        urls.add(url);
        urls.add(url1);
        urls.add(url2);
        adapter.setUrl(urls);
        mViewPager.setAdapter(adapter);
    }

    static class TouchImageAdapter extends PagerAdapter {

        public  int count;
        ArrayList<String> urls;
        @Override
        public int getCount() {
        	return 3;
        }
        public void setUrl(ArrayList<String> urls){
            this.urls = urls;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            TouchImageView img = new TouchImageView(container.getContext());
            Picasso.with(ViewPagerExampleActivity.viewInstanse).load(urls.get(position)).into(img);
            container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            return img;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}
