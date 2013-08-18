package com.nibdev.otrtav2.view.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nibdev.otrtav2.model.database.DBLocal;
import com.nibdev.otrtav2.model.scripts.Script;

public class ScriptSelectSpinnerAdapter extends BaseAdapter {


	private String mTopString;
	private List<Script> mScripts;

	public ScriptSelectSpinnerAdapter(DBLocal dbloc){
		mScripts = new ArrayList<Script>(dbloc.getSortedScripts().values());
	}

	public void setTopString(String ts){
		mTopString = ts;
	}

	private int addTs(){
		return  ((mTopString == null)  ? 0 : 1);
	}

	@Override
	public int getCount() {
		return mScripts.size() + addTs();
	}

	@Override
	public Object getItem(int position) {
		int pos = position - addTs();
		if (pos == -1) return null;
		return mScripts.get(pos);
	}

	@Override
	public long getItemId(int position) {
		int pos = position - addTs();
		if (pos == -1) return -1;
		return (long)mScripts.get(pos).getId();

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
			tv.setText((CharSequence) mScripts.get(position - addTs()).getName());
		}
		return v;
	}

}
