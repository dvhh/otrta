package com.nibdev.otrtav2.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.view.adapters.CodeListAdapter;

public class FragmentCodeList extends Fragment {

	private ListView mLvCodes;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getActivity().setTitle(getArguments().getString("NAME"));
		
		View v= inflater.inflate(R.layout.fragment_codelist, null);

		
		mLvCodes = (ListView)v.findViewById(R.id.lv_codes);
		mLvCodes.setDivider(null);
		
		CodeListAdapter cla = new CodeListAdapter(getArguments().getInt("ID"));
		mLvCodes.setAdapter(cla);
		
		
		return v;
	}

}
