package com.nibdev.otrtav2.view.adapters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.model.database.DBLocal;
import com.nibdev.otrtav2.model.database.DBLocal.OnNewLocalDataListener;
import com.nibdev.otrtav2.service.OTRTAService;

public class VendorGridAdapter extends BaseAdapter implements OnNewLocalDataListener, SectionIndexer {

	private List<Map<String, Object>> mVendorData; 
	private static HashSet<Integer> mAnimatedPositions = new HashSet<Integer>();

	//Sections
	private String[] mSections;
	private List<Integer> mSectionsStart;
	private List<String> mSectionsList;
	
	public VendorGridAdapter(){
		DBLocal localDb = OTRTAService.getInstance().getLocalDb();
		localDb.addOnNewLocalDataListener(this);
		mVendorData = localDb.getVendorsWithModelCounts();
		createSectionData();
	}

	@Override
	public void onNewData() {
		DBLocal localDb = OTRTAService.getInstance().getLocalDb();
		mVendorData = localDb.getVendorsWithModelCounts();
		createSectionData();
		notifyDataSetChanged();
	}
	
	private void createSectionData(){
		mSectionsList = new ArrayList<String>();
		mSectionsStart = new ArrayList<Integer>();
		for (int i = 0; i < mVendorData.size(); i++){
			String vletter = mVendorData.get(i).get(DBLocal.COLUMN_NAME).toString();
			if (vletter == null || vletter.length() < 1) continue;
			vletter = vletter.substring(0, 1).toUpperCase(Locale.ENGLISH);
			if (!mSectionsList.contains(vletter)){
				mSectionsList.add(vletter);
				mSectionsStart.add(i);
			}
		}
		mSections = mSectionsList.toArray(new String[mSectionsList.size()]);
	}

	@Override
	public int getCount() {
		return mVendorData.size();
	}

	@Override
	public Object getItem(int position) {
		return mVendorData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return (Long) mVendorData.get(position).get(DBLocal.COLUMN_ID);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		if (v == null){
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gvitem_vendor, null);
		}
		TextView tvVendName = (TextView)v.findViewById(R.id.tv_name);
		TextView tvModCount = (TextView)v.findViewById(R.id.tv_count);

		tvVendName.setText((CharSequence) mVendorData.get(position).get(DBLocal.COLUMN_NAME));
		int modCount = (Integer) mVendorData.get(position).get(DBLocal.COLUMN_MODELCOUNT);
		tvModCount.setText(modCount + " Model" + (modCount > 1 ? "s" : ""));

		if (mAnimatedPositions.add(position)){
			Animation animation;
			if (position % 2 == 0){
				animation = AnimationUtils.loadAnimation(parent.getContext(), R.anim.slide_in_left);
			}else{
				animation = AnimationUtils.loadAnimation(parent.getContext(), R.anim.slide_in_right);
			}
			animation.setDuration(parent.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
			v.startAnimation(animation);
		}
		return v;
	}

	@Override
	public int getPositionForSection(int section) {
		return mSectionsStart.get(section);
	}

	@Override
	public int getSectionForPosition(int position) {
		String letter = mVendorData.get(position).get(DBLocal.COLUMN_NAME).toString();
		return (letter == null || letter.length() < 1) ? 0 : mSectionsList.indexOf(letter.substring(0, 1).toUpperCase(Locale.ENGLISH));
	}

	@Override
	public Object[] getSections() {
		return mSections;
	}

}
