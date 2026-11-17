package model;

public class Disease {
    private int diseaseId;
    private String diseaseName;
    private String description;
    private String recommendedAction;

    public Disease(int diseaseId, String diseaseName,
                   String description, String recommendedAction) {
        this.diseaseId = diseaseId;
        this.diseaseName = diseaseName;
        this.description = description;
        this.recommendedAction = recommendedAction;
    }

    public int getDiseaseId()             { return diseaseId; }
    public String getDiseaseName()        { return diseaseName; }
    public String getDescription()        { return description; }
    public String getRecommendedAction()  { return recommendedAction; }
}