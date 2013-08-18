package com.nibdev.otrtav2.view.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.model.NQ;
import com.nibdev.otrtav2.model.database.DBLocal;
import com.nibdev.otrtav2.model.database.DBRemote;
import com.nibdev.otrtav2.model.jdata.Code;
import com.nibdev.otrtav2.model.tools.MD5;
import com.nibdev.otrtav2.model.tools.ServiceData;
import com.nibdev.otrtav2.service.OTRTAService;

public class CodeListAdapter extends BaseAdapter {

	private List<Map<String, Object>> mSortedCodes;
	private ProgressDialog mCheckDeleteDialog;

	public CodeListAdapter(int modelId){
		DBLocal localDb = OTRTAService.getInstance().getLocalDb();

		List<Map<String, Object>> codes = localDb.getCodeAllocationsForModelId(modelId);
		TreeMap<String, Integer> allCodeTypes = localDb.getSortedCodeTypes();
		List<Integer> codeTypeIds = new ArrayList<Integer>(allCodeTypes.values());
		List<String> codeTypeNames = new ArrayList<String>(allCodeTypes.keySet());

		TreeMap<String, Map<String,Object>> tmpSort = new TreeMap<String, Map<String,Object>>();
		SparseIntArray tmpCount = new SparseIntArray();

		for (Map<String, Object> unsorted : codes){
			int ctId = (Integer) unsorted.get(DBLocal.COLUMN_CODETYPEID);
			String ctName = codeTypeNames.get(codeTypeIds.indexOf(ctId));		
			int count = 0;
			if (tmpCount.get(ctId, -1) > -1){
				count = tmpCount.get(ctId);
				ctName += " [" + (count+1) + "]";
			}
			tmpCount.put(ctId, count + 1);		
			unsorted.put("NAME", ctName);
			tmpSort.put(ctName, unsorted);
		}
		mSortedCodes = new ArrayList<Map<String,Object>>(tmpSort.values());
	}


	@Override
	public int getCount() {
		return mSortedCodes.size();
	}

	@Override
	public Object getItem(int position) {
		return mSortedCodes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return (Long) mSortedCodes.get(position).get(DBLocal.COLUMN_ID);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null){
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lvitem_code, null);
		}

		TextView tvName = (TextView)v.findViewById(R.id.tv_name);
		ImageButton ibTrash = (ImageButton)v.findViewById(R.id.ib_trash);
		ImageButton ibSend = (ImageButton)v.findViewById(R.id.ib_send);
		NQ.v(v, R.id.ib_edit, ImageButton.class).setVisibility(View.GONE);
		
		ibTrash.setOnTouchListener(mOnRecolorTouchListener);
		ibTrash.setOnClickListener(mOnTrashClickListener);
		ibSend.setOnTouchListener(mOnSendTouchListener);
		
		tvName.setText((CharSequence) mSortedCodes.get(position).get("NAME"));
		NQ.v(v, R.id.tv_id, TextView.class).setText("ID: " + mSortedCodes.get(position).get(DBLocal.COLUMN_ID));	

		ibTrash.setTag(mSortedCodes.get(position).get(DBLocal.COLUMN_ID));
		ibSend.setTag(mSortedCodes.get(position).get(DBLocal.COLUMN_CODEID));



		return v;
	}

	private OnClickListener mOnTrashClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//prüfen ob gelöscht werden darf
			int codeAllocationId = ((Long)v.getTag()).intValue();

			mCheckDeleteDialog = new ProgressDialog(v.getContext());
			mCheckDeleteDialog.setMessage("Checking permission..");
			mCheckDeleteDialog.show();
			CheckPermissionTask cpt = new CheckPermissionTask();
			cpt.execute(codeAllocationId);
		}
	};


	private OnTouchListener mOnSendTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			mOnRecolorTouchListener.onTouch(v, event);

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				Code c = OTRTAService.getInstance().getLocalDb().getCodeById((Integer)v.getTag());
				OTRTAService.getInstance().getIrSender().sendCode(c.getData(), true);
				break;
			}
			case MotionEvent.ACTION_UP: {
				OTRTAService.getInstance().getIrSender().cancelCode();
				break;
			}
			case MotionEvent.ACTION_OUTSIDE: {
				OTRTAService.getInstance().getIrSender().cancelCode();
				break;
			}
			case MotionEvent.ACTION_CANCEL: {
				OTRTAService.getInstance().getIrSender().cancelCode();
				break;
			}

			}
			return true;
		}
	};

	private OnTouchListener mOnRecolorTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				v.getBackground().setColorFilter(0xFF33B5E5,PorterDuff.Mode.SRC_ATOP);
				v.invalidate();
				break;
			}
			case MotionEvent.ACTION_UP: {
				v.getBackground().clearColorFilter();
				v.invalidate();
				break;
			}
			case MotionEvent.ACTION_OUTSIDE: {
				v.getBackground().clearColorFilter();
				v.invalidate();
				break;
			}
			case MotionEvent.ACTION_CANCEL: {
				v.getBackground().clearColorFilter();
				v.invalidate();
				break;
			}

			}
			return false;
		}
	};



	private class CheckPermissionTask extends AsyncTask<Integer, Void, String>{
		private int deleteId;

		@Override
		protected String doInBackground(Integer... params) {
			try {
				JSONObject obj = new JSONObject();
				String sn = Build.SERIAL;
				String md5 = MD5.md5(sn);
				deleteId = params[0];
				obj.put("codeAllocationID", deleteId);
				obj.put("hashSum", md5);
				return ServiceData.HttpPostAnswer(DBRemote.URL_CHECKDELETE, obj);


			} catch (Exception dontcare) {} 
			return "0";
		}

		@Override
		protected void onPostExecute(String result) {
			int res = Integer.parseInt(result);
			mCheckDeleteDialog.dismiss();
			if (res != 19999){
				Toast.makeText(mCheckDeleteDialog.getContext(), "You are not allowed to delete this code", Toast.LENGTH_SHORT).show();
			}else{
				new AlertDialog.Builder(mCheckDeleteDialog.getContext())
				.setMessage("Are you sure?").setPositiveButton("Delete", mOnDeleteClickListener)
				.setNegativeButton("No way", null).create().show();
			}

		}

		private DialogInterface.OnClickListener mOnDeleteClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Thread t = new Thread(){
					public void run() {
						try{
							JSONObject obj = new JSONObject();
							String sn = Build.SERIAL;
							String md5 = MD5.md5(sn);
							obj.put("codeAllocationID", deleteId);
							obj.put("hashSum", md5);

							String ans = ServiceData.HttpPostAnswer(DBRemote.URL_DODELETE, obj);
							if (Integer.parseInt(ans) == 19999){
								OTRTAService.getInstance().getLocalDb().deleteCodeAllocation(deleteId);
								new Handler(Looper.getMainLooper()).post(new Runnable() {
									@Override
									public void run() {
										Toast.makeText(mCheckDeleteDialog.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
									}
								});
							}
						} catch (Exception not){}
					};

				};
				t.start();
			}
		};
	}

}
