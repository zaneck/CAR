package rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import rest.service.FtpService;

@Path("/ftp")
public class FtpResource {

	private static final String LS = "<a href=ls>ls</ls>";
	private static final String PWD = "<a href=pwd>pwd</ls>";
	private static final String FTP_SERVER = "<h1>FTP server</h1>";
	
	private FtpService client;

	public FtpResource() {
		this.client=new FtpService();
	}

	@GET
	@Produces("text/html")
	public String sayHello() {
		return FTP_SERVER +
				"<ul>"+
					"<li>"+LS+"</li>"+
					"<li>"+PWD+"</li>"+
				"</ul>";
	}

	@GET
	@Path("ls")
	public String ls() {
		return this.client.ls();	 
	}

	@GET
	@Path("pwd")
	public String pwd() {
		this.client.pwd();
		this.client.pwd();
		this.client.pwd();
		return this.client.pwd();
	}
}

