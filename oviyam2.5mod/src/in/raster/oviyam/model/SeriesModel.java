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
public class SeriesModel implements Serializable {

    // variables
    private String seriesIUID;
    private String seriesNumber;
    private String seriesDate;
    private String seriesTime;
    private String seriesDescription;
    private String modality;
    private String numberOfInstances;
    private String bodyPartExamined;



    //Constructor
    /**
     * Used to create a instance of SeriesModel.
     * @param dataSet The Dataset instance contains the Series Information.
     */
    public SeriesModel(Dataset ds) {
        seriesIUID = ds.getString(Tags.SeriesInstanceUID);
        seriesNumber = ds.getString(Tags.SeriesNumber)!=null ? ds.getString(Tags.SeriesNumber) : "unknown";
        seriesDate = ds.getString(Tags.SeriesDate)!=null ? ds.getString(Tags.SeriesDate) : "";
        seriesTime = ds.getString(Tags.SeriesTime)!=null ? ds.getString(Tags.SeriesTime) : "";
        seriesDescription = ds.getString(Tags.SeriesDescription)!=null? ds.getString(Tags.SeriesDescription).replace("^", " ") : "";
        modality = ds.getString(Tags.Modality)!=null ? ds.getString(Tags.Modality) : "unknown";
        numberOfInstances = ds.getString(Tags.NumberOfSeriesRelatedInstances)!=null ? ds.getString(Tags.NumberOfSeriesRelatedInstances) : "";
        bodyPartExamined = ds.getString(Tags.BodyPartExamined)!=null ? ds.getString(Tags.BodyPartExamined) : "";
    }

    /**
     * Getter for property seriesIUID
     * @return Value of property seriesIUID
     */
    public String getSeriesIUID() {
        return seriesIUID;
    }

    /**
     * Getter for property seriesNumber
     * @return Value of property seriesNumber
     */
    public String getSeriesNumber() {
        return seriesNumber;
    }

    /**
     * Getter for property seriesDate
     * @return Value of property seriesDate
     */
    public String getSeriesDate() {
        return seriesDate;
    }

    /**
     * Getter for property seriesTime
     * @return Value of property seriesTime
     */
    public String getSeriesTime() {
        return seriesTime;
    }

    /**
     * Getter for property seriesDescription
     * @return Value of property seriesDescription
     */
    public String getSeriesDescription() {
        return seriesDescription;
    }

    /**
     * Getter for property modality
     * @return Value of property modality
     */
    public String getModality() {
        return modality;
    }

    /**
     * Getter for property numberOfInstances
     * @return Value of property numberOfInstances
     */
    public String getNumberOfInstances() {
        return numberOfInstances;
    }

    /**
     * Getter for property bodyPartExamined
     * @return Value of property bodyPartExamined
     */
    public String getBodyPartExamined() {
        return bodyPartExamined;
    }

}