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
import com.nibdev.otrtav2.view.adapters.ListAdapterDB2CodeSets;

public class FragmentDB2CodeSetList extends Fragment {

		private ListView mLvCodesets;
		private ListAdapterDB2CodeSets mAdapter;
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		int vendorid = getArguments().getInt("VENDORID");
		int devtypeid = getArguments().getInt("DTID");
		
		View v = inflater.inflate(R.layout.fragment_db2codesetlist, null);
		mLvCodesets = NQ.v(v, R.id.lv_items, ListView.class);
		mAdapter = new ListAdapterDB2CodeSets(vendorid, devtypeid);
		mLvCodesets.setAdapter(mAdapter);
		mLvCodesets.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Bundle args = new Bundle();
				args.putInt("CODESETID", ((Long)arg3).intValue());
				FragmentDB2CodeList fdb2cl = new FragmentDB2CodeList();
				fdb2cl.setArguments(args);
				getFragmentManager().beginTransaction().replace(R.id.frame_contet, fdb2cl).addToBackStack(null).commit();
			}
			
		});
		return v;
	}

}
