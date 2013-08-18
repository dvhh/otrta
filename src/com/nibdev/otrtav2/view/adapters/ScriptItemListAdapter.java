package com.nibdev.otrtav2.view.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.model.scripts.ScriptItem;
import com.nibdev.otrtav2.model.scripts.ScriptItem.ItemType;
import com.nibdev.otrtav2.service.OTRTAService;

public class ScriptItemListAdapter extends BaseAdapter {

	private List<ScriptItem> mItems;

	public ScriptItemListAdapter(List<ScriptItem> items, OnTouchListener otl){
		mItems = items;
	}


	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null){
			LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.lvitem_scriptitem, parent, false);
		}

		TextView tv = (TextView) v.findViewById(R.id.tv_text);
		ImageView iv = (ImageView) v.findViewById(R.id.iv_icon);

		ScriptItem item = mItems.get(position);
		if (item.getType() == ItemType.CODE){
			iv.setImageResource(R.drawable.ic_action_code_light);
			tv.setText(OTRTAService.getInstance().getLocalDb().getModelNameAndCodeTypeNameByCodeAllocationId(item.getValue()));
		}else if (item.getType() == ItemType.DELAY){
			iv.setImageResource(R.drawable.ic_action_delay_light);
			tv.setText("Delay\r\n" + item.getValue() + "s");
		}else if (item.getType() == ItemType.SKRIPT){
			iv.setImageResource(R.drawable.ic_action_scripts_light);
			tv.setText("Script\r\n" + item.getValue() + "");
		}

		return v;

	}






}
