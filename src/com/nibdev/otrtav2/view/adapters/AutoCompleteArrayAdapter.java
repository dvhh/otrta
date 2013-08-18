package com.nibdev.otrtav2.view.adapters;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

@SuppressLint("DefaultLocale")
public class AutoCompleteArrayAdapter extends ArrayAdapter<String> implements Filterable {
	private List<String> mOriginalValues;
	private List<String> mObjects;
	private Filter mFilter;

	public AutoCompleteArrayAdapter(Context context, int textViewResourceId, List<String> data) {
		super(context, textViewResourceId, data);
		mOriginalValues = data;
		mObjects = new ArrayList<String>();
	}

	@Override
	public int getCount() {
		return mObjects.size();
	}

	@Override
	public String getItem(int position) {
		return mObjects.get(position);
	}
	
	public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new CustomFilter();
		}
		return mFilter;
	}

	public int getOriginalPosition(int position){
		return mOriginalValues.indexOf(mObjects.get(position));
	}
	

	private class CustomFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			if(constraint == null || constraint.length() == 0) {
				List<String> list = new ArrayList<String>(mOriginalValues);
				results.values = list;
				results.count = list.size();
			} else {
				List<String> newValues = new ArrayList<String>();
				for(int i = 0; i < mOriginalValues.size(); i++) {
					String item = mOriginalValues.get(i);
					if(item.toLowerCase().contains(constraint.toString().toLowerCase())) {
						newValues.add(item);
					}
				}
				results.values = newValues;
				results.count = newValues.size();
			}       
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			mObjects = (List<String>) results.values;
			notifyDataSetChanged();
		}

	}

}