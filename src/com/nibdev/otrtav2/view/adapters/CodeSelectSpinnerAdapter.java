package com.nibdev.otrtav2.view.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nibdev.otrtav2.model.database.DBLocal;

public class CodeSelectSpinnerAdapter extends BaseAdapter {


	private String mTopString;
	private List<Map<String, Object>> mSortedCodes;

	public CodeSelectSpinnerAdapter(DBLocal dbloc, int modelId){
		List<Map<String, Object>> codes = dbloc.getCodeAllocationsForModelId(modelId);
		TreeMap<String, Integer> allCodeTypes = dbloc.getSortedCodeTypes();
		List<Integer> codeTypeIds = new ArrayList<Integer>(allCodeTypes.values());
		List<String> codeTypeNames = new ArrayList<String>(allCodeTypes.keySet());

		TreeMap<String, Map<String,Object>> tmpSort = new TreeMap<String, Map<String,Object>>();
		SparseIntArray tmpCount = new SparseIntArray();

		for (Map<String, Object> unsorted : codes){
			int ctId = (Integer) unsorted.get(DBLocal.COLUMN_CODETYPEID);
			String ctName = codeTypeNames.get(codeTypeIds.indexOf(ctId));		
			int count = 0;
			if (tmpCount.get(ctId, -1) > -1){
				count = tmpCount.get(ctId);
				ctName += " [" + (count+1) + "]";
			}
			tmpCount.put(ctId, count + 1);		
			unsorted.put("NAME", ctName);
			tmpSort.put(ctName, unsorted);
		}
		mSortedCodes = new ArrayList<Map<String,Object>>(tmpSort.values());
	}

	public void setTopString(String ts){
		mTopString = ts;
	}

	private int addTs(){
		return  ((mTopString == null)  ? 0 : 1);
	}

	@Override
	public int getCount() {
		return mSortedCodes.size() + addTs();
	}

	@Override
	public Object getItem(int position) {
		int pos = position - addTs();
		if (pos == -1) return null;
		return mSortedCodes.get(pos);
	}

	@Override
	public long getItemId(int position) {
		int pos = position - addTs();
		if (pos == -1) return -1;
		return (Long)mSortedCodes.get(pos).get(DBLocal.COLUMN_ID);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null){
			v = ((LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(android.R.layout.simple_list_item_1, parent, false);	
		}

		TextView tv = (TextView)v.findViewById(android.R.id.text1);
		if (position == 0 && (addTs() == 1)){
			tv.setText(mTopString);

		}else{
			tv.setText((CharSequence) mSortedCodes.get(position - addTs()).get("NAME"));
		}
		return v;
	}

}
