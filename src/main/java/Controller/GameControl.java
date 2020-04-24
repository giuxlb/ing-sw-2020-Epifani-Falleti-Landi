package Controller;

import Model.Game;
import Model.Player;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GameControl {
    private Game game;
    private Socket[] sockets;
    private ArrayList<Player> players;
    private boolean athenaEffectTurn;
    private VirtualView virtualView;


    /***
     * Costructor of GameControl, it creates a new Game and,
     * after receiving the names from the clients, it creates and inserts the player
     */
    public GameControl(){
        Game game = new Game();
        players = new ArrayList<Player>();

        //ricevo dal client nome 1 e data
        String player_name_0 = virtualView.askForUsername(0);
        Calendar player_date = virtualView.askForDate(0);
        this.addPlayer(new Player(player_name_0,player_date));
        boolean flag = false;
        String player_name_1;

        //continuo a chiedere il nome al secondo giocatore finchè non è diverso dal primo
        while (!flag) {
            //ricevo dal client nome 2
            player_name_1 = virtualView.askForUsername(1);
            if(!player_name_1.equals(player_name_0)){ flag=true;}

        }
        player_date = virtualView.askForDate(1);
        this.addPlayer(new Player(player_name_1,player_date));

        //riordino i giocatori in base all'età
        this.sortPlayersByAge();

    }

    public void startGame(){
        //chiedo pos iniz al player1
        //ricevo pos iniz player1

        while(true){


            startNextTurn();
            game.nextTurnNumber();
        }


    }

    /***
     * Method that starts the currentPlayer's turn, this
     * method will be inserted in a loop in startGame
     */
    public void startNextTurn(){
        TurnControl turn = new TurnControl(players.get(game.getTurnNumber()),athenaEffectTurn,game.getBoardGame(),game);
        turn.start();
    }

    /***
     * Check if it is possible to insert a worker on the position (x,y)
     * @param x pos x
     * @param y pos y
     * @return true if it is possible
     */
    private boolean checkValidInitialPosition(int x, int y){
        if(x>4 || x<0 || y>4 || y<0) return false;

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

    public void win(Player player){
        virtualView.sendWinMessage(player);
        game.win(player);
    }

    public void lose(Player player){
        virtualView.sendLoseMessage(player);
        game.lose(player);
    }

    /***
     * Adds a player in both the model and the controller
     * @param player the player to be added
     */
    public void addPlayer(Player player){
        this.players.add(player);
        this.game.addPlayer(player);
    }


    /***
     * Sorts the Arraylist of players in the Controller by age, the younger first
     */
    public void sortPlayersByAge(){
        //TODO da estendere per 3 giocatori

        if(this.players.get(0).getBirthDate().compareTo(this.players.get(1).getBirthDate())<0){
            Player copy = this.players.remove(0);
            this.players.set(0,this.players.get(1));
            this.players.set(1,copy);
        }
    }
}
