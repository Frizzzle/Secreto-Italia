package mdtu.com.secretoitalia;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mdtu.com.secretoitalia.Adapters.BagAdapter;
import mdtu.com.secretoitalia.Model.InventoryItem;
import mdtu.com.secretoitalia.Wrappers.BagWrapper;
import mdtu.com.secretoitalia.Wrappers.SearchDialogThread;

/**
 * Created by koctyabondar on 9/11/15.
 */
public class BagActivity extends Activity implements BagAdapter.callback {
     private BagAdapter adapter;
     private ListView bagListView;
     private TextView count;
     private String itemID;
    private ProgressDialog activityDialog;
    private InventoryItem currentItem;
    private Integer maxCount;
    public static BagActivity bagAcvitityInstance;
    private String itemSize;
    private boolean isNotNetworkConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bag_view);
        bagAcvitityInstance = this;
        interetSwitchEvent();
        initView();
    }
    private void interetSwitchEvent(){
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                ConnectivityManager connectivity = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);


                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info == null) {
                    // There are no active networks.
                    isNotNetworkConnected = true;

                } else {
                    isNotNetworkConnected = false;
                }



            }

        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }
    private void networkAlert(){
        android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(BagActivity.this);
        alert.setTitle("No Internet connection");
        alert.setMessage("Make sure your device is connected to the internet");
        alert.setPositiveButton("OK", null);
        alert.show();
    }
    private void initView() {
        initActionBar();
        bagListView = (ListView) findViewById(R.id.bagItems);
        adapter = BagAdapter.createBagListAdapter(BagWrapper.getInstance().getBag(),bagListView,this);
        bagListView.setAdapter(adapter);
        activityDialog = new ProgressDialog(this);

    }
    private void initActionBar() {
        LinearLayout lar1 = (LinearLayout)findViewById(R.id.include5);
        TextView menu1 = (TextView)lar1.findViewById(R.id.appTitle);
        menu1.setText("YOUR BAG: " + BagWrapper.getInstance().getCountInBag() + " ITEMS");
        LinearLayout toggle = (LinearLayout) lar1.findViewById(R.id.show_menu);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)toggle.getLayoutParams();
        params.setMargins(-10, -10, -10, -10);
        toggle.setLayoutParams(params);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageView img = (ImageView) lar1.findViewById(R.id.menuIcon);
        img.setImageResource(R.mipmap.side_arrow_icon);
        img.setAlpha(100);
    }

    @Override
    public int showDialog(String id,String size) {
        this.itemID = id;
        this.itemSize = size;
        RelativeLayout linearLayout = new RelativeLayout(this);
        final NumberPicker aNumberPicker = new NumberPicker(this);
        aNumberPicker.setMaxValue(10);
        aNumberPicker.setMinValue(1);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
        RelativeLayout.LayoutParams numPicerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        numPicerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        linearLayout.setLayoutParams(params);
        linearLayout.addView(aNumberPicker, numPicerParams);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Select the quantity");
        alertDialogBuilder.setView(linearLayout);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                Log.e("", "New Quantity Value : " + aNumberPicker.getValue());
                                if (isNotNetworkConnected) {
                                    networkAlert();
                                    return;
                                }
                                BagWrapper.getInstance().updateCount(itemID,Integer.toString(aNumberPicker.getValue()),itemSize);
                                adapter = BagAdapter.createBagListAdapter(BagWrapper.getInstance().getBag(),bagListView,bagAcvitityInstance);
                                LinearLayout lar1 = (LinearLayout)findViewById(R.id.include5);
                                TextView menu1 = (TextView)lar1.findViewById(R.id.appTitle);
                                menu1.setText("YOUR BAG: " + BagWrapper.getInstance().getCountInBag() + " ITEMS");
                                bagListView.setAdapter(adapter);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        return 0;
    }

    @Override
    public void goCheck() {
        if (isNotNetworkConnected) {
            networkAlert();
            return;
        }
        ProgressDialog pd = ProgressDialog.show(this, "Check", "Checking items...", true, false);
        SearchDialogThread searchThread = new SearchDialogThread(this,pd);
        searchThread.start();

    }
    public int check() {
        for (InventoryItem item :
                BagWrapper.getInstance().getBag()) {

            maxCount = BagWrapper.getInstance().checkInStock(item,this);

            if (maxCount == 0) {
                currentItem = item;
                return 1 ;
            }else if(Integer.parseInt(item.count) > maxCount) {
                currentItem = item;
                return 2 ;
            }
        }
        return 0 ;
    }
    public void showAlert(int code) {
        switch (code) {
            case 0 :
                Intent goCheckout = new Intent(BagActivity.this,Checkout.class);
                startActivity(goCheckout);
                return;
            case 1 :
                android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(this);
                alert.setTitle("\" " + currentItem.name + "\" - out of stock");
                alert.setMessage("Size : "+currentItem.size+"\nAvailable quantity: "+maxCount+"\nPlease, remove it");
                alert.setPositiveButton("OK", null);
                alert.show();
                break;
            case 2 :
                android.support.v7.app.AlertDialog.Builder alert1= new android.support.v7.app.AlertDialog.Builder(this);
                alert1.setTitle("\" " + currentItem.name + "\"- out of stock");
                alert1.setMessage("Size : "+currentItem.size+"\nAvailable quantity: "+maxCount+"\nPlease, reduce the quantity of items");
                alert1.setPositiveButton("OK", null);
                alert1.show();
                break;

        }
    }
    @Override
    public void removes() {
        LinearLayout lar1 = (LinearLayout)findViewById(R.id.include5);
        TextView menu1 = (TextView)lar1.findViewById(R.id.appTitle);
        menu1.setText("YOUR BAG: " + BagWrapper.getInstance().getCountInBag() + " ITEMS");
        bagListView.setAdapter(adapter);
    }

    @Override
    public void goFinish() {
        finish();
    }
}
