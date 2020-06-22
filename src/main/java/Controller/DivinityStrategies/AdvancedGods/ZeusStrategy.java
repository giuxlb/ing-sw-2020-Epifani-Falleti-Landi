package Controller.DivinityStrategies.AdvancedGods;

import Controller.Coordinates;
import Controller.DivinityStrategies.DefaultStrategy;
import Controller.TurnStrategy;
import Model.Board;
import Model.BoardCell;
import Model.Player;
import Model.Worker;

import java.util.ArrayList;

public class ZeusStrategy extends DefaultStrategy implements TurnStrategy {
    /***
     * Similar to the standard, but it also return the cell where the worker of the player is placed.
     * @param player the player
     * @param worker the worker that is building
     * @param board board
     * @return the available build spots list
     */
    public ArrayList<Coordinates> checkAvailableBuildSpots(Player player, Worker worker, Board board){
        int pos_x=worker.getPositionX();
        int pos_y=worker.getPositionY();
        BoardCell[][] boardCopy = board.getBoardGame();
        ArrayList<Coordinates> valid_positions = new ArrayList<Coordinates>();
        for(int i = pos_x-1; i<= pos_x+1;i++){
            for(int j = pos_y-1; j<= pos_y+1;j++){
                if(i>=0 && i<=4 && j>=0 && j<=4){
                    if(boardCopy[i][j].getHeight()<4){
                        if(boardCopy[i][j].getWorkerBuilder()!=null){
                            if(boardCopy[i][j].getWorkerBuilder().getColor().equals(worker.getColor())){
                                valid_positions.add(new Coordinates(i,j));
                            }
                        }else{
                        valid_positions.add(new Coordinates(i,j));
                        }
                    }
                }
            }
        }
        return valid_positions;
    }
}
