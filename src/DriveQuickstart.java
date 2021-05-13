import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.SocketTimeoutException;
import java.security.GeneralSecurityException;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mortbay.jetty.AbstractGenerator.Output;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Get;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpDownloader.DownloadState;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;



public class DriveQuickstart {
	private static final String APPLICATION_NAME = "CLINICA-PROJECT";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    // Directory to store user credentials for this application.
    private static final java.io.File CREDENTIALS_FOLDER //
            = new java.io.File(System.getProperty("user.home"), "credentials");
 
    private static final String CLIENT_SECRET_FILE_NAME = "secrets.json";
 
    //
    // Global instance of the scopes required by this quickstart. If modifying these
    // scopes, delete your previously saved credentials/ folder.
    //
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
 
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
 
        java.io.File clientSecretFilePath = new java.io.File(CREDENTIALS_FOLDER, CLIENT_SECRET_FILE_NAME);
 
        if (!clientSecretFilePath.exists()) {
            throw new FileNotFoundException("Please copy " + CLIENT_SECRET_FILE_NAME //
                    + " to folder: " + CREDENTIALS_FOLDER.getAbsolutePath());
        }
 
        // Load client secrets.
        InputStream in = new FileInputStream(clientSecretFilePath);
 
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
 
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, SCOPES).setDataStoreFactory(new FileDataStoreFactory(CREDENTIALS_FOLDER))
                        .setAccessType("offline").build();
 
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }
    
    public static Drive getDriveServico() throws GeneralSecurityException, IOException {
    	final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        // 3: Read client_secret.json file & create Credential object.
        Credential credential = getCredentials(HTTP_TRANSPORT);
        // 5: Create Google Drive Service.
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential) //
                .setApplicationName(APPLICATION_NAME).build();
        return service;
    }
    
    public static final List<File> getGoogleFilesByName(String fileNameLike) throws IOException, GeneralSecurityException {
    	 
        Drive driveService = DriveQuickstart.getDriveServico();
 
        String pageToken = null;
        List<File> list = new ArrayList<File>();
 
        String query = " name contains '" + fileNameLike + "' " //
                + " and mimeType != 'application/vnd.google-apps.folder' ";
 
        do {
            FileList result = driveService.files().list().setQ(query).setSpaces("drive") //
                    // Fields will be assigned values: id, name, createdTime, mimeType
                    .setFields("nextPageToken, files(id, name, createdTime, mimeType)")//
                    .setPageToken(pageToken).execute();
            for (File file : result.getFiles()) {
                list.add(file);
            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);
        //
        return list;
    }
 
 
    public static void main(String... args) throws IOException, GeneralSecurityException {
        Drive service =DriveQuickstart.getDriveServico();
        FileList result = service.files().list().setPageSize(20).setFields("nextPageToken, files(id, name)").execute();
        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
            	if(file.getId().equals( "1kA4TDnutb6GQqbe9hO2xLi4YaMtw5icQ")) {
            		System.out.printf("%s (%s)\n", file.getName(), file.getProperties());
            		//dowloadAtualizacao();
            	}
                
            }
            
        }

        
    }
    
    
    public static  void dowloadAtualizacao( ){
	    try {
	    	Drive service = DriveQuickstart.getDriveServico();
	        String fileId = "1kA4TDnutb6GQqbe9hO2xLi4YaMtw5icQ";
	        OutputStream outputStream = new ByteArrayOutputStream();
	        service.files().get(fileId).executeMediaAndDownloadTo(outputStream);

	    	ByteArrayOutputStream buffer = (ByteArrayOutputStream) outputStream;
		    InputStream inputStream = new ByteArrayInputStream(buffer.toByteArray());
	        FileOutputStream fileOut = new FileOutputStream("C:\\System Clinica\\GabClinic-2.0.exe");//destino uso padrao
	        BufferedInputStream in = new BufferedInputStream(inputStream);
	        BufferedOutputStream out = new BufferedOutputStream(fileOut);

	        byte[] buffer1 = new byte[10240];
	        int len = 0;

	        while((len = in.read(buffer1)) > 0) {
	            out.write(buffer1, 0 , len);
	            System.out.println(buffer1.toString());
	        }

	    in.close();
	    out.close();
	    System.out.println("Atualizadoo com sucesso!!");
	    } catch(FileNotFoundException e) {
	        e.printStackTrace();
	    } catch(IOException io){
	        io.printStackTrace();	
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    
}
