package Controller.DivinityStrategies.SimpleGodsSimpleRules;

import Controller.Coordinates;
import Controller.DivinityStrategies.DefaultStrategy;
import Controller.TurnStrategy;
import Model.Board;
import Model.Game;
import Model.Worker;

import java.util.ArrayList;

/*
Tua costruzione: il tuo lavoratore può costruire una cupola su qualsiasi livello, compreso il terreno.
Da chiedere prima della costruzione.
 */
public class AtlasStrategy extends DefaultStrategy implements TurnStrategy {

    /***
     * Method that implements the Atlas's build mechanic: instead of doing a +1 on the cell height,
     * it sets the height straight to 4 (dome level)
     * @param worker the worker that is building
     * @param valid_positions the array of valid build coordinates
     * @param index the index of the array of coordinates in which the player wants to build
     * @param game the game, used to call the method buildTo()
     */
    public void build(Worker worker, ArrayList<Coordinates> valid_positions, int index, Game game, Board board){
        Coordinates build = valid_positions.get(index);
        game.buildTo(build.getX(),build.getY(),4);
    }
}
