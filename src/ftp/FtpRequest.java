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
import java.net.ServerSocket;
import java.net.Socket;

public class FtpRequest implements Runnable{

	private static final String ERREUR_WRITE_BW = "erreur write";
	
	private Socket socket;
	private ServerSocket ss;
	private boolean log;
	private String directory;
	private String[] commandeCourante;
	private BufferedReader br;
	private DataOutputStream dataOut;
	private String user;
	private Socket dsocket;
	private String root;

	public FtpRequest(Socket socket, ServerSocket ss) throws IOException{
		this.socket=socket;
		this.ss=ss;
		this.log=false;
		this.directory= "";
		this.commandeCourante=null;
		this.root=directory;
	
		this.br=new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.dataOut=new DataOutputStream(this.socket.getOutputStream());
		
		this.sendMessage("220 hello");
	}

	/*tant que a connection n'est pas close...*/
	public void processRequest(){
		try {
			while(!socket.isClosed()){
				String message = this.br.readLine();
				if(message!=null){
					try{
						this.commandeCourante= message.split(" ");
					}
					catch(Exception e){
						System.out.println("----erreur");
						this.commandeCourante=new String[1];
						this.commandeCourante[0]=message;
					}
					this.parseCommande();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void parseCommande() throws IOException {
		System.out.println("parse commande "+this.commandeCourante[0]);
				
		if(this.commandeCourante[0].compareTo("USER")==0){
			this.processUSER();
		}
		else if(this.commandeCourante[0].compareTo("PASS")==0){
			this.processPASS();
		}
		else if(this.commandeCourante[0].compareTo("LIST")==0 && this.log){
			this.processLIST();
		}
		else if(this.commandeCourante[0].compareTo("RETR")==0 && this.log){
			this.processRETR();
		}
		else if(this.commandeCourante[0].compareTo("STOR")==0 && this.log){
			this.processSTOR();
		}
		else if(this.commandeCourante[0].compareTo("QUIT")==0 && this.log){
			this.processQUIT();
		}
		else if(this.commandeCourante[0].compareTo("PWD")==0 && this.log){
			this.processPWD();
		}
		else if(this.commandeCourante[0].compareTo("CWD")==0 && this.log){
			this.processCWD();
		}
		else if(this.commandeCourante[0].compareTo("CDUP")==0 && this.log){
			this.processCDUP();
		}
		else if(this.commandeCourante[0].compareTo("SYST")==0 && this.log){
			this.processSYST();
		}
		else if(this.commandeCourante[0].compareTo("PASV")==0 && this.log){
			this.processPASV();
		}
		else{
			this.commandeInconnue();
		}
	}

	private void processNeedToLog() {
		System.out.println("no user log");
		
	}

	private void processPASV() throws IOException {
		System.out.println("PASV");
		if(this.log){
			this.sendMessage("227 passive mode (0,0,0,0,14,53)");
			this.dsocket=ss.accept();
		}
		else{
		this.sendMessage("530 not log");
		}
	}

	public void processSYST() throws IOException {
			System.out.println("215 UNIX");
			this.sendMessage("215 Unix");
	}

	public void commandeInconnue() throws IOException {
		System.out.println("comande inconnue "+this.commandeCourante[0]);
		this.sendMessage("502 commande inconnue");
	}

	public void processUSER() throws IOException{
		System.out.println("USER "+this.commandeCourante[1]);
		this.log=false;
		this.directory=System.getProperty("user.dir")+"/server/";
		if(this.commandeCourante[1].compareTo("anonymous")==0){
			this.user="anonymous";
			this.directory=this.directory.concat("anonymous");
			this.log=true;
			this.sendMessage("230 anonymous ok");
		}
		else if(this.commandeCourante[1].compareTo("bilbon")==0){
			this.user="bilbon";
			this.directory=this.directory.concat("bilbon");
			this.log=true;
			this.sendMessage("331 user ok need password");
		}
		else{
			this.sendMessage("332 who are you");
		}
		this.root=this.directory;
	}

	public void processPASS() throws IOException{
		System.out.println("PASS"+this.commandeCourante[0]);
		
		if(this.user=="bilbon" && this.commandeCourante[1].compareTo("hello_world")==0){
			this.sendMessage("230 pass ok");
			this.log=true;
		}
		else{
			this.sendMessage("332 pass fail");
		}
	}

	public void processRETR(){
		System.out.println("To DO!RETR");
	}

	public void processSTOR(){
		System.out.println("To DO!STOR");
	}

	public void processLIST(){
		
	}

	public void processQUIT() throws IOException{
		System.out.println("QUIT");
			this.sendMessage("221 QUIT\r\n");
			this.close();
	}

	private void close() {
		// TODO Auto-generated method stub
		
	}

	private void sendMessage(String string) throws IOException{
		System.out.println("envoie message "+string);
		this.dataOut.writeBytes(string+"\r\n");
		this.dataOut.flush();
	}
	
	private void sendListe(String string) throws IOException{
		System.out.println("envoie liste " +string);
		this.sendMessage("125 listing");
		
		DataOutputStream dos= new DataOutputStream(this.socket.getOutputStream());
		dos.writeBytes(string);
		dos.flush();
		
		this.sendMessage("226 end listing");
		this.socket.close();
	}

	public void processPWD() throws IOException{
		System.out.println("PWD");
		
		if(!log){
			this.sendMessage("530 not log");
		}
		else{
			this.sendMessage("227 "+this.directory);
		}
	}

	public void processCWD() throws IOException{
		System.out.println("CWD");
		if(!log){
			this.sendMessage("530 not log");
		}
		else{
			
		}
	}

	public void processCDUP() throws IOException{
		System.out.println("CDUP");
		if(!log){
			this.sendMessage("530 not log");
		}
		else{
			if(this.user.compareTo("anonymous")==0){
				this.sendMessage("200 "+this.directory);
			}
			else{
				if(this.directory.compareTo(this.root)==0){
					this.sendMessage("200 "+this.directory);
				}
				else{
					String[] tmp = this.directory.split("/");
					String res=""; 
					for(int i=0; i<tmp.length-2; i++){
						System.out.println(tmp[i]);
						res = tmp[i];
					}
					this.directory=res;
				this.sendMessage("200 "+this.directory);
				}
			}
		}
	}

	@Override
	public void run() {
		this.processRequest();
	}
}
