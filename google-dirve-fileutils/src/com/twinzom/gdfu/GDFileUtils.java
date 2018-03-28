package com.twinzom.gdfu;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.List;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.TeamDrive;
import com.google.api.services.drive.model.TeamDriveList;

/**
 * @author twinzom
 * 
 * <p>
 * Google Drive File Utils (GDFU) - Google Drive API client library.
 * </p>
 * <p>
 * This library is inspired by Apache common FileUtils.
 * Aim to file manipulation simplifier and local-file system like
 * </p>
 * <p>
 * This client covered the following areas:
 * </p>
 * <ul>
 * <li>uploading file
 * <li>downloading file 
 * <li>creating folder
 * <li>listing files and folders
 * <li>copying files and folders
 * <li>cloning files and folders
 * <li>moving files and folders
 * <li>deleting files and folders
 * <li>converting file id to relative paths
 * <li>converting relative paths to file id
 * <li>renaming file
 * <li>listing Team Drives 
 * </ul>
 * <p>
 * @version v0.1
 */
public class GDFileUtils {

	/**
     * The Google Drive upload URL (v3)
     */
    private static final String GOOGLE_DRIVE_UPLOAD_URL = "https://www.googleapis.com/upload/drive/v3/files";
	
	/** 
	 * Default upload chunk size is 10 mb 
	 */
	private static final int  DEFAULT_CHUNK_SIZE = 10 * 1024 * 1024;
	
	/** 
	 * Default upload chunk timeout is 30 seconds 
	 */
	private static final int  DEFAULT_CHUNK_TIMEOUT = 1000 * 30;
	
    /**
     * The UTF-8 character set, used to decode octets in URLs.
     */
    private static final Charset UTF8 = Charset.forName("UTF-8");
    
    /**
     * The mime-type of folder
     */
    private static final String MIME_TYPE_FOLDER = "application/vnd.google-apps.folder";
    
    /**
     * HTTP status code Permanent Redirect
     */
    private static final int HTTP_PERM_REDIR = 308;
    
    //-----------------------------------------------------------------------
	/** 
	 * The chunk size for each chunk of resumable upload process 
	 */
	private int chunkSize = DEFAULT_CHUNK_SIZE;

	/** 
	 * The buffer byte array for resumable upload process 
	 */
	private byte[] chunkBuffer = new byte[(int) chunkSize];
	
	/**
	 * The chunk timeout value for each chunk of resumable upload process
	 */
	private int chunkTimeout = DEFAULT_CHUNK_TIMEOUT;
	
	/** 
	 * The service definition of Google Drive 
	 */
	private Drive drive;
	
	/** 
	 * The Google Credential provided by consumer 
	 */
	private GoogleCredential credential;
	
	/**
	 *  The Team Drive that is pointed to
	 */
	private TeamDrive teamDrive;

    
    //-----------------------------------------------------------------------
    /**
     * To init this utils class, you have to provide GoogleCredential for authorization
     * 
     * <p>
     * The credential is needed for build the Google Drive service and for resumable upload process
     * </p>
     * 
     * @param credential
     * @throws GeneralSecurityException
     * @throws IOException
     */
	public GDFileUtils(GoogleCredential credential)
			throws GeneralSecurityException, IOException {

		this.credential = credential;

		this.drive = new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(),
				JacksonFactory.getDefaultInstance(), credential).build();

	}
	
	/**
	 * Get the chunk size was set 
	 * 
	 * @return
	 */
	public int getChunkSize() {
		return chunkSize;
	}

	/**
	 * Set the chunk size (in bytes) 
	 * 
	 * @param chunkSize
	 */
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	/**
	 * Get timeout value of each upload chunk was set (in milliseconds) 
	 * 
	 * @return
	 */
	public int getChunkTimeout() {
		return chunkTimeout;
	}

	/**
	 * Set timeout value of each upload chunk (in milliseconds)
	 * 
	 * @param chunkTimeout
	 */
	public void setChunkTimeout(int chunkTimeout) {
		this.chunkTimeout = chunkTimeout;
	}
	
	
	/**
	 * Get GoogleCredential object was set
	 * 
	 * @return
	 */
	public GoogleCredential getCredential() {
		return credential;
	}

	/**
	 * Set credential
	 * 
	 * @param credential
	 */
	public void setCredential(GoogleCredential credential) {
		this.credential = credential;
	}
    
	/**
	 * Uploads file to Google Drive in resumable mode
	 * 
     * <p>
     * This utils class doesn't support Simple upload and Multipart upload, but Resumable upload.
     * </p>
     * 
     * <p>
     * What Google Drive API guide said, "For more reliable transfer, especially important with 
     * large files. Resumable uploads are a good choice for most applications, since they also work 
     * for small files at the cost of one additional HTTP request per upload." 
     * </p>
	 * 
	 * @param metadata
	 * @param localFile
	 * @param parents
	 * @throws IOException
	 */
    public void upload (File metadata, java.io.File localFile,
			java.util.List<String> parentIds) throws IOException {
    	
    	String uploadUrlStr = GOOGLE_DRIVE_UPLOAD_URL+"?uploadType=resumable&supportsTeamDrives=true";

		URL url = new URL(uploadUrlStr);
		HttpURLConnection request = (HttpURLConnection) url.openConnection();
		request.setRequestMethod("POST");
		request.setDoInput(true);
		request.setDoOutput(true);
		request.setRequestProperty("Authorization",
				"Bearer " + Authorization.getAccessToken(credential));
		request.setRequestProperty("Content-Type",
				"application/json; charset="+UTF8);
		
		// TODO: Remove handcode and handle metadata
		String body = "{\"name\": \"" + localFile.getName()
				+ "\", \"parents\": [" + Util.listToString(parentIds, ",", "\"")
				+ "]}";
		OutputStream outputStream = request.getOutputStream();
		outputStream.write(body.getBytes());
		outputStream.close();
		request.connect();

		if (request.getResponseCode() != HttpURLConnection.HTTP_OK) {
			// TODO: Exception handling
			// Oops... got some problem
			// Throw exception
			System.out.println("Cannot get URL");
		}

		long startChunk = 0;
		String sessionUri = request.getHeaderField("location");
		int responseCode = 308;
		FileInputStream fileInputStream = new FileInputStream(localFile);
		while (startChunk <= localFile.length() && responseCode==HTTP_PERM_REDIR) {
			responseCode = uploadChunk(sessionUri, fileInputStream, localFile.length(), startChunk);
			startChunk += chunkSize;
		}
		fileInputStream.close();
    }
    
    /**
     * This method is helping process each chunk for resumable update
     * 
     * @param sessionUri
     * @param fileInputStream
     * @param localFileSize
     * @param chunkStart
     * @return
     * @throws IOException
     */
    private int uploadChunk(String sessionUri, FileInputStream fileInputStream, long localFileSize,
			long chunkStart) throws IOException {

		URL uploadUrl = new URL(sessionUri);
		HttpURLConnection uploadRequest = (HttpURLConnection) uploadUrl
				.openConnection();
		uploadRequest.setRequestMethod("PUT");
		uploadRequest.setDoOutput(true);
		uploadRequest.setConnectTimeout(chunkTimeout);

		long uploadedBytes = chunkSize;
		byte[] buffer = chunkBuffer;

		if (chunkStart + uploadedBytes > localFileSize) {
			uploadedBytes = localFileSize - chunkStart;
			buffer = new byte[(int) uploadedBytes];
		}

		uploadRequest.setRequestProperty("Content-Length", "" + localFileSize);
		uploadRequest.setRequestProperty("Content-Range", "bytes " + chunkStart
				+ "-" + (chunkStart + uploadedBytes - 1) + "/" + localFileSize);

		
		OutputStream uploadOutputStream = uploadRequest.getOutputStream();

		fileInputStream.read(buffer);
		uploadOutputStream.write(buffer);
		uploadOutputStream.flush();
		
		uploadOutputStream.close();
		return uploadRequest.getResponseCode();
	}
    
    /**
     * Upload local folder itself, its files and sub-folders
     * 
     * <p>
     * If existing folder was found on Google Drive (checking by name),
     * it would upload files to existing folder but not creating a new one. 
     * </p>
     * 
     * @param localFolder
     * @param destFolderId
     * @throws IOException
     */
    public void uploadFolder (java.io.File localFolder, String destFolderId) throws IOException {
    	Collection<java.io.File> localFiles = Arrays.asList(localFolder.listFiles());
		for (java.io.File localFile : localFiles) {
			if (localFile.isFile()) {
				File metadata = new File();
		        metadata.setParents(Collections.singletonList(destFolderId));
				upload(metadata, localFile, Collections.singletonList(destFolderId));
			} else {
				java.util.List<File> files = getFilesInFolderByName(localFile.getName(), destFolderId);
				
				File remoteFolder = null;
				for (File file : files) {
					if (file.getMimeType().equals(MIME_TYPE_FOLDER)) {
						remoteFolder = file;
						break;
					}
				}
				
				if (remoteFolder == null) {
					remoteFolder = mkFolder(localFile.getName(), destFolderId);
				}
				
				uploadFolder(localFile, destFolderId);
			}
		}
    }
    
    
    
    /**
     * Download file from Google Drive
     * 
     * <p>
     * With given file object.
     * </p>
     * 
     * @param file
     * @param localFile
     * @throws IOException
     */
    public void download (File file, java.io.File localFile) throws IOException {
		
    	this.download(file.getId(), localFile);
    	
    }
    
    /**
     * Download file from Google Drive
     * 
     * <p>
     * With given file id.
     * </p>
     * 
     * @param file
     * @param localFile
     * @throws IOException
     */
    public void download (String fileId, java.io.File localFile) throws IOException {
		
    	if (!localFile.exists()) {
			localFile.createNewFile();
		}
    	
		FileOutputStream fos = new FileOutputStream(localFile);
		drive.files().get(fileId).executeMediaAndDownloadTo(fos);
		fos.flush();
    	
    }
    
	/**
	 * Makes a folder
	 * 
	 * <p>
	 * With given parent folder (File object).
	 * </p>
	 * 
	 * @param parent
	 * @param folderName
	 * @return
	 * @throws IOException
	 */
	public File mkFolder (File parent, String folderName) throws IOException {
		
		return this.mkFolder(parent.getId(), folderName);
		
	}
	
	/**
	 * Makes a folder
	 * 
	 * <p>
	 * With given parent folder (File id).
	 * </p>
	 * 
	 * @param parent
	 * @param folderName
	 * @return
	 * @throws IOException
	 */
	public File mkFolder (String folderName, String folderId) throws IOException {
		
		File fileMetadata = new File();
		fileMetadata.setName(folderName);
		fileMetadata.setParents(Arrays.asList(folderId));
		fileMetadata.setMimeType("application/vnd.google-apps.folder");
		

		File file = drive.files()
						.create(fileMetadata)
						.setFields("id")
						.setSupportsTeamDrives(true)
						.execute();
		
		return file;
	}
    

	/**
	 * Finds files within a given folder (and optionally its sub-folders)
	 * 
	 * <p>
	 * With given folder (File object).
	 * </p>
	 * 
	 * <p>
	 * If teamDrive was set, this method will return teamDrive's files.
	 * </p>
	 * 
	 * @param folder
	 * @param q
	 * @return
	 * @throws IOException
	 */
	public Collection<File> listFiles(File folder, String q) throws IOException {

		return this.listFiles(folder.getId(), q);
		
	}
	
	/**
	 * Finds files within a given folder (and optionally its sub-folders)
	 * 
	 * <p>
	 * With given folder (File id).
	 * </p>
	 * 
	 * <p>
	 * If teamDrive was set, this method will return teamDrive's files.
	 * </p>
	 * 
	 * @param folder
	 * @param q
	 * @return
	 * @throws IOException 
	 */
	public Collection<File> listFiles(String folderId, String q) throws IOException {
		
		List preparedQuery = drive.files().list();
		
		if (!folderId.isEmpty()) {
			preparedQuery.setQ("'"+folderId+"' in parents ");
		}
		
		if (q != null) {
			preparedQuery.setQ(preparedQuery.getQ() + " " + q);
		}
		
		if (teamDrive != null) {
			preparedQuery.setIncludeTeamDriveItems(true)
						 .setTeamDriveId(teamDrive.getId())
						 .setSupportsTeamDrives(true)
						 .setCorpora("teamDrive");
		}
		
		java.util.List<File> files = preparedQuery.execute().getFiles();
		
		return files;
	}
    
    /**
     * Get files in give folder that matched given file name.
     * 
     * @param fileName
     * @param folderId
     * @return
     * @throws IOException
     */
    public java.util.List<File> getFilesInFolderByName (String fileName, String folderId) throws IOException {
    	
    	java.util.List<File> files = null;
    	FileList result = null;
    	
    	if (teamDrive != null) {
    		result = drive.files().list()
    				.setQ("'"+folderId+"' in parents and trashed = false")
    				.setIncludeTeamDriveItems(true)
    				.setTeamDriveId(teamDrive.getId())
    				.setSupportsTeamDrives(true)
    				.setCorpora("teamDrive")
    				.execute();
		} else {
			result = drive.files().list()
					.setQ("'"+folderId+"' in parents and trashed = false")
					.execute();
		}

		files = result.getFiles();
		
		java.util.List<File> matchedNameFiles = new ArrayList<File>();
		for (File file: files) {
			if (file.getName().equals(fileName)) {
				matchedNameFiles.add(file);
			}
		}
		
		return matchedNameFiles;
    }

    /**
     * Rename a file or folder to new name
     * 
     * <p>
	 * With given file object.
	 * </p>
     * 
     * @param file
     * @param newName
     * @throws IOException
     */
    public void rename (File file, String newName) throws IOException {
		
    	this.rename(file.getId(), newName);
    	
	}
    
    /**
     * Rename a file or folder to new name
     * 
     * <p>
	 * With given file id.
	 * </p>
     * 
     * @param file
     * @param newName
     * @throws IOException
     */
    public void rename (String fileId, String newName) throws IOException {
		File content = new File();
		content.setName(newName);
		drive.files().update(fileId, content).setSupportsTeamDrives(true).execute();
	}
    

    /**
     * Get all Team Drives that can be accessed
     * 
     * @return
     * @throws IOException
     */
    public java.util.List<TeamDrive> listTeamDrives () throws IOException {
    	TeamDriveList result = drive.teamdrives().list().execute();
		return result.getTeamDrives();
    }
    

	/**
	 * Get file object by given file id
	 * 
	 * @param fileId
	 * @return
	 * @throws IOException
	 */
	public File getFileById (String fileId) throws IOException {
		File file = drive.files().get(fileId).setSupportsTeamDrives(true).execute();
		return file;
	}

	/* TODO: TO BE DONE.
	public static void copyFileToFolder(File file, File folder) {}
    
    public static void copyFolderToFolder(File srcFolder, File destFolder) {}
    
    public static void copyFolderContent(File srcFolder, File destFolder) {}
    
    public static void cloneFileToFolder(File file, File folder) {}
    
    public static void cloneFolderToFolder(File srcFolder, File destFolder) {}
    
    public static void cloneFolderContent(File srcFolder, File destFolder) {}
    
    public static void moveFileToFolder(File file, File folder) {}
    
    public static void moveFolderToFolder(File srcFolder, File destFolder) {}
    
    public static void moveFolderContent(File srcFolder, File destFolder) {}
    
    public static void deleteFile(File file) {}
    
    public static void deleteFolder(File folder) {}
    
    public static void cleanFolder(File folder) {}
    
    public static List<File> getFileByPath (String path) {return null;}
    
    public static List<String> getPaths (File file) {return null;}
    
    public static List<String> getPaths (String fileId) {return null;}
    */
}
