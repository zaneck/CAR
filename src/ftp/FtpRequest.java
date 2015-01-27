package ftp;

public class FtpRequest implements Runnable{

	private String commande;
	
	public FtpRequest(String commande){
		this.commande=commande;
	}
	
	/*tant que a connection n'est pas close...*/
	public void processRequest(){
		
	}
	
	public void processUSER(){
		
	}
	
	public void processPASS(){

	}

	public void processRETR(){

	}

	public void processSTOR(){

	}

	public void processLIST(){

	}

	public void processQUIT(){

	}

	public void processPWD(){

	}
	
	public void processCWD(String directory){
		
	}
	
	public void CDUP(){
		
	}

	@Override
	public void run() {
		this.processRequest();
	}
}
