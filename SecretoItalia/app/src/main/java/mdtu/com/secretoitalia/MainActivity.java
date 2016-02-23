package mdtu.com.secretoitalia;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import lib.SlidingMenu;
import mdtu.com.secretoitalia.Adapters.InventoryAdapter;
import mdtu.com.secretoitalia.Adapters.SideMenuAdapter;
import mdtu.com.secretoitalia.Model.Category;
import mdtu.com.secretoitalia.Model.InventoryItem;
import mdtu.com.secretoitalia.Wrappers.BagWrapper;

public class MainActivity extends Activity implements  AdapterView.OnItemClickListener {


    public static MainActivity mainAcvitityInstance = null;
    private ListView itemList = null;
    private InventoryAdapter adapter = null;
    private ArrayList<Category> categoryList;
    private ArrayList<InventoryItem> inventoryList;
    private SlidingMenu menu;
    private boolean isNotNetworkConnected = true;
    private ListView sideMenuListView;
    private String selectedItemID ;
    private String categoryName;
    private String menuCategName = "";
    private ProgressDialog activityDialog;

    private SideMenuAdapter sideAdapter;
    private String subcategoryId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainAcvitityInstance = this;

        //
        setContentView(R.layout.main_view);
        activityDialog = new ProgressDialog(this);
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);

        menu.setBehindWidth((int) getBehindWidth());
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(mainAcvitityInstance, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.side_menu);
        selectedItemID = "";
        this.categoryName = " ";
        sideMenuListView = (ListView) findViewById(R.id.sideMenuListView);
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
                    menu.setBehindWidth(0);
                    if(sideMenuListView.getCount() < 1 ) {
                        TextView internetConnection = (TextView) findViewById(R.id.internetConnection);
                        internetConnection.setAlpha(1.0f);
                    }


                } else {
                    menu.setBehindWidth((int) getBehindWidth());
                    isNotNetworkConnected = false;
                    initParse();
                    sideMenuListView.setOnItemClickListener(mainAcvitityInstance);
                    TextView internetConnection = (TextView) findViewById(R.id.internetConnection);
                    internetConnection.setAlpha(0.0f);
                }

                //Play with the info about current network state


            }

        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }
    private void showSplesh() {

    }
    private double getBehindWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x * 0.75;
    }

    private void initParse(){
        // Enable Local Datastore.



        loadCategory();
        requestForStartsInventoryItems();

    }
    private void initView() {


        LinearLayout lar1 = (LinearLayout)findViewById(R.id.include);
        TextView menu1 = (TextView)lar1.findViewById(R.id.appTitle);
        menu1.setText("SECRETO ITALIA");
        LinearLayout toggle = (LinearLayout) lar1.findViewById(R.id.show_menu);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNotNetworkConnected) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(mainAcvitityInstance);
                    alert.setTitle("No Internet connection");
                    alert.setMessage("Make sure your device is connected to the internet");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    return;
                }
                menu.showMenu();
            }
        });
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
                    AlertDialog.Builder alert = new AlertDialog.Builder(mainAcvitityInstance);
                    alert.setTitle("No Internet connection");
                    alert.setMessage("Make sure your device is connected to the internet");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    return;
                }
                Intent goBagActivity = new Intent(MainActivity.this, BagActivity.class);
                //goBagActivity.setFlags(Intentю)
                startActivity(goBagActivity);
            }
        });
        ImageView bagIcon = (ImageView) lar1.findViewById(R.id.showBagImage);
        bagIcon.setImageResource(R.drawable.thebagicon);

        LinearLayout lar = (LinearLayout) findViewById(R.id.include2);
        TextView menu = (TextView)lar.findViewById(R.id.appTitle);
        menu.setText("MENU");
        ImageView img = (ImageView) lar.findViewById(R.id.menuIcon);
        img.setAlpha(0);

    }
    private void loadCategory() {
        activityDialog.setMessage("Loading category...");
        activityDialog.setCancelable(false);
        activityDialog.show();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("WomenCategories");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> ClientList, ParseException e) {
                activityDialog.hide();
                if (e == null) {
                    categoryList = new ArrayList<Category>();
                    for (int i = 0; i < ClientList.size(); i++) {
                        String name = ClientList.get(i).getString("categoryName");
                        String objectId = ClientList.get(i).getObjectId();
                        String categoryId = ClientList.get(i).getString("categoryId");
                        String subCategoryId = ClientList.get(i).getString("subcategoryId");
                        Boolean finalLevel = ClientList.get(i).getBoolean("finalLevel");

                        categoryList.add(new Category(objectId, name, categoryId, subCategoryId, finalLevel));
                    }
                    sideMenuListView = (ListView) findViewById(R.id.sideMenuListView);
                    ArrayList<Category> tempList = new ArrayList<Category>();

                    for (Category item :
                            categoryList) {
                        if ((Integer.parseInt(item.subcategoryId) == 0)) {
                            tempList.add(item);
                        }
                    }
                    sideAdapter = SideMenuAdapter.createCategoryListAdapter(tempList);
                    sideMenuListView.setAdapter(sideAdapter);
                }
            }
        });
    }
    private void requestForStartsInventoryItems() {
        activityDialog.setMessage("Loading items...");
        activityDialog.setCancelable(false);
        activityDialog.show();
        ParseQuery<ParseObject> queryInventory = ParseQuery.getQuery("ObjectLists");
        queryInventory.whereContains("CategoryId", "6");
        queryInventory.include("itemId");
        queryInventory.whereContains("SubCategoryId", "0");
        queryInventory.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> ClientList, ParseException e) {
                activityDialog.hide();
                if (e == null) {
                    inventoryList = new ArrayList<InventoryItem>();
                    for (int i = 0; i < ClientList.size(); i++) {
                        ParseObject item = ClientList.get(i).getParseObject("itemId");
                        String tempname = item.getString("name");
                        String objectId = item.getObjectId();
                        Double price = item.getDouble("price");
                        Double finalPrice = item.getDouble("finalPrice");
                        String url = item.getString("url");
                        String url1 = item.getString("url1");
                        String url2 = item.getString("url2");
                        String secrId = item.getString("secretoId");
                        // String tempId = ClientList.get(i).getString("objectId");
                        InventoryItem newItem = new InventoryItem(tempname, objectId, url, url1, url2, price.toString(), finalPrice.toString(),secrId);
                        boolean isContained = false;
                        for (InventoryItem invItem :
                                inventoryList) {
                            if(invItem.objectId.toString().equals(newItem.objectId.toString())){
                                isContained = true;
                            }
                        }
                        if(!isContained) {
                            inventoryList.add(newItem);
                        }

                    }
                    itemList = (ListView) findViewById(R.id.itemsList);
                    adapter = InventoryAdapter.createInventoryListAdapter("Offers", inventoryList);
                    itemList.setAdapter(adapter);

                    itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String itemId = (String) view.getTag();
                            Log.i("ID", itemId);
                            for (InventoryItem item :
                                    inventoryList) {
                                if (item.objectId == itemId) {
                                    if (isNotNetworkConnected) {
                                        AlertDialog.Builder alert = new AlertDialog.Builder(mainAcvitityInstance);
                                        alert.setTitle("No Internet connection");
                                        alert.setMessage("Make sure your device is connected to the internet");
                                        alert.setPositiveButton("OK", null);
                                        alert.show();
                                        return;
                                    }
                                    Intent details = new Intent(MainActivity.this, ItemDetail.class);
                                    details.putExtra("objectId", item.objectId);
                                    details.putExtra("name", item.name);
                                    details.putExtra("price", item.price);
                                    details.putExtra("finalPrice", item.finalPrice);
                                    details.putExtra("url", item.url);
                                    details.putExtra("url1", item.url1);
                                    details.putExtra("url2", item.url2);
                                    details.putExtra("secrId", item.secretoId);

                                    startActivity(details);
                                    return;
                                }
                            }


                        }
                    });
                    LinearLayout lar1 = (LinearLayout)findViewById(R.id.include);
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
            }
        });
    }
    public void requestForChangeInventoryItems(int categoryId,int subcategoryId) {
        activityDialog.setMessage("Loading items...");
        activityDialog.setCancelable(false);
        activityDialog.show();
        ParseQuery<ParseObject> queryInventory = ParseQuery.getQuery("ObjectLists");
        queryInventory.whereContains("CategoryId", Integer.toString(categoryId));
        queryInventory.include("itemId");
        if(subcategoryId != -1) {
            queryInventory.whereContains("SubCategoryId", Integer.toString(subcategoryId));
        }

        queryInventory.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> ClientList, ParseException e) {
                activityDialog.hide();
                if (e == null) {
                    inventoryList = new ArrayList<InventoryItem>();
                    for (int i = 0; i < ClientList.size(); i++) {
                        ParseObject item = ClientList.get(i).getParseObject("itemId");
                        String tempname = item.getString("name");
                        String objectId = item.getObjectId();
                        Double price = item.getDouble("price");
                        Double finalPrice = item.getDouble("finalPrice");
                        String url = item.getString("url");
                        String url1 = item.getString("url1");
                        String url2 = item.getString("url2");
                        String secrId = item.getString("secretoId");
                        inventoryList.add(new InventoryItem(tempname, objectId, url, url1, url2, price.toString(), finalPrice.toString(),secrId));
                    }
                    adapter = InventoryAdapter.createInventoryListAdapter(categoryName, inventoryList);
                    itemList.setAdapter(adapter);

                }
            }
        });
    }

    @Override
    protected void onResume() {
        LinearLayout lar1 = (LinearLayout)findViewById(R.id.include);
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
    protected void onDestroy() {

        super.onDestroy();

        finish();
        System.gc();
    }
    private void login() {
        final Dialog dialog = new Dialog(MainActivity.this);

        dialog.setContentView(R.layout.user_dialog);
        dialog.setTitle("Please Sign In");
        dialog.show();
        Button signIn = (Button) dialog.findViewById(R.id.signIn);
        Button join = (Button) dialog.findViewById(R.id.join);
        Button cancell = (Button) dialog.findViewById(R.id.cancell);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignIn.class);
                startActivity(intent);
                dialog.hide();
            }
        });
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Join.class);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isNotNetworkConnected) {
            AlertDialog.Builder alert = new AlertDialog.Builder(mainAcvitityInstance);
            alert.setTitle("No Internet connection");
            alert.setMessage("Make sure your device is connected to the internet");
            alert.setPositiveButton("OK", null);
            alert.show();
            return;
        }
        String[] split = ((String) view.getTag()).split("=");
        int categoryId = Integer.valueOf(split[0]);
        int subCategoryId = Integer.valueOf(split[1]);
        String name = split[2];
        ArrayList<Category> tempList = new ArrayList<Category>();
        this.categoryName = name;
        for (Category item :
                categoryList) {
            if (categoryId == -1) {
                this.categoryName = menuCategName;
                tempList.add(item);
                break;
            }
            if ((Integer.parseInt(item.categoryId) == categoryId) && (item.finalLevel)) {
               if (!sideAdapter.isContain(categoryId,item.finalLevel)) {
                   tempList.add(item);
               }
            }
            if (((Integer.parseInt(item.categoryId) == categoryId)) && ((Integer.parseInt(item.categoryId) == subCategoryId))) {
                this.categoryName = item.name;
                this.subcategoryId = item.subcategoryId;
            }
            if ((Integer.parseInt(item.categoryId) == categoryId)&& (!item.finalLevel)) {
                menuCategName = item.name;
            }

        }

        if (tempList.size() == 0 || !selectedItemID.isEmpty()) {
            if (tempList.size() != 0 ) {
                requestForChangeInventoryItems(Integer.parseInt(selectedItemID), -1);
            }else {
                requestForChangeInventoryItems(categoryId,subCategoryId);
                clickBackToggle();
            }
            menu.showContent();
            return;
        }else {
            selectedItemID = (Integer.toString(categoryId));
            tempList.add(0,new Category("Lol","All","-1","-1",true));
        }

        setUpActionBarInSideMenu(menuCategName);
        sideAdapter = SideMenuAdapter.createCategoryListAdapter(tempList);
        sideMenuListView.setAdapter(sideAdapter);


    }
    private void setUpActionBarInSideMenu(String nameCategory) {
        LinearLayout lar = (LinearLayout)findViewById(R.id.include2);
        TextView menu = (TextView)lar.findViewById(R.id.appTitle);
        menu.setText(nameCategory);
        LinearLayout toggle = (LinearLayout) lar.findViewById(R.id.show_menu);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)toggle.getLayoutParams();
        params.setMargins(-10,-10,-10,-10);
        toggle.setLayoutParams(params);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBackToggle();
            }
        });
        ImageView img = (ImageView) lar.findViewById(R.id.menuIcon);
        img.setImageResource(R.mipmap.side_arrow_icon);
        img.setAlpha(100);
    }
    public void clickBackToggle(){
        if (isNotNetworkConnected) {
            AlertDialog.Builder alert = new AlertDialog.Builder(mainAcvitityInstance);
            alert.setTitle("No Internet connection");
            alert.setMessage("Make sure your device is connected to the internet");
            alert.setPositiveButton("OK", null);
            alert.show();
            return;
        }
        ArrayList<Category> tempList = new ArrayList<Category>();
        for (Category item :
                categoryList) {
            if ((Integer.parseInt(item.subcategoryId) == 0)) {
                tempList.add(item);
            }
        }
        sideAdapter = SideMenuAdapter.createCategoryListAdapter(tempList);
        sideMenuListView.setAdapter(sideAdapter);
        selectedItemID = "";
        LinearLayout lar = (LinearLayout)findViewById(R.id.include2);
        TextView menu = (TextView)lar.findViewById(R.id.appTitle);
        menu.setText("MENU");
        ImageView img = (ImageView) lar.findViewById(R.id.menuIcon);
        img.setAlpha(0);
    }
}
