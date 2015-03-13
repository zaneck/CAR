package ftp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class FtpRequest implements Runnable{

	private Socket socket;
	private ServerSocket dss;
	private boolean log;
	private String directory;
	private String[] commandeCourante;
	private BufferedReader br;
	private DataOutputStream dataOut;
	private String user;
	private Socket dsocket;
	private String root;

	/* creation thread connection*/
	public FtpRequest(Socket socket, ServerSocket dss) throws IOException{
		this.socket=socket;
		this.dss=dss;
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
			this.processSTOR();//To Do
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
		else if(this.commandeCourante[0].compareTo("PORT")==0 && this.log){
			this.processPORT();
		}
		else{
			this.commandeInconnue();
		}
	}

	private void processPORT() throws UnknownHostException, IOException {
		String[] split = this.commandeCourante[1].split(",");

		// 	Récupération de l'adress IP
		String ip = split[0];
		for(int i = 1 ; i <= 3 ; i++){
			ip += "." + split[i];
		}

		// 	Lecture du port
		int port = Integer.parseInt(split[4]);
		port *= 256;
		port += Integer.parseInt(split[5]);

		// 	Ouverture de la connexion
		System.out.println(ip);
		System.out.println(port);
		this.dsocket = new Socket(Inet4Address.getByName(ip), port);

	}

	private void processPASV() throws IOException {
		System.out.println("PASV");
		if(this.log){
			this.sendMessage("227 passive mode (0,0,0,0,14,53)");
			this.dsocket=dss.accept();
			System.out.println("pasv accept");
		}
		else{
			this.sendMessage("530 not log");
		}
	}

	public void processSYST() throws IOException {
		if(this.log){
			System.out.println("215 UNIX");
			this.sendMessage("215 Unix");
		}
		else{
			this.sendMessage("530 not log");
		}
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
			this.root=this.directory+"/";
		}
		else if(this.commandeCourante[1].compareTo("bilbon")==0){
			this.user="bilbon";
			this.directory=this.directory.concat("bilbon");
			this.log=true;
			this.sendMessage("331 user ok need password");
			this.root=this.directory+"/";
		}
		else{
			this.sendMessage("332 who are you");
		}
	}

	public void processPASS() throws IOException{
		System.out.println("PASS");

		if(this.user=="bilbon" && this.commandeCourante[1].compareTo("hello_world")==0){
			this.sendMessage("230 pass ok");
			this.log=true;
		}
		else{
			this.sendMessage("332 pass fail");
		}
	}

	public void processRETR() throws IOException{
		System.out.println("RETR");
		if(!log){
			this.sendMessage("530 not log");
		}
		else{
			this.envoyeFichier();
		}
	}

	public void processSTOR() throws IOException{
		System.out.println("STOR");
		if(!log){
			this.sendMessage("530 not log");
		}
		else{
			this.recoitFichier();
		}
	}

	public void processLIST() throws IOException{
		System.out.println("LIST");
		if(!log){
			this.sendMessage("530 not log");
		}
		else{
			File dossier;
			dossier= new File(this.directory);
			String res="";

			for(File in : dossier.listFiles()){
				if(in.isDirectory()){
					res+=in.getName() +" -d \r\n";
				}
				else{
					res+=in.getName() +" -f \r\n";
				}
			}
			this.sendListe(res);
		}
	}

	public void processQUIT() throws IOException{
		System.out.println("QUIT");
		this.sendMessage("221 QUIT\r\n");
		this.br.close();
	}

	private void sendMessage(String string) throws IOException{
		System.out.println("envoie message "+string);
		this.dataOut.writeBytes(string+"\r\n");
		this.dataOut.flush();
	}

	private void sendListe(String string) throws IOException{
		System.out.println("envoie liste\n " +string);
		this.sendMessage("125 listing");

		DataOutputStream dos= new DataOutputStream(this.dsocket.getOutputStream());
		dos.writeBytes(string);
		dos.flush();

		this.sendMessage("226 end listing");
		dos.close();
	}

	private void envoyeFichier() throws IOException {
		System.out.println("envoie fichier "+this.commandeCourante[1]);
		File fichier = new File(this.directory+"/"+this.commandeCourante[1]);

		if(!fichier.exists()){
			this.sendMessage("404 file not found");
		}
		else{
			this.sendMessage("125 download");
			DataOutputStream dos= new DataOutputStream(this.dsocket.getOutputStream());

			FileInputStream fis = new FileInputStream(fichier);
			byte[] tmp = new byte[this.dsocket.getSendBufferSize()];
			int readb = fis.read(tmp);

			while(readb>0){
				System.out.println(readb);
				dos.write(tmp,0,readb);
				readb=fis.read(tmp);
			}
			fis.close();
			dos.flush();

			this.sendMessage("226 end download");
			dos.close();
		}
	}

	private void recoitFichier() throws IOException {
		System.out.println("reception fichier "+this.commandeCourante[1]);

		this.sendMessage("125 upload");

		InputStream is= this.dsocket.getInputStream();
		FileOutputStream fos = new FileOutputStream(this.directory+"/"+this.commandeCourante[1]);
		byte[] tmp = new byte[this.dsocket.getReceiveBufferSize()];
		int readb = is.read(tmp);

		while(readb != -1){
			fos.write(tmp, 0, readb);
			readb = is.read(tmp);
		}

		fos.flush();
		fos.close();

		this.sendMessage("226 end upload");
		is.close();
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

		String[] tmp;
		String res="/";

		if(!log){
			this.sendMessage("530 not log");
		}
		else{
			if(this.user.compareTo("anonymous")==0){
				this.sendMessage("200 "+this.directory);
			}
			else{
				if(this.commandeCourante[1].compareTo("..")==0){//remonter
					if(this.directory.equals(this.root) || this.directory.equals(this.root+"/")
							|| (this.directory+"/").equals(this.root)){
						this.sendMessage("200 "+this.directory);
					}
					else{
						System.out.println("-----------------"+this.root);
						System.out.println("-----------------"+this.directory);
						tmp=this.directory.split("/");
						for(int i=1; i<tmp.length-1; i++){
							res+=tmp[i]+"/";
							this.directory=res;
						}
					}
				}
				else{//descente
					File dossier = new File(this.directory+"/"+this.commandeCourante[1]);
					if(dossier.exists()){
						this.directory=dossier.getAbsolutePath();
					}
				}
				this.sendMessage("200 "+this.directory);
			}
		}
	}

	public void processCDUP() throws IOException{
		if(this.log){
			this.commandeCourante="CWD ..".split(" ");
			this.processCWD();
		}
		else{
			this.sendMessage("530 not log");
		}
	}

	@Override
	public void run() {
		this.processRequest();
	}
}
