package Controller.DivinityStrategies.SimpleGodsSimpleRules;

/*
Tua costruzione: puoi costruire una volta in più, ma non nella stessa casella.
Da chiedere dopo la prima costruzione.
 */

import Controller.Coordinates;
import Controller.DivinityStrategies.DefaultStrategy;
import Controller.TurnStrategy;
import Controller.VirtualView;
import Model.Board;
import Model.Game;
import Model.Player;
import Model.Worker;

import java.util.ArrayList;
import java.util.List;


public class DemeterStrategy extends DefaultStrategy implements TurnStrategy {
    /***
     * Similar to the default turn, but after che first build, it removes from the move_spots list
     * the position of the first build, and resend it to the client to do a second build
     * @param player player of this turn
     * @param worker the worker of this turn
     * @param board board
     * @param game game
     * @param extraEffect the effect on this turn
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
        //vview.upload(game.getBoardGameImmutable());

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
        //vview.upload(game.getBoardGameImmutable());

        if (vview.askDivinityActivation(player,player.getGameCard())) {
            /*Rimuovo da build_spots la posizione dove il player ha fatto la prina costruzione*/
            build_spots.remove(index);

            /*Mando le caselle al client, ricevo indice per seconda costruzione*/
            index = vview.sendAvailableBuild(player, build_spots);

            /*Costruisco*/
            build(worker, build_spots, index, game, board);

            vview.upload(board);
            //vview.upload(game.getBoardGameImmutable());
        }

        return 0;
    }
}
