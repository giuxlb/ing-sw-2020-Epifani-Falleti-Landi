package Controller.DivinityStrategies.SimpleGodsSimpleRules;

import Controller.Coordinates;
import Controller.DivinityStrategies.DefaultStrategy;
import Controller.TurnStrategy;
import Controller.VirtualView;
import Model.Board;
import Model.Game;
import Model.Player;
import Model.Worker;

import java.util.ArrayList;

/*
Tua costruzione: il tuo lavoratore può costruire una cupola su qualsiasi livello, compreso il terreno.
Da chiedere prima della costruzione.
 */
public class AtlasStrategy extends DefaultStrategy implements TurnStrategy {

    /***
     * Method that implements the Atlas's build mechanic: instead of doing a +1 on the cell height,
     * it sets the height straight to 4 (dome level)
     * @param worker the worker that is building
     * @param valid_positions the array of valid build coordinates
     * @param index the index of the array of coordinates in which the player wants to build
     * @param game the game, used to call the method buildTo()
     */
    public void build(Worker worker, ArrayList<Coordinates> valid_positions, int index, Game game, Board board){
        Coordinates build = valid_positions.get(index);
        game.buildTo(build.getX(),build.getY(),4);
    }

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
        // vview.upload(game.getBoardGame());

        /*Salvo la posizione dopo lo spostamento*/
        Coordinates final_position = new Coordinates(worker.getPositionX(),worker.getPositionY());

        /*Controllo se il giocatore ha vinto*/
        boolean win = checkWinCondition(starting_position,final_position,board);
        if (win) {
            game.stopGame(player);
            return 0;
        }

        boolean atlas_build = vview.askDivinityActivation(player,player.getGameCard());

        /*Calcolo le caselle disponibili per la costruzione*/
        ArrayList<Coordinates> build_spots = checkAvailableBuildSpots(player,worker,board);

        /*Mando le caselle al client, ricevo indice della costruzione*/
        index = vview.sendAvailableBuild(player,build_spots);

        /*Costruisco*/
        if(atlas_build) { build(worker, build_spots, index, game, board); }
        else
        { super.build(worker,build_spots,index,game,board);}


        vview.upload(board);
        // vview.upload(game.getBoardGame());

        return 0;
    }
}
