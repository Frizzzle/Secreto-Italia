package mdtu.com.secretoitalia.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import mdtu.com.secretoitalia.ImageLoadTask;
import mdtu.com.secretoitalia.MainActivity;
import mdtu.com.secretoitalia.Model.Category;
import mdtu.com.secretoitalia.R;

/**
 * Created by koctyabondar on 9/8/15.
 */
public class SideMenuAdapter extends BaseAdapter {

    private Context mContext;
    private Animation animationEnlarge;
    private String title;
    private ArrayList<Category> categoryList;


    public SideMenuAdapter() {
        this.mContext = MainActivity.mainAcvitityInstance.getApplicationContext();
        animationEnlarge = AnimationUtils.loadAnimation(mContext, R.anim.slide_up_left);
    }

    public static SideMenuAdapter createCategoryListAdapter(ArrayList<Category> categoryList){
        SideMenuAdapter result = new SideMenuAdapter();
        result.categoryList = categoryList;
        return result;
    }
    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public boolean isContain(int id,boolean finals) {
        for (Category item :
                categoryList) {
            if ((Integer.parseInt(item.categoryId) == id) && (item.finalLevel)) {
                return true;
            }
        }return false;
    }
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater)
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.side_item_card, viewGroup, false);

        TextView itemName  = (TextView) rowView.findViewById(R.id.textSideItem);
        TextView arrow  = (TextView) rowView.findViewById(R.id.sideArrow);
        Integer tempId = Integer.parseInt(categoryList.get(i).subcategoryId);

        arrow.setText(categoryList.get(i).finalLevel?" ":">");
        itemName.setText(categoryList.get(i).name);

        CardView cardView = (CardView) rowView.findViewById(R.id.card_view);
        cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
        cardView.setMaxCardElevation(0.0f);
        cardView.setRadius(0.0f);

        rowView.setTag(categoryList.get(i).categoryId + '=' + categoryList.get(i).subcategoryId + '='+ categoryList.get(i).name);

        return rowView;

    }
}