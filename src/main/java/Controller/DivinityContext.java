package Controller;

import Controller.DivinityStrategies.*;
import Model.*;

import java.util.ArrayList;

public class DivinityContext {
    private TurnStrategy strategy;

    private void setTurnStrategy(TurnStrategy strategy){
        this.strategy=strategy;
    }

    public int turn(Player player, Worker worker, Board board,Game game, int extraEffect,VirtualView virtualview){return strategy.turn(player,worker,board,game,extraEffect,virtualview);}

    public void move(Worker worker, ArrayList<Coordinates> valid_positions, int index, Game game, Board board){ strategy.move(worker,valid_positions,index,game,board); }

    public void build(Worker worker, ArrayList<Coordinates> valid_positions, int index, Game game, Board board){ strategy.build(worker, valid_positions,index,game,board); }

    public ArrayList<Coordinates> checkAvailableMoveSpots(Player player, Worker worker, Board board, boolean athenaeffect) {return strategy.checkAvailableMoveSpots(player,worker,board,athenaeffect);}

    public ArrayList<Coordinates> checkAvailableBuildSpots(Player player,Worker worker, Board board) {return strategy.checkAvailableBuildSpots(player,worker,board);}

    public boolean checkWinCondition(Coordinates starting_position,Coordinates final_position, Board board) {return strategy.checkWinCondition(starting_position,final_position,board);}

    /***
     * Select the strategy based on the card given in input
     * @param card the card associated to a player
     */
    public void selectStrategy(Card card){
        switch(card.toString().toUpperCase()){
            case "APOLLO": this.setTurnStrategy(new ApolloStrategy());
                break;
            case "ARTEMIS": this.setTurnStrategy(new ArtemisStrategy());
                break;
            case "ATHENA": this.setTurnStrategy(new AthenaStrategy());
                break;
            case "ATLAS": this.setTurnStrategy(new AtlasStrategy());
                break;
            case "DEMETER": this.setTurnStrategy(new DemeterStrategy());
                break;
            default: this.setTurnStrategy(new DefaultStrategy());

        }

    }
}
