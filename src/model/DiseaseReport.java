package model;

import java.time.LocalDate;

public class DiseaseReport {
    private int reportId;
    private int diseaseId;
    private int regionId;
    private String livestockType;
    private LocalDate reportDate;
    private int affectedCount;
    private String reportStatus;
    private String notes;

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
    public void setNotes(String notes) { this.notes = notes; }
}