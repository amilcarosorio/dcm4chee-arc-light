/*
 * **** BEGIN LICENSE BLOCK *****
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
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at https://github.com/gunterze/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * J4Care.
 * Portions created by the Initial Developer are Copyright (C) 2013
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See @authors listed below
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
 * **** END LICENSE BLOCK *****
 */

package org.dcm4chee.arc.conf;

import org.dcm4che3.net.DeviceExtension;
import org.dcm4che3.soundex.FuzzyStr;
import org.dcm4che3.util.StringUtils;

import java.net.URI;
import java.util.*;

/**
 * @author Gunter Zeilinger <gunterze@gmail.com>
 * @since Jul 2015
 */
public class ArchiveDeviceExtension extends DeviceExtension {

    private String fuzzyAlgorithmClass;

    private String storageID;
    private String bulkDataSpoolDirectory;
    private String queryRetrieveViewID;
    private boolean queryMatchUnknown = true;
    private boolean sendPendingCGet;
    private Duration sendPendingCMoveInterval;
    private boolean personNameComponentOrderInsensitiveMatching;
    private String wadoSR2HtmlTemplateURI;
    private String wadoSR2TextTemplateURI;
    private String patientUpdateTemplateURI;
    private String unzipVendorDataToURI;
    private String[] mppsForwardDestinations = {};
    private final HashSet<String> wadoSupportedSRClasses = new HashSet<>();

    private final EnumMap<Entity,AttributeFilter> attributeFilters = new EnumMap<>(Entity.class);

    private QueryRetrieveView[] queryRetrieveViews = {};

    private final Map<String, StorageDescriptor> storageDescriptorMap = new HashMap<>();
    private final Map<String, QueueDescriptor> queueDescriptorMap = new HashMap<>();
    private final Map<String, ExporterDescriptor> exporterDescriptorMap = new HashMap<>();
    private final ArrayList<ExportRule> exportRules = new ArrayList<>();
    private Duration exportTaskPollingInterval;
    private int exportTaskFetchSize = 5;

    private final ArrayList<ArchiveCompressionRule> compressionRules = new ArrayList<>();
    private final ArrayList<ArchiveAttributeCoercion> attributeCoercions = new ArrayList<>();

    private transient FuzzyStr fuzzyStr;
    private int qidoMaxNumberOfResults;

    public String getFuzzyAlgorithmClass() {
        return fuzzyAlgorithmClass;
    }

    public void setFuzzyAlgorithmClass(String fuzzyAlgorithmClass) {
        this.fuzzyStr = fuzzyStr(fuzzyAlgorithmClass);
        this.fuzzyAlgorithmClass = fuzzyAlgorithmClass;
    }

    public FuzzyStr getFuzzyStr() {
        if (fuzzyStr == null)
            if (fuzzyAlgorithmClass == null)
                throw new IllegalStateException("No Fuzzy Algorithm Class configured");
            else
                fuzzyStr = fuzzyStr(fuzzyAlgorithmClass);
        return fuzzyStr;
    }

    private static FuzzyStr fuzzyStr(String s) {
        try {
            return (FuzzyStr) Class.forName(s).newInstance();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(s);
        }
    }

    public String getBulkDataSpoolDirectory() {
        return bulkDataSpoolDirectory;
    }

    public void setBulkDataSpoolDirectory(String bulkDataSpoolDirectory) {
        this.bulkDataSpoolDirectory = bulkDataSpoolDirectory;
    }

    public String getStorageID() {
        return storageID;
    }

    public void setStorageID(String storageID) {
        this.storageID = storageID;
    }

    public String getQueryRetrieveViewID() {
        return queryRetrieveViewID;
    }

    public void setQueryRetrieveViewID(String queryRetrieveViewID) {
        this.queryRetrieveViewID = queryRetrieveViewID;
    }

    public boolean isQueryMatchUnknown() {
        return queryMatchUnknown;
    }

    public void setQueryMatchUnknown(boolean queryMatchUnknown) {
        this.queryMatchUnknown = queryMatchUnknown;
    }

    public boolean isPersonNameComponentOrderInsensitiveMatching() {
        return personNameComponentOrderInsensitiveMatching;
    }

    public void setPersonNameComponentOrderInsensitiveMatching(boolean personNameComponentOrderInsensitiveMatching) {
        this.personNameComponentOrderInsensitiveMatching = personNameComponentOrderInsensitiveMatching;
    }

    public boolean isSendPendingCGet() {
        return sendPendingCGet;
    }

    public void setSendPendingCGet(boolean sendPendingCGet) {
        this.sendPendingCGet = sendPendingCGet;
    }

    public Duration getSendPendingCMoveInterval() {
        return sendPendingCMoveInterval;
    }

    public void setSendPendingCMoveInterval(Duration sendPendingCMoveInterval) {
        this.sendPendingCMoveInterval = sendPendingCMoveInterval;
    }

    public String[] getWadoSupportedSRClasses() {
        return wadoSupportedSRClasses.toArray(StringUtils.EMPTY_STRING);
    }

    public void setWadoSupportedSRClasses(String... wadoSupportedSRClasses) {
        this.wadoSupportedSRClasses.clear();
        this.wadoSupportedSRClasses.addAll(Arrays.asList(wadoSupportedSRClasses));
    }

    public boolean isWadoSupportedSRClass(String cuid) {
        return wadoSupportedSRClasses.contains(cuid);
    }

    public String getWadoSR2HtmlTemplateURI() {
        return wadoSR2HtmlTemplateURI;
    }

    public void setWadoSR2HtmlTemplateURI(String wadoSR2HtmlTemplateURI) {
        this.wadoSR2HtmlTemplateURI = wadoSR2HtmlTemplateURI;
    }

    public String getWadoSR2TextTemplateURI() {
        return wadoSR2TextTemplateURI;
    }

    public void setWadoSR2TextTemplateURI(String wadoSR2TextTemplateURI) {
        this.wadoSR2TextTemplateURI = wadoSR2TextTemplateURI;
    }

    public String getPatientUpdateTemplateURI() {
        return patientUpdateTemplateURI;
    }

    public void setPatientUpdateTemplateURI(String patientUpdateTemplateURI) {
        this.patientUpdateTemplateURI = patientUpdateTemplateURI;
    }

    public String getUnzipVendorDataToURI() {
        return unzipVendorDataToURI;
    }

    public void setUnzipVendorDataToURI(String unzipVendorDataToURI) {
        this.unzipVendorDataToURI = unzipVendorDataToURI;
    }

    public String[] getMppsForwardDestinations() {
        return mppsForwardDestinations;
    }

    public void setMppsForwardDestinations(String... mppsForwardDestinations) {
        this.mppsForwardDestinations = mppsForwardDestinations;
    }

    public int getQidoMaxNumberOfResults() {
        return qidoMaxNumberOfResults;
    }

    public void setQidoMaxNumberOfResults(int qidoMaxNumberOfResults) {
        this.qidoMaxNumberOfResults = qidoMaxNumberOfResults;
    }

    public AttributeFilter getAttributeFilter(Entity entity) {
        return attributeFilters.get(entity);
    }

    public void setAttributeFilter(Entity entity, AttributeFilter filter) {
        attributeFilters.put(entity, filter);
    }

    public QueryRetrieveView[] getQueryRetrieveViews() {
        return queryRetrieveViews;
    }

    public void setQueryRetrieveViews(QueryRetrieveView... queryRetrieveViews) {
        this.queryRetrieveViews = queryRetrieveViews;
    }

    public QueryRetrieveView getQueryRetrieveView(String viewID) {
        for (QueryRetrieveView view : queryRetrieveViews) {
            if (view.getViewID().equals(viewID))
                return view;
        }
        return null;
    }

    public StorageDescriptor getStorageDescriptor(String storageID) {
        return storageDescriptorMap.get(storageID);
    }

    public StorageDescriptor removeStorageDescriptor(String storageID) {
        return storageDescriptorMap.remove(storageID);
    }

    public void addStorageDescriptor(StorageDescriptor descriptor) {
        storageDescriptorMap.put(descriptor.getStorageID(), descriptor);
    }

    public Collection<StorageDescriptor> getStorageDescriptors() {
        return storageDescriptorMap.values();
    }

    public QueueDescriptor getQueueDescriptor(String queueName) {
        return queueDescriptorMap.get(queueName);
    }

    public QueueDescriptor removeQueueDescriptor(String queueName) {
        return queueDescriptorMap.remove(queueName);
    }

    public void addQueueDescriptor(QueueDescriptor descriptor) {
        queueDescriptorMap.put(descriptor.getQueueName(), descriptor);
    }

    public Collection<QueueDescriptor> getQueueDescriptors() {
        return queueDescriptorMap.values();
    }

    public ExporterDescriptor getExporterDescriptor(String exporterID) {
        return exporterDescriptorMap.get(exporterID);
    }

    public ExporterDescriptor removeExporterDescriptor(String exporterID) {
        return exporterDescriptorMap.remove(exporterID);
    }

    public int getExportTaskFetchSize() {
        return exportTaskFetchSize;
    }

    public void setExportTaskFetchSize(int exportTaskFetchSize) {
        this.exportTaskFetchSize = exportTaskFetchSize;
    }

    public Duration getExportTaskPollingInterval() {
        return exportTaskPollingInterval;
    }

    public void setExportTaskPollingInterval(Duration exportTaskPollingInterval) {
        this.exportTaskPollingInterval = exportTaskPollingInterval;
    }

    public void addExporterDescriptor(ExporterDescriptor destination) {
        exporterDescriptorMap.put(destination.getExporterID(), destination);
    }

    public Collection<ExporterDescriptor> getExporterDescriptors() {
        return exporterDescriptorMap.values();
    }

    public void removeExportRule(ExportRule rule) {
        exportRules.remove(rule);
    }

    public void clearExportRules() {
        exportRules.clear();
    }

    public void addExportRule(ExportRule rule) {
        exportRules.add(rule);
    }

    public Collection<ExportRule> getExportRules() {
        return exportRules;
    }

    public void removeCompressionRule(ArchiveCompressionRule rule) {
        compressionRules.remove(rule);
    }

    public void clearCompressionRules() {
        compressionRules.clear();
    }

    public void addCompressionRule(ArchiveCompressionRule rule) {
        compressionRules.add(rule);
    }

    public Collection<ArchiveCompressionRule> getCompressionRules() {
        return compressionRules;
    }

    public void removeAttributeCoercion(ArchiveAttributeCoercion coercion) {
        attributeCoercions.remove(coercion);
    }

    public void clearAttributeCoercions() {
        attributeCoercions.clear();
    }

    public void addAttributeCoercion(ArchiveAttributeCoercion coercion) {
        attributeCoercions.add(coercion);
    }

    public Collection<ArchiveAttributeCoercion> getAttributeCoercions() {
        return attributeCoercions;
    }

    @Override
    public void reconfigure(DeviceExtension from) {
        ArchiveDeviceExtension arcdev = (ArchiveDeviceExtension) from;
        fuzzyAlgorithmClass = arcdev.fuzzyAlgorithmClass;
        fuzzyStr = arcdev.fuzzyStr;
        storageID = arcdev.storageID;
        bulkDataSpoolDirectory = arcdev.bulkDataSpoolDirectory;
        queryRetrieveViewID = arcdev.queryRetrieveViewID;
        queryMatchUnknown = arcdev.queryMatchUnknown;
        personNameComponentOrderInsensitiveMatching = arcdev.personNameComponentOrderInsensitiveMatching;
        sendPendingCGet = arcdev.sendPendingCGet;
        sendPendingCMoveInterval = arcdev.sendPendingCMoveInterval;
        wadoSupportedSRClasses.clear();
        wadoSupportedSRClasses.addAll(arcdev.wadoSupportedSRClasses);
        wadoSR2HtmlTemplateURI = arcdev.wadoSR2HtmlTemplateURI;
        wadoSR2TextTemplateURI = arcdev.wadoSR2TextTemplateURI;
        patientUpdateTemplateURI = arcdev.patientUpdateTemplateURI;
        qidoMaxNumberOfResults = arcdev.qidoMaxNumberOfResults;
        queryRetrieveViews = arcdev.queryRetrieveViews;
        mppsForwardDestinations = arcdev.mppsForwardDestinations;
        attributeFilters.clear();
        attributeFilters.putAll(arcdev.attributeFilters);
        storageDescriptorMap.clear();
        storageDescriptorMap.putAll(arcdev.storageDescriptorMap);
        queueDescriptorMap.clear();
        queueDescriptorMap.putAll(arcdev.queueDescriptorMap);
        exporterDescriptorMap.clear();
        exporterDescriptorMap.putAll(arcdev.exporterDescriptorMap);
        exportTaskPollingInterval = arcdev.exportTaskPollingInterval;
        exportTaskFetchSize = arcdev.exportTaskFetchSize;
        exportRules.clear();
        exportRules.addAll(arcdev.exportRules);
        compressionRules.clear();
        compressionRules.addAll(arcdev.compressionRules);
        attributeCoercions.clear();
        attributeCoercions.addAll(arcdev.attributeCoercions);
    }
}
