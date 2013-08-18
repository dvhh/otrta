package com.nibdev.otrtav2.model.scripts;

import java.util.HashSet;

import com.nibdev.otrtav2.model.database.DBLocal;
import com.nibdev.otrtav2.model.jdata.Code;
import com.nibdev.otrtav2.model.scripts.ScriptItem.ItemType;
import com.nibdev.otrtav2.service.irinterfaces.IrFace;

public class ScriptExecutor {

	private static boolean _cancel;
	private static HashSet<Integer> _sentScriptIds;
	
	public interface OnSendProgressChanged{
		void sendProgressChanged(int total, int count);
		void executionFinished();
	}
	private OnSendProgressChanged mListener;

	private Script mScript;
	private DBLocal mDb;
	private IrFace mIrSender;

	public ScriptExecutor(Script s, DBLocal db, IrFace irsender){
		if (_sentScriptIds == null) _sentScriptIds = new HashSet<Integer>();
		mScript = s;
		mDb = db;
		mIrSender = irsender;
	};

	public void setOnProgressChangedListener(OnSendProgressChanged listener){
		mListener = listener;
	}

	public void execute(){
		_cancel = false;
		
		if (!mExecutorThread.isAlive()){
			mExecutorThread.start();
		}
	}

	public void cancel(){
		_cancel = true;

		try {
			mExecutorThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void joinExecuteThread() throws InterruptedException{
		mExecutorThread.join();
	}
	
	public static void resetSentIds(){
		if (_sentScriptIds != null) _sentScriptIds.clear();
	}


	private Thread mExecutorThread = new Thread(){
		public void run() {
			try{
				
				if (_sentScriptIds.contains(mScript.getId())) return;
				_sentScriptIds.add(mScript.getId());
				
				for (ScriptItem s : mScript.getItems()){
					if (_cancel) return;
					
					if (mListener != null) mListener.sendProgressChanged(mScript.getItems().size(), mScript.getItems().indexOf(s));
					
					
					ItemType type = s.getType();
					
					if (type == ItemType.CODE){
						Code c = mDb.getCodeByCodeAllocationId(s.getValue());
						mIrSender.sendCodeAndSleep(c.getData(), c.getData().sleepTime());

					}else if (type == ItemType.DELAY){
						for (int i = 0; i < s.getValue(); i++){
							for (int y = 0; y < 10; y++){
								if (_cancel) return;
								Thread.sleep(100);
							}
						}
						
					}else if (type == ItemType.SKRIPT){
						Script inner = mDb.getScriptById(s.getValue());
						ScriptExecutor innerSe = new ScriptExecutor(inner, mDb, mIrSender);
						innerSe.execute();
						innerSe.joinExecuteThread();
						
					}
				}
				
				_sentScriptIds.remove(mScript.getId());

			}catch (Exception ex){
				ex.printStackTrace();
			}
			
			if (mListener != null) mListener.executionFinished();
			
		};
		
	
	};
	
	



}
