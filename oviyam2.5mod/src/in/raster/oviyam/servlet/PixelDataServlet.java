package in.raster.oviyam.servlet;

import in.raster.oviyam.xml.handler.LanguageHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.dcm4che2.io.DicomInputStream;

public class PixelDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	/*
	 * Initialize the Logger.
	 */
	private static Logger log = Logger.getLogger(PixelDataServlet.class);

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String imageURL = "";
		// Sets the response content type to jpg image.

		// Reads the parameters form the request object which is sent by user.
		String study = request.getParameter("study");
		String series = request.getParameter("series");
		String object = request.getParameter("object");
		String frameNo = request.getParameter("frameNumber");
		String contentType = request.getParameter("contentType");
		String serverURL = request.getParameter("serverURL");
		int frameNumber = 0;

//		String protocol = request.getScheme();
//		String host = request.getServerName();
//		String port = String.valueOf(request.getServerPort());
		
//		imageURL = protocol + "://" + host + ":" + port
//				+ "/wado?requestType=WADO&studyUID=" + study + "&seriesUID="
//				+ series + "&objectUID=" + object; //+ "&transferSyntax=" + UID.ExplicitVRLittleEndian;  +"&windowCenter="+windowCenter+"&windowWidth="+windowWidth;
		
//		imageURL = serverURL + "?requestType=WADO&studyUID=" + study + "&seriesUID="
//				+ series + "&objectUID=" + object + "&transferSyntax=" + UID.ExplicitVRLittleEndian;		

		

		if (frameNo != null) {
			try {
				frameNumber = Integer.parseInt(frameNo);
			} catch (Exception e) {
				e.printStackTrace();
				frameNumber = 0;
			}
		}
		
		InputStream resultInStream = null;
		// Gets the response's outputStream instance to write a image in the
		// response.
//		boolean isgzip = false;
//		String encodings = request.getHeader("Accept-Encoding");
//		String[] browserflags = encodings.split(",");

//		for (int i = 0; i < browserflags.length; i++) {
//			String string = browserflags[i];
//			if (string.equalsIgnoreCase("gzip")) {
//				isgzip = true;
//				response.setHeader("Content-Encoding", "deflate");
//				response.setHeader("Vary", "Accept-Encoding");
//				break;
//			}
//		}
//		OutputStream resultOutStream = isgzip ? new DeflaterOutputStream(response.getOutputStream()) : response.getOutputStream();
		OutputStream resultOutStream = response.getOutputStream();

		try {			
			// Initialize the URL for the requested image.			
			DicomInputStream dis = null;						
			
			if(serverURL.contains("wado")) {
				imageURL = serverURL + "?requestType=WADO&studyUID=" + study + "&seriesUID="
						+ series + "&objectUID=" + object + "&transferSyntax=" + UID.ExplicitVRLittleEndian;
				if (contentType != null) {
					imageURL += "&contentType=" + contentType;
					response.setContentType(contentType);
				}
				resultInStream = new URL(imageURL).openStream();					
				dis = new DicomInputStream(resultInStream);				
			} else {
				String dest = LanguageHandler.source.getAbsolutePath();
				dest = dest.substring(0, dest.indexOf("oviyam2-1-config.xml") - 1);
				dis = new DicomInputStream(new File(dest + File.separator + "oviyam2" + File.separator + study + File.separator + object));
			}
			
			DicomObject d = dis.readDicomObject();
			

			Integer bitsStored = null;

			if ((bitsStored = d.getInt(Tag.BitsStored)) == null) {
				bitsStored = 12;
			}

			int offset = d.getInt(Tag.Rows) * d.getInt(Tag.Columns);			
			if (bitsStored > 8) {
				offset *= 2;
			}
			byte[] dest = new byte[offset];			
			byte[] src = d.getBytes(Tag.PixelData);			
			
			System.arraycopy(src, frameNumber * offset, dest, 0,dest.length);		
			response.setHeader("Content-Length", String.valueOf(dest.length));
			resultOutStream.write(dest);	

				
			
			dis.close();			

			resultOutStream.flush();			

		} catch (Exception e) {			
//			log.error("Unable to read and write the image from " + protocol
//					+ "://" + host + ":" + port, e);
			log.error("Unable to read and write the image from " + serverURL, e);

		} finally {
			if (resultInStream!=null) {
				resultInStream.close();
			}
			if(resultOutStream!=null) {
				resultOutStream.close();
			}
		}

	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
