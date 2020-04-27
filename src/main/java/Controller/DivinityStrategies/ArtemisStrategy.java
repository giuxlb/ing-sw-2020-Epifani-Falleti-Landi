package Controller.DivinityStrategies;

import Controller.Coordinates;
import Controller.TurnStrategy;
import Controller.VirtualView;
import Model.Board;
import Model.Game;
import Model.Player;
import Model.Worker;

import java.util.ArrayList;

/*
Tuo spostamento: il tuo lavoratore può spostarsi una volta in più, ma non può tornare dove è partito
 */


public class ArtemisStrategy extends DefaultStrategy implements TurnStrategy {

    /***
     * Similar to the Default turn, but after the first move it does another move, but first it
     * removes the initial position from the second move_spots Array
     * @param player player of this turn
     * @param worker worker that was chosen
     * @param board board
     * @param game game
     * @param athenaeffect athena effect
     */
    public void turn(Player player, Worker worker, Board board, Game game, boolean athenaeffect, VirtualView vview){

        /*Salvo le coordinate iniziali*/
        Coordinates starting_position = new Coordinates(worker.getPositionX(),worker.getPositionY());

        /*Calcolo le caselle disponibili per lo spostamento*/
        ArrayList<Coordinates> move_spots = checkAvailableMoveSpots(player,worker,board,athenaeffect);

        /*Se l'array è vuoto, il worker non puo spostarsi, quindi il player ha perso*/
        //if (move_spots.size()==0) {game.lose(player);}

        /*Mando le caselle al client, ricevo l'indice dello spostamento*/
        int index = vview.sendAvailableMove(player,move_spots);

        /*Muovo il worker*/
        move(worker,move_spots,index,game,board);

        /*Salvo la posizione intermedia*/
        Coordinates middle_position = new Coordinates(worker.getPositionX(),worker.getPositionY());

        /*Controllo se il giocatore ha vinto*/
        boolean win = checkWinCondition(starting_position,middle_position,board);
        //if (win) {game.win(player);}

        /*Calcolo le caselle disponibili per il secondo spostamento*/
        move_spots = checkAvailableMoveSpots(player,worker,board,athenaeffect);

        /*Rimuovo dall'Array la posizione iniziale*/
        for(Coordinates c : move_spots){
            if(c.equals(starting_position)){
                move_spots.remove(c);
            }
        }

        /*Se l'array è vuoto, il worker non puo spostarsi, quindi il player ha perso*/
        //if (move_spots.size()==0) {game.lose(player);}

        /*Mando le caselle al client, ricevo l'indice dello spostamento*/
        index = vview.sendAvailableMove(player,move_spots);

        /*Muovo il worker*/
        move(worker,move_spots,index,game,board);

        /*Salvo la finale intermedia*/
        Coordinates final_position = new Coordinates(worker.getPositionX(),worker.getPositionY());

        /*Controllo se il giocatore ha vinto*/
         win = checkWinCondition(middle_position,final_position,board);
        //if (win) {game.win(player);}

        /*Calcolo le caselle disponibili per la costruzione*/
        ArrayList<Coordinates> build_spots = checkAvailableBuildSpots(player,worker,board);

        /*Mando le caselle al client, ricevo indice della costruzione*/
        index = vview.sendAvailableBuild(player,build_spots);

        /*Costruisco*/
        build(worker,build_spots,index,game,board);


    }



}
