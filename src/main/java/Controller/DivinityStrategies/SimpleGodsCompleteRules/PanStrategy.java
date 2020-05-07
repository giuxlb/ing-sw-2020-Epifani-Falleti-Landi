package Controller.DivinityStrategies.SimpleGodsCompleteRules;

/*Condizione di vittoria: vinci anche se il tuo costruttore è sceso di due o più livelli
Sempre attivo.
* */

import Controller.Coordinates;
import Controller.DivinityStrategies.DefaultStrategy;
import Controller.TurnStrategy;
import Model.Board;

public class PanStrategy extends DefaultStrategy implements TurnStrategy {

    /***
     * Calls the super checkWinCondition, if it return false,
     * check if the height of the starting position minus the height of the final positon is equal or greaten than 2
     * @param starting_position the position before the move
     * @param final_position the position after the move
     * @param board board
     * @return true if this move wins the game
     */
    public boolean checkWinCondition(Coordinates starting_position, Coordinates final_position, Board board) {
        boolean win = super.checkWinCondition(starting_position,final_position,board);
        if (!win){
            if((board.getBoardGame()[starting_position.getX()][starting_position.getY()].getHeight() - board.getBoardGame()[final_position.getX()][final_position.getY()].getHeight())>=2){
                win = true;
            }
        }
        return win;
    }
}