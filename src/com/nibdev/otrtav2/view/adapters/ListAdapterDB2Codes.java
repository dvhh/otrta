package com.nibdev.otrtav2.view.adapters;

import java.util.List;
import java.util.Map;

import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.model.NQ;
import com.nibdev.otrtav2.model.database.DB2;
import com.nibdev.otrtav2.service.IrDataCompat;
import com.nibdev.otrtav2.service.OTRTAService;

public class ListAdapterDB2Codes extends BaseAdapter {

	private List<Map<String, Object>> mCodes; 

	public ListAdapterDB2Codes(int codesetId){
		DB2 db = OTRTAService.getInstance().getDB2();
		mCodes = db.getCodesForCodeSetId(codesetId);
	}

	@Override
	public int getCount() {
		return mCodes.size();
	}

	@Override
	public Object getItem(int position) {
		return mCodes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = NQ.baseAdaptViewInit(convertView, parent, R.layout.lvitem_code);

		TextView tvName = NQ.v(v, R.id.tv_name, TextView.class);
		TextView tvHide = NQ.v(v, R.id.tv_id, TextView.class);

		ImageButton ibHide1 = NQ.v(v, R.id.ib_trash, ImageButton.class);
		ImageButton ibHide2 = NQ.v(v, R.id.ib_edit, ImageButton.class);
		ImageButton ibSend = NQ.v(v, R.id.ib_send, ImageButton.class);

		tvHide.setVisibility(View.GONE);
		ibHide1.setVisibility(View.GONE);
		ibHide2.setVisibility(View.GONE);

		tvName.setText(mCodes.get(position).get(DB2.COLUMN_NAME).toString());
		ibSend.setTag(position);
		ibSend.setOnTouchListener(mOnSendTouchListener);
		//ibSend.setOnClickListener(mOnSendClickListener);
		
		return v;
	}
	

	private OnTouchListener mOnSendTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			mOnRecolorTouchListener.onTouch(v, event);

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				IrDataCompat c = IrDataCompat.fromJson(mCodes.get((Integer)v.getTag()).get(DB2.COLUMN_CODEDATA).toString());
				//Code c = OTRTAService.getInstance().getLocalDb().getCodeById((Integer)v.getTag());
				OTRTAService.getInstance().getIrSender().sendCode(c, true);
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

}
