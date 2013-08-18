package com.nibdev.otrtav2.view.custom;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.model.NQ;

public class SendGlow {

	private static Context mContext;
	private static LinearLayout mContainer;
	private static View mGlowView;
	private static WindowManager mWm;
	private static LayoutParams mLp;
	private static AnimatorSet mAnimSet;

	private SendGlow(){}
	
	public static void init(Context c){
		mContext = c.getApplicationContext();
		mWm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

		mContainer = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.overlay_glow, null);
		mContainer.setFocusable(false);
		mContainer.setClickable(false);
		mContainer.setKeepScreenOn(false);
		mContainer.setLongClickable(false);
		mContainer.setFocusableInTouchMode(false);

		mGlowView = mContainer.findViewById(R.id.v_glow);
		mGlowView.setAlpha(0f);

		mLp = new LayoutParams();
		mLp.height = LayoutParams.WRAP_CONTENT; 
		mLp.width = LayoutParams.MATCH_PARENT;
		mLp.flags = 280; // You can try LayoutParams.FLAG_FULLSCREEN too
		mLp.format = PixelFormat.TRANSLUCENT; // You can try different formats
		mLp.type = LayoutParams.TYPE_SYSTEM_OVERLAY;
		mLp.gravity = Gravity.TOP;


		ObjectAnimator animIn = ObjectAnimator.ofFloat(mGlowView, "alpha", 0.5f, 1f);
		animIn.setDuration(25);
		ObjectAnimator animOut = ObjectAnimator.ofFloat(mGlowView, "alpha", 1f, 0f);
		animOut.setDuration(75);
		mAnimSet = new AnimatorSet();
		mAnimSet.playSequentially(animIn, animOut);
		mAnimSet.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
				mWm.addView(mContainer, mLp);
			}

			@Override
			public void onAnimationRepeat(Animator animation) {}

			@Override
			public void onAnimationEnd(Animator animation) {
				mWm.removeView(mContainer);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				mWm.removeView(mContainer);
			}
		});
	}

	public static void sGlowOnce(){
		NQ.onUiThread(mAnimRunnable);
	}
	
	private static Runnable mAnimRunnable = new Runnable() {
		@Override
		public void run() {
			if (!mAnimSet.isRunning()){
				mAnimSet.start();
			}
		}
	};



}
