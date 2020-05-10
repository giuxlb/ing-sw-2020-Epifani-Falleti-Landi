package Controller.DivinityStrategies.AdvancedGods;

import Controller.Coordinates;
import Controller.DivinityStrategies.DefaultStrategy;
import Controller.TurnStrategy;
import Controller.VirtualView;
import Model.*;

import java.util.ArrayList;

/*Tua costruzione: il tuo lavoratore può costruire una volta in più, anche nella stessa casella ma non in una casella perimetrale.*/
public class HestiaStrategy extends DefaultStrategy implements TurnStrategy {


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
        ArrayList<Coordinates> build_spots = super.checkAvailableBuildSpots(player,worker,board);

        /*Mando le caselle al client, ricevo indice della costruzione*/
        index = vview.sendAvailableBuild(player,build_spots);

        if(index==-1){
            return -1;
        }

        /*Costruisco*/
        build(worker,build_spots,index,game,board);

        vview.upload(board);
        // vview.upload(game.getBoardGame());

        build_spots = checkAvailableBuildSpots(player,worker,board);

        if(build_spots.size()!=0) {
            int ask_divinity = vview.askDivinityActivation(player, player.getGameCard());
            if (ask_divinity == -1) {
                return -1;
            }
            if (ask_divinity == 1) {
                index = vview.sendAvailableBuild(player,build_spots);
                if(index==-1) return -1;
                build(worker,build_spots,index,game,board);
                vview.upload(board);
            }
        }
        return 0;
    }


    public ArrayList<Coordinates> checkAvailableBuildSpots(Player player,Worker worker, Board board){
        int pos_x=worker.getPositionX();
        int pos_y=worker.getPositionY();
        BoardCell[][] boardCopy = board.getBoardGame();
        ArrayList<Coordinates> valid_positions = new ArrayList<Coordinates>();
        for(int i = pos_x-1; i<= pos_x+1;i++){
            for(int j = pos_y-1; j<= pos_y+1;j++){
                if(i>0 && i<4 && j>0 && j<4){
                    if(!(i==pos_x && j==pos_y) &&
                            boardCopy[i][j].getWorkerBuilder()==null &&
                            boardCopy[i][j].getHeight()<4){
                        valid_positions.add(new Coordinates(i,j));
                    }
                }
            }
        }
        return valid_positions;
    }
}
