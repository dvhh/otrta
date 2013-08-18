package com.nibdev.otrtav2.view.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.model.database.DBLocal;
import com.nibdev.otrtav2.service.OTRTAService;

public class SearchGridAdapter extends BaseAdapter {

	private TreeMap<String, TypedSearchEntry> mAllEntries;
	private List<TypedSearchEntry> mMatchedEntries;
	private SearchTask mSearchTask;

	public SearchGridAdapter(){

		mAllEntries = new TreeMap<String, SearchGridAdapter.TypedSearchEntry>();
		mMatchedEntries = new ArrayList<SearchGridAdapter.TypedSearchEntry>();

		DBLocal localDb = OTRTAService.getInstance().getLocalDb();

		TreeMap<String, Integer> sortedVendors = localDb.getSortedVendors();
		List<String> vendNameList = new ArrayList<String>(sortedVendors.keySet());
		List<Integer> vendIDList = new ArrayList<Integer>(sortedVendors.values());

		List<Map<String, Object>> models = localDb.getModels(null, null);
		SparseArray<String> devTypes = localDb.getDeviceTypes();
		for (Map<String, Object> modmap : models){
			TypedSearchEntry entry = new TypedSearchEntry();
			entry.ID = (Long) modmap.get(DBLocal.COLUMN_ID);
			entry.ModelName = new SpannableString((CharSequence) modmap.get(DBLocal.COLUMN_NAME));

			//vendor
			int vendId = (Integer) modmap.get(DBLocal.COLUMN_VENID);
			int vendInd = vendIDList.indexOf(vendId);

			if (vendInd >= 0){
				String vendName = vendNameList.get(vendInd);
				entry.VendorName = new SpannableString(vendName);

				//devType
				int devTypeId = (Integer) modmap.get(DBLocal.COLUMN_DEVTYPEID);
				entry.DeviceTypeName = devTypes.get(devTypeId);

				mAllEntries.put(entry.ModelName.toString(), entry);
			}
		}
	}

	@Override
	public int getCount() {
		int count = mMatchedEntries.size();
		return count;
	}

	@Override
	public Object getItem(int position) {
		return mMatchedEntries.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mMatchedEntries.get(position).ID;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		if (v == null){
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gvitem_search, null);
		}

		TextView tvModName = (TextView)v.findViewById(R.id.tv_name);
		TextView tvVendName = (TextView)v.findViewById(R.id.tv_vendor);
		TextView tvType = (TextView)v.findViewById(R.id.tv_type);

		tvModName.setText(mMatchedEntries.get(position).ModelName);
		tvVendName.setText(mMatchedEntries.get(position).VendorName);
		tvType.setText(mMatchedEntries.get(position).DeviceTypeName);

		return v;
	}

	public void stopSearchTask() {
		if (mSearchTask != null){
			mSearchTask.cancel(false);
		}
	}

	public void setTextChanged(String search) {
		if (mSearchTask != null) stopSearchTask();
		mSearchTask = new SearchTask();
		mSearchTask.execute(search);
	}


	private class SearchTask extends AsyncTask<String, TypedSearchEntry, List<TypedSearchEntry>>{

		@Override
		protected void onPreExecute() {
			mMatchedEntries.clear();
			notifyDataSetChanged();
		}

		@Override
		protected List<TypedSearchEntry> doInBackground(String... params) {
			Locale defaultLocale = Locale.getDefault();
			String searchString = params[0].toLowerCase(defaultLocale);
			List<TypedSearchEntry> matches = new ArrayList<TypedSearchEntry>();

			for (TypedSearchEntry sentry : mAllEntries.values()){
				if (isCancelled()) return null;

				boolean publish = false;

				sentry.VendorName = new SpannableString(sentry.VendorName.toString());
				if (sentry.VendorName.toString().toLowerCase(defaultLocale).contains(searchString)){
					publish = true;
					int start = sentry.VendorName.toString().toLowerCase(defaultLocale).indexOf(searchString);
					sentry.VendorName.setSpan(new ForegroundColorSpan(Color.RED), start, start + searchString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}

				sentry.ModelName = new SpannableString(sentry.ModelName.toString());
				if (sentry.ModelName.toString().toLowerCase(defaultLocale).contains(searchString)){
					publish = true;
					int start = sentry.ModelName.toString().toLowerCase(defaultLocale).indexOf(searchString);
					sentry.ModelName.setSpan(new ForegroundColorSpan(Color.RED), start, start + searchString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}

				if (publish){
					matches.add(sentry);
				}

			}
			return matches;
		}



		@Override
		protected void onPostExecute(List<TypedSearchEntry> result) {
			if (result != null){
				mMatchedEntries.addAll(result);
				notifyDataSetChanged();
			}
		}


	}

	public static class TypedSearchEntry{
		SpannableString ModelName;
		SpannableString VendorName;
		String DeviceTypeName;
		long ID;

		public String getName(){
			return ModelName.toString();
		}

		public int getID(){
			return ((Long)ID).intValue();
		}
	}





}
