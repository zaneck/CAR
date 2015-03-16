package rest.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.core.header.FormDataContentDisposition;

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
		String res = FTP_SERVER;

		res+="<ul><li><a href="+BASE_URL+">retour</a></li>";
		res+="<form action=\""+BASE_URL+"DOupload\" method=\"post\" enctype=\"multipart/form-data\">";
		res+="<p>";
		res+="Selectioner un fichier : <input type=\"file\" name=\"file\" size=\"45\" />";
		res+="</p>";
		res+="<input type=\"submit\" value=\"Upload\" />";
		res+="</form>";

		return res;
	}

	@POST
	@Path("/DOupload")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public String uploadFile(
			@FormParam("file") InputStream uploadedInputStream,
			@FormParam("file") FormDataContentDisposition fileDetail) {

		try {
			this.client.post(uploadedInputStream,fileDetail.getFileName());
		} catch (IOException e) {
			return "<a href=\"+BASE_URL+\">retour</a><h4>ERREUR</h4>";
		}

		return "<a href=\"+BASE_URL+\">retour</a><h4>transfere okR</h4>";
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
		String head=FTP_SERVER +"<a href="+BASE_URL+"cdup>cdup</a>"+"<p>"+"path:"+this.client.pwd().substring(3)+"</p>";
		head+="<a href="+BASE_URL+"upload>upload</a>"+"<ul>";
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
		@Produces(MediaType.APPLICATION_OCTET_STREAM)
		@Path("/get/{fichier}")
		public String get(@PathParam("fichier") String fichier){
			this.client.get(fichier);
			return this.corps();
		}
}

