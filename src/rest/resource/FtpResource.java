package rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import rest.service.FtpService;

@Path("/ftp")
public class FtpResource {

	private FtpService client;

	public FtpResource() {
		this.client=new FtpService();
	}

	@GET
	@Produces("text/html")
	public String sayHello() {
		return "<h1>Hello World</h1>";
	}

	@GET
	@Path("ls")
	public String ls() {
		return this.client.ls();		 
	}

	@GET
	@Path("pwd")
	public String pwd() {
		return this.client.pwd();
	}
}

