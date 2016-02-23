package mdtu.com.secretoitalia.Wrappers;

import android.app.ProgressDialog;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import mdtu.com.secretoitalia.MainActivity;
import mdtu.com.secretoitalia.Model.Attribute;
import mdtu.com.secretoitalia.Model.InventoryItem;

/**
 * Created by koctyabondar on 9/11/15.
 */
public class BagWrapper {
    private ArrayList<InventoryItem> inventoryItems;
    private static String orderId = "";
    private static BagWrapper wrapper = null;
    private String currentItemId;
    private String currentCount;
    public static BagWrapper getInstance() {
        if(wrapper == null) {
            wrapper = new BagWrapper();
        }
        return wrapper;
    }
    private BagWrapper () {
        inventoryItems = new ArrayList<InventoryItem>();
        ParseQuery<ParseObject> localItems = ParseQuery.getQuery("localCart");
        localItems.fromLocalDatastore();
        localItems.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list.size() == 0) {
                    return;
                }
                for (ParseObject item :
                        list) {
                    String objectId = item.getString("objId");
                    String name = item.getString("name");
                    String url = item.getString("url");
                    String url1 = item.getString("url1");
                    String url2 = item.getString("url2");
                    String price = item.getString("price");
                    String finalPrice = item.getString("finalPrice");
                    String secretoId = item.getString("secretoId");
                    String size = item.getString("size");
                    String count = item.getString("count");
                    InventoryItem currentItem = new InventoryItem(name, objectId, url, url1, url2, price, finalPrice, secretoId);
                    currentItem.size = size;
                    currentItem.count = count;
                    inventoryItems.add(currentItem);
                }
            }
        });

    }


    public int countInStock(Attribute item) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("itemAttributes");
        query.whereContains("objectId",item.objectId);
        Integer count = 0;
        try {
            List<ParseObject> findResult = query.find();
            count = findResult.get(0).getInt("attValue");
            return count;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return count;
    }
    public int checkInStock(InventoryItem item,Context context) {
        final ParseQuery<ParseObject> queryInventory = ParseQuery.getQuery("Cat");
        queryInventory.whereContains("objectId", item.objectId);
        Integer maxCount = 0;
        try {
            List<ParseObject> attributeList = queryInventory.find();
            for (int i = 0; i < attributeList.size(); i++) {
                ParseRelation<ParseObject> relet = attributeList.get(i).getRelation("attributes");
                ParseQuery<ParseObject> queryAtt = relet.getQuery();
                List<ParseObject> attribute = queryAtt.find();
                for (int k = 0; k < attribute.size(); k++) {
                    String attname = attribute.get(k).getString("attName").toString();
                    if(item.size == null){
                        maxCount = attribute.get(k).getInt("attValue");
                    }else  if (item.size.toString().equals(attname)) {
                        maxCount = attribute.get(k).getInt("attValue");
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return maxCount;
    }
    public int removeInStock(InventoryItem item,Integer value) {
        final ParseQuery<ParseObject> queryInventory = ParseQuery.getQuery("Cat");
        queryInventory.whereContains("objectId", item.objectId);
        Integer maxCount = 0;
        try {
            List<ParseObject> attributeList = queryInventory.find();
            for (int i = 0; i < attributeList.size(); i++) {
                ParseRelation<ParseObject> relet = attributeList.get(i).getRelation("attributes");
                ParseQuery<ParseObject> queryAtt = relet.getQuery();
                List<ParseObject> attribute = queryAtt.find();
                for (int k = 0; k < attribute.size(); k++) {
                    String attname = attribute.get(k).getString("attName").toString();
                    if (item.size.toString().equals(attname)) {
                        attribute.get(k).put("attValue", attribute.get(k).getInt("attValue") - value);
                        attribute.get(k).saveInBackground();
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return maxCount;
    }
    public boolean addToBag(InventoryItem item) {
        InventoryItem newItem = new  InventoryItem(item);
        boolean flag = true;
        for (InventoryItem currentItem :
                inventoryItems) {

            if ((currentItem.objectId.toString().equals(newItem.objectId.toString()))&&(currentItem.size.toString().equals(newItem.size.toString()))) {
                flag = false;
            }
        }
        if(flag) {
            saveInDataStore(newItem);
            inventoryItems.add(newItem);
            return true;
        }else {
            for (InventoryItem curItem :
                    inventoryItems) {
                if((curItem.objectId.toString().equals(newItem.objectId.toString()))&&(curItem.size.toString().equals(newItem.size.toString())))
                curItem.count = Integer.toString(Integer.parseInt(curItem.count) + 1);
                updateCountInDataStore(curItem);
            }
            return true;
        }



    }
    private void saveInDataStore(InventoryItem item){
        ParseObject currentItem = new ParseObject("localCart");
        currentItem.put("objId", item.objectId);
        currentItem.put("name", item.name);
        currentItem.put("url", item.url);
        currentItem.put("url1", item.url1);
        currentItem.put("url2", item.url2);
        currentItem.put("price", item.price);
        currentItem.put("finalPrice", item.finalPrice);
        currentItem.put("secretoId", item.secretoId);
        currentItem.put("size", item.size);
        currentItem.put("count", item.count);

        currentItem.pinInBackground();
    }
    public boolean contain(InventoryItem item) {
        for (InventoryItem currentItem :
                inventoryItems) {
            if (currentItem.objectId.toString().equals(item.objectId.toString())) {
                return true;
            }

        }
        return false;
    }
    public ArrayList<InventoryItem> getBag() {
        return inventoryItems;
    }
    public int getCountInBag() {
        int count = 0;
        for (InventoryItem currentItem :
                inventoryItems) {
           count += Integer.parseInt(currentItem.count);
        }
        return count;
    }
    public double getTotalPrice() {
        double count = 0.0;
        for (InventoryItem currentItem :
                inventoryItems) {
            count += (Double.parseDouble(currentItem.finalPrice) == 0.0?Double.parseDouble(currentItem.price):Double.parseDouble(currentItem.finalPrice)) * Double.parseDouble(currentItem.count);
        }
        return count;
    }

    public void remove(String id,String size) {

        for (int i = 0 ;i<inventoryItems.size();i++) {
            if((inventoryItems.get(i).objectId.toString().equals(id.toString()))&&(inventoryItems.get(i).size.toString().equals(size.toString()))) {
                removeInDataStore(inventoryItems.get(i));
                inventoryItems.remove(i);

            }
        }
    }
    private void removeInDataStore(InventoryItem item){
        currentItemId = item.objectId;
        final ParseQuery<ParseObject> localItems = ParseQuery.getQuery("localCart");
        localItems.fromLocalDatastore();
        localItems.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list.size() == 0) {
                    return;
                }
                ParseObject obj = null;
                for (ParseObject item :
                        list) {
                    String objectId = item.getString("objId");

                    if(objectId == currentItemId) {
                        obj = item;
                    }
                }
                try {
                    obj.unpin();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        });

    }
    public void updateCount(String id,String value,String size) {
        for (int i = 0 ;i<inventoryItems.size();i++) {
            if((inventoryItems.get(i).objectId.toString().equals(id.toString()))&&(inventoryItems.get(i).size.toString().equals(size.toString()))){
                inventoryItems.get(i).count = value;
                updateCountInDataStore(inventoryItems.get(i));
            }
        }
    }
    private void updateCountInDataStore(InventoryItem item){
        currentItemId = item.objectId;
        currentCount = item.count;

        final ParseQuery<ParseObject> localItems = ParseQuery.getQuery("localCart");
        localItems.fromLocalDatastore();
        localItems.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list.size() == 0) {
                    return;
                }
                ParseObject obj = null;
                for (ParseObject item :
                        list) {
                    String objectId = item.getString("objId");

                    if(objectId == currentItemId) {
                        obj = item;
                        obj.put("count",currentCount);
                        obj.pinInBackground();
                        return;
                    }
                }

            }
        });

    }

    public void removeAll() {
        try {
            ParseObject.unpinAll();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        inventoryItems.clear();
    }
}
