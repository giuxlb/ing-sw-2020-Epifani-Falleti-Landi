package Controller;

import Model.Game;
import Model.Player;

import java.net.Socket;
import java.util.ArrayList;

public class GameControl {
    private Game game;
    private TurnControl turnControl;
    private Socket[] sockets;
    private Player[] players;
    private int currentPlayer;
    private boolean athenaEffectTurn;


    /***
     * Costructor of GameControl, it creates a new Game and,
     * after receiving the names from the clients, it creates and inserts the player
     */
    public GameControl(){
        Game game = new Game();
        players = new Player[3];

        //ricevo dal client nome 1
        //players[0] = new Player(socket.getPlayerName());
        game.addPlayer(players[0]);

        //ricevo dal client nome 2
        //players[1] = new Player(socket.getPlayerName());
        game.addPlayer(players[1]);

        currentPlayer=0;


    }

    public void startGame(){
        //chiedo pos iniz al player1
        //ricevo pos iniz player1


    }

    /***
     * Method that starts the currentPlayer's turn, this
     * method will be inserted in a loop in startGame
     */
    public void startNextTurn(){
        TurnControl turn = new TurnControl(players[currentPlayer],athenaEffectTurn);
    }

    /***
     * Check if it is possible to insert a worker on the position (x,y)
     * @param x pos x
     * @param y pos y
     * @return true if it is possible
     */
    private boolean checkValidInitialPosition(int x, int y){
        //TODO controllo parametri dentro board

        Player[] players = this.game.getPlayers();

        for ( Player p : players)
        {
            for(int i=0;i<2;i++){
                if(p.getWorker(i)!=null) {
                    if (p.getWorker(i).getPositionX() == x && p.getWorker(i).getPositionY() == y) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private void insertInitialPosition(int currentPlayer, int x, int y){

    }

}
