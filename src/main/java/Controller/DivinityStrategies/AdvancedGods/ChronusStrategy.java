package Controller.DivinityStrategies.AdvancedGods;

import Controller.Coordinates;
import Controller.DivinityStrategies.DefaultStrategy;
import Controller.TurnStrategy;
import Controller.VirtualView;
import Model.Board;
import Model.Game;
import Model.Player;
import Model.Worker;

import java.util.ArrayList;

/*Condizione di vittoria: vinci anche quando ci sono 5 torri complete.*/
public class ChronusStrategy extends DefaultStrategy implements TurnStrategy {
    public int turn(Player player, Worker worker, Board board, Game game, int extraEffect, VirtualView vview){
        boolean win = checkChronusWinCondition(board);
        if (win){
            game.stopGame(player);
            return 0;
        }

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
        win = checkWinCondition(starting_position,final_position,board);
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

        win = checkChronusWinCondition(board);
        if(win){
            game.stopGame(player);
            return 0;
        }

        vview.upload(board);
        // vview.upload(game.getBoardGame());

        return 0;
    }

    private boolean checkChronusWinCondition(Board board){
        int count=0;
        for(int i=0;i<5;i++){
            for(int j=0;j<5;j++){
                if(board.getBoardGame()[i][j].getHeight()==4){
                    count++;
                }
            }
        }
        return count >= 5;
    }

}
