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
    public Player(String name) {
        this.username = name;
        this.workers = new Worker[2];
    }

    /***
     * Setter for i-worker
     * @param i worker index
     * @param x x-position on boardGame
     * @param y y-position on boardGame
     */
    public void setWorker(int i, int x, int y) {
        this.workers[i] = new Worker(x, y, this.color);
    }

    /***
     * Setter of the card
     * @param card the card to assigng to the Player
     */
    public void chooseCard(Card card) {
        this.gameCard = card;
    }

    /***
     * calls the method to move a worker.
     * @param worker the worker to move
     * @param x new position x of the worker
     * @param y new position y of the worker
     */
    public void moveWith(Worker worker, int x, int y) {
        worker.moveTo(x, y);
    }

    /***
     * Getter for i-worker
     * @param i worker index
     * @return
     */
    public Worker getWorker(int i) {
        return this.workers[i];
    }

    /***
     * Getter for the reference of the first worker
     * @return the reference of the first worker
     */

    /***
     *
     * @return
     */
    public Card getGameCard() {
        return this.gameCard;
    }

    /***
     *
     * @param c
     */
    public void setColor(Color c) {
        this.color=c;
        for(int i=0;i<2;i++){
            workers[i].setColor(c);
        }
    }

}