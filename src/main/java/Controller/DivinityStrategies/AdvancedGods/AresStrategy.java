package Controller.DivinityStrategies.AdvancedGods;

import Controller.Coordinates;
import Controller.DivinityStrategies.DefaultStrategy;
import Controller.TurnStrategy;
import Controller.VirtualView;
import Model.*;
import java.util.ArrayList;
/*Fine del tuo turno: puoi rimuovere un blocco libero (non una cupola) adiacente al lavoratore che non hai mosso.*/
public class AresStrategy extends DefaultStrategy implements TurnStrategy {

    /**
     * After the normal turn, it finds the other worker and calculates the list of available remove spots.
     * If the list is not empty, asks the player for the effect activation. If the player accept,
     * asks the player which block to remove.
     * @param player the player that is playing this turn
     * @param worker the chosen worker
     * @param board the board
     * @param game the game
     * @param extraEffect applied on this turn
     * @param vview the virtual view
     * @return extra effext or error value
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

        if(index==-1){
            return -1;
        }


        /*Muovo il worker*/
        move(worker,move_spots,index,game,board);

        vview.upload(board);
        // vview.upload(game.getBoardGame());

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

        if(index==-1){
            return -1;
        }

        /*Costruisco*/
        build(worker,build_spots,index,game,board);

        vview.upload(board);
        // vview.upload(game.getBoardGame());

        //trovo il worker non mosso
        int other_worker;
        if(player.getWorker(0)==worker) other_worker=1;
        else other_worker=0;

        //calcolo i remove spots
        ArrayList<Coordinates> remove_spots = checkAvailableRemoveSpots(player.getWorker(other_worker),board);
        if(remove_spots.size()>0) {
            int ask_divinity = vview.askDivinityActivation(player, player.getGameCard());
            if (ask_divinity == -1) return -1;
            if (ask_divinity == 1) {
                index = vview.sendAvailableRemove(player,remove_spots);
                if(index==-1){
                    return -1;
                }
                Coordinates remove = remove_spots.get(index);
                int height = game.getBoardGame().getBoardHeight(remove.getX(),remove.getY());
                game.buildTo(remove.getX(),remove.getY(),height-1);
                vview.upload(board);
            }
        }
        return 0;
    }

    /***
     * Finds the spots that are available to be removed
     * @param worker the worker that is removing
     * @param board the board
     * @return the arraylist of available positions
     */
    public ArrayList<Coordinates> checkAvailableRemoveSpots(Worker worker, Board board){
        BoardCell[][] boardCopy = board.getBoardGame();
        int pos_x = worker.getPositionX();
        int pos_y = worker.getPositionY();
        ArrayList<Coordinates> remove_spots = new ArrayList<>();
        for(int i = pos_x-1;i <= pos_x+1;i++){
            for(int j = pos_y-1;j <= pos_y+1;j++){
                if(i>=0 && i<=4 && j>=0 && j<=4){
                    if(boardCopy[i][j].getWorkerBuilder()==null && boardCopy[i][j].getHeight()>0 && boardCopy[i][j].getHeight()<4){
                        remove_spots.add(new Coordinates(i,j));
                    }
                }
            }
        }
        return remove_spots;
    }
}
