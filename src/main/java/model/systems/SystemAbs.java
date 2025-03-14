package model.systems;

public abstract class SystemAbs {
    protected String name;
    protected boolean isActive;

    public SystemAbs(String name) {
        this.name = name;
        this.isActive = true;
    }

    public abstract void operate();

    public void activate() {
        isActive = true;
        System.out.println(name + " system activated.");
    }

    public void deactivate() {
        isActive = false;
        System.out.println(name + " system deactivated.");
    }

    // **GETTERS AND SETTERS**
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
        return name + " System (Active: " + isActive + ")";
    }
}
