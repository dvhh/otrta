package com.nibdev.otrtav2.view.fragments;

import java.util.Map;

import android.animation.LayoutTransition;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.activities.ActivityMain;
import com.nibdev.otrtav2.activities.ActivityMain.OnBackKeyListener;
import com.nibdev.otrtav2.model.database.DBLocal;
import com.nibdev.otrtav2.model.database.DBRemote;
import com.nibdev.otrtav2.view.adapters.SearchGridAdapter;
import com.nibdev.otrtav2.view.adapters.SearchGridAdapter.TypedSearchEntry;
import com.nibdev.otrtav2.view.adapters.VendorGridAdapter;

public class FragmentVendorGrid extends Fragment implements OnBackKeyListener {

	private GridView mGvVendors;
	private AutoCompleteTextView mActvSearch;

	private SearchGridAdapter mSearchAdapter;
	private VendorGridAdapter mVendorAdapter;

	private boolean mSearchWasVisible;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		setHasOptionsMenu(true);

		View v= inflater.inflate(R.layout.fragment_vendorgrid, null);

		mGvVendors = (GridView)v.findViewById(R.id.gv_vendorlist);
		mActvSearch = (AutoCompleteTextView)v.findViewById(R.id.actv_search);		

		mSearchAdapter = new SearchGridAdapter();
		mVendorAdapter = new VendorGridAdapter();
		mGvVendors.setAdapter(mVendorAdapter);
		mGvVendors.setOnItemClickListener(mOnVendorClickListener);

		mActvSearch.addTextChangedListener(mOnSearchTextWatcher);
		if (!mSearchWasVisible){
			mActvSearch.setVisibility(View.GONE);
		}
		getActivity().setTitle(R.string.app_name);

		if (getActivity() instanceof ActivityMain){
			((ActivityMain)getActivity()).setOnBackKeyListener(this);
		}

		return v;

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (getActivity() instanceof ActivityMain){
			((ActivityMain)getActivity()).setOnBackKeyListener(null);
		}
	}


	@Override
	public boolean handlesBackKey() {
		if (mActvSearch.getVisibility() == View.VISIBLE){
			hideSearch();
			return true;
		}else{
			return false;
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_vendorlistfragment, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_search){
			if (mActvSearch.getVisibility() == View.VISIBLE){
				hideSearch();
			}else{
				showSearch();				
			}
			return true;
		}else if (item.getItemId() == R.id.action_sync){
			DBRemote.getInstance().startFullSyncThread();
			return true;
		}
		return false;
	}



	private void showSearch(){
		//set transiontn
		LayoutTransition l = new LayoutTransition();
		l.enableTransitionType(LayoutTransition.APPEARING);
		RelativeLayout rl = (RelativeLayout)getView().findViewById(R.id.rl_vendorlist);
		rl.setLayoutTransition(l);
		//animate
		mActvSearch.setVisibility(View.VISIBLE);
		//show keyboard
		mActvSearch.requestFocus();
		InputMethodManager keyboard = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		keyboard.showSoftInput(mActvSearch, 0);

		mSearchWasVisible = true;
	}

	private void hideSearch(){
		//set transiontn
		LayoutTransition l = new LayoutTransition();
		l.enableTransitionType(LayoutTransition.DISAPPEARING);
		RelativeLayout rl = (RelativeLayout)getView().findViewById(R.id.rl_vendorlist);
		rl.setLayoutTransition(l);
		//animate
		mActvSearch.setVisibility(View.GONE);
		//set text to ""
		mActvSearch.setText("");

		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mActvSearch.getWindowToken(), 0);

		mSearchWasVisible = false;
	}


	private OnItemClickListener mOnVendorClickListener = new OnItemClickListener(){
		@SuppressWarnings("unchecked")
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			Long vendorId = arg3;
			if (vendorId > 0){
				Bundle args = new Bundle();
				args.putString("NAME", (String)((Map<String, Object>)arg0.getItemAtPosition(arg2)).get(DBLocal.COLUMN_NAME));
				args.putInt("ID", vendorId.intValue());
				FragmentModelList fml = new FragmentModelList();
				fml.setArguments(args);
				getFragmentManager().beginTransaction().replace(R.id.frame_contet, fml).addToBackStack(null).commit();
			}
		}

	};

	private OnItemClickListener mOnModelClickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			TypedSearchEntry tse = (TypedSearchEntry)arg0.getItemAtPosition(arg2);
			int modId = tse.getID();
			if (modId > 0){
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mActvSearch.getWindowToken(), 0);

				Bundle args = new Bundle();
				args.putInt("ID", modId);
				args.putString("NAME", tse.getName());
				FragmentCodeList fcl = new FragmentCodeList();
				fcl.setArguments(args);
				getFragmentManager().beginTransaction().replace(R.id.frame_contet, fcl).addToBackStack(null).commit();
			}
		}

	};

	private TextWatcher mOnSearchTextWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (s.length() == 0){
				mSearchAdapter.stopSearchTask();
				mGvVendors.setAdapter(mVendorAdapter);
				mGvVendors.setOnItemClickListener(mOnVendorClickListener);
			}else{
				mGvVendors.setAdapter(mSearchAdapter);
				mGvVendors.setOnItemClickListener(mOnModelClickListener);
				mSearchAdapter.setTextChanged(s.toString());
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

		@Override
		public void afterTextChanged(Editable s) {}
	};






}
