package com.nibdev.otrtav2.view.adapters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.model.NQ;

public class GridAdapterUserLayouts extends BaseAdapter {

	private File mSkinFolder;
	private List<String> mFolderNames;

	public GridAdapterUserLayouts(File skinFolder){
		mSkinFolder = skinFolder;
		readDir();
	}

	private void readDir(){
		mFolderNames = new ArrayList<String>();
		if (mSkinFolder.exists()){
			for (File f : mSkinFolder.listFiles()){
				if (f.isDirectory()){
					for (File iff : f.listFiles()){
						if (iff.getName().toLowerCase(Locale.ENGLISH).equals("index.html")){
							mFolderNames.add(f.getName());
							break;
						}
					}
				}
			}
		}
	}



	@Override
	public int getCount() {
		return mFolderNames.size();
	}

	@Override
	public Object getItem(int position) {
		return new File(mSkinFolder, mFolderNames.get(position));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = NQ.baseAdaptViewInit(convertView, parent, R.layout.gvitem_layout);

		TextView tvName = (TextView)v.findViewById(R.id.tv_name);
		tvName.setText(mFolderNames.get(position));


		return v;
	}

}
