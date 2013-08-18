package com.nibdev.otrtav2.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.model.NQ;
import com.nibdev.otrtav2.view.adapters.ListAdapterDB2DeviceTypes;

public class FragmentDB2DeviceTypeList extends Fragment {

		private ListView mLvDeviceTypes;
		private ListAdapterDB2DeviceTypes mAdapter;
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		
		View v = inflater.inflate(R.layout.fragment_db2devicetypelist, null);
		mLvDeviceTypes = NQ.v(v, R.id.lv_items, ListView.class);
		mAdapter = new ListAdapterDB2DeviceTypes();
		mLvDeviceTypes.setAdapter(mAdapter);
		mLvDeviceTypes.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Bundle args = new Bundle();
				args.putInt("DTID", arg2+1);
				FragmentDB2VendorList fdb2csl = new FragmentDB2VendorList();
				fdb2csl.setArguments(args);
				getFragmentManager().beginTransaction().replace(R.id.frame_contet, fdb2csl).addToBackStack(null).commit();
			}
			
		});
		return v;
	}

}
