package Controller.DivinityStrategies.SimpleGodsSimpleRules;

/*
Turno dell'avversario: se nel tuo ultimo turno un tuo lavoratore è salito di livello, in questo turno i lavoratori avversari non possono salire di livello
 Non c'è bisogno di chiedere l'attivazione.
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

public class AthenaStrategy extends DefaultStrategy implements TurnStrategy {
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

        /*Controllo se il worker è salito*/
        boolean has_gone_up = board.getBoardGame()[final_position.getX()][final_position.getY()].getHeight() > board.getBoardGame()[starting_position.getX()][starting_position.getY()].getHeight();

        /*Calcolo le caselle disponibili per la costruzione*/
        ArrayList<Coordinates> build_spots = checkAvailableBuildSpots(player,worker,board);

        /*Mando le caselle al client, ricevo indice della costruzione*/
        index = vview.sendAvailableBuild(player,build_spots);

        /*Costruisco*/
        build(worker,build_spots,index,game,board);

        vview.upload(board);
       // vview.upload(game.getBoardGameImmutable());

        if (has_gone_up){
            return 1;
        }
        else return 0;
    }
}
