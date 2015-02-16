package ftp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class FtpRequest implements Runnable{

	private static final String ERREUR_WRITE_BW = "erreur write";
	
	private Socket socket;
	private String commandeCourante;
	private BufferedWriter bw;
	private BufferedReader br;
	private String user;
	private boolean log;
	private String directory;

	private int port;

	public FtpRequest(Socket socket){
		this.socket=socket;

		try {
			/*creation BufferedReader*/
			InputStream ip;

			ip = this.socket.getInputStream();

			InputStreamReader ipr = new InputStreamReader(ip);
			this.br = new BufferedReader(ipr);
			/*fin BufferedReader*/

			/*creation BufferedWriter*/
			OutputStream ou;
			ou=this.socket.getOutputStream(); 
			OutputStreamWriter ouw = new OutputStreamWriter(ou);
			this.bw = new BufferedWriter(ouw);
			/*fin BufferedWriter*/
			
			/*var diverse*/
			this.directory=System.getProperty("user.dir")+"/server";
			System.out.println("getenv " + this.directory);
			log=false;
			/*fin var diverse*/
			
			char[] ready = "----220 ftp server ready----\n\r".toCharArray();
			bw.write(ready);
			bw.flush();
		} catch (IOException e) {
			System.err.println("input/output erreur");
			e.printStackTrace();
			System.exit(0);
		}
	}

	/*tant que a connection n'est pas close...*/
	public void processRequest(){
		try {
			while(!socket.isClosed()){
				this.commandeCourante = this.br.readLine();
				this.parseCommande();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void parseCommande() {
		String com=" ";
		System.out.println("parse commande "+this.commandeCourante);
		if(this.commandeCourante.length()>3){
			com = this.commandeCourante.substring(0, 4);
		}
		else if(this.commandeCourante.length()==3){
			com = this.commandeCourante;
		}
		if(com.compareTo("USER")==0){
			this.processUSER();
		}
		else if(com.compareTo("PASS")==0){
			this.processPASS();
		}
		else if(com.compareTo("LIST")==0 && this.log){
			this.processLIST();
		}
		else if(com.compareTo("RETR")==0 && this.log){
			this.processRETR();
		}
		else if(com.compareTo("STOR")==0 && this.log){
			this.processSTOR();
		}
		else if(com.compareTo("QUIT")==0 && this.log){
			this.processQUIT();
		}
		else if(com.compareTo("PWD")==0 && this.log){
			this.processPWD();
		}
		else if(com.compareTo("CWD")==0 && this.log){
			this.processCWD();
		}
		else if(com.compareTo("CDUP")==0 && this.log){
			this.processCDUP();
		}
		else if(com.compareTo("SYST")==0 && this.log){
			this.processSYST();
		}
		else if(com.compareTo("PORT")==0 && this.log){
			this.processPORT();
		}
		else if(this.log){
			this.commandeInconnue();
		}
		else{
			this.processNeedToLog();
		}
	}

	private void processNeedToLog() {
		System.out.println("no user log");
		
		try {
			bw.write("530 no user log\r\n");
			bw.flush();
		} catch (IOException e) {
			System.err.println(ERREUR_WRITE_BW);
			e.printStackTrace();
		}
	}

	private void processPORT() {
		System.out.println(this.commandeCourante);
		String[] co = this.commandeCourante.split(",");
		this.port = Integer.parseInt(co[4])*256+ Integer.parseInt(co[5]);
		
		try {
			bw.write("200 PORT ok\r\n");
			bw.flush();
		} catch (IOException e) {
			System.err.println(ERREUR_WRITE_BW);
			e.printStackTrace();
		}
	}

	public void processSYST() {
		try {
			bw.write("215 UNIX\r\n");
			bw.flush();
		} catch (IOException e) {
			System.err.println(ERREUR_WRITE_BW);
			e.printStackTrace();
		}
		
	}

	public void commandeInconnue() {
		System.out.println("comande inconnue "+this.commandeCourante);
		try {
			bw.write("502 commande inconnue");
			bw.flush();
		} catch (IOException e) {
			System.err.println(ERREUR_WRITE_BW);
			e.printStackTrace();
		}
		
	}

	public void processUSER(){
		this.user=commandeCourante.substring(5);
		System.out.println(this.user);
		try {
			bw.write("331 User ok need passphrase\r\n");
			bw.flush();
		} catch (IOException e) {
			System.err.println(ERREUR_WRITE_BW);
			e.printStackTrace();
		}
	}

	public void processPASS(){
		System.out.println("PASS");
		
		if(this.user.compareTo("anonymous")==0){
			this.log=true;
			System.out.println("anonymous pass");
			try {
				bw.write("230 Pass ok\r\n");
				bw.flush();
			} catch (IOException e) {
				System.err.println(ERREUR_WRITE_BW);
				e.printStackTrace();
			}
		}
		else{
			if(this.commandeCourante.substring(5).compareTo("hello_world")==0){
				try {
					bw.write("230 Pass ok\r\n");
					bw.flush();
					System.out.println("login ok");
					this.log=true;
				} catch (IOException e) {
					System.err.println(ERREUR_WRITE_BW);
					e.printStackTrace();
				}
			}
			else{
				try {
					bw.write("530 Pass fail\r\n");
					bw.flush();
					System.out.println("login fail");
				} catch (IOException e) {
					System.err.println(ERREUR_WRITE_BW);
					e.printStackTrace();
				}
			}
		}
	}

	public void processRETR(){
		System.out.println("To DO!RETR");
	}

	public void processSTOR(){
		System.out.println("To DO!STOR");
	}

	public void processLIST(){
		File f = new File(this.directory);
		String listing ="";
		
		
		Socket sData;
		
		try {
			BufferedWriter bwData;
			sData = new Socket(socket.getInetAddress(), this.port);
			bwData = new BufferedWriter(new OutputStreamWriter(sData.getOutputStream()));		
		
			DataOutputStream dtpDataOutputStream = new DataOutputStream(this.socket.getOutputStream());

	        
			
			System.out.println("listing");
			bw.write("150 listing...\n");
			bw.flush();
			
			System.out.println("on liste");
			
			for(File entry : f.listFiles()){
				listing += entry.getName()+"\r\n";
			}
			
			dtpDataOutputStream.writeBytes(listing);
	        dtpDataOutputStream.flush();
			
	        this.socket.close();
	        
			/*bwData.write(listing);
			bwData.flush();
			bw.write("226 listing done");
			bw.flush();
			bwData.close();*/
			
			System.out.println("fin listing");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void processQUIT(){
		System.out.println("QUIT");
		try {
			bw.write("426 QUIT\r\n");
			bw.flush();
			bw.close();
			br.close();
		} catch (IOException e) {
			System.err.println(ERREUR_WRITE_BW);
			e.printStackTrace();
		}
	}

	public void processPWD(){
		System.out.println("To DO!PWD");
	}

	public void processCWD(){
		System.out.println("To DO!CWF");
	}

	public void processCDUP(){
		System.out.println("To DO!CDUP");
	}

	@Override
	public void run() {
		this.processRequest();
	}
}
