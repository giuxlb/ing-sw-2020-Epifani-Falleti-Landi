package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    private ArrayList<Player> players;
    private Board boardGame;
    //private int turnNumber;
    private TurnPhases turnPhase;
    private Card[] choosenCards;

    public Game()
    {
        players=new ArrayList<Player>();
        this.boardGame = new Board();
    }


    /*public void addPlayer(Player p)
    {
        if (turnNumber == 0)
        {
            players = new Player[3];
            players[turnNumber] = p;
            turnNumber++;
        }
        else if (turnNumber > 2)
        {
            turnNumber = 0;
        }
        else
        {
            //should control for other players with the same username of p
            players[turnNumber] = p;
            turnNumber++;

        }
    }*/

    /***
     *
     * @param name
     */
    public void addPlayer(String name){
        players.add(new Player(name));
    }

    /**
     * It manages the turns of a Game
     */
    public void startGame()
    {
        //turnNumber = 0;
        turnPhase = TurnPhases.MOVE;


    }

    /**
     * it manages the end of a game
     */
    public void stopGame()
    {

    }

    /**
     * It changes the turnNumber, in order to allow the next player to make his moves
     */
    /*public void nextTurnNumber()
    {
        if (players[2] != null)
            turnNumber = (turnNumber+1)%3;
        else
            turnNumber = (turnNumber+1)%2;
    }*/

    /**
     * it changes the phase of the turn of a player
     */
    /*public void nextTurnPhase()
    {
        turnPhase = turnPhase.changeFrom();
        if (turnPhase == TurnPhases.CHANGE_PLAYER)
            this.nextTurnNumber();

    }*/


    /***
     * Choose an initail position for each player's worker
     * @param player
     * @param i worker index
     * @param x x-position on boardGame
     * @param y y-position on boardGame
     */
    public void chooseInitialPosition(Player player, int i, int x, int y){
        player.setWorker(i, x, y);
        boardGame.setBoardWorker(x,y, player.getWorker(i));
    }

    /**
     * It removes the worker from the BoardCell previously occupied by that worker and creates a new worker in the new BoardCell
     * where the player moved the worker
     * @param w is the worker moved
     * @param x is the x coordinate of the new position
     * @param y is the y coordinate of the new position
     */
    public void moveWorker(Worker w, int x, int y)
    {
        boardGame.setBoardWorker(w.getPositionX(),w.getPositionY(),null);
        boardGame.setBoardWorker(x,y,w);
        w.moveTo(x,y);
    }

    /**
     *
     * @param x x coordinate of the position where player decided to built
     * @param y  y coordinate of the position where player decided to built
     * @param level level built
     */

    public void buildTo(int x, int y,int level)
    {
        boardGame.setBoardHeight(x,y,level);
    }

    /**
     *
     * @return the turnNUmber
     */
    /*public int getTurnNumber() {
        return turnNumber;
    }*/

    public Player getPlayer(int i){
        return this.players.get(i);
    }
    /**
     *
     * @return the players of the game
     */
    public List<Player> getPlayers() {

        return Collections.unmodifiableList(new ArrayList<>(players));
    }

    /***
     *
     * @return
     */
    public Board getBoardGame() {
        return boardGame;
    }

    /***
     *
     * @return
     */
    public TurnPhases getTurnPhase() {
        return turnPhase;
    }
}
