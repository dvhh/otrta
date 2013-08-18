package com.nibdev.otrtav2.model.scripts;

import java.util.ArrayList;
import java.util.List;

public class Script {

	private int mId;
	private String mName;
	private List<ScriptItem> mItems;
	
	
	public Script(){
		mId = -1;
		mName = "New";
		mItems = new ArrayList<ScriptItem>();
	}
	
	public int getId() {
		return mId;
	}
	public void setId(int id) {
		mId = id;
	}
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		mName = name;
	}
	public List<ScriptItem> getItems() {
		return mItems;
	}
	public void setItems(List<ScriptItem> items) {
		mItems = items;
	}
	
}
