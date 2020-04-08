package Controller;

public interface TurnStrategy {
    public void move();
    public void build();
    public void checkAvailableMoveSpots();
    public void checkAvailableBuildSpots();
}
