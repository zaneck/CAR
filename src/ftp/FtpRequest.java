package ftp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class FtpRequest implements Runnable{

	private Socket socket;
	private String commandeCourante;
	private BufferedWriter bw;
	private BufferedReader br;

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
			char[] ready = "220 ftp server ready\n\r".toCharArray();
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
		System.out.println(com);

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
		else{
			this.commandeInconnue();
		}
	}

	private void commandeInconnue() {
		// TODO Auto-generated method stub

	}

	public void processUSER(){
		System.out.println(this.commandeCourante);
		System.out.println("To DO!");
	}

	public void processPASS(){
		System.out.println("To DO!");
	}

	public void processRETR(){
		System.out.println("To DO!");
	}

	public void processSTOR(){
		System.out.println("To DO!");
	}

	public void processLIST(){
		System.out.println("To DO!");
	}

	public void processQUIT(){
		System.out.println("To DO!");
	}

	public void processPWD(){
		System.out.println("To DO!");
	}

	public void processCWD(){
		System.out.println("To DO!");
	}

	public void processCDUP(){
		System.out.println("To DO!");
	}

	@Override
	public void run() {
		this.processRequest();
	}
}
