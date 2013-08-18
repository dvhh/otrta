package com.nibdev.otrtav2.view.adapters;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nibdev.otrtav2.model.NQ;
import com.nibdev.otrtav2.model.database.DB2;
import com.nibdev.otrtav2.service.OTRTAService;

public class ListAdapterDB2CodeSets extends BaseAdapter {
	
	private List<Map<String, Object>> mCodeSets; 
	
	public ListAdapterDB2CodeSets(int vendorId, int devTypeId){
		DB2 db = OTRTAService.getInstance().getDB2();
		mCodeSets = db.getCodeSetsForVendor(vendorId, devTypeId);
		
	}
	
	@Override
	public int getCount() {
		return mCodeSets.size();
	}

	@Override
	public Object getItem(int position) {
		return mCodeSets.get(position);
	}

	@Override
	public long getItemId(int position) {
		return  ((Integer)mCodeSets.get(position).get(DB2.COLUMN_CODESETID)).longValue();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = NQ.baseAdaptViewInit(convertView, parent, android.R.layout.simple_list_item_1);
		
		TextView tv = NQ.v(v, android.R.id.text1, TextView.class);
		tv.setText("Set " + Integer.toHexString((Integer)mCodeSets.get(position).get(DB2.COLUMN_CODESETID)).toUpperCase(Locale.ENGLISH));
		
		return v;
	}

}
