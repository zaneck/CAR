package rest.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;

import org.apache.commons.net.ftp.FTPClient;

public class FtpService {
	
	private FTPClient client;
	private Socket ds;
	
	public FtpService() {
		this.client=new FTPClient();
		try {
			this.client.connect("localhost", 3636);
			this.client.login("anonymous", "");
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
			this.ds =new Socket(this.client.getRemoteAddress(), 3637);
			
			this.client.list();
			
			this.ds.close();
			
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

}
