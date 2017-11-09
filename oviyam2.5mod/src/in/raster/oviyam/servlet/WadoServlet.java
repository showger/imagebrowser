/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of Oviyam, an web viewer for DICOM(TM) images
 * hosted at http://skshospital.net/pacs/webviewer/oviyam_0.6-src.zip
 *
 * The Initial Developer of the Original Code is
 * Raster Images
 * Portions created by the Initial Developer are Copyright (C) 2014
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * Babu Hussain A
 * Devishree V
 * Meer Asgar Hussain B
 * Prakash J
 * Suresh V
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package in.raster.oviyam.servlet;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Locale;
import java.util.zip.DeflaterOutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.image.OverlayUtils;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.tool.dcmqr.DcmQR;

import in.raster.oviyam.util.core.MoveScu;
import in.raster.oviyam.xml.handler.LanguageHandler;;


/**
 * 
 * @author asgar
 */
public class WadoServlet extends HttpServlet {

	/*
	 * Initialize the Logger.
	 */
	private static Logger log = Logger.getLogger(WadoServlet.class);

	/**
	 * Handles the HTTP <code>GET</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		boolean isgzip = false;
		/*String encodings = request.getHeader("Accept-Encoding");
		String[] browserflags = encodings.split(",");

		for (int i = 0; i < browserflags.length; i++) {
			String string = browserflags[i];
			if (string.equalsIgnoreCase("gzip")) {
				isgzip = true;
				response.setHeader("Content-Encoding", "deflate");
				response.setHeader("Vary", "Accept-Encoding");
			}
		}*/
		
		// Reads the parameters from the request object which is sent by user.
		String dcmUrl = request.getParameter("dicomURL");
		String study = request.getParameter("study");
		String series = request.getParameter("series");
		String object = request.getParameter("object");
		String retrieve = request.getParameter("retrieveType");
		String contentType = request.getParameter("contentType");
		if (contentType == null || contentType.length() == 0) {
			contentType = "image/jpeg";
		}
		
		String frameNo = request.getParameter("frameNumber");
		int frameNumber = -1;
		try {
			frameNumber = Integer.parseInt(frameNo);					
		} catch(Exception ex) {
			frameNumber = -1;
		}

		// String dest = ServerConfigLocator.locate().getServerHomeDir() +
		// File.separator + "data";
		String dest = LanguageHandler.source.getAbsolutePath();
		dest = dest.substring(0, dest.indexOf("oviyam2-1-config.xml") - 1);
		String inputDcmFilePath = dest + File.separator + "oviyam2" + File.separator + study + File.separator + object;
		File inputDicomFile = new File(inputDcmFilePath);	
		
		if(inputDicomFile.exists()) {
			if(contentType.equalsIgnoreCase("application/dicom")) {
				startStream(response, inputDicomFile, contentType,isgzip);
			} else {
				if(frameNumber<0) {
					File inputJpgFile = new File(inputDcmFilePath + ".jpg");
					if (!inputJpgFile.exists()) {
						readDicom(inputDicomFile, inputJpgFile);
					}		
					startStream(response, inputJpgFile, contentType,isgzip);
				} else {
					response.setContentType("image/jpeg");
					
					BufferedImage img = readFrame(inputDicomFile, frameNumber);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(img, "jpg", baos);
					byte[] imgInByte = baos.toByteArray();
					baos.close();
					
					startStream(response, imgInByte, contentType, isgzip);
				}
			}
		}
		else {			
		/*
		 * String cmoveParam[] = new String[]{srcUrl, "-L " + "ASGARMAC",
		 * "-cmove " + "ASGARMAC", "-I", "-qStudyInstanceUID=" + study,
		 * "-qSeriesInstanceUID=" + series, "-qSOPInstanceUID=" + object,
		 * "-cstore", "CT", "-cstoredest " + dest};
		 */

			synchronized (this) {
				try {
					if (retrieve.equalsIgnoreCase("C-MOVE")) {
						String[] aets = dcmUrl.substring(dcmUrl.indexOf("://") + 3,
								dcmUrl.indexOf("@")).split(":");
						String cMoveDest = aets[1];
						if (cMoveDest == null || cMoveDest.length() == 0) {
							cMoveDest = "OVIYAM2";
						}					
	
						String cmoveParam[] = new String[] { dcmUrl, "--dest",
								cMoveDest, "--suid", study, "--Suid", series,
								"--iuid", object};					
						MoveScu.main(cmoveParam);					
					} else if (retrieve.equalsIgnoreCase("C-GET")) {
						String sopClassUID = request.getParameter("sopClassUID");
						String[] aets = null;
						String srcUrl = null;
	
						if (dcmUrl.indexOf("://") > 0) {
							aets = dcmUrl.substring(dcmUrl.indexOf("://") + 3,
									dcmUrl.indexOf("@")).split(":");
							srcUrl = aets[0].trim()
									+ dcmUrl.substring(dcmUrl.indexOf("@"));
						} else {
							srcUrl = dcmUrl;
						}
						
						String cstoreDest = dest + File.separator + "oviyam2"
								+ File.separator + study;
						File cstoreFile = new File(cstoreDest);
						if (!cstoreFile.exists()) {
							cstoreFile.mkdirs();
						}
	
						/*
						 * String cgetParam[] = new String[]{srcUrl, "-L " +
						 * aets[1].trim(), "-cget", "-I", "-qStudyInstanceUID=" +
						 * study, "-qSeriesInstanceUID=" + series,
						 * "-qSOPInstanceUID=" + object, "-cstore", modality +
						 * ":LE", "-cstoredest", cstoreDest};
						 */
	
						String cgetParam[] = new String[] { srcUrl,
								"-L " + aets[1].trim(), "-cget", "-I",
								"-qStudyInstanceUID=" + study,
								"-qSeriesInstanceUID=" + series,
								"-qSOPInstanceUID=" + object, "-cstore",
								sopClassUID + ":1.2.840.10008.1.2.1",
								"-cstoredest", cstoreDest };
	
						DcmQR.main(cgetParam);
					}
	
				} catch (Exception ex) {
					log.error(
							"Error occurred while getting DICOM file using C-GET...",
							ex);
				}
	
				File outputJpegFile = new File(dest + File.separator + "oviyam2" + File.separator + study + File.separator + object + ".jpg");
				readDicom(inputDicomFile, outputJpegFile);
	            startStream(response, outputJpegFile, contentType,isgzip);	
			} // for synchronized
		}  // ELSE

	}

	public void convertToJPG(File output, BufferedImage image) {
		try {
			ImageWriter imageWriter = null;
			Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpg");
			if (iter.hasNext()) {
				imageWriter = iter.next();
			}
			ImageOutputStream ios = ImageIO.createImageOutputStream(output);
			imageWriter.setOutput(ios);
			ImageWriteParam iwParam = new JPEGImageWriteParam(Locale.getDefault());
			iwParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwParam.setCompressionQuality(1F);
			imageWriter.write(null, new IIOImage(convertToRGB(image), null,null), iwParam);
			ios.flush();
			imageWriter.dispose();
			ios.close();
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
	}

	private BufferedImage convertToRGB(BufferedImage srcImage) {
		BufferedImage newImage = new BufferedImage(srcImage.getWidth(null),
				srcImage.getHeight(null), BufferedImage.TYPE_INT_BGR);
		Graphics bg = newImage.getGraphics();
		bg.drawImage(srcImage, 0, 0, null);
		bg.dispose();
		return newImage;
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private void startStream(HttpServletResponse response, File file,String contentType,boolean isGzip) {
		FileInputStream fis = null;
		OutputStream os = null;

		response.setContentType(contentType);
		try {
			fis = new FileInputStream(file);
			os = response.getOutputStream();
			/*if (isGzip) {
				os = new DeflaterOutputStream(response.getOutputStream());
			} else {
				os = response.getOutputStream();
			}*/

			byte[] buffer = new byte[4096];
			int bytes_read;

			// write the file into the response's output stream.
			while ((bytes_read = fis.read(buffer)) != -1) {
				os.write(buffer, 0, bytes_read);
			}

			os.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Close the output stream.
			if (os != null) {
				try {
					os.close();
				} catch (Exception ignore) {

				}
			}

			// Close the file input stream
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception ignore) {
				}
			}
		}
	}
	
	private void startStream(HttpServletResponse response, byte[] bytes,String contentType,boolean isGzip) {
		OutputStream os = null;
		response.setContentType(contentType);
		try {	
			os = response.getOutputStream();
			/*if (isGzip) {
				os = new DeflaterOutputStream(response.getOutputStream());
			} else {
				os = response.getOutputStream();
			}*/		
			os.write(bytes);
			os.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Close the output stream.
			if (os != null) {
				try {
					os.close();
				} catch (Exception ignore) {

				}
			}			
		}
	}

	private BufferedImage overlayDicom(File file) throws FileNotFoundException,
			IOException {
		FileInputStream fis = new FileInputStream(file);
		DicomInputStream dis = new DicomInputStream(fis);
		DicomObject obj = dis.readDicomObject();
		Iterator<ImageReader> iter = ImageIO
				.getImageReadersByFormatName("DICOM");
		ImageReader reader = iter.next();

		String overlay = obj.getString(Tag.OverlayData);
		BufferedImage bufferImg = null;
		if (overlay != null && overlay.length() > 0) {
			bufferImg = OverlayUtils.extractOverlay(obj, Tag.OverlayData,
					reader, "FFFFFF");
		}

		return bufferImg;
	}

	private BufferedImage addImages(BufferedImage image1, BufferedImage image2) {
		Graphics2D g2d = image1.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.drawImage(image2, 0, 0, null);
		g2d.dispose();

		return image1;
	}
	
	 public void readDicom(File dicomFile, File outputJPGFile) throws IOException {
    	 ImageInputStream iis = ImageIO.createImageInputStream(dicomFile);
         
         Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("DICOM");
         ImageReader reader = (ImageReader) iter.next();
         reader.setInput(iis, false);
         BufferedImage bufferImage1 = reader.read(0);
         BufferedImage bufferImage2 = overlayDicom(dicomFile);
         
         if(bufferImage2!=null) { // has overlay image
         	BufferedImage finalBufferImage = addImages(bufferImage1,
						bufferImage2);
				convertToJPG(outputJPGFile, finalBufferImage);
         } else {
         	convertToJPG(outputJPGFile, bufferImage1);
         }  
         reader.dispose();         
    }
	 
	 public BufferedImage readFrame(File dicomFile,int frame_number) throws IOException {
    	 ImageInputStream iis = ImageIO.createImageInputStream(dicomFile);
         
         Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("DICOM");
         ImageReader reader = (ImageReader) iter.next();
         reader.setInput(iis, false);
         BufferedImage img = reader.read(frame_number-1);
         reader.dispose();
         return img;
         
    } 
}