package com.nibdev.otrtav2.view.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nibdev.otrtav2.model.NQ;
import com.nibdev.otrtav2.model.database.DB2;
import com.nibdev.otrtav2.service.OTRTAService;

public class ListAdapterDB2Vendors extends BaseAdapter implements SectionIndexer {


	private List<Map<String, Object>> mVendors; 

	//secionindex
	private List<String> mSectionsList;
	private List<Integer> mSectionsStart;
	private String[] mSections;



	public ListAdapterDB2Vendors(int devtype){
		DB2 db = OTRTAService.getInstance().getDB2();
		mVendors = db.getSortedVendorsForDeviceType(devtype);

		//create sections
		mSectionsList = new ArrayList<String>();
		mSectionsStart = new ArrayList<Integer>();
		for (int i = 0; i < mVendors.size(); i ++){
			String vletter = mVendors.get(i).get(DB2.COLUMN_NAME).toString().substring(0, 1);
			if (!mSectionsList.contains(vletter)){
				mSectionsList.add(vletter);
				mSectionsStart.add(i);
			}
		}
		mSections = mSectionsList.toArray(new String[mSectionsList.size()]);

	}

	@Override
	public int getCount() {
		return mVendors.size();
	}

	@Override
	public Object getItem(int position) {
		return mVendors.get(position);
	}

	@Override
	public long getItemId(int position) {
		return ((Integer)mVendors.get(position).get(DB2.COLUMN_VENDORID)).longValue();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = NQ.baseAdaptViewInit(convertView, parent, android.R.layout.simple_list_item_1);

		TextView tv = NQ.v(v, android.R.id.text1, TextView.class);
		tv.setText(mVendors.get(position).get(DB2.COLUMN_NAME).toString());

		return v;
	}

	@Override
	public int getPositionForSection(int section) {
		return mSectionsStart.get(section);
	}

	@Override
	public int getSectionForPosition(int position) {
		String letter = mVendors.get(position).get(DB2.COLUMN_NAME).toString().substring(0, 1);
		return mSectionsList.indexOf(letter);
	}

	@Override
	public Object[] getSections() {
		return mSections;
	}

}
