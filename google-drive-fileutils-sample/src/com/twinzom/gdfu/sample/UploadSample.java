package com.twinzom.gdfu.sample;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.drive.model.File;
import com.twinzom.gdfu.Authorization;
import com.twinzom.gdfu.GDFileUtils;

public class UploadSample extends Sample {
	
	public static void main(String[] args) throws IOException, GeneralSecurityException {
		GoogleCredential credential = Authorization.getCredential(KEY_FILE, Collections.singleton("https://www.googleapis.com/auth/drive"));
		GDFileUtils gdfu = new GDFileUtils(credential);
		
		Collection<File> beforeFiles = gdfu.listFiles("0B64VHJrvrPWHc09wdHh0aXFOYjg", null, Arrays.asList("id", "name"));
		System.out.println("------------------------------------------------------------");
		System.out.println("Before upload: files under a folder (id:0B64VHJrvrPWHc09wdHh0aXFOYjg):");
		for (File file : beforeFiles) {
			System.out.println(file);
		}
		
		System.out.println("------------------------------------------------------------");
		System.out.println("Upload now...");
		java.io.File localFile = new java.io.File("/Users/twinsen/Downloads/Soothe Your Crying Baby  8 Hours White Noise For Infants.mp3");
		gdfu.upload(null, localFile, Arrays.asList("0B64VHJrvrPWHc09wdHh0aXFOYjg"));
		System.out.println("Upload done...");
		
		Collection<File> afterFiles = gdfu.listFiles("0B64VHJrvrPWHc09wdHh0aXFOYjg", null, Arrays.asList("id", "name"));
		System.out.println("------------------------------------------------------------");
		System.out.println("After upload: files under a folder (id:0B64VHJrvrPWHc09wdHh0aXFOYjg):");
		for (File file : afterFiles) {
			System.out.println(file);
		}
		
	}

}
