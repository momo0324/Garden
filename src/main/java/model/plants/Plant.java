package model.plants;

import java.util.List;
import model.Garden;

public abstract class Plant {
    protected String name;
    private int consecutiveHealthyHours = 0;
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
    private int additionalSunlightHours = 0;  // 新增：额外的阳光时间
    // ✅ Image paths for different growth stages
    protected String growingImagePath;
    protected int currentSurvivalTime;
    protected String matureImagePath;
    private int currentWaterLevel = 0;
    private boolean isDead = false;  // 新增：死亡状态
    private String currentPest = null;  // 新增：当前感染的害虫

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
        this.currentSurvivalTime = survivalTime;
        this.vulnerableToPests = vulnerableToPests;
        this.isFullyGrown = false;
        this.currentGrowthHours = 0;
        this.isHarvested = false;
        this.growingImagePath = growingImagePath;
        this.matureImagePath = matureImagePath;
    }

    public void decreaseSurvivalTime() {
        if (currentSurvivalTime > 0) {
            currentSurvivalTime--;
        }
    }

    public void resetSurvivalTime(int currentLightHours, int currentTemperature) {
        if (currentWaterLevel >= minWaterRequirement &&
                currentLightHours >= sunlightNeeded &&
                currentTemperature >= minIdealTemperature &&
                currentTemperature <= maxIdealTemperature) {

            consecutiveHealthyHours++;
            if (consecutiveHealthyHours >= 3) { // ✅ Reset only after 3 stable hours
                this.currentSurvivalTime = survivalTime;
                System.out.println(name + " survival time reset.");
            }
        } else {
            consecutiveHealthyHours = 0; // ✅ Reset counter if any condition fails
        }
    }

    public int getCurrentSurvivalTime() {
        return currentSurvivalTime;
    }

    public void grow(int sunlightHours) {
        // 检查水分是否充足
        if (currentWaterLevel < minWaterRequirement) {
            System.out.println(name + " needs more water to grow.");
            return;
        }

        // 检查温度是否适宜
        Garden garden = Garden.getInstance();
        int currentTemperature = garden.getCurrentTemperature();
        if (currentTemperature < minIdealTemperature || currentTemperature > maxIdealTemperature) {
            System.out.println(name + " cannot grow in current temperature: " + currentTemperature + "°C");
            return;
        }

        // increase growth hours if water and temperature are suitable
        currentGrowthHours += sunlightHours;

        if (currentGrowthHours >= hoursToGrow) {
            isFullyGrown = true;
            System.out.println(name + " has fully grown.");
        } else {
            System.out.println(name + " is growing. Hours: " + currentGrowthHours + "/" + hoursToGrow);
        }
    }
    public void growOneDay(int sunlightHours) {
        if (!isFullyGrown) {
            currentGrowthHours += sunlightHours;
            if (currentGrowthHours >= hoursToGrow) {
                isFullyGrown = true;
                System.out.println(name + " has fully grown!");
            } else {
                System.out.println(name + " growth progress: " + currentGrowthHours + "/" + hoursToGrow);
            }
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

    public void water(int amount) {
        currentWaterLevel = Math.min(currentWaterLevel + amount, maxWaterRequirement);
        System.out.println(name + " received " + amount + "ml water. Current water level: " + currentWaterLevel + "ml");
    }

    public void addSunlight(int hours) {
        this.additionalSunlightHours += hours;
        System.out.println(name + " received " + hours + " additional hours of sunlight. Total additional: " + additionalSunlightHours + "h");
    }

    public int getAdditionalSunlightHours() {
        return additionalSunlightHours;
    }

    public void resetAdditionalSunlight() {
        this.additionalSunlightHours = 0;
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

    public int getCurrentWaterLevel() {
        return currentWaterLevel;
    }

    public void setCurrentWaterLevel(int waterLevel) {
        this.currentWaterLevel = waterLevel;
    }

    public void applyPestDamage(String pest) {
        if (isVulnerableTo(pest)) {
            System.out.println(name + " is attacked by " + pest + "!");
            currentPest = pest;  // 设置当前害虫
            survivalTime -= 2;  // Reduce survival time faster for pests
            checkDeathCondition();  // 检查是否应该死亡
        }
    }

    public void removePest() {
        currentPest = null;  // 移除害虫
        System.out.println(name + " has been treated for pests.");
    }

    public String getCurrentPest() {
        return currentPest;
    }

    public boolean isDead() {
        return isDead;
    }

    private void checkDeathCondition() {
        // 如果植物有虫且在存活时间内没有成熟，就会死亡
        if (currentPest != null && !isFullyGrown && survivalTime <= 0) {
            isDead = true;
            System.out.println(name + " has died due to pest infestation!");
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
        if(isFullyGrown || isHarvested){
            return matureImagePath;
        } else {
            return growingImagePath;
        }
    }
}
