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

package in.raster.oviyam.model;

import java.io.Serializable;

import org.dcm4che.data.Dataset;
import org.dcm4che.dict.Tags;

/**
 * 
 * @author asgar
 */
public class InstanceModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Variables
	private String sopIUID;
	private String instanceNumber;
	private String sopClassUID;
	private String numberOfFrames;	
	private String thumbSize;

	/**
	 * Constructor User to create a instance of InstanceModel. The properties of
	 * InstanceModel instance will be initialized.
	 * 
	 * @param dataSet
	 *            The Dataset instance contains the Instance informations.
	 */
	public InstanceModel(Dataset ds) {
		sopIUID = ds.getString(Tags.SOPInstanceUID);
		instanceNumber = ds.getString(Tags.InstanceNumber)!=null ? ds.getString(Tags.InstanceNumber) : "unknown";
		sopClassUID = ds.getString(Tags.SOPClassUID)!=null ? ds.getString(Tags.SOPClassUID) : "unknown";
		numberOfFrames = ds.getString(Tags.NumberOfFrames)!=null ? ds.getString(Tags.NumberOfFrames) : "0";
		
		int rows,columns;
		
		rows = ds.getInteger(Tags.Rows)!=null ? ds.getInteger(Tags.Rows) : 512;
		columns = ds.getInteger(Tags.Columns)!=null ? ds.getInteger(Tags.Columns) : 512;	
		
		if(columns>rows) {			
			thumbSize = "width: 70px;";
		} else {			
			thumbSize = "height: 70px;";
		}
	}

	/**
	 * Getter for property sopIUID.
	 * 
	 * @return Value of property sopIUID.
	 */
	public String getSopIUID() {
		return sopIUID;
	}

	/**
	 * Getter for property instanceNumber.
	 * 
	 * @return Value of property instanceNumber.
	 */
	public String getInstanceNumber() {
		return instanceNumber;
	}

	/**
	 * Getter for property sopClassUID.
	 * 
	 * @return Value of property sopClassUID.
	 */
	public String getSopClassUID() {
		return sopClassUID;
	}

	/**
	 * Getter for property numberOfFrames.
	 * 
	 * @return Value of property numberOfFrames.
	 */
	public String getNumberOfFrames() {
		return numberOfFrames;
	}
	
	/**
	 * Getter for property thumbSize.
	 * 
	 * @return Value of property thumbSize.
	 */
	public String getThumbSize() {
		return thumbSize;
	}
}