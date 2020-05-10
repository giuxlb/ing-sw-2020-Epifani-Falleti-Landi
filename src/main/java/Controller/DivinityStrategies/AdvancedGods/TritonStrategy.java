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
/*Tuo spostamento: ogni volta che il tuo lavoratore si sposta su una casella perimetrale, può subito spostarsi di nuovo*/
public class TritonStrategy extends DefaultStrategy implements TurnStrategy {
    public int turn(Player player, Worker worker, Board board, Game game, int extraEffect, VirtualView vview){
        /*Calcolo l'extra effect di questo turno*/
        boolean athenaeffect = false;
        if(extraEffect==1){
            athenaeffect = true;
        }

        boolean casellaPerimetrale = true;
        int index;

        while(casellaPerimetrale) {
            /*Salvo la posizione iniziale*/
            Coordinates starting_position = new Coordinates(worker.getPositionX(), worker.getPositionY());

            /*Calcolo le caselle disponibili per lo spostamento*/
            ArrayList<Coordinates> move_spots = checkAvailableMoveSpots(player, worker, board, athenaeffect);

            /*Se l'array è vuoto, il worker non puo spostarsi, quindi il player ha perso*/
            if (move_spots.size() == 0) {
                game.playerLose(player);
                return 0;
            }
            /*Mando le caselle al client, ricevo l'indice dello spostamento*/
            index = vview.sendAvailableMove(player, move_spots);

            if (index == -1) {
                return -1;
            }


            /*Muovo il worker*/
            move(worker, move_spots, index, game, board);

            vview.upload(board);
            // vview.upload(game.getBoardGame());

            /*Salvo la posizione dopo lo spostamento*/
            Coordinates final_position = new Coordinates(worker.getPositionX(), worker.getPositionY());

            /*Controllo se il giocatore ha vinto*/
            boolean win = checkWinCondition(starting_position, final_position, board);
            if (win) {
                game.stopGame(player);
                return 0;
            }

            if(final_position.getX()==0 || final_position.getX()==4 || final_position.getY()==0 || final_position.getY()==4){
                int ask_divinity = vview.askDivinityActivation(player,player.getGameCard());
                if(ask_divinity==-1) return -1;
                if(ask_divinity==1) casellaPerimetrale=true;
                else{casellaPerimetrale=false;}
            }else{
                casellaPerimetrale=false;
            }
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
