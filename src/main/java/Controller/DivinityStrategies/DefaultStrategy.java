package Controller.DivinityStrategies;

import Controller.Coordinates;
import Controller.TurnStrategy;
import Controller.VirtualView;
import Model.*;

import java.net.CookieHandler;
import java.util.ArrayList;

public class DefaultStrategy implements TurnStrategy {

    /***
     * The standard flow of the turn:
     * -Calculate the valid positions for the movement
     * -Checks if the array is empty, if so the player has lost, if not it sends the array to the view
     * -Receive the index of the chosen position and modifies the model with the movement
     * -Checks if this was a winning move, if not it goes on and calculate the valid positions for the build
     * -Sends the array to the view, and receive the index of the chosen build spot
     * -Modify the model with del buildUp method
     * @param player the player that is playing this turn
     * @param worker the chosen worker
     * @param board the board
     * @param game the game
     * @param extraEffect applied on this turn
     */
    public int turn(Player player, Worker worker, Board board, Game game, int extraEffect, VirtualView vview){
        /*Calcolo l'extra effect di questo turno*/
        boolean athenaeffect = false;
        if(extraEffect==1){
            athenaeffect = true;
        }

        /*Salvo la posizione iniziale*/
        Coordinates starting_position = new Coordinates(worker.getPositionX(),worker.getPositionY());

        /*Calcolo le caselle disponibili per lo spostamento*/
        ArrayList<Coordinates> move_spots = checkAvailableMoveSpots(player,worker,board,athenaeffect);

        /*Se l'array è vuoto, il worker non puo spostarsi, quindi il player ha perso*/
        if (move_spots.size()==0) {
            game.playerLose(player);
            return 0;
        }
        /*Mando le caselle al client, ricevo l'indice dello spostamento*/
        int index = vview.sendAvailableMove(player,move_spots);

        /*Muovo il worker*/
        move(worker,move_spots,index,game,board);
        vview.upload(board);

        /*Salvo la posizione dopo lo spostamento*/
        Coordinates final_position = new Coordinates(worker.getPositionX(),worker.getPositionY());

        /*Controllo se il giocatore ha vinto*/
        boolean win = checkWinCondition(starting_position,final_position,board);
        if (win) {
            game.stopGame(player);
            return 0;
        }

        /*Calcolo le caselle disponibili per la costruzione*/
        ArrayList<Coordinates> build_spots = checkAvailableBuildSpots(player,worker,board);

        /*Mando le caselle al client, ricevo indice della costruzione*/
        index = vview.sendAvailableBuild(player,build_spots);

        /*Costruisco*/
        build(worker,build_spots,index,game,board);
        vview.upload(board);

        return 0;
    }

    /***
     * Extracts the Coordinates from the array of valid positions and calls the moveWorker method from game.
     * @param worker the worker that is moving
     * @param valid_positions the arraylist of valid positions
     * @param index the index of the position that the player has choosen
     * @param game the game
     * @param board the board
     */
    public void move(Worker worker, ArrayList<Coordinates> valid_positions, int index, Game game,Board board){
        //default move
        Coordinates move = valid_positions.get(index);
        game.moveWorker(worker,move.getX(),move.getY());

    }

    /***
     * Extracts the Coordinates from the array of valid positions and calls the buildUp method from game.
     * @param worker the worker that is building
     * @param valid_positions the arraylist of valid positions
     * @param index the index of the position that the player has choosen
     * @param game the game
     * @param board the board
     */
    public void build(Worker worker, ArrayList<Coordinates> valid_positions, int index, Game game,Board board){
        //default build
        Coordinates build = valid_positions.get(index);
        game.buildUp(build.getX(),build.getY());
    }


    /***
     * Checks the 3x3 matrix around the worker to determine in which the worker can move.
     * @param player the player
     * @param worker the worker that is moving
     * @param board board
     * @param athenaeffect the athena effect restriction, if true the worker cant move in higher cells
     * @return an Arraylist with all the Cooordinates of the spots in which the worker can move
     */
    public ArrayList<Coordinates> checkAvailableMoveSpots(Player player, Worker worker, Board board,boolean athenaeffect) {
        int pos_x=worker.getPositionX();
        int pos_y=worker.getPositionY();
        BoardCell[][] boardCopy = board.getBoardGame();
        ArrayList<Coordinates> valid_positions = new ArrayList<Coordinates>();
        for(int i = pos_x-1 ; i <= pos_x+1 ; i++){
            for(int j = pos_y-1 ; j <= pos_y+1 ; j++){
                if(/*Posizione è dentro la scacchiera*/ i>=0 && i<=4 && j>=0 && j<=4) {
                    if(/*non sono nella casella del mio worker*/ i!=pos_x && j!=pos_y &&
                        /*nella casella non c'è un altro worker*/ boardCopy[i][j].getWorkerBuilder()==null &&
                        /*L'altezza della casella non è superiore di 1 rispetto a quella del worker*/ boardCopy[pos_x][pos_y].getHeight() >= boardCopy[i][j].getHeight()-1 &&
                        /*nella casella non  c'è una cupola*/ boardCopy[i][j].getHeight()<4){
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
            }
        }
        return valid_positions;
    }

    /***
     * Checks the 3x3 matrix around the worker to determine in which the worker can build.
     * @param player the player
     * @param worker the worker that is building
     * @param board board
     * @return an Arraylist with all the Cooordinates of the spots in which the worker can build
     */
    public ArrayList<Coordinates> checkAvailableBuildSpots(Player player,Worker worker, Board board){
        int pos_x=worker.getPositionX();
        int pos_y=worker.getPositionY();
        BoardCell[][] boardCopy = board.getBoardGame();
        ArrayList<Coordinates> valid_positions = new ArrayList<Coordinates>();
        for(int i = pos_x-1; i<= pos_x+1;i++){
            for(int j = pos_y-1; j<= pos_y+1;j++){
                if(i>=0 && i<=4 && j>=0 && j<=4){
                    if(i!=pos_x && j!=pos_y &&
                        boardCopy[i][j].getWorkerBuilder()==null &&
                        boardCopy[i][j].getHeight()<4){
                        valid_positions.add(new Coordinates(i,j));
                    }
                }
            }
        }
        return valid_positions;
    }


    /***
     * Method that checks if the player has won after a move.
     * It checks if the starting position has an height value of "2" and if the final position has an height value of "3".
     * To be executed after every move.
     * @param starting_position the position before the move
     * @param final_position the position after the move
     * @param board board
     * @return true if the player has won
     */
    public boolean checkWinCondition(Coordinates starting_position,Coordinates final_position, Board board){
        if (board.getBoardGame()[starting_position.getX()][starting_position.getY()].getHeight()==2 && board.getBoardGame()[final_position.getX()][final_position.getY()].getHeight()==3){
            return true;
        }
        return false;
    }
}
