package rest.resource;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import rest.service.FtpService;

@Path("/ftp")
public class FtpResource {
	private static final String FTP_SERVER = "<h1>FTP server</h1>";
	private FtpService client;

	public FtpResource() {
		this.client=new FtpService();
	}

	@GET
	public String presentation() {
		return corps();
	}

	@GET
	@Path("cdup")
	public String cdup(){
		try {
			this.client.cdup();

			String res = corps();

			return res;

		} catch (IOException e) {
			String res = corps();
			return res;
		}
	}

	private String corps() {
		String res=FTP_SERVER +"<a href=cdup>cdup<a>"+"<p>"+"path:"+this.client.pwd().substring(3)+"</p>"+"<ul>";
		String[] ls = this.client.ls().split(",");

		for(String n : ls){
			String[] tmp=n.split(" ");
			res= res + "<li>"+tmp[0]+ "</li>";
		}
		res= res + "</ul>";
		return res;
	}
}

