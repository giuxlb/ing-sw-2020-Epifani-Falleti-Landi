package Model;

public class Player {
    private String username;
    private Worker[] workers;
    private Card gameCard;

    /***
     * Costruttore della classe Player
     * @param name nome da assegnare al giocatore
     */
    public Player (String name){
        this.username = name;
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
}
