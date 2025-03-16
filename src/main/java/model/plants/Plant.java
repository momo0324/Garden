package model.plants;

import java.util.List;

public abstract class Plant {
    protected String name;
    protected int minWaterRequirement;
    protected int maxWaterRequirement;
    protected int hoursToGrow;  // Fixed value
    protected int sunlightNeeded; // Fixed value
    protected int minIdealTemperature;
    protected int maxIdealTemperature;
    protected int survivalTime;
    protected List<String> vulnerableToPests;
    protected boolean isFullyGrown;
    protected int currentGrowthHours;
    protected boolean isHarvested;
    private int daysAfterMaturity = 0;
    // ✅ Image paths for different growth stages
    protected String growingImagePath;
    protected String matureImagePath;

    public Plant(String name, int minWaterRequirement, int maxWaterRequirement,
                 int hoursToGrow, int sunlightNeeded, int minIdealTemperature,
                 int maxIdealTemperature, int survivalTime, List<String> vulnerableToPests,
                 String growingImagePath, String matureImagePath) {
        this.name = name;
        this.minWaterRequirement = minWaterRequirement;
        this.maxWaterRequirement = maxWaterRequirement;
        this.hoursToGrow = hoursToGrow;
        this.sunlightNeeded = sunlightNeeded;
        this.minIdealTemperature = minIdealTemperature;
        this.maxIdealTemperature = maxIdealTemperature;
        this.survivalTime = survivalTime;
        this.vulnerableToPests = vulnerableToPests;
        this.isFullyGrown = false;
        this.currentGrowthHours = 0;
        this.isHarvested = false;
        this.growingImagePath = growingImagePath;
        this.matureImagePath = matureImagePath;
    }

    public void grow(int hours, int sunlightHours) {
        System.out.println("grow hours + sunlightHours");
        System.out.println(hours);
        System.out.println(sunlightHours);
        if (sunlightHours < sunlightNeeded) {
            System.out.println(name + " is not getting enough sunlight.");
            return;
        }

        currentGrowthHours += hours;

        if (currentGrowthHours >= hoursToGrow) {
            isFullyGrown = true;
            System.out.println(name + " has fully grown.");
        }
    }
    public void growOneDay(int sunlightHours) {
        if (!isFullyGrown) {
            grow(24, sunlightHours);  // Each cycle represents 1 day
        }
    }


    public boolean isRotten() {
        if (isFullyGrown && !isHarvested) {
            daysAfterMaturity++;
            if (daysAfterMaturity > 5) { // If unharvested for 5+ days
                System.out.println(name + " has rotted away.");
                return true;
            }
        }
        return false;
    }

    public boolean isVulnerableTo(String pest) {
        return vulnerableToPests.contains(pest);
    }

    public boolean isFullyGrown() {
        return isFullyGrown;
    }

    public boolean harvest() {
        if (!isFullyGrown) {
            System.out.println(name + " is not ready to be harvested yet.");
            return false;
        }
        if (isHarvested) {
            System.out.println(name + " has already been harvested.");
            return false;
        }

        isHarvested = true;
        System.out.println(name + " has been successfully harvested.");
        return true;
    }

    // GETTERS AND SETTERS

    public String getName() {
        return name;
    }

    public int getMinWaterRequirement() {
        return minWaterRequirement;
    }

    public void setMinWaterRequirement(int minWaterRequirement) {
        this.minWaterRequirement = minWaterRequirement;
    }

    public int getMaxWaterRequirement() {
        return maxWaterRequirement;
    }

    public void setMaxWaterRequirement(int maxWaterRequirement) {
        this.maxWaterRequirement = maxWaterRequirement;
    }

    public int getHoursToGrow() {
        return hoursToGrow;
    }

    public void setHoursToGrow(int hoursToGrow) {
        this.hoursToGrow = hoursToGrow;
    }

    public int getSunlightNeeded() {
        return sunlightNeeded;
    }

    public void setSunlightNeeded(int sunlightNeeded) {
        this.sunlightNeeded = sunlightNeeded;
    }

    public int getMinIdealTemperature() {
        return minIdealTemperature;
    }

    public void setMinIdealTemperature(int minIdealTemperature) {
        this.minIdealTemperature = minIdealTemperature;
    }

    public int getMaxIdealTemperature() {
        return maxIdealTemperature;
    }

    public void setMaxIdealTemperature(int maxIdealTemperature) {
        this.maxIdealTemperature = maxIdealTemperature;
    }

    public int getSurvivalTime() {
        return survivalTime;
    }

    public void setSurvivalTime(int survivalTime) {
        this.survivalTime = survivalTime;
    }

    public List<String> getVulnerableToPests() {
        return vulnerableToPests;
    }

    public void setVulnerableToPests(List<String> vulnerableToPests) {
        this.vulnerableToPests = vulnerableToPests;
    }

    public boolean getIsFullyGrown() {
        return isFullyGrown;
    }

    public void setFullyGrown(boolean fullyGrown) {
        isFullyGrown = fullyGrown;
    }

    public int getCurrentGrowthHours() {
        return currentGrowthHours;
    }

    public void setCurrentGrowthHours(int currentGrowthHours) {
        this.currentGrowthHours = currentGrowthHours;
    }

    public boolean getIsHarvested() {
        return isHarvested;
    }

    public void setHarvested(boolean harvested) {
        isHarvested = harvested;
    }

    public void applyPestDamage(String pest) {
        if (isVulnerableTo(pest)) {
            System.out.println(name + " is attacked by " + pest + "!");
            survivalTime -= 2;  // Reduce survival time faster for pests
        }
    }

    public boolean checkSurvival(int currentTemperature, int waterReceived) {
        if (waterReceived < minWaterRequirement) {
            System.out.println(name + " is wilting due to lack of water.");
            survivalTime--;
        } else if (waterReceived > maxWaterRequirement) {
            System.out.println(name + " is overwatered.");
        }

        if (currentTemperature < minIdealTemperature || currentTemperature > maxIdealTemperature) {
            System.out.println(name + " is struggling due to extreme temperature.");
            survivalTime--;
        }

        return survivalTime > 0; // Returns false if the plant has died
    }

    @Override
    public String toString() {
        return name + " (Water: " + minWaterRequirement + "-" + maxWaterRequirement + "ml/day, " +
                "Sunlight: " + sunlightNeeded + " hrs/day, " +
                "Growth Time: " + hoursToGrow + " hours, " +
                "Ideal Temperature: " + minIdealTemperature + "-" + maxIdealTemperature + "°C)";
    }

    public String getCurrentImagePath() {
        if(getIsFullyGrown()){
            return matureImagePath;

        }else{
            return growingImagePath;
        }
    }
}
