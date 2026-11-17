package model;

public class Disease {
    private int diseaseId;
    private String diseaseName;
    private String description;
    private String recommendedAction;

    public Disease() { }

    public Disease(int diseaseId, String diseaseName,
                   String description, String recommendedAction) {
        this.diseaseId = diseaseId;
        this.diseaseName = diseaseName;
        this.description = description;
        this.recommendedAction = recommendedAction;
    }

    public int getDiseaseId()            { return diseaseId; }
    public String getDiseaseName()       { return diseaseName; }
    public String getDescription()       { return description; }
    public String getRecommendedAction() { return recommendedAction; }

    public void setDiseaseId(int diseaseId)                     { this.diseaseId = diseaseId; }
    public void setDiseaseName(String diseaseName)              { this.diseaseName = diseaseName; }
    public void setDescription(String description)              { this.description = description; }
    public void setRecommendedAction(String recommendedAction)  { this.recommendedAction = recommendedAction; }

    @Override
    public String toString() { return diseaseName; }
}
