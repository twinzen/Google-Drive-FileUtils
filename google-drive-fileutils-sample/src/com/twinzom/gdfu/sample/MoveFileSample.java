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

public class MoveFileSample extends Sample {
	
	public static void main(String[] args) throws IOException, GeneralSecurityException {
		GoogleCredential credential = Authorization.getCredential(KEY_FILE, Collections.singleton("https://www.googleapis.com/auth/drive"));
		GDFileUtils gdfu = new GDFileUtils(credential);
		
		Collection<File> allFiles = gdfu.listFiles("", null, null);
		System.out.println("------------------------------------------------------------");
		System.out.println("All files:");
		for (File file : allFiles) {
			System.out.println(file);
		}
		
		Collection<File> files = gdfu.listFiles("0B64VHJrvrPWHWThNRXUtd244TXM", null, null);
		System.out.println("------------------------------------------------------------");
		System.out.println("Files under a folder (id:0B64VHJrvrPWHWThNRXUtd244TXM):");
		for (File file : files) {
			System.out.println(file);
		}
		
		gdfu.moveFileToFolder("1zUIC3EZqdVRir_DMiH0pxm31UKEljVlC", "0B64VHJrvrPWHWThNRXUtd244TXM");
	}

}
