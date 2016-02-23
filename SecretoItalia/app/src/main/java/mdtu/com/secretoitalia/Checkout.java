package mdtu.com.secretoitalia;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import mdtu.com.secretoitalia.Adapters.SideMenuAdapter;
import mdtu.com.secretoitalia.Model.Attribute;
import mdtu.com.secretoitalia.Model.Category;
import mdtu.com.secretoitalia.Model.InventoryItem;
import mdtu.com.secretoitalia.Wrappers.BagWrapper;

import static android.telephony.PhoneNumberUtils.isGlobalPhoneNumber;

/**
 * Created by koctyabondar on 9/11/15.
 */
public class Checkout extends Activity implements View.OnClickListener {
    private ArrayList<String> akisArray;
    private TextView firstName;
    private TextView lastName;
    private TextView phone;
    private TextView shipping;
    private TextView maxAbove;
    private TextView delivery;
    private TextView total;
    private Spinner akisSpinner;
    private ProgressDialog activityDialog;
    private boolean isNotNetworkConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);
        activityDialog = new ProgressDialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        interetSwitchEvent();
        getAkis();
        initView();
        getAttribute();
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
        android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(Checkout.this);
        alert.setTitle("No Internet connection");
        alert.setMessage("Make sure your device is connected to the internet");
        alert.setPositiveButton("OK", null);
        alert.show();
    }
    private void initView() {
        initActionBar();
        Button placeOrder = (Button) findViewById(R.id.orderbutton);
        placeOrder.setOnClickListener(this);
        firstName = (TextView) findViewById(R.id.firstName);
        lastName = (TextView) findViewById(R.id.lastName);
        phone = (TextView) findViewById(R.id.phone);
        shipping = (TextView) findViewById(R.id.costOfShopping);
        maxAbove = (TextView) findViewById(R.id.shippingText);
        delivery = (TextView) findViewById(R.id.deliveryCash);
        total = (TextView) findViewById(R.id.totalSum);
        getUserData();
    }

    private void getUserData() {
        activityDialog.setMessage("Loading category...");
        activityDialog.setCancelable(false);
        activityDialog.show();
        ParseUser user = ParseUser.getCurrentUser();
        String first_name = user.getString("first_name");
        String last_name = user.getString("last_name");
        String phoneStr = user.getString("telephone");

        firstName.setText(first_name);
        lastName.setText(last_name);
        phone.setText(phoneStr);
    }

    private void getAkis() {
        activityDialog.setMessage("Loading category...");
        activityDialog.setCancelable(false);
        activityDialog.show();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Akis");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> ClientList, ParseException e) {
                activityDialog.hide();
                if (e == null) {
                    akisArray = new ArrayList<String>();
                    for (ParseObject item :
                            ClientList) {
                        akisArray.add(item.getString("akisName"));
                    }
                    initSpinnerAkis();
                }
            }

        });
    }

    private void getAttribute() {
        activityDialog.setMessage("Loading category...");
        activityDialog.setCancelable(false);
        activityDialog.show();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("checkout");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> ClientList, ParseException e) {
                activityDialog.hide();
                if (e == null) {
                    akisArray = new ArrayList<String>();
                    Double maxValue = 0.0;
                    Double deliveryValue = 0.0;
                    Double shippingValue = 0.0;
                    for (ParseObject item :
                            ClientList) {
                        if (item.getString("checkoutAtribute").toString().equals("priceOfCash")) {
                            deliveryValue = item.getDouble("value");
                            delivery.setText("€ " + Double.toString(DoubleRound.round(deliveryValue,2)));
                        }
                        if (item.getString("checkoutAtribute").toString().equals("priceOfShipping")) {
                            shippingValue = item.getDouble("value");
                            shipping.setText("€ " + Double.toString(DoubleRound.round(shippingValue,2)));
                        }
                        if (item.getString("checkoutAtribute").toString().equals("aboveWhatValue")) {
                            maxValue = item.getDouble("value");
                            maxAbove.setText("(Free shipping on All orders Over € " + maxValue + ")");
                        }
                    }
                    Double totalPrice = BagWrapper.getInstance().getTotalPrice();
                    if (totalPrice > maxValue) {
                        shipping.setText("FREE");

                    } else {
                        totalPrice += shippingValue;
                    }
                    totalPrice += deliveryValue;

                    total.setText("€ " + ((Double) DoubleRound.round(totalPrice, 2)).toString());
                }
            }

        });
    }


    private void initSpinnerAkis() {
        akisSpinner = (Spinner) findViewById(R.id.akisSpinner);

        akisSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        List<String> akis = new ArrayList<String>();
        for (String item :
                akisArray) {
            akis.add(item);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, akis);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        akisSpinner.setAdapter(dataAdapter);

    }

    private void initActionBar() {
        LinearLayout lar1 = (LinearLayout) findViewById(R.id.include6);
        TextView menu1 = (TextView) lar1.findViewById(R.id.appTitle);
        menu1.setText("CHECKOUT");
        LinearLayout toggle = (LinearLayout) lar1.findViewById(R.id.show_menu);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) toggle.getLayoutParams();
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
    public void onClick(View v) {
        if (isNotNetworkConnected) {
            networkAlert();
            return;
        }
        if (!isGlobalPhoneNumber(phone.getText().toString())) {
            Toast.makeText(this, "Please input correct phone ", Toast.LENGTH_SHORT).show();
            return;
        }
        String akis = (String) akisSpinner.getSelectedItem();

        if (akis == null) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("Please, select an Akis Express Branch Store");
            alert.setPositiveButton("OK", null);
            alert.show();
            return;
        }
        activityDialog.setMessage("Place order...");
        activityDialog.setCancelable(false);
        activityDialog.show();
        ParseUser.getCurrentUser().put("first_name", firstName.getText().toString());
        ParseUser.getCurrentUser().put("last_name", lastName.getText().toString());
        ParseUser.getCurrentUser().put("telephone", phone.getText().toString());
        ParseUser.getCurrentUser().saveInBackground();

        final AlertDialog.Builder builder = new AlertDialog.Builder(Checkout.this);
        builder.setMessage("Send this order to Secreto Italia?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        ParseObject currentOrder = new ParseObject("order");
                        currentOrder.put("userName", firstName.getText().toString());
                        currentOrder.put("shippingStore", akisSpinner.getSelectedItem());
                        currentOrder.put("finalOrderPrice", Double.parseDouble(total.getText().toString().replace('€', ' ')));
                        currentOrder.put("userSurname", lastName.getText().toString());
                        currentOrder.put("telephone", phone.getText().toString());
                        currentOrder.put("user", ParseUser.getCurrentUser());
                        currentOrder.saveInBackground();
                        ArrayList<InventoryItem> inventoryItems = BagWrapper.getInstance().getBag();
                        for (InventoryItem item :
                                inventoryItems) {
                            ParseObject orderItem = new ParseObject("orderItems");
                            orderItem.put("itemName", item.name);
                            orderItem.put("itemPhotoUrl", item.url);
                            orderItem.put("itemPrice", Double.parseDouble(item.getPrice()));
                            orderItem.put("itemQty", Double.parseDouble(item.count));
                            orderItem.put("itemSecretoId", item.secretoId);
                            orderItem.put("itemSize", item.size);
                            orderItem.put("orderItemId", item.objectId);
                            orderItem.put("parent", currentOrder);
                            orderItem.saveInBackground();
                            int maxValue = BagWrapper.getInstance().checkInStock(item, Checkout.this);
                            if (maxValue < Integer.parseInt(item.count)) {
                                android.support.v7.app.AlertDialog.Builder alert1 = new android.support.v7.app.AlertDialog.Builder(Checkout.this);
                                alert1.setTitle("\" " + item.name + "\"- out of stock");
                                alert1.setMessage("Size : " + item.size + "\nAvailable quantity: " + maxValue + "\nPlease, reduce the quantity of items");
                                alert1.setPositiveButton("OK", null);
                                activityDialog.hide();
                                alert1.show();
                                return;
                            } else {
                                BagWrapper.getInstance().removeInStock(item, Integer.parseInt(item.count));
                            }
                        }


                        activityDialog.hide();
                        final AlertDialog.Builder builder1 = new AlertDialog.Builder(Checkout.this);
                        builder1.setMessage("Thank you for purchase")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        BagWrapper.getInstance().removeAll();
                                        Intent intent = new Intent(Checkout.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                });
                        builder1.show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        activityDialog.hide();
                    }
                });
        builder.show();


    }
}


