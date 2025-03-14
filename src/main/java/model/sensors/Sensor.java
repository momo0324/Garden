package model.sensors;

public abstract class Sensor {
    protected String name;
    protected boolean isActive;

    public Sensor(String name) {
        this.name = name;
        this.isActive = true;
    }

    public abstract void readValue();

    public void activate() {
        isActive = true;
        System.out.println(name + " sensor activated.");
    }

    public void deactivate() {
        isActive = false;
        System.out.println(name + " sensor deactivated.");
    }

    // GETTERS AND SETTERS
    public String getName() {
        return name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return name + " Sensor (Active: " + isActive + ")";
    }
}
