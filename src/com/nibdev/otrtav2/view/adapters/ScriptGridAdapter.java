package com.nibdev.otrtav2.view.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.model.NQ;
import com.nibdev.otrtav2.model.scripts.Script;
import com.nibdev.otrtav2.service.OTRTAService;

public class ScriptGridAdapter extends BaseAdapter {

	private TreeMap<String, Script> mScripts;	
	private List<Script> mScriptList;
	
	public ScriptGridAdapter(){
		mScripts = OTRTAService.getInstance().getLocalDb().getSortedScripts();
		mScriptList = new ArrayList<Script>(mScripts.values());
	}
	
	public void reloadScripts() {
		mScripts = OTRTAService.getInstance().getLocalDb().getSortedScripts();
		mScriptList = new ArrayList<Script>(mScripts.values());
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mScripts.size();
	}

	@Override
	public Object getItem(int position) {
		return mScriptList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mScriptList.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null){
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gvitem_script, null);
		}
		
		TextView tvName = (TextView)v.findViewById(R.id.tv_name);
		tvName.setText(mScriptList.get(position).getName());
		TextView tvCount = (TextView)v.findViewById(R.id.tv_count);
		tvCount.setText(mScriptList.get(position).getItems().size() + " Item" + (mScriptList.get(position).getItems().size() == 1 ? "" : "s"));
		NQ.v(v, R.id.tv_id, TextView.class).setText("ID: " + mScriptList.get(position).getId());
		
		return v;
	}
	
	

}
