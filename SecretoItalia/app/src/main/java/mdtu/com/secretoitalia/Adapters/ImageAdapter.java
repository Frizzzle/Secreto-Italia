/*
 * Copyright (C) 2011 Patrik �kerfeldt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mdtu.com.secretoitalia.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mdtu.com.secretoitalia.R;


public class ImageAdapter extends BaseAdapter {

	private LayoutInflater mInflater;

	private static ArrayList<String> urls;
	private Context mContext;

	public ImageAdapter(Context context, ArrayList<String> urls) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mContext = context;
        this.urls=urls;
	}

	@Override
	public int getCount() {
		return urls.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.image_item, null);
		}
		final ImageView imageView = ((ImageView) convertView.findViewById(R.id.imgView));
		Picasso.with(mContext).load(urls.get(position)).into(imageView);

		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		return convertView;
	}

}
