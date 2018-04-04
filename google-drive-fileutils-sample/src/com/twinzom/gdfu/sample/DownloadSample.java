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

public class DownloadSample extends Sample {
	
	public static void main(String[] args) throws IOException, GeneralSecurityException {
		GoogleCredential credential = Authorization.getCredential(KEY_FILE, Collections.singleton("https://www.googleapis.com/auth/drive"));
		GDFileUtils gdfu = new GDFileUtils(credential);
		
		System.out.println("------------------------------------------------------------");
		System.out.println("Download now...");
		java.io.File localFile = new java.io.File("/Users/twinsen/Downloads/local.png");
		gdfu.download("1tGxfv46nbimltwlgdcnmk_NOg_E9lcM0", localFile);
		System.out.println("Download done...");
		
	}

}
