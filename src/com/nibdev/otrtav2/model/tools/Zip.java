package com.nibdev.otrtav2.model.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Activity;
import android.util.Log;

public class Zip {
	
	public static void unzipRaw(Activity a, int rawId, File destinationFolder, boolean overwrite) {
		if (destinationFolder.exists() && !overwrite) return;
		destinationFolder.mkdirs();
	
		try  { 
			InputStream is = a.getResources().openRawResource(rawId);
			ZipInputStream zin = new ZipInputStream(is);
			
			ZipEntry zentry = null;
			while ((zentry = zin.getNextEntry()) != null){
				boolean isDir = zentry.isDirectory();
				if (isDir){
					File createDir = new File(destinationFolder, zentry.getName());
					createDir.mkdirs();
				}else{
					FileOutputStream fout = new FileOutputStream(new File(destinationFolder, zentry.getName()));
					int c = 0;
					byte[] buffer = new byte[64 * 1024];
					while ((c = zin.read(buffer)) > 0){
						fout.write(buffer, 0, c);
					}
					fout.close();
				}
				zin.closeEntry(); 
			}
			zin.close(); 
		} catch(Exception e) { 
			Log.e("Decompress", "unzip", e); 
		} 
	}
	
	public static void unzipFile(Activity a, File zipFile, File destinationFolder, boolean overwrite) {
		if (destinationFolder.exists() && !overwrite) return;
		destinationFolder.mkdirs();
	
		try  { 
			InputStream is = new FileInputStream(zipFile);
			ZipInputStream zin = new ZipInputStream(is);
			
			ZipEntry zentry = null;
			while ((zentry = zin.getNextEntry()) != null){
				boolean isDir = zentry.isDirectory();
				if (isDir){
					File createDir = new File(destinationFolder, zentry.getName());
					createDir.mkdirs();
				}else{
					FileOutputStream fout = new FileOutputStream(new File(destinationFolder, zentry.getName()));
					int c = 0;
					byte[] buffer = new byte[64 * 1024];
					while ((c = zin.read(buffer)) > 0){
						fout.write(buffer, 0, c);
					}
					fout.close();
				}
				zin.closeEntry(); 
			}
			zin.close(); 
		} catch(Exception e) { 
			Log.e("Decompress", "unzip", e); 
		} 
	}
}
