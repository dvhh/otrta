package com.nibdev.otrtav2.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ExpandableListView;

import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.view.adapters.ModelListAdapter;
import com.nibdev.otrtav2.view.adapters.ModelListAdapter.OnModelClickListener;

public class FragmentModelList extends Fragment {

	private ExpandableListView mELVModels;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View v= inflater.inflate(R.layout.fragment_modellist, null);
		mELVModels = (ExpandableListView)v.findViewById(R.id.elv_modellist);
		mELVModels.setDivider(null);
		mELVModels.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				mELVModels.setIndicatorBounds(mELVModels.getRight() - 150, mELVModels.getWidth());
			}
		});		
		
		getActivity().setTitle(getArguments().getString("NAME"));
		int vendorId = getArguments().getInt("ID");
		ModelListAdapter mla = new ModelListAdapter(vendorId);
		mla.setOnModelClickListener(mOnModelClickListner);
		mELVModels.setAdapter(mla);
		
		
		int count = mla.getGroupCount();
		for (int position = 1; position <= count; position++){
			mELVModels.expandGroup(position - 1);
		}
		return v;

	}
	
	
	private OnModelClickListener mOnModelClickListner = new OnModelClickListener() {

		@Override
		public void onModelClick(long id, String name) {
			Bundle args = new Bundle();
			args.putInt("ID", ((Long)id).intValue());
			args.putString("NAME", name);
			FragmentCodeList fcl = new FragmentCodeList();
			fcl.setArguments(args);
			getFragmentManager().beginTransaction().replace(R.id.frame_contet, fcl).addToBackStack(null).commit();
		}

	
	};
	
	
}
