package com.nibdev.otrtav2.model.tools;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.http.AndroidHttpClient;

public class ServiceData {
	private ServiceData(){}

	public static ArrayList<JSONObject> HttpGetAnswerJsonObjectList(String url) throws ClientProtocolException, IOException, JSONException, HttpException  {
		String raw = HttpGetAnswer(url);
		JSONArray jarr = new JSONArray(raw);
		ArrayList<JSONObject> objs = new ArrayList<JSONObject>();
		for (int i = 0; i < jarr.length(); i++){
			objs.add(jarr.getJSONObject(i));
		}
		return objs;
	}

	public static JSONObject HttpGetAnswerJsonObject(String url) throws ClientProtocolException, IOException, JSONException, HttpException  {
		String raw = HttpGetAnswer(url);
		JSONObject jobj = new JSONObject(raw);
		return jobj;
	}


	public static String HttpGetAnswer(String url) throws ClientProtocolException, IOException, JSONException, HttpException{
		AndroidHttpClient client = AndroidHttpClient.newInstance(null);
		client.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 7500);
		client.getParams().setParameter(HttpConnectionParams.SO_TIMEOUT, 7500);
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response = client.execute(httpGet);
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		if (statusCode == 200) {
			String resp = EntityUtils.toString(response.getEntity());
			client.close();
			return resp;
		} else {
			try {
				client.close();
			}catch (Exception ex){}
			throw new HttpException("STATUSCODE!=200");
		}
	}

	public static String HttpPostAnswer(String url, JSONObject jobj) throws ClientProtocolException, IOException, JSONException, HttpException{
		AndroidHttpClient client = AndroidHttpClient.newInstance(null);
		client.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 7500);
		client.getParams().setParameter(HttpConnectionParams.SO_TIMEOUT, 7500);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new StringEntity(jobj.toString()));
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		HttpResponse response = client.execute(httpPost);
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		if (statusCode == 200) {
			String resp = EntityUtils.toString(response.getEntity());
			client.close();
			return resp;
		} else {
			try {
				client.close();
			}catch (Exception ex){}
			throw new HttpException("STATUSCODE!=200");
		}
	}

	public static ArrayList<JSONObject> HttpPostAnswer(String url, JSONArray jarr) throws ClientProtocolException, IOException, JSONException, HttpException{
		AndroidHttpClient client = AndroidHttpClient.newInstance(null);
		client.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 10000);
		client.getParams().setParameter(HttpConnectionParams.SO_TIMEOUT, 10000);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new StringEntity(jarr.toString()));
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		HttpResponse response = client.execute(httpPost);
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		if (statusCode == 200) {
			String resp = EntityUtils.toString(response.getEntity());
			client.close();
			JSONArray retjarr = new JSONArray(resp);
			ArrayList<JSONObject> objs = new ArrayList<JSONObject>();
			for (int i = 0; i < retjarr.length(); i++){
				objs.add(retjarr.getJSONObject(i));
			}
			return objs;
		} else {
			try {
				client.close();
			}catch (Exception ex){}
			throw new HttpException("STATUSCODE!=200");
		}
	}



	public static JSONObject HttpPostAnswerJsonObject(String url, JSONObject obj) throws ClientProtocolException, IOException, JSONException, HttpException{
		AndroidHttpClient client = AndroidHttpClient.newInstance(null);
		client.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 10000);
		client.getParams().setParameter(HttpConnectionParams.SO_TIMEOUT, 10000);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new StringEntity(obj.toString()));
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		HttpResponse response = client.execute(httpPost);
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		if (statusCode == 200) {
			String raw = EntityUtils.toString(response.getEntity());
			JSONObject jobj = new JSONObject(raw);
			client.close();
			return jobj;
		} else {
			try {
				client.close();
			}catch (Exception ex){}
			throw new HttpException("STATUSCODE!=200");
		}
	}

	public static JSONArray HttpPostAnswerJsonArray(String url, JSONObject obj) throws ClientProtocolException, IOException, JSONException, HttpException{
		AndroidHttpClient client = AndroidHttpClient.newInstance(null);
		client.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 10000);
		client.getParams().setParameter(HttpConnectionParams.SO_TIMEOUT, 10000);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new StringEntity(obj.toString()));
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		HttpResponse response = client.execute(httpPost);
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		if (statusCode == 200) {
			String raw = EntityUtils.toString(response.getEntity());
			JSONArray jarr = new JSONArray(raw);
			client.close();
			return jarr;
		} else {
			try {
				client.close();
			}catch (Exception ex){}
			throw new HttpException("STATUSCODE!=200");
		}
	}
}
