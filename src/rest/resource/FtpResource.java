package rest.resource;

import java.io.IOException;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import rest.service.FtpService;

@Path("/ftp")
public class FtpResource {
	private static final String BASE_URL = "/rest/api/ftp/";
	private static final String HEAD="<head><meta charset=\"utf-8\" /><title>FTP Server</title></head><body>";
	private static final String FOOT="</body>";
	private static final String FTP_SERVER = "<h1>FTP server</h1>";
	private FtpService client;

	public FtpResource() {
		this.client=new FtpService();
	}

	@GET
	@Produces("text/html")
	public String presentation() {
		return corps();
	}

	@GET
	@Produces("text/html")
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

	@GET
	@Produces("text/html")
	@Path("/upload")
	public String upload(){
		String res = HEAD+FTP_SERVER;

		res+="<ul><li><a href="+BASE_URL+">retour</a></li>";
		res+="<form action=\""+BASE_URL+"up\" method=\"post\" enctype=\"multipart/form-data\">";
		res+="<p>";
		res+="filename : <input type=\"text\" name=\"filename\" size=\"45\" />";
		res+="Selectioner un fichier : <input type=\"file\" name=\"file\" size=\"45\" />";
		res+="</p>";
		res+="<input type=\"submit\" value=\"Upload\" />";
		res+="</form>";

		return res+FOOT;
	}

	@POST
	@Path("/up")
	public String up( @FormParam("file") String fichier,
			@FormParam("filename") String filename) {
		this.client.post(fichier, filename);
		return this.corps();
	}

	@GET
	@Produces("text/html")
	@Path("/cd/{dossier}")
	public String cd( @PathParam("dossier") String dossier ) {
		try {
			this.client.cd(dossier);
		} catch (IOException e) {
			return this.corps()+"<h4>error: dossier incorrecte</h4>";
		}
		return this.corps();		 
	}

	private String corps() {
		String head=HEAD+FTP_SERVER;
		head+="<table>";
		head+="<tr><td><a href="+BASE_URL+"cdup>Dossier Parent</a></td>";
		head+="<td><a href="+BASE_URL+"upload>upload</a></td></tr></tr></table>";
		head+="<p>path:"+this.client.pwd().substring(3)+"</p>";
		head+="<ul>";
		String corps="";

		String[] ls = this.client.ls().split(",");

		for(String n : ls){
			String[] tmp=n.split(" ");
			if(tmp.length==2){
				if(tmp[1].equals("-d")){
					corps= corps + "<li>> "+"<a href="+BASE_URL+"cd/"+tmp[0]+">"+tmp[0]+"</a>"+"</li>";
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
		return head+corps+FOOT;
	}

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/get/{fichier}")
	public Response get(@PathParam("fichier") String fichier){
		try {
			return Response.ok(this.client.get(fichier),
					MediaType.APPLICATION_OCTET_STREAM).build();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//return this.corps();
		return null;
	}
}

