package rest.resource;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import rest.service.FtpService;


@Path("/ftp")
public class FtpResource {
	private static final String BASE_URL = "/rest/api/ftp/";
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
	@Path("/cdup")
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
		String head=FTP_SERVER +"<a href="+BASE_URL+"cdup>cdup</a>"+"<p>"+"path:"+this.client.pwd().substring(3)+"</p>"+"<ul>";
		String corps="";
		
		String[] ls = this.client.ls().split(",");

		for(String n : ls){
			String[] tmp=n.split(" ");
			if(tmp.length==2){
				if(tmp[1].equals("-d")){
					corps= corps + "<li>"+"<a href="+BASE_URL+"cd/"+tmp[0]+">"+tmp[0]+"</a>"+"</li>";
				}
				else{
					corps= corps + "<li>"+"<a href="+BASE_URL+"get/"+tmp[0]+">"+tmp[0]+"</a>"+"</li>";
				}
			}
		}
		if(corps.equals("")){
			corps+="<li>Dossier vide</li>";
		}
		corps= corps + "</ul>";
		return head+corps;
	}

	@GET
	@Path("/cd/{dossier}")
	public String cd( @PathParam("dossier") String dossier ) {
		try {
			this.client.cd(dossier);
		} catch (IOException e) {
			return this.corps()+"<h4>error: dossier cd incorrecte</h4>";
		}
		return this.corps();		 
	}
}

