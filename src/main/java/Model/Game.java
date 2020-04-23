package Model;

import java.util.Observer;

public class Game {
    private Player[] players;
    private Board boardGame;
    private int turnNumber;
    private TurnPhases turnPhase;
    private Card[] choosenCards;

    public Game()
    {
        this.boardGame = new Board();
    }

    /**
     * It adds new players to the game. If turnNumber is equal to 0, it instantiates the array of players and add one to it.
     * If turnNumber is 1 or 2, it simply adds a player to the array. If turnNumber is greater than 2, it stops, because Game can
     * have maximum 3 players
     * @param p is the player it has to add to the array of player
     */
    public void addPlayer(Player p)
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
    }

    /**
     * It manages the turns of a Game
     */
    public void startGame()
    {
        turnNumber = 0;
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
    public void nextTurnNumber()
    {
        if (players[2] != null)
            turnNumber = (turnNumber+1)%3;
        else
            turnNumber = (turnNumber+1)%2;
    }

    /**
     * it changes the phase of the turn of a player
     */
    public void nextTurnPhase()
    {
        turnPhase = turnPhase.changeFrom();
        if (turnPhase == TurnPhases.CHANGE_PLAYER)
            this.nextTurnNumber();

    }

    /***
     * Create, assign and places a worker on the board
     * @param player the player that places the worker
     * @param x pos x
     * @param y pos y
     * @param index the worker index
     */
    public void chooseInitialPosition(Player player,int x,int y,int index){
        player.assignWorker(x,y,index);
        boardGame.setBoardWorker(x,y,player.getWorker(index));
    }

    /**
     * It removes the worker from the BoardCell previously occupied by that worker and assign it in the new BoardCell
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

    /***
     * Just assign a worker to a cell, without removing the reference of the previous spot
     * Utility function to be used in strategies like Apollo
     * @param w the worker moved
     * @param x pos x
     * @param y pos y
     */
    public void moveWorkerWithoutNull(Worker w, int x, int y){
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

    /***
     * Does a +1 on the height of the cell
     * @param x pos x
     * @param y pos y
     */
    public void buildUp(int x, int y){boardGame.buildOnBoard(x,y);}

    /**
     *
     * @return the turnNUmber
     */
    public int getTurnNumber() {
        return turnNumber;
    }

    /**
     *
     * @return the players of the game
     */
    public Player[] getPlayers() {
        return players;
    }

    public Board getBoardGame() {
        return boardGame;
    }

    public TurnPhases getTurnPhase() {
        return turnPhase;
    }

    public static void main(String[] args){


    }
}
