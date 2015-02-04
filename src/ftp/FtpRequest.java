package ftp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import sun.nio.ch.SocketAdaptor;

public class FtpRequest implements Runnable{

	private static final String ERREUR_WRITE_BW = "erreur write";
	
	private Socket socket;
	private String commandeCourante;
	private BufferedWriter bw;
	private BufferedReader br;
	private String user;
	private BufferedWriter bwData;

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
		String com = this.commandeCourante.substring(0, 4);

		if(com.compareTo("USER")==0){
			this.processUSER();
		}
		else if(com.compareTo("PASS")==0){
			this.processPASS();
		}
		else if(com.compareTo("LIST")==0){
			this.processLIST();
		}
		else if(com.compareTo("RETR")==0){
			this.processRETR();
		}
		else if(com.compareTo("STOR")==0){
			this.processSTOR();
		}
		else if(com.compareTo("QUIT")==0){
			this.processQUIT();
		}
		else if(com.compareTo("PWD")==0){
			this.processPWD();
		}
		else if(com.compareTo("CWD")==0){
			this.processCWD();
		}
		else if(com.compareTo("CDUP")==0){
			this.processCDUP();
		}
		else if(com.compareTo("SYST")==0){
			this.processSYST();
		}
		else if(com.compareTo("PORT")==0){
			this.processPORT();
		}
		else{
			this.commandeInconnue();
		}
	}

	private void processPORT() {
		System.out.println(this.commandeCourante);
		String[] co = this.commandeCourante.split(",");
		int port = Integer.parseInt(co[4])*256+ Integer.parseInt(co[5]);
		
		Socket sData;
		try {
			sData = new Socket(socket.getInetAddress(), port);
			this.bwData = new BufferedWriter(new OutputStreamWriter(sData.getOutputStream()));		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
		try {
			System.out.println("listing");
			bw.write("150 listing...\n");
			bw.flush();
			
			System.out.println("on liste");
			
			bwData.write("bob");
			bwData.flush();
			
			bw.write("226 listing done!");
			bw.flush();
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
