
package Model;

public class Game {
    private Player[] players;
    private Board boardGame;
    private int turnNumber;
    private TurnPhases turnPhase;
    private Card[] choosenCards;

    public Game(Board newBoard)
    {
        this.boardGame = newBoard;
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
            return;
        }
        else
        {
            //should control for other players with the same username of p
            players[turnNumber] = p;
            turnNumber++;
        }
    }

    /**
     * It manages the turns of the Game
     */
    public void startGame()
    {



    }

    /**
     *
     */
    public void stopGame()
    {

    }



    public void nextTurnNumber()
    {
        if (players[2] != null)
            turnNumber = (turnNumber+1)%3;
        else
            turnNumber = (turnNumber+1)%2;
    }


    public void nextTurnPhase()
    {
        turnPhase = turnPhase.changeFrom();
        if (turnPhase == TurnPhases.CHANGE_PLAYER)
            this.nextTurnNumber();
    }

    public Worker chooseInitialPosition(int x, int y)
    {
        Worker w = new Worker(x,y);
        boardGame.boardGame[x][y].workerBuilder = w;
        return w;
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
        boardGame.boardGame[w.getx()][w.gety()].workerBuilder = null;
        boardGame.boardGame[x][y].workerBuilder = w;
        w.moveTo(x,y);
    }



    public void buildTo(int level,int x, int y)
    {
        boardGame.boardGame[x][y].buildHere(level);
    }

}