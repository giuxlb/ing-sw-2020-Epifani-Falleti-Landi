package Model;

public class Player {
    private String username;
    private Worker[] workers;
    private Card gameCard;
    private Color color;
    private int countforConstructor=0;
    private int countforGetter=0;

    /***
     * Costruttore della classe Player
     * @param name nome da assegnare al giocatore
     */
    public Player (String name){
        this.username = name;
        this.workers = new Worker[2];
    }

    /***
     * @author (to acknoledge some untested methods)
     * Build two worker objects for each player, assign for each worker a position (x,y) on boardGame and his player's color
     * @param x
     * @param y
     */
    public void assignWorker(int x,int y){
        if(countforConstructor<2) {
            this.workers[countforConstructor] = new Worker(x, y, this.color);
            countforConstructor++;
        } else{
            countforConstructor=0;
            this.workers[countforConstructor] = new Worker(x, y, this.color);
        }
    }
    public void assignWorker1(int x_1,int y_1){
        this.workers[0]= new Worker(x_1,y_1,this.color);
    }

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
     * @author Alfredo Landi (to acknoledge some untested methods)
     * Get needed player's worker
     * @return
     */
    public Worker getWorker(){
        if(countforGetter<2) {
            countforGetter++;
            return this.workers[countforGetter];
        } else{
            countforGetter=0;
            return this.workers[countforGetter];
        }
    }
    public Worker getWorker1(){return this.workers[0];}

    public Worker getWorker2(){return this.workers[1];}

}
