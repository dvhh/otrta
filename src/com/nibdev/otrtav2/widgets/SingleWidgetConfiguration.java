package com.nibdev.otrtav2.widgets;

public class SingleWidgetConfiguration {

	public static final int TYPE_CODE = 100;
	public static final int TYPE_SCRIPT = 101;
	public static final int TYPE_ALLOFF = 102;
	public static final int TYPE_LAYOUTSTART = 103;

	private int mType;
	private int mWidgetID;
	private int mAllocationID;

	private String mTopText;
	private String mBottomText;
	
	private String mConfigText;
	
	
	public SingleWidgetConfiguration(){
		mTopText = "";
		mBottomText = "";
	}
	
	public int getWidgetID() {
		return mWidgetID;
	}
	public void setWidgetID(int widgetID) {
		mWidgetID = widgetID;
	}
	public int getAllocationID() {
		return mAllocationID;
	}
	public void setAllocationID(int allocationID) {
		mAllocationID = allocationID;
	}
	public int getType() {
		return mType;
	}
	public void setType(int type) {
		mType = type;
	}
	public String getTopText() {
		return mTopText;
	}
	public void setTopText(String topText) {
		mTopText = topText;
	}
	public String getBottomText() {
		return mBottomText;
	}
	public void setBottomText(String bottomText) {
		mBottomText = bottomText;
	}

	public String getConfigText() {
		return mConfigText;
	}

	public void setConfigText(String configText) {
		mConfigText = configText;
	}

}
