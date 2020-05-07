package Controller.DivinityStrategies.SimpleGodsSimpleRules;

import Controller.Coordinates;
import Controller.DivinityStrategies.DefaultStrategy;
import Controller.TurnStrategy;
import Model.*;

import java.util.ArrayList;

/*
Tuo spostamento: il tuo lavoratore può spostarsi in una casella occupata da un avversario, scambiando le posizioni
Non ho bisogno di chiedere se l'user vuole attivare l'effetto: può scegliere durante il movimento.
 */

public class ApolloStrategy extends DefaultStrategy implements TurnStrategy {

    /***
     * Checks if in the chosen position there is a worker,
     * if there isn't, it works like the Default move,
     * if there is, it exchange the positions of the 2 workers.
     * @param worker the worker that is moving
     * @param valid_positions the Arraylist of valid poitions
     * @param index the index of the valid_position chosen
     * @param game game
     * @param board board
     */
    public void move(Worker worker, ArrayList<Coordinates> valid_positions, int index, Game game, Board board){
        Coordinates move = valid_positions.get(index);
        Worker worker_to_exchange = board.getBoardGame()[move.getX()][move.getY()].getWorkerBuilder();
        if(worker_to_exchange==null){
            //NON BISOGNA SCAMBIARE
            game.moveWorker(worker,move.getX(),move.getY());

        }
        else{
            //BISOGNA SCAMBIARE
            game.moveWorker(worker_to_exchange,worker.getPositionX(),worker.getPositionY());
            game.moveWorkerWithoutNull(worker,move.getX(),move.getY());

        }
    }

    /***
     * Similar to the default method, but it also return the cell where an opponent's worker is present
     * @return the list of all the available spots to move, with the Apollo modifier
     */
    public ArrayList<Coordinates> checkAvailableMoveSpots(Player player, Worker worker, Board board, boolean athenaeffect) {
        int pos_x=worker.getPositionX();
        int pos_y=worker.getPositionY();
        BoardCell[][] boardCopy = board.getBoardGame();
        ArrayList<Coordinates> valid_positions = new ArrayList<>();
        for(int i = pos_x-1 ; i <= pos_x+1 ; i++){
            for(int j = pos_y-1 ; j <= pos_y+1 ; j++){
                if(/*Posizione è dentro la scacchiera*/ i>=0 && i<=4 && j>=0 && j<=4) {
                    if(/*non sono nella casella del mio worker*/ !(i==pos_x && j==pos_y) &&
                            /*L'altezza della casella non è superiore di 1 rispetto a quella del worker*/ boardCopy[pos_x][pos_y].getHeight() >= boardCopy[i][j].getHeight()-1 &&
                            /*nella casella non  c'è una cupola*/ boardCopy[i][j].getHeight()<4){
                        if(boardCopy[i][j].getWorkerBuilder()!=null){
                            if (!boardCopy[i][j].getWorkerBuilder().getColor().equals(worker.getColor())){
                                if(athenaeffect){
                                    if(boardCopy[pos_x][pos_y].getHeight() >= boardCopy[i][j].getHeight()){
                                        valid_positions.add(new Coordinates(i,j));
                                    }
                                }
                                else {
                                    valid_positions.add(new Coordinates(i, j));
                                }
                            }
                        }
                        else {
                            if (athenaeffect) {
                                if (boardCopy[pos_x][pos_y].getHeight() >= boardCopy[i][j].getHeight()) {
                                    valid_positions.add(new Coordinates(i, j));
                                }
                            } else {
                                valid_positions.add(new Coordinates(i, j));
                            }
                        }
                    }
                }
            }
        }
        return valid_positions;
    }
}
