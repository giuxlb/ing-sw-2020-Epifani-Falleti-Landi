package Model;

import Client.View.Data;

import java.util.Calendar;

public class Player {
    private String username;
    private Worker[] workers;
    private String gameCard;
    private Color color;
    private Data birthDate;

    /***
     * Costruttore della classe Player
     * @param name nome da assegnare al giocatore
     */
    public Player (String name,Data birthDate){
        this.username = name;
        this.birthDate = birthDate;
        this.workers = new Worker[2];
    }


    /***
     * Creates and assign a worker to the player
     * @param x pos x
     * @param y pos y
     * @param index index of the worker
     */
    public void assignWorker(int x,int y,int index){this.workers[index]= new Worker(x,y,this.color);}

    /***
     *
     * @param c
     */
    public void setColor(Color c){this.color = c;}

    /***
     * Setter of the card
     * @param card the card to assigng to the Player
     */
    public void chooseCard(String card){
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
     * @author Adriano Falleti
     * Get the attribute username
     * @return
     */
    public String getUsername() {return username; }

    /***
     *
     * @return the card of this player
     */
    public String getGameCard(){return this.gameCard;}

    /***
     *
     * @return the birth date of this player
     */
    public Data getBirthDate(){return this.birthDate;}

    public Color getColor() {
        return color;
    }
}
