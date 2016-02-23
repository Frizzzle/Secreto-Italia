package mdtu.com.secretoitalia.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import mdtu.com.secretoitalia.ImageLoadTask;
import mdtu.com.secretoitalia.MainActivity;
import mdtu.com.secretoitalia.Model.InventoryItem;
import mdtu.com.secretoitalia.R;

/**
 * Created by koctyabondar on 9/7/15.
 */
public class InventoryAdapter extends BaseAdapter {

    private Context mContext;
    private Animation animationEnlarge;
    private String title;
    private ArrayList<InventoryItem> inventoryItems;

    public InventoryAdapter() {
        this.mContext = MainActivity.mainAcvitityInstance.getApplicationContext();
        animationEnlarge = AnimationUtils.loadAnimation(mContext, R.anim.slide_up_left);
    }

    public static InventoryAdapter createInventoryListAdapter(String title,ArrayList<InventoryItem> inventoryItems){
        InventoryAdapter result = new InventoryAdapter();
        result.title = title;
        result.inventoryItems = inventoryItems;

        return result;
    }
    @Override
    public int getCount() {
        return inventoryItems.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater)
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_card, viewGroup, false);

        TextView price = (TextView) rowView.findViewById(R.id.price);
        TextView title = (TextView) rowView.findViewById(R.id.cardTitle);
        TextView name = (TextView) rowView.findViewById(R.id.itemTitle);
        TextView finalPrice = (TextView) rowView.findViewById(R.id.discount);
        if (i == 0) {
            title.setText(this.title);
        }
        name.setText(inventoryItems.get(i).name);
        price.setText(inventoryItems.get(i).price + " $");
        price.setText("€ " + inventoryItems.get(i).price);
        if (Double.parseDouble(inventoryItems.get(i).finalPrice) > 0.0) {
            price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            finalPrice.setText("€ " + inventoryItems.get(i).finalPrice );
        }else {
            finalPrice.setText("");
        }
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
        String imageUrl = inventoryItems.get(i).url;

            Picasso.with(mContext).load(imageUrl).into(imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        rowView.setTag(inventoryItems.get(i).objectId);

        CardView cardView = (CardView) rowView.findViewById(R.id.card_view);
        cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
        cardView.setMaxCardElevation(0.0f);
        cardView.setRadius(0.0f);

        return rowView;

    }
}
