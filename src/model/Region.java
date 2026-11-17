package model;

public class Region {
    private int regionId;
    private String regionName;
    private String countyName;

    public Region() { }

    public Region(int regionId, String regionName, String countyName) {
        this.regionId = regionId;
        this.regionName = regionName;
        this.countyName = countyName;
    }

    public int getRegionId()       { return regionId; }
    public String getRegionName()  { return regionName; }
    public String getCountyName()  { return countyName; }

    public void setRegionId(int regionId)          { this.regionId = regionId; }
    public void setRegionName(String regionName)   { this.regionName = regionName; }
    public void setCountyName(String countyName)   { this.countyName = countyName; }

    @Override
    public String toString() { return regionName; }
}
