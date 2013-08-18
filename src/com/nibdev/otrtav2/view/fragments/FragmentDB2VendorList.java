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
import com.nibdev.otrtav2.view.adapters.ListAdapterDB2Vendors;

public class FragmentDB2VendorList extends Fragment {

	private ListView mLvVendors;
	private ListAdapterDB2Vendors mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		int deviceTypeId = getArguments().getInt("DTID");

		View v = inflater.inflate(R.layout.fragment_db2vendorlist, null);
		mLvVendors = NQ.v(v, R.id.lv_items, ListView.class);
		if (mAdapter == null){
			mAdapter = new ListAdapterDB2Vendors(deviceTypeId);
		}
		mLvVendors.setAdapter(mAdapter);
		mLvVendors.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Bundle args = getArguments();
				args.putInt("VENDORID", ((Long)arg3).intValue());
				FragmentDB2CodeSetList fdb2csl = new FragmentDB2CodeSetList();
				fdb2csl.setArguments(args);
				getFragmentManager().beginTransaction().replace(R.id.frame_contet, fdb2csl).addToBackStack(null).commit();
			}

		});

		return v;
	}

}
