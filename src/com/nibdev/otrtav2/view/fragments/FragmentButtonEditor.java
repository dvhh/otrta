package com.nibdev.otrtav2.view.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nibdev.otrtav2.model.database.DBLocal;
import com.nibdev.otrtav2.service.OTRTAService;

public class FragmentButtonEditor extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
			ListView lv = new ListView(getActivity());
			
			
			List<Map<String, Object>> buttons = OTRTAService.getInstance().getLocalDb().getAllButtons();
			
			
			List<String> buttonNames = new ArrayList<String>();
			for (Map<String, Object> button : buttons){
				buttonNames.add(button.get(DBLocal.COLUMN_NAME).toString());
			}
			
			
			lv.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, buttonNames));
			
			
			return lv;
	
	}
	
}
