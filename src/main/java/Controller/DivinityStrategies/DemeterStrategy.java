package Controller.DivinityStrategies;

/*
Tua costruzione: puoi costruire una volta in più, ma non nella stessa casella.
 */

import Controller.Coordinates;
import Controller.TurnStrategy;
import Controller.VirtualView;
import Model.Board;
import Model.Game;
import Model.Player;
import Model.Worker;

import java.util.ArrayList;


public class DemeterStrategy extends DefaultStrategy implements TurnStrategy {
    /***
     * Similar to the default turn, but after che first build, it removes from the move_spots list
     * the position of the first build, and resend it to the client to do a second build
     * @param player player of this turn
     * @param worker the worker of this turn
     * @param board board
     * @param game game
     * @param athenaeffect athena effect
     */
    public void turn(Player player, Worker worker, Board board, Game game, boolean athenaeffect, VirtualView vview){

        /*Salvo la posizione iniziale*/
        Coordinates starting_position = new Coordinates(worker.getPositionX(),worker.getPositionY());

        /*Calcolo le caselle disponibili per lo spostamento*/
        ArrayList<Coordinates> move_spots = checkAvailableMoveSpots(player,worker,board,athenaeffect);

        /*Se l'array è vuoto, il worker non puo spostarsi, quindi il player ha perso*/
        //if (move_spots.size()==0) {game.lose(player);}
        /*Mando le caselle al client, ricevo l'indice dello spostamento*/
        int index = vview.sendAvailableMove(player,move_spots);

        /*Muovo il worker*/
        move(worker,move_spots,index,game,board);

        /*Salvo la posizione dopo lo spostamento*/
        Coordinates final_position = new Coordinates(worker.getPositionX(),worker.getPositionY());

        /*Controllo se il giocatore ha vinto*/
        boolean win = checkWinCondition(starting_position,final_position,board);
        //if (win) {game.win(player);}

        /*Calcolo le caselle disponibili per la costruzione*/
        ArrayList<Coordinates> build_spots = checkAvailableBuildSpots(player,worker,board);

        /*Mando le caselle al client, ricevo indice della costruzione*/
        index = vview.sendAvailableBuild(player,build_spots);

        /*Costruisco*/
        build(worker,build_spots,index,game,board);

        /*Rimuovo da build_spots la posizione dove il player ha fatto la prina costruzione*/
        build_spots.remove(index);

        /*Mando le caselle al client, ricevo indice per seconda costruzione*/
        index = vview.sendAvailableBuild(player,build_spots);

        /*Costruisco*/
        build(worker,build_spots,index,game,board);

    }
}
