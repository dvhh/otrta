package com.nibdev.otrtav2.activities;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.nibdev.otrtav2.service.OTRTAService;

public class ActivitySendByUri extends Activity{


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try{
			Intent i = getIntent();
			if (i != null){

				String authorityLower = i.getData().getAuthority().toLowerCase(Locale.ENGLISH);

				if (authorityLower.equals("code")){
					String idParam = i.getData().getQueryParameter("id").toLowerCase(Locale.ENGLISH);
					if (idParam.equals("none")){
						//send cancel
						Intent sendInt  = new Intent(getApplicationContext(), OTRTAService.class);
						sendInt.putExtra("ACTION", OTRTAService.ACTION_CANCELCODE);
						startService(sendInt);

					}else{
						//send code
						int id = Integer.parseInt(idParam);
						String repeatParam = i.getData().getQueryParameter("repeat");
						Intent sendInt  = new Intent(getApplicationContext(), OTRTAService.class);
						sendInt.putExtra("ACTION", OTRTAService.ACTION_SENDCODE);
						sendInt.putExtra("REPEAT", (repeatParam != null && Boolean.parseBoolean(repeatParam)));
						sendInt.putExtra("ID", id);
						startService(sendInt);
					}


				}else if(authorityLower.equals("script")){
					//send script
					String idParam = i.getData().getQueryParameter("id").toLowerCase(Locale.ENGLISH);
					int id = Integer.parseInt(idParam);
					Intent sendInt  = new Intent(getApplicationContext(), OTRTAService.class);
					sendInt.putExtra("ACTION", OTRTAService.ACTION_SENDSCRIPT);
					sendInt.putExtra("ID", id);
					startService(sendInt);

				}else if (authorityLower.equals("button")){
					//send button
					String nameParam = i.getData().getQueryParameter("name").toLowerCase(Locale.ENGLISH);
					Intent sendInt = new Intent(getApplicationContext(), OTRTAService.class);
					sendInt.putExtra("ACTION", OTRTAService.ACTION_SENDBUTTON);
					sendInt.putExtra("NAME", nameParam);
					startService(sendInt);
				}
			}

		}catch (Exception egal){}

		finish();
	}



}
