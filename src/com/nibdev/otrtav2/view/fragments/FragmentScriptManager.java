package com.nibdev.otrtav2.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.model.scripts.Script;
import com.nibdev.otrtav2.service.OTRTAService;
import com.nibdev.otrtav2.view.adapters.ScriptGridAdapter;
import com.nibdev.otrtav2.view.custom.StringInputDialog;
import com.nibdev.otrtav2.view.custom.StringInputDialog.OnTextChanged;

public class FragmentScriptManager extends Fragment {


	private GridView mGvScripts;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View v= inflater.inflate(R.layout.fragment_scriptmanager, null);
		mGvScripts = (GridView)v.findViewById(R.id.gv_scripts);
		mGvScripts.setAdapter(new ScriptGridAdapter());
		mGvScripts.setOnItemClickListener(mOnScriptClickListener);
		registerForContextMenu(mGvScripts);
		return v;
	}



	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_scriptmanagerfragment, menu);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.menu_scriptmanagerfragment_gv_context, menu);
		menu.setHeaderTitle("Actions");
	}

	public boolean onContextItemSelected(MenuItem item) {
		if (getUserVisibleHint()){
			AdapterContextMenuInfo adapInfo = (AdapterContextMenuInfo) item.getMenuInfo();
			final Long id = adapInfo.id;
			if (item.getItemId() == R.id.action_rename){
				String currentName = OTRTAService.getInstance().getLocalDb().getScriptById(id.intValue()).getName();
				StringInputDialog.getStringInputDialog(getActivity(), currentName, "Change name", "Save", null, InputType.TYPE_CLASS_TEXT, new OnTextChanged() {
					@Override
					public void textChanged(String text) {
						if (!text.isEmpty()){
							Script s = OTRTAService.getInstance().getLocalDb().getScriptById(id.intValue());
							s.setName(text.trim());
							OTRTAService.getInstance().getLocalDb().saveUpdateScript(s);
							((ScriptGridAdapter)mGvScripts.getAdapter()).reloadScripts();
						}
					}
				}).show();	
			}else if (item.getItemId() == R.id.action_delete){
				Script tmp = new Script();
				tmp.setId(id.intValue());
				OTRTAService.getInstance().getLocalDb().deleteScript(tmp);
				((ScriptGridAdapter)mGvScripts.getAdapter()).reloadScripts();
			}else if (item.getItemId() == R.id.action_duplicate){
				Script orig = OTRTAService.getInstance().getLocalDb().getScriptById(id.intValue());
				orig.setId(-1);
				OTRTAService.getInstance().getLocalDb().saveUpdateScript(orig);
				((ScriptGridAdapter)mGvScripts.getAdapter()).reloadScripts();
			}
			return true;
		}
		return false;
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_add){
			OTRTAService.getInstance().getLocalDb().saveUpdateScript(new Script());
			((ScriptGridAdapter)mGvScripts.getAdapter()).reloadScripts();
			return true;
		}
		return false;
	}



	private OnItemClickListener mOnScriptClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			Bundle args = new Bundle();
			args.putInt("ID", ((Long)arg3).intValue());
			FragmentScriptEditor fse = new FragmentScriptEditor();
			fse.setArguments(args);
			getFragmentManager().beginTransaction().replace(R.id.frame_contet, fse).addToBackStack(null).commit();
		}

	};


}
