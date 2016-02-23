package mdtu.com.secretoitalia.Adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mdtu.com.secretoitalia.BagActivity;
import mdtu.com.secretoitalia.Checkout;
import mdtu.com.secretoitalia.DoubleRound;
import mdtu.com.secretoitalia.MainActivity;
import mdtu.com.secretoitalia.Model.InventoryItem;
import mdtu.com.secretoitalia.R;
import mdtu.com.secretoitalia.Wrappers.BagWrapper;
import mdtu.com.secretoitalia.Wrappers.SearchDialogThread;

/**
 * Created by koctyabondar on 9/11/15.
 */
public class BagAdapter extends BaseAdapter {

    private Context mContext;
    private Animation animationEnlarge;
    private String title;
    private ListView bagLIst;
    private ArrayList<InventoryItem> inventoryItems;
    private callback del;
    private TextView count;

    public BagAdapter() {
        this.mContext = BagActivity.bagAcvitityInstance.getApplicationContext();
        animationEnlarge = AnimationUtils.loadAnimation(mContext, R.anim.slide_up_left);
    }

    public static BagAdapter createBagListAdapter(ArrayList<InventoryItem> inventoryItems,ListView bagList,callback del){
        BagAdapter result = new BagAdapter();
        result.inventoryItems = inventoryItems;
        result.bagLIst = bagList;
        result.del =del;

        return result;
    }
    @Override
    public int getCount() {
        return inventoryItems.size() +1 ;
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
        View rowView = null;
        if ( i  ==  inventoryItems.size()) {
                if(inventoryItems.size() == 0) {
                    rowView = inflater.inflate(R.layout.under_bag_empty, viewGroup, false);
                    Button continueButton = (Button) rowView.findViewById(R.id.continueButton);
                    continueButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            del.goFinish();
                        }
                    });
                }else {
                    rowView = inflater.inflate(R.layout.under_bag_card, viewGroup, false);
                    TextView price = (TextView) rowView.findViewById(R.id.totalPrice);
                    price.setText("€ " + Double.toString(DoubleRound.round(BagWrapper.getInstance().getTotalPrice(), 2)));
                    Button checount = (Button) rowView.findViewById(R.id.checkoutButton);
                    checount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            del.goCheck();
                        }
                    });
                    Button continueButton = (Button) rowView.findViewById(R.id.continueButton);
                    continueButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            del.goFinish();

                        }
                    });
                }

        }else {
            rowView = inflater.inflate(R.layout.item_bag_card, viewGroup, false);

            TextView price = (TextView) rowView.findViewById(R.id.bagPrice);
            TextView name = (TextView) rowView.findViewById(R.id.bagNameItem);
            TextView size = (TextView) rowView.findViewById(R.id.bagSize);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.bagImage);
            ImageView recycle = (ImageView) rowView.findViewById(R.id.recycle);
            count = (TextView) rowView.findViewById(R.id.bagQTV);

            InventoryItem currentItem = inventoryItems.get(i);
            name.setText(currentItem.name);
            price.setText("€ " + Double.toString(DoubleRound.round(Double.parseDouble(currentItem.getPrice()) * Integer.parseInt(currentItem.count),2)));
            size.setText(currentItem.size);
            Picasso.with(mContext).load(currentItem.url).into(imageView);

            recycle.setTag(currentItem.objectId + '=' + currentItem.size);

            recycle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] split = ((String) v.getTag()).split("=");
                    BagWrapper.getInstance().remove(split[0],split[1]);
                    bagLIst.setAdapter(BagAdapter.createBagListAdapter(BagWrapper.getInstance().getBag(), bagLIst, del));
                    del.removes();
                }
            });

            Button editCount = (Button) rowView.findViewById(R.id.edit);
            editCount.setTag(currentItem.objectId + '=' + currentItem.size);
            if (currentItem.count != null) {
                count.setText("QTY: " +currentItem.count);
            }else {
                count.setText("QTY: 1");
            }
            editCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] split = ((String) v.getTag()).split("=");
                    del.showDialog(split[0],split[1]);
                }
            });


            CardView cardView = (CardView) rowView.findViewById(R.id.card_view);
            cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
            cardView.setMaxCardElevation(0.0f);
            cardView.setRadius(0.0f);

        }

        return rowView;

    }
    public interface callback {
        public int showDialog(String id,String size);
        public void goCheck();
        public void goFinish();
        public void removes();
    }
}
