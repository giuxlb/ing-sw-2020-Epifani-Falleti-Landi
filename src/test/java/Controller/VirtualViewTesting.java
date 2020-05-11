package Controller;


import Model.Board;
import Model.Card;
import Model.Player;

import java.util.ArrayList;

public class VirtualViewTesting extends VirtualView {

    protected VirtualViewTesting(){

    }

    @Override
    public int sendAvailableMove(Player p, ArrayList<Coordinates> move_spots){
        return 0;
    }

    @Override
    public int sendAvailableBuild(Player p, ArrayList<Coordinates> build_spots){
        return 0;
    }

    @Override
    public void upload(Board board){

    }

    @Override
    public int askDivinityActivation(Player p, String card){
        return 1;
    }

    @Override
    public Coordinates askForWorker(Player p,ArrayList<Coordinates> workers){
        return workers.get(0);
    }
}
