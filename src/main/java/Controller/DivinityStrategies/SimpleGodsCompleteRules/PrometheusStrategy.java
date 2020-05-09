package Controller.DivinityStrategies.SimpleGodsCompleteRules;

import Controller.Coordinates;
import Controller.DivinityStrategies.DefaultStrategy;
import Controller.TurnStrategy;
import Controller.VirtualView;
import Model.Board;
import Model.Game;
import Model.Player;
import Model.Worker;

import java.util.ArrayList;

/*Tuo turno: se il tuo worker non sale di livello, può costruire sia prima sia dopo il movimento.*/

public class PrometheusStrategy extends DefaultStrategy implements TurnStrategy {
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


        int index;

        int ask_divinity = vview.askDivinityActivation(player,player.getGameCard());
        if(ask_divinity==-1){
            return -1;
        }

        if(ask_divinity==1){
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


            athenaeffect=true;
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
        index = vview.sendAvailableMove(player,move_spots);

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

        return 0;
    }
}
