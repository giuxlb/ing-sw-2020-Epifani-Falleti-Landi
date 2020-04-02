package Model;

public class Player {
    private String username;
    private Worker[] workers;
    private Card gameCard;
    private Color color;

    /***
     * Costruttore della classe Player
     * @param name nome da assegnare al giocatore
     */
    public Player (String name){
        this.username = name;
        this.workers = new Worker[2];
    }

    /***
     * Creates and set the initial position of the first worker
     * @param x_1 x of the initial position
     * @param y_1 y of the initial position
     */
    public void assignWorker1(int x_1,int y_1){
        this.workers[0]= new Worker(x_1,y_1,this.color);
    }

    /***
     * Creates and set the initial position of the second worker
     * @param x_2 x of the initial position
     * @param y_2 y of the initial position
     */
    public void assignWorker2(int x_2,int y_2){
        this.workers[1]= new Worker(x_2,y_2,this.color);
    }

    /***
     * Setter of the card
     * @param card the card to assigng to the Player
     */
    public void chooseCard(Card card){
        this.gameCard=card;
    }

    /***
     * calls the method to move a worker.
     * @param worker the worker to move
     * @param x new position x of the worker
     * @param y new position y of the worker
     */
    public void moveWith(Worker worker,int x, int y){
        worker.moveTo(x,y);
    }

    /***
     * @author Alfredo Landi (to acknoledge some undebugged methods)
     * Get needed player's worker
     * @return
     */
    public Worker getWorker(int p){
        return this.workers[p];
    }

    /***
     * Getter for the reference of the first worker
     * @return the reference of the first worker
     */
    public Worker getWorker1(){return this.workers[0];}

    /***
     * Getter for the reference of the second worker
     * @return the reference of the second worker
     */
    public Worker getWorker2(){return this.workers[1];}

    /***
     *
     * @return the card of this player
     */
    public Card getGameCard(){return this.gameCard;}
}
