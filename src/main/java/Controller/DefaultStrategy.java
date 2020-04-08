package Controller;

public class DefaultStrategy implements TurnStrategy {
    public void move(){
        //default move
        System.out.println("Default move");
    }
    public void build(){
        //default build
        System.out.println("Default build");
    }


    public void checkAvailableMoveSpots() {

    }

    public void checkAvailableBuildSpots(){

    }
}
