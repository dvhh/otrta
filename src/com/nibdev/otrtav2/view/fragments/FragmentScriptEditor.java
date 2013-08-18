package com.nibdev.otrtav2.view.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.model.scripts.Script;
import com.nibdev.otrtav2.model.scripts.ScriptExecutor;
import com.nibdev.otrtav2.model.scripts.ScriptExecutor.OnSendProgressChanged;
import com.nibdev.otrtav2.model.scripts.ScriptItem;
import com.nibdev.otrtav2.model.scripts.ScriptItem.ItemType;
import com.nibdev.otrtav2.service.OTRTAService;
import com.nibdev.otrtav2.view.adapters.ScriptItemListAdapter;
import com.nibdev.otrtav2.view.custom.StringInputDialog;
import com.nibdev.otrtav2.view.custom.StringInputDialog.OnTextChanged;
import com.nibdev.otrtav2.view.custom.dslv.DragSortListView;
import com.nibdev.otrtav2.view.custom.dslv.DragSortListView.DropListener;
import com.nibdev.otrtav2.view.custom.dslv.DragSortListView.RemoveListener;
import com.nibdev.otrtav2.view.fragments.FragmentSelectCodeAllocationDialog.OnSelectedCodeChangedListener;
import com.nibdev.otrtav2.view.fragments.FragmentSelectScriptDialog.OnSelectedScriptChangedListener;

public class FragmentScriptEditor extends Fragment {



	private Script mScript;

	private TextView mTvName;
	private ImageButton mIbCode;
	private ImageButton mIbScript;
	private ImageButton mIbDelay;
	private DragSortListView mLvItems;
	private ProgressDialog mTestProgressDialog;


	private ScriptItemListAdapter mAdapter;
	private ScriptItem mEditItem;
	private ScriptExecutor mWorkingScriptExecutor;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		setHasOptionsMenu(true);

		View v= inflater.inflate(R.layout.fragment_scripteditor, null);
		mScript = OTRTAService.getInstance().getLocalDb().getScriptById(getArguments().getInt("ID"));

		mTvName = (TextView)v.findViewById(R.id.tv_name);
		mLvItems = (DragSortListView)v.findViewById(R.id.lv_items);
		mIbCode = (ImageButton)v.findViewById(R.id.ib_trashh);
		mIbScript = (ImageButton)v.findViewById(R.id.ib_script);
		mIbDelay = (ImageButton)v.findViewById(R.id.ib_delay);

		mTvName.setText(mScript.getName());

		mAdapter = new ScriptItemListAdapter(mScript.getItems(), null);
		mLvItems.setAdapter(mAdapter);
		mLvItems.setOnItemClickListener(mOnExistingItemClickListener);
		mLvItems.setDropListener(mLvDropListener);
		mLvItems.setRemoveListener(mLvRemoveListener);

		mIbCode.setOnClickListener(mOnNewItemClickListener);
		mIbScript.setOnClickListener(mOnNewItemClickListener);
		mIbDelay.setOnClickListener(mOnNewItemClickListener);

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_scripteditorfragment, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_test){
			mWorkingScriptExecutor = new ScriptExecutor(mScript, OTRTAService.getInstance().getLocalDb(), OTRTAService.getInstance().getIrSender());
			mWorkingScriptExecutor.setOnProgressChangedListener(mOnTestScriptProgressChanged);
			ScriptExecutor.resetSentIds();

			mTestProgressDialog = new ProgressDialog(getActivity());
			mTestProgressDialog.setOnDismissListener(mOnTestProgressDismissListener);
			mTestProgressDialog.setMessage("Executing");
			mTestProgressDialog.show();

			mWorkingScriptExecutor.execute();
			return true;
		}
		return false;
	}


	private OnItemClickListener mOnExistingItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			mEditItem = mScript.getItems().get(arg2);

			if (mEditItem.getType() == ItemType.CODE){
				Bundle args = new Bundle();
				args.putInt("CAID", mEditItem.getValue());
				FragmentSelectCodeAllocationDialog dfsc = new FragmentSelectCodeAllocationDialog();
				dfsc.setArguments(args);
				dfsc.setOnSelectedCodeChangedListener(mOnCodeChangedListener);
				dfsc.show(getFragmentManager(), "DFSC");

			}else if (mEditItem.getType() == ItemType.SKRIPT){
				FragmentSelectScriptDialog fssd = new FragmentSelectScriptDialog();
				fssd.setOnSelectedScriptChangedListener(mOnScriptChangedListener);
				fssd.show(getFragmentManager(), "FSSD");


			}else if (mEditItem.getType() == ItemType.DELAY){
				StringInputDialog.getStringInputDialog(getActivity(), mEditItem.getValue() + "", "Delay[s]", "Set", null, InputType.TYPE_CLASS_NUMBER, mOnDelayChangedListener).show();
			}

		}
	};


	private OnClickListener mOnNewItemClickListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			mEditItem = null;

			int btId = v.getId();

			if (btId == R.id.ib_trashh){
				FragmentSelectCodeAllocationDialog dfsc = new FragmentSelectCodeAllocationDialog();
				dfsc.setOnSelectedCodeChangedListener(mOnCodeChangedListener);
				dfsc.show(getFragmentManager(), "DFSC");


			}else if (btId == R.id.ib_script){
				FragmentSelectScriptDialog fssd = new FragmentSelectScriptDialog();
				fssd.setOnSelectedScriptChangedListener(mOnScriptChangedListener);
				fssd.show(getFragmentManager(), "FSSD");

			}else if (btId == R.id.ib_delay){
				StringInputDialog.getStringInputDialog(getActivity(), "1", "Delay[s]", "Set", null, InputType.TYPE_CLASS_NUMBER, mOnDelayChangedListener).show();
			}
		}

	};



	private OnSelectedCodeChangedListener mOnCodeChangedListener = new OnSelectedCodeChangedListener() {
		@Override
		public void selectedCodeChanged(int codeAllocationId) {
			if (mEditItem == null){
				ScriptItem newItem = new ScriptItem();
				newItem.setType(ItemType.CODE);
				newItem.setValue(codeAllocationId);

				mScript.getItems().add(mScript.getItems().size(), newItem);
				OTRTAService.getInstance().getLocalDb().saveUpdateScript(mScript);
				mAdapter.notifyDataSetChanged();
			}else{
				mEditItem.setValue(codeAllocationId);
				OTRTAService.getInstance().getLocalDb().saveUpdateScript(mScript);
				mAdapter.notifyDataSetChanged();
			}
		}
	};

	private OnTextChanged mOnDelayChangedListener = new OnTextChanged() {
		@Override
		public void textChanged(String text) {
			if (mEditItem == null){
				try{
					ScriptItem newItem = new ScriptItem();
					newItem.setType(ItemType.DELAY);
					int delay = Integer.parseInt(text);
					newItem.setValue(delay);

					mScript.getItems().add(mScript.getItems().size(), newItem);
					OTRTAService.getInstance().getLocalDb().saveUpdateScript(mScript);
					mAdapter.notifyDataSetChanged();
				}catch (Exception ex){
					return;
				}
			}else{
				try{
					int delay = Integer.parseInt(text);
					mEditItem.setValue(delay);
					OTRTAService.getInstance().getLocalDb().saveUpdateScript(mScript);
					mAdapter.notifyDataSetChanged();
				}catch (Exception ex){
					return;
				}
			}
		}
	};

	private OnSelectedScriptChangedListener mOnScriptChangedListener = new OnSelectedScriptChangedListener() {
		@Override
		public void selectedScriptChanged(Script s) {
			if (mEditItem == null){
				try{
					ScriptItem newItem = new ScriptItem();
					newItem.setType(ItemType.SKRIPT);
					newItem.setValue(s.getId());

					mScript.getItems().add(mScript.getItems().size(), newItem);
					OTRTAService.getInstance().getLocalDb().saveUpdateScript(mScript);
					mAdapter.notifyDataSetChanged();
				}catch (Exception ex){
					return;
				}
			}else{
				try{
					mEditItem.setValue(s.getId());
					OTRTAService.getInstance().getLocalDb().saveUpdateScript(mScript);
					mAdapter.notifyDataSetChanged();
				}catch (Exception ex){
					return;
				}
			}

		}
	};


	private DropListener mLvDropListener = new DropListener() {
		@Override
		public void drop(int from, int to) {
			if (from == to) return;

			ScriptItem removeAt = mScript.getItems().remove(from);
			if(to < from) {
				mScript.getItems().add(to, removeAt);
			}else{
				mScript.getItems().add(to, removeAt);
			}
			OTRTAService.getInstance().getLocalDb().saveUpdateScript(mScript);
			mAdapter.notifyDataSetChanged();
		}
	};

	private RemoveListener mLvRemoveListener = new RemoveListener() {
		@Override
		public void remove(int which) {
			mScript.getItems().remove(which);
			OTRTAService.getInstance().getLocalDb().saveUpdateScript(mScript);
			mAdapter.notifyDataSetChanged();
		}
	};

	private OnSendProgressChanged mOnTestScriptProgressChanged = new OnSendProgressChanged() {
		@Override
		public void sendProgressChanged(final int total, final int count) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (mTestProgressDialog.isShowing()){
						if (mTestProgressDialog.isIndeterminate()){
							mTestProgressDialog.setOnDismissListener(null);
							mTestProgressDialog.dismiss();
							mTestProgressDialog = new ProgressDialog(getActivity());
							mTestProgressDialog.setIndeterminate(false);
							mTestProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
							mTestProgressDialog.setOnDismissListener(mOnTestProgressDismissListener);
							mTestProgressDialog.show();
						}
						mTestProgressDialog.setMax(total);
						mTestProgressDialog.setProgress(count);
					}

				}
			});

		}

		@Override
		public void executionFinished() {
			mTestProgressDialog.dismiss();
		}
	};

	private OnDismissListener mOnTestProgressDismissListener = new OnDismissListener() {
		@Override
		public void onDismiss(DialogInterface dialog) {
			if (mWorkingScriptExecutor != null){
				mWorkingScriptExecutor.cancel();
				try {
					mWorkingScriptExecutor.joinExecuteThread();
				} catch (InterruptedException e) {}
			}
		}
	};


}
