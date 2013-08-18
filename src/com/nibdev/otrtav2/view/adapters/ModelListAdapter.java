package com.nibdev.otrtav2.view.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.model.database.DBLocal;
import com.nibdev.otrtav2.service.OTRTAService;
import com.nibdev.otrtav2.view.custom.NonScrollableGridView;

public class ModelListAdapter extends BaseExpandableListAdapter {


	private int mVendorId;
	
	private List<Integer> mDeviceTypeIds;
	private List<String> mDeviceTypeNames;
	private HashMap<Integer, List<Map<String, Object>>> mModelsDeviceTypped;
	
	private List<InnerAdapter> mInnerAdapters;
	
	public ModelListAdapter(int vendorId){
		DBLocal localDb = OTRTAService.getInstance().getLocalDb();
		mInnerAdapters = new ArrayList<InnerAdapter>();
		
		mVendorId = vendorId;
		
		TreeMap<String, Integer> allDeviceTypes = localDb.getSortedDeviceTypes();
		mModelsDeviceTypped = localDb.getModelsWithDeviceTypedAndCodeCount(mVendorId);
		mDeviceTypeIds = new ArrayList<Integer>(mModelsDeviceTypped.keySet());
		
		List<Integer> allDtIds = new ArrayList<Integer>(allDeviceTypes.values());
		List<String> allDtNames = new ArrayList<String>(allDeviceTypes.keySet());
		mDeviceTypeNames = new ArrayList<String>();
		for (int dtId : mDeviceTypeIds){
			mDeviceTypeNames.add(allDtNames.get(allDtIds.indexOf(dtId)));
			mInnerAdapters.add(new InnerAdapter(mModelsDeviceTypped.get(dtId)));
		}
	}


	@Override
	public Object getChild(int groupPosition, int childPosition) {
		int devTypId = mDeviceTypeIds.get(groupPosition);
		return mModelsDeviceTypped.get(devTypId).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		int devTypId = mDeviceTypeIds.get(groupPosition);
		return (Long) mModelsDeviceTypped.get(devTypId).get(childPosition).get(DBLocal.COLUMN_ID);
	}



	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		int devTypId = mDeviceTypeIds.get(groupPosition);
		return mModelsDeviceTypped.get(devTypId);
	}

	@Override
	public int getGroupCount() {
		return mModelsDeviceTypped.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return mDeviceTypeIds.get(groupPosition);
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null){
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.elvitem_group_devicetype, null);
		}
		
		TextView tvName = (TextView)v.findViewById(R.id.tv_name);
		String devTypeName = mDeviceTypeNames.get(groupPosition);
		tvName.setText(devTypeName + "s");
		
		return v;
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		NonScrollableGridView v = (NonScrollableGridView)convertView;
		if (v == null){
			v = new NonScrollableGridView(parent.getContext(), null);
			v.setOnItemClickListener(mOnInnerClickListener);
			v.setNumColumns(2);
		}
		
		if (v.getAdapter() == null || v.getAdapter() != mInnerAdapters.get(groupPosition)){
			v.setAdapter(mInnerAdapters.get(groupPosition));
		}
				
		
		
		return v;
	}
	
	
	

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	
	
	static class InnerAdapter extends BaseAdapter{

		private List<Map<String, Object>> mModels; 
		
		public InnerAdapter(List<Map<String, Object>> data){
			mModels = data;
		}
		
		@Override
		public int getCount() {
			return mModels.size();
		}

		@Override
		public Object getItem(int position) {
			return mModels.get(position);
		}

		@Override
		public long getItemId(int position) {
			return (Long)mModels.get(position).get(DBLocal.COLUMN_ID);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null){
				v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gvitem_model, null);
			}
			
			TextView tvName = (TextView)v.findViewById(R.id.tv_name);
			TextView tvCount = (TextView)v.findViewById(R.id.tv_count);
			String modelName = (String) mModels.get(position).get(DBLocal.COLUMN_NAME);
			tvName.setText(modelName);
			int codeCount = (Integer)mModels.get(position).get(DBLocal.COLUMN_CODECOUNT);
			tvCount.setText(codeCount + " Code" + (codeCount == 1 ? "" : "s"));
			
			return v;
		}
		
	}
	
	
	public interface OnModelClickListener{
		void onModelClick(long id, String name);
	}
	private OnModelClickListener mListener;
	public void setOnModelClickListener(OnModelClickListener listener){
		mListener = listener;
	}
	
	
	private OnItemClickListener mOnInnerClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			InnerAdapter ia = (InnerAdapter) arg0.getAdapter();
			Map<String, Object> modData = ia.mModels.get(arg2);
			long id = (Long) modData.get(DBLocal.COLUMN_ID);
			String name = (String) modData.get(DBLocal.COLUMN_NAME);
			if (mListener != null) mListener.onModelClick(id, name);
		}
	};
}
