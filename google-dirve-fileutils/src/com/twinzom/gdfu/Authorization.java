package com.twinzom.gdfu;

import java.io.IOException;
import java.util.Set;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

public class Authorization {

	public static GoogleCredential getCredential(String secretFilePath,
			Set<String> scopes) throws IOException {

		GoogleCredential credential = GoogleCredential.fromStream(
				Authorization.class.getResourceAsStream(secretFilePath))
				.createScoped(scopes);

		return credential;
	}
	
	public static String getAccessToken (GoogleCredential credential) {
		String accessToken = credential.getAccessToken();
		Long expiresIn = credential.getExpiresInSeconds();
		
		// check if token will expire in a minute
		if (accessToken == null || expiresIn != null && expiresIn <= 60) {
			try {
				credential.refreshToken();
			} catch (IOException e) {
				// Oops...
				e.printStackTrace();
			}
		}
		
		return credential.getAccessToken();
	}

}
