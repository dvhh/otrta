package com.nibdev.otrtav2.model.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonData {
	private GsonData(){};

	public static Gson gSON = getGson();
	private static Gson _gInstance;

	private static Gson getGson(){
		if (_gInstance == null){
			GsonBuilder builder = new GsonBuilder();
			//builder.registerTypeAdapter(ItemBase.class, new ItemBaseJsonAdapter());
			_gInstance =  builder.create();
		}
		return _gInstance;
	}


	public static String ObjectToGSON(Object o){
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Base64OutputStream baseos = new Base64OutputStream(baos, Base64.DEFAULT);
			DeflaterOutputStream dfos = new DeflaterOutputStream(baseos);
			dfos.write(gSON.toJson(o).getBytes());
			dfos.flush();
			dfos.close();
			String ret = new String(baos.toByteArray());
			return ret;
		}catch (Exception ex){
			ex.printStackTrace();
		}
		return null;
	}

	public static <T> T GSONToObject(Class<T> type, String data){
		try{
			ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
			Base64InputStream baseis = new Base64InputStream(bais, Base64.DEFAULT);
			InflaterInputStream iis = new InflaterInputStream(baseis);
			InputStreamReader isr = new InputStreamReader(iis);
			T obj = gSON.fromJson(isr, type);
			isr.close();
			return obj;
		}catch (Exception ex){
			ex.printStackTrace();
		}
		return null;
	}

	
}
