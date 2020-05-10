package Controller;

import Controller.DivinityStrategies.*;
import Controller.DivinityStrategies.AdvancedGods.ZeusStrategy;
import Controller.DivinityStrategies.SimpleGodsCompleteRules.HephaestusStrategy;
import Controller.DivinityStrategies.SimpleGodsCompleteRules.MinotaurStrategy;
import Controller.DivinityStrategies.SimpleGodsCompleteRules.PanStrategy;
import Controller.DivinityStrategies.SimpleGodsCompleteRules.PrometheusStrategy;
import Controller.DivinityStrategies.SimpleGodsSimpleRules.*;
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
    public void selectStrategy(String card){
        switch(card.toUpperCase()){
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
            case "HEPHAESTUS": this.setTurnStrategy(new HephaestusStrategy());
                break;
            case "PAN": this.setTurnStrategy(new PanStrategy());
                break;
            case "PROMETHEUS": this.setTurnStrategy(new PrometheusStrategy());
                break;
            case "MINOTAUR": this.setTurnStrategy(new MinotaurStrategy());
                break;
            case "ZEUS": this.setTurnStrategy(new ZeusStrategy());
                break;
            default: this.setTurnStrategy(new DefaultStrategy());

        }

    }
}
