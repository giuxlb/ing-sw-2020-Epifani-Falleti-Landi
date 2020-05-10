package Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Game {
    private Player[] players;
    private Board boardGame;
    private int turnNumber;
    private TurnPhases turnPhase;
    private ArrayList<String> availableCards;
    private ArrayList<String> chosenCards;
    private boolean gameStopped;
    private Player winner;
    private Player lastLostPlayer;


    public Game()
    {
        this.boardGame = new Board();
        this.gameStopped = false;
        this.winner = null;
        this.lastLostPlayer = null;

        //aggiungo tutte le carte implementate ad un array
        availableCards = new ArrayList<String>();
        for(Card carta : Card.values()){
            availableCards.add(carta.toString());
        }
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
        if(turnNumber==0){
            p.setColor(Color.ANSI_YELLOW);
        }else if(turnNumber==1){
            p.setColor(Color.ANSI_GREEN);
        }else if(turnNumber==2){
            p.setColor(Color.ANSI_PURPLE);
        }
    }

    /**
     * It manages the turns of a Game
     */
    public void startGame()
    {
        gameStopped = false;
        turnNumber = 0;
    }

    /**
     * it manages the end of a game
     */
    public void stopGame(Player p)
    {
        gameStopped = true;
        if(p!=null){
            winner = p;
        }
        boardGame.reset();
        chosenCards.clear();
        players[0]=null;
        players[1]=null;
        players[2]=null;
        turnNumber = 0;

    }

    public void playerLose(Player p){
        lastLostPlayer=p;

        boardGame.setBoardWorker(p.getWorker(0).getPositionX(),p.getWorker(0).getPositionY(),null);
        boardGame.setBoardWorker(p.getWorker(1).getPositionX(),p.getWorker(1).getPositionY(),null);


        if(p.equals(players[2])){
            players[2]=null;
        }
        else if(p.equals(players[1])){
            players[1]=players[2];
            players[2]=null;
        }
        else if(p.equals(players[0])){
            players[0]=players[2];
            players[2]=null;
        }

        if(turnNumber == 2){
            turnNumber = 0;
        }

        //controlla il numero di giocatori rimasti
        int count = 0;
        Player winner = null;
        for(int i = 0; i<3 ; i++){
            if(players[i]!=null){
                count++;
                winner=players[i];
            }
        }
        //se è rimasto un solo giocatore, si ferma il gioco e lo setta come winner
        if(count==1){
            gameStopped = true;
            this.winner = winner;
        }
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


    public void setBoardGame(Board boardGame) {
        this.boardGame = boardGame;
    }

    public List<Board> getBoardGameImmutable()
    {
        ArrayList<Board> board = new ArrayList<Board>();
        board.add(boardGame);
        return Collections.unmodifiableList(board);
    }

    public TurnPhases getTurnPhase() {
        return turnPhase;
    }

    public void setChosenCards(ArrayList<String> chosenCards) {
        this.chosenCards = chosenCards;
    }

    public ArrayList<String> getAvailableCards() {
        return availableCards;
    }

    public boolean isGameStopped() {return gameStopped;}

    public Player getWinner() {return winner;}

    public Player getLastLostPlayer() {return lastLostPlayer;}

    public void clearLastLostPlayer() {lastLostPlayer = null;}
}
