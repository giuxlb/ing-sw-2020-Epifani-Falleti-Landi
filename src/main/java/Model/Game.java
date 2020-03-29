package Model;

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
     * It manages the turns of a Game
     */
    public void startGame()
    {



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

    /**
     * It instantiates a new worker for the object player calling the method Player.assignWorker and uploads tha state
     * of the board
     * @param player is the player who made the first move with his first worker
     * @param x_1 x coordinate of the initial position of the worker
     * @param y_1 y coordinate of the initial position of the worker
     */

    public void chooseInitialPosition1(Player player,int x_1, int y_1)
    {
        player.assignWorker1(x_1,y_1);
        boardGame.setBoardWorker(x_1,y_1,player.getWorker1());
    }

    /**
     * It instantiates a new worker for the object player calling the method Player.assignWorker and uploads tha state
     * of the board
     * @param player is the player who made the first move with his second worker
     * @param x_2 x coordinate of the initial position of the worker
     * @param y_2 y coordinate of the initial position of the worker
     */
    public void chooseInitialPosition2(Player player,int x_2, int y_2)
    {
        player.assignWorker2(x_2,y_2);
        boardGame.setBoardWorker(x_2,y_2,player.getWorker2());
    }

    /***
     * @author Alfredo Landi (to acknoledge some undebugged methods)
     * For each player's worker assign an initial position (x,y) on boardGame
     * @param player
     * @param x
     * @param y
     */
    public void chooseInitialPosition(Player player, int x, int y){
        player.assignWorker(x,y);
        boardGame.setBoardWorker(x,y, player.getWorker());
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



    public void buildTo(int x, int y,int level)
    {
        boardGame.setBoardHeight(x,y,level);
    }


    /*public static void main(String[] args){
        Game game = new Game();
        Player player1 = new Player("Giux");
        Player player2 = new Player("Alf");
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.chooseInitialPosition1(player1,1,1);
        game.chooseInitialPosition2(player1,3,3);
        game.chooseInitialPosition1(player2,4,4);
        game.chooseInitialPosition2(player2,3,4);
        game.boardGame.printBoard();
        System.out.println("   ");
        game.moveWorker(player1.getWorker1(),0,0);
        game.boardGame.printBoard();
        game.buildTo(1,0,1);
        System.out.println("   ");
        game.boardGame.printBoard();
    }*/
}
