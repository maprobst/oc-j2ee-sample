package com.probst;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@Path("/forminput")
@Produces(MediaType.TEXT_HTML)
public class FormInput {

	@POST
	@Path("/form-param")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response getFormDataUsingFormParam(@FormParam("name") String name, @FormParam("phone") String phoneNumber) {

		return Response.ok(getHtmlResponse(name, phoneNumber)).build();
	}

	public String getHtmlResponse(String name, String phoneNumber) {
		StringBuilder responseStr = new StringBuilder("<html><head><title>Form Data</title></head><body>");
		responseStr.append("<h2>Submitted form data</h2><div><span>Name : " + name + "</span><br/><span>Phone : "
				+ phoneNumber + "</span></div></body>");
		return responseStr.toString();
	}

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(MultipartFormDataInput input) throws IOException {
		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

		// Get file data to save
		List<InputPart> inputParts = uploadForm.get("attachment");

		for (InputPart inputPart : inputParts) {
			try {

				MultivaluedMap<String, String> header = inputPart.getHeaders();
				String fileName = getFileName(header);

				// convert the uploaded file to inputstream
				InputStream inputStream = inputPart.getBody(InputStream.class, null);

				byte[] bytes = IOUtils.toByteArray(inputStream);

				String path = "/data" + File.separator + "uploads";
				File customDir = new File(path);

				if (!customDir.exists()) {
					customDir.mkdir();
				}
				File file = new File(customDir, fileName);
				writeFile(bytes, file);

				return Response.status(200).entity("Uploaded file name : " + fileName).build();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private String getFileName(MultivaluedMap<String, String> header) {

		String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

		for (String filename : contentDisposition) {
			if ((filename.trim().startsWith("filename"))) {

				String[] name = filename.split("=");

				String finalFileName = name[1].trim().replaceAll("\"", "");
				return finalFileName;
			}
		}
		return "unknown";
	}

	// Utility method
	private void writeFile(byte[] content, File file) throws IOException {
		System.out.println("----->" + file.getAbsolutePath());
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream fop = new FileOutputStream(file);
		fop.write(content);
		fop.flush();
		fop.close();
	}

}
