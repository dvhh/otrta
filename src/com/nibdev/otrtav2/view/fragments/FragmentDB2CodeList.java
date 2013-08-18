package com.nibdev.otrtav2.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.model.NQ;
import com.nibdev.otrtav2.view.adapters.ListAdapterDB2Codes;

public class FragmentDB2CodeList extends Fragment {

	private ListView mLvCodes;
	private ListAdapterDB2Codes mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		int codesetid = getArguments().getInt("CODESETID");
		View v = inflater.inflate(R.layout.fragment_db2codes, null);
		mLvCodes = NQ.v(v, R.id.lv_items, ListView.class);
		mLvCodes.setDivider(null);
		mAdapter = new ListAdapterDB2Codes(codesetid);
		mLvCodes.setAdapter(mAdapter);
		return v;
	}

}
