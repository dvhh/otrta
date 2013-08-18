package com.nibdev.otrtav2.view.fragments;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.activities.ActivityUserLayout;
import com.nibdev.otrtav2.model.NQ;
import com.nibdev.otrtav2.view.adapters.GridAdapterUserLayouts;

public class FragmentUserLayoutManager extends Fragment {

	private GridView mGvLayouts;
	private Button mBtEditButtons;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		View v= inflater.inflate(R.layout.fragment_userlayoutmanager, null);
		mGvLayouts = (GridView)v.findViewById(R.id.gv_layouts);
		mBtEditButtons = NQ.v(v, R.id.bt_editbuttons, Button.class);
		mGvLayouts.setOnItemClickListener(mOnLayoutClickListener);
		
		File skinFolder = Environment.getExternalStorageDirectory();
		skinFolder = new File(skinFolder, "otrta");
		
		mGvLayouts.setAdapter(new GridAdapterUserLayouts(skinFolder));
		
		mBtEditButtons.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getFragmentManager().beginTransaction().replace(R.id.frame_contet, new FragmentButtonEditor()).addToBackStack(null).commit();				
			}
		});
		
		return v;
	}
	
	
	private OnItemClickListener mOnLayoutClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if ((arg0.getItemAtPosition(arg2) != null) && (arg0.getItemAtPosition(arg2) instanceof File)){
				File skinpath = (File)arg0.getItemAtPosition(arg2);
				Intent i = new Intent(getActivity(), ActivityUserLayout.class);
				i.putExtra("SKINPATH", skinpath.getAbsolutePath());
				startActivity(i);
			}
		}
		
	};
}
