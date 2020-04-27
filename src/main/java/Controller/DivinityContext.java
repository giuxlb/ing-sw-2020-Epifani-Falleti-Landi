package Controller;

import Controller.DivinityStrategies.*;
import Model.*;

import java.util.ArrayList;

public class DivinityContext {
    private TurnStrategy strategy;

    private void setTurnStrategy(TurnStrategy strategy){
        this.strategy=strategy;
    }

    public void turn(Player player, Worker worker, Board board,Game game, boolean athenaeffect,View virtualview){strategy.turn(player,worker,board,game,athenaeffect,virtualview);}

    public void move(Worker worker, ArrayList<Coordinates> valid_positions, int index, Game game, Board board){ strategy.move(worker,valid_positions,index,game,board); }

    public void build(Worker worker, ArrayList<Coordinates> valid_positions, int index, Game game, Board board){ strategy.build(worker, valid_positions,index,game,board); }

    public void checkAvailableMoveSpots(Player player, Worker worker, Board board, boolean athenaeffect) {strategy.checkAvailableMoveSpots(player,worker,board,athenaeffect);}

    public void checkAvailableBuildSpots(Player player,Worker worker, Board board) {strategy.checkAvailableBuildSpots(player,worker,board);}

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
