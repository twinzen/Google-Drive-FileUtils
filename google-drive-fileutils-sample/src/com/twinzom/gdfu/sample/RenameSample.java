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

public class RenameSample extends Sample {
	
	public static void main(String[] args) throws IOException, GeneralSecurityException {
		GoogleCredential credential = Authorization.getCredential(KEY_FILE, Collections.singleton("https://www.googleapis.com/auth/drive"));
		GDFileUtils gdfu = new GDFileUtils(credential);
		
		gdfu.rename("1TLGurS33El_7v2G5JXbawdLA7rdgtcOW", "EntypoXXXXXXXXXXXXX.ttf");
		
	}

}
