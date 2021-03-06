package rest.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.commons.net.ftp.FTPClient;

public class FtpService {
	
	private FTPClient client;
	private Socket ds;
	
	public FtpService() {
		this.client=new FTPClient();
		try {
			this.client.connect("localhost", 3636);
			this.client.login("bilbon", "hello_world");
			this.client.enterLocalPassiveMode();
			
			System.out.println("----Start ok----");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String ls() {
		try {
			this.client.enterLocalPassiveMode();
			this.client.pasv();
			this.client.getReplyString();
			
			this.ds =new Socket(this.client.getRemoteAddress(), 3637);
			
			this.client.list();
			
			
			BufferedReader bob = new BufferedReader(new InputStreamReader(this.ds.getInputStream()));
			
			String ret ="";
			
			String tmp = bob.readLine();
			
			while(tmp != null){
				ret += tmp+",";
				tmp = bob.readLine();
			}
			client.completePendingCommand();
			return ret;
			//this.ds.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String pwd() {
		try {
			this.client.pwd();
			return this.client.getReplyString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void cdup() throws IOException{
			this.client.cdup();
		
	}
	
	public void cd(String directory) throws IOException{
		this.client.cwd(directory);
	}

	public InputStream get(String filename) throws IOException {
		try {
			this.client.enterLocalPassiveMode();
			this.client.pasv();
			this.client.getReplyString();
			
			this.ds =new Socket(this.client.getRemoteAddress(), 3637);

			return this.client.retrieveFileStream(filename);
//			this.client.retrieveFile(filename, ds.getOutputStream());
//			
//			return ds.getInputStream();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	public void post(String file, String filename){
		try {
			this.client.enterLocalPassiveMode();
			this.client.pasv();
			this.client.getReplyString();
			
			this.ds =new Socket(this.client.getRemoteAddress(), 3637);
			
			this.client.storeFile(filename, new ByteArrayInputStream(file.getBytes()));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
