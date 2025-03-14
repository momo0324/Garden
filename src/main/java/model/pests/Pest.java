package model.pests;

public abstract class Pest {
    protected String name;
    protected int timeToSpread;  // Hours before the pest spreads to another plant
    protected int timeToKill;    // Hours required to remove the pest using pest control
    protected boolean isEliminated;

    public Pest(String name, int timeToSpread, int timeToKill) {
        this.name = name;
        this.timeToSpread = timeToSpread;
        this.timeToKill = timeToKill;
        this.isEliminated = false;
    }

    public void spread() {
        System.out.println(name + " is spreading to nearby plants!");
    }

    public void eliminate() {
        isEliminated = true;
        System.out.println(name + " has been eliminated.");
    }

    // GETTERS AND SETTERS
    public String getName() {
        return name;
    }

    public int getTimeToSpread() {
        return timeToSpread;
    }

    public void setTimeToSpread(int timeToSpread) {
        this.timeToSpread = timeToSpread;
    }

    public int getTimeToKill() {
        return timeToKill;
    }

    public void setTimeToKill(int timeToKill) {
        this.timeToKill = timeToKill;
    }

    public boolean isEliminated() {
        return isEliminated;
    }

    public void setEliminated(boolean eliminated) {
        isEliminated = eliminated;
    }

    @Override
    public String toString() {
        return name + " (Spreads in " + timeToSpread + " hours, Eliminated in " + timeToKill + " hours)";
    }
}
