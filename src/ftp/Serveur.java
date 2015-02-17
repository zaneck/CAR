package ftp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur {
	
	public static void main(String[] args) {
		 		 
		 /*creation serversocket*/
		 try {
			@SuppressWarnings("resource")
			final ServerSocket ss = new  ServerSocket(3636);
			final ServerSocket dss = new ServerSocket(3637);
		
		/*fin creation serversocket*/
		 
		Socket socket =null;
		
		/*accepter une connection passer la main a un thread*/
		while(true){
			socket = ss.accept();
			System.out.println("hello "+socket.getInetAddress());
			Thread tFtp =new Thread(new FtpRequest(socket, dss));
			tFtp.start();
		}
		
		 } catch (NumberFormatException e) {
			 System.err.println("Argument erreur");
			 e.printStackTrace();
		 } catch (IOException e) {
			 System.err.println("serveur erreur");
			 e.printStackTrace();
		 }
	}

}
