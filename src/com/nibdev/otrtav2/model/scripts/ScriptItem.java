package com.nibdev.otrtav2.model.scripts;

public class ScriptItem {

	public enum ItemType{CODE, SKRIPT, DELAY};
	
	private ItemType mType;
	private int mValue;
	
	public int getValue() {
		return mValue;
	}

	public void setValue(int value) {
		this.mValue = value;
	}

	public ItemType getType() {
		return mType;
	}

	public void setType(ItemType type) {
		this.mType = type;
	}
	
}
