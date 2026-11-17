package model;

import java.time.LocalDate;

public class DiseaseReport {
    private int reportId;          // report_id (0 before it is saved)
    private int diseaseId;
    private int regionId;
    private String livestockType;
    private LocalDate reportDate;
    private int affectedCount;
    private String reportStatus;
    private String notes;

    // Display-only: filled from the JOIN in DiseaseReportDAO.mapRow, never saved
    private String diseaseName;
    private String regionName;

    public DiseaseReport() { }

    // Existing constructor, unchanged so teammates' code still compiles
    public DiseaseReport(int reportId, int diseaseId, int regionId,
                         String livestockType, LocalDate reportDate,
                         int affectedCount, String reportStatus) {
        this.reportId = reportId;
        this.diseaseId = diseaseId;
        this.regionId = regionId;
        this.livestockType = livestockType;
        this.reportDate = reportDate;
        this.affectedCount = affectedCount;
        this.reportStatus = reportStatus;
    }

    public int getReportId()         { return reportId; }
    public int getDiseaseId()        { return diseaseId; }
    public int getRegionId()         { return regionId; }
    public String getLivestockType() { return livestockType; }
    public LocalDate getReportDate() { return reportDate; }
    public int getAffectedCount()    { return affectedCount; }
    public String getReportStatus()  { return reportStatus; }
    public String getNotes()         { return notes; }
    public String getDiseaseName()   { return diseaseName; }
    public String getRegionName()    { return regionName; }

    public void setReportId(int reportId)               { this.reportId = reportId; }
    public void setDiseaseId(int diseaseId)             { this.diseaseId = diseaseId; }
    public void setRegionId(int regionId)               { this.regionId = regionId; }
    public void setLivestockType(String livestockType)  { this.livestockType = livestockType; }
    public void setReportDate(LocalDate reportDate)     { this.reportDate = reportDate; }
    public void setAffectedCount(int affectedCount)     { this.affectedCount = affectedCount; }
    public void setReportStatus(String reportStatus)    { this.reportStatus = reportStatus; }
    public void setNotes(String notes)                  { this.notes = notes; }
    public void setDiseaseName(String diseaseName)      { this.diseaseName = diseaseName; }
    public void setRegionName(String regionName)        { this.regionName = regionName; }
}
