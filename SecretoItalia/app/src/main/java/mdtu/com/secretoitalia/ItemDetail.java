package mdtu.com.secretoitalia;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.facebook.FacebookSdk;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mdtu.com.secretoitalia.Adapters.InventoryAdapter;
import mdtu.com.secretoitalia.Adapters.NoDefaultSpinner;
import mdtu.com.secretoitalia.Adapters.SideMenuAdapter;
import mdtu.com.secretoitalia.ImageSlideLib.ViewPagerExampleActivity;
import mdtu.com.secretoitalia.ImageViewer.ImageViewer;
import mdtu.com.secretoitalia.Model.Attribute;
import mdtu.com.secretoitalia.Model.Category;
import mdtu.com.secretoitalia.Model.InventoryItem;
import mdtu.com.secretoitalia.WebViews.ReadPolicy;
import mdtu.com.secretoitalia.WebViews.SizeGuide;
import mdtu.com.secretoitalia.Wrappers.BagWrapper;

/**
 * Created by koctyabondar on 9/10/15.
 */
public class ItemDetail extends Activity implements View.OnClickListener{
     private InventoryItem currentItem;
    private ArrayList<Attribute> attributeArrayList;
    SliderLayout sliderShow;
    NoDefaultSpinner sizeSpinner;
    private Button addToBag;
    ProgressDialog activityDialog = null;
    AlertDialog.Builder outOfStock;
    boolean isOut;
    ParseQuery qeury;
    private boolean isNotNetworkConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail);
        isOut=true;
        String objectId = getIntent().getStringExtra("objectId");
        String name = getIntent().getStringExtra("name");
        String price = getIntent().getStringExtra("price");
        String finalPrice = getIntent().getStringExtra("finalPrice");
        String url = getIntent().getStringExtra("url");
        String url1 = getIntent().getStringExtra("url1");
        String url2 = getIntent().getStringExtra("url2");
        String secrId = getIntent().getStringExtra("secrId");
        currentItem = new InventoryItem(name,objectId,url,url1,url2,price,finalPrice,secrId);
        outOfStock =new AlertDialog.Builder(this);
        interetSwitchEvent();
        initView();
        FacebookSdk.sdkInitialize(getApplicationContext());

        ParseFacebookUtils.initialize(this);
    }

    void initView() {

        getAttributeFromParse();
        RelativeLayout under = (RelativeLayout)findViewById(R.id.underInclude);
        Button readPolicy = (Button) under.findViewById(R.id.policyButton);
        readPolicy.setOnClickListener(this);
        Button sizeGuide = (Button) under.findViewById(R.id.guideButton);
        sizeGuide.setOnClickListener(this);

        RelativeLayout code = (RelativeLayout)findViewById(R.id.itemCodeInclude);
        TextView productCode = (TextView) code.findViewById(R.id.productCode);
        productCode.setText(currentItem.secretoId);

        LinearLayout lar1 = (LinearLayout)findViewById(R.id.include3);
        TextView menu1 = (TextView)lar1.findViewById(R.id.appTitle);
        menu1.setText("SECRETO ITALIA");
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
        ImageView bagIcon = (ImageView) lar1.findViewById(R.id.showBagImage);
        bagIcon.setImageResource(R.drawable.thebagicon);
        TextView bagCount = (TextView) lar1.findViewById(R.id.bagCountText);
        Integer countItems = BagWrapper.getInstance().getCountInBag();
        if(countItems >= 100 || bagCount.getTextSize() == 12 ) {
            bagCount.setTextSize(10);
        }else {
            bagCount.setTextSize(12);
        }
        bagCount.setText(Integer.toString(countItems));
        bagCount.setVisibility(View.VISIBLE);

        ImageView call = (ImageView) findViewById(R.id.callToOrder);
        call.setOnClickListener(this);

        BaseSliderView.ScaleType type = BaseSliderView.ScaleType.CenterInside;

        sliderShow = (SliderLayout) findViewById(R.id.imageSlider);
        sliderShow.stopAutoCycle();
        DefaultSliderView sliderView = new DefaultSliderView(this);
        sliderView.image(currentItem.url).setScaleType(type);
        sliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
            @Override
            public void onSliderClick(BaseSliderView baseSliderView) {
                Log.i("click1:", "click");
                goToFullScreen(currentItem.url);
            }
        });
        DefaultSliderView sliderView1 = new DefaultSliderView(this);
        sliderView1.image(currentItem.url1).setScaleType(type);
        sliderView1.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
            @Override
            public void onSliderClick(BaseSliderView baseSliderView) {
                Log.i("click2:", "click");
                goToFullScreen(currentItem.url1);
            }
        });
        DefaultSliderView sliderView2 = new DefaultSliderView(this);
        sliderView2.image(currentItem.url2).setScaleType(type);
        sliderView2.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
            @Override
            public void onSliderClick(BaseSliderView baseSliderView) {
                Log.i("click3:", "click");
                goToFullScreen(currentItem.url2);
            }
        });
        sliderShow.getPagerIndicator().setDefaultIndicatorColor(0xFFFFFFFF, 0xffcc0000);
        sliderShow.addSlider(sliderView);
        sliderShow.addSlider(sliderView1);
        sliderShow.addSlider(sliderView2);

        addToBag = (Button) findViewById(R.id.addToBag);
        addToBag.setOnClickListener(this);

        LinearLayout bagToggle = (LinearLayout) lar1.findViewById(R.id.showBag);
        bagToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser == null) {
                    login();
                    return;
                }
                if (isNotNetworkConnected) {
                    networkAlert();
                    return ;
                }
                Intent goBagActivity = new Intent(ItemDetail.this, BagActivity.class);
                //goBagActivity.setFlags(Intent)
                startActivity(goBagActivity);
            }
        });
        TextView name = (TextView) findViewById(R.id.itemNameDetail);
        TextView price = (TextView) findViewById(R.id.priceDetail);
        TextView finalPrice = (TextView) findViewById(R.id.finalPrice);

        name.setText(currentItem.name);
        price.setText("€ " + currentItem.price);
        if (Double.parseDouble(currentItem.finalPrice) > 0.0) {
            price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            finalPrice.setText("€ " + currentItem.finalPrice);
        }else {
            finalPrice.setText("");
        }
    }

    private void login() {
        final Dialog dialog = new Dialog(ItemDetail.this);

        dialog.setContentView(R.layout.user_dialog);
        dialog.setTitle("Please Sign In");
        dialog.show();
        Button signIn = (Button) dialog.findViewById(R.id.signIn);
        Button join = (Button) dialog.findViewById(R.id.join);
        Button cancell = (Button) dialog.findViewById(R.id.cancell);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetail.this,SignIn.class);
                startActivity(intent);
                dialog.hide();
            }
        });
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetail.this, SignIn.class);
                startActivity(intent);
                dialog.hide();
            }
        });
        cancell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });
        return;
    }
    private void setSpinner() {
        sizeSpinner = (NoDefaultSpinner) findViewById(R.id.sizeSpinner);
        sizeSpinner.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (isNotNetworkConnected) {
                        networkAlert();
                        return false;
                    }
                    if (!isOut) {
                        ((NoDefaultSpinner)v).showContextMenu();
                        return false;
                    } else {
                        outOfStock.setMessage("Out of stock");
                        outOfStock.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        outOfStock.show();
                        return true;
                    }


                }
                return false;
            }


        });

        sizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0) {
                    addToBag.setText("ADD TO BAG");
                    return;
                }
                for (Attribute item :
                      attributeArrayList) {
                    if(sizeSpinner.getSelectedItem().toString() == item.name.toString()) {
                        if (BagWrapper.getInstance().countInStock(item) == 0) {

                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        List<String> categories = new ArrayList<String>();
        ArrayList<Integer> priority = new ArrayList<Integer>();
        for (Attribute item :
                attributeArrayList) {
            priority.add(item.priority);
        }
        Collections.sort(priority);

        for(int pri = 0;pri < priority.size();pri++) {
            for(int cat = 0;cat < attributeArrayList.size();cat++) {
                if(attributeArrayList.get(cat).priority == priority.get(pri)){
                    categories.add(attributeArrayList.get(cat).name);
                    break;
                }

            }
        }
        if(categories.size() == 0) {
            categories.add("");
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sizeSpinner.setAdapter(dataAdapter);

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
    public void goToFullScreen(String urlFull) {
        Intent fullScreen = new Intent(ItemDetail.this,ViewPagerExampleActivity.class);
        fullScreen.putExtra("url",currentItem.url);
        fullScreen.putExtra("url1",currentItem.url1);
        fullScreen.putExtra("url2", currentItem.url2);
        startActivity(fullScreen);
    }

    private void getAttributeFromParse() {
        activityDialog = new ProgressDialog(this);
        activityDialog.setMessage("Loading size...");
        activityDialog.setCancelable(false);
        activityDialog.show();
        final ParseQuery<ParseObject> queryInventory = ParseQuery.getQuery("Cat");
        queryInventory.whereContains("objectId", currentItem.objectId);
        queryInventory.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> ClientList, ParseException e) {
                if (e == null) {
                    attributeArrayList = new ArrayList<Attribute>();
                    for (int i = 0; i < ClientList.size(); i++) {
                        ParseRelation<ParseObject> relet = ClientList.get(i).getRelation("attributes");
                        ParseQuery<ParseObject> query = relet.getQuery();
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> list, ParseException e) {
                                for (int i = 0; i < list.size(); i++) {
                                    String name = list.get(i).getString("attName");
                                    String objectId = list.get(i).getObjectId();
                                    int id = list.get(i).getInt("attValue");
                                    if (id > 0) {
                                        isOut = false;
                                    }
                                    int priority = list.get(i).getInt("attPriority");
                                    attributeArrayList.add(new Attribute(id, name, objectId, priority));
                                }
                                setSpinner();
                                activityDialog.hide();
                            }
                        });

                    }


                }
            }
        });
    }


    @Override
    protected void onResume() {


        Button addToBag = (Button) findViewById(R.id.addToBag);
        addToBag.setText("ADD TO BAG");
        addToBag.setOnClickListener(this);
        LinearLayout lar1 = (LinearLayout)findViewById(R.id.include3);
        TextView bagCount = (TextView) lar1.findViewById(R.id.bagCountText);
        Integer countItems = BagWrapper.getInstance().getCountInBag();
        if(countItems >= 100 || bagCount.getTextSize() == 12 ) {
            bagCount.setTextSize(10);
        }else {
            bagCount.setTextSize(12);
        }
        bagCount.setText(Integer.toString(countItems));
        bagCount.setVisibility(View.VISIBLE);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();
    }
    @Override
    protected void onDestroy() {

        super.onDestroy();

        finish();
        System.gc();
    }
    private void networkAlert(){
        AlertDialog.Builder alert = new AlertDialog.Builder(ItemDetail.this);
        alert.setTitle("No Internet connection");
        alert.setMessage("Make sure your device is connected to the internet");
        alert.setPositiveButton("OK", null);
        alert.show();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.popupButton :
                 if (isNotNetworkConnected) {
                     networkAlert();
                    return;
                }
                Intent goBagActivity = new Intent(ItemDetail.this, BagActivity.class);
                //goBagActivity.setFlags(Intent)
                startActivity(goBagActivity);
                break;
            case R.id.callToOrder :
                Intent call = new Intent(Intent.ACTION_CALL);

                call.setData(Uri.parse("tel:" + "+35799278801"));
                startActivity(call);
                break;
            case R.id.policyButton :
                if (isNotNetworkConnected) {
                    networkAlert();
                    return;
                }
                Intent readPolicy = new Intent(ItemDetail.this, ReadPolicy.class);
                startActivity(readPolicy);
                break;
            case R.id.guideButton :
                if (isNotNetworkConnected) {
                    networkAlert();
                    return;
                }
                Intent sizeGuide = new Intent(ItemDetail.this, SizeGuide.class);
                startActivity(sizeGuide);
                break;
            case R.id.addToBag :
                if (isNotNetworkConnected) {
                    networkAlert();
                    return;
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser == null) {
                    login();
                    return;
                }

                String sizeItem = (String) sizeSpinner.getSelectedItem();

                if(sizeItem == null) {
                    if (attributeArrayList.size() == 0){
                        AlertDialog.Builder alert = new AlertDialog.Builder(this);
                        alert.setMessage("Out of stock");
                        alert.setPositiveButton("OK", null);
                        alert.show();
                        return;
                    }
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setMessage("Please, select size");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    return;
                }
                currentItem.size = sizeItem;
                if(BagWrapper.getInstance().checkInStock(currentItem, this) == 0) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setMessage("Out of stock");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    return;
                }

                if (BagWrapper.getInstance().addToBag(currentItem)) {
                    Toast.makeText(this,"item added to bag",Toast.LENGTH_SHORT).show();
                    Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(500);

                    LinearLayout lar1 = (LinearLayout)findViewById(R.id.include3);
                    TextView bagCount = (TextView) lar1.findViewById(R.id.bagCountText);
                    Integer countItems = BagWrapper.getInstance().getCountInBag();
                    if(countItems >= 100 || bagCount.getTextSize() == 12 ) {
                        bagCount.setTextSize(10);
                    }else {
                        bagCount.setTextSize(12);
                    }
                    bagCount.setText(Integer.toString(countItems));
                    bagCount.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.show_menu :
                finishActivity(1);
        }
    }
}
