package Controller;

import Model.Game;
import Model.Player;

import java.net.Socket;

public class GameControl {
    private Game game;
    private TurnControl turnControl;
    private Socket socket;

    public GameControl(){
        Game game = new Game();
        //ricevo dal client nome 1
        //ricevo dal client nome 2
        game.addPlayer(player1);
        game.addPlayer(player2);
        //ricevo dal client le posizioni iniziali del player1

    }

    public void startGame(){
        this.socket = null;
        //ricevo pos iniz player1

    }

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

    private void insertInitialPosition(Player player, int x, int y){

    }

}
