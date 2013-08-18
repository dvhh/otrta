package com.nibdev.otrtav2.activities;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.nibdev.otrtav2.service.OTRTAService;
import com.nibdev.otrtav2.view.fragments.FragmentSelectCodeAllocationDialog;
import com.nibdev.otrtav2.view.fragments.FragmentSelectCodeAllocationDialog.OnSelectedCodeChangedListener;

public class ActivitySelectCode extends FragmentActivity {

	private FragmentSelectCodeAllocationDialog mCodeSelectFrag;
	private String mChangingButtonName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mChangingButtonName = getIntent().getStringExtra("BUTTON");
		int allocId = OTRTAService.getInstance().getLocalDb().getButtonAllocation(mChangingButtonName);
				
		mCodeSelectFrag = new FragmentSelectCodeAllocationDialog();
		Bundle args = new Bundle();
		args.putInt("CAID", allocId);
		mCodeSelectFrag.setArguments(args);
		mCodeSelectFrag.setOnSelectedCodeChangedListener(new OnSelectedCodeChangedListener() {
			@Override
			public void selectedCodeChanged(int codeAllocationId) {
				if (codeAllocationId > 0){
					OTRTAService.getInstance().getLocalDb().saveButtonAllocation(mChangingButtonName, codeAllocationId);
				}
				finish();
			}
		});

		mCodeSelectFrag.show(getSupportFragmentManager(), "fscad");

	}

	@Override
	protected void onStart() {
		super.onStart();
		mCodeSelectFrag.getDialog().setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				ActivitySelectCode.this.finish();				
			}
		});
		mCodeSelectFrag.getDialog().setTitle(mChangingButtonName);
	}

}
