package com.nibdev.otrtav2.view.adapters;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nibdev.otrtav2.model.NQ;

public class ListAdapterDB2DeviceTypes extends BaseAdapter {
	
	private SparseArray<String> mDtNames;
	
	public ListAdapterDB2DeviceTypes(){
		
		mDtNames = new SparseArray<String>();
		mDtNames.put(1, "TV ?");
		mDtNames.put(2, "Cablebox ?");
		mDtNames.put(3, "Tuner / Hifi ?");
		mDtNames.put(4, "BD Player ?");
		mDtNames.put(5, "? ?");
	}
	
	@Override
	public int getCount() {
		return 5;
	}

	@Override
	public Object getItem(int position) {
		return position + 1;
	}

	@Override
	public long getItemId(int position) {
		return position + 1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = NQ.baseAdaptViewInit(convertView, parent, android.R.layout.simple_list_item_1);
		
		TextView tv = NQ.v(v, android.R.id.text1, TextView.class);
		
		tv.setText("" + (position+1) + " / " + mDtNames.get(position + 1));
		
		return v;
	}

}
