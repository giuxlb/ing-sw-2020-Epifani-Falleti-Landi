package Model;

public class BoardCell {
    private int height;
    private Worker workerBuilder;

    /***
     * Put a worker on this BoardCell
     * @param workerBuilder
     */
    public void setWorker(Worker workerBuilder){
        this.workerBuilder=workerBuilder;
    }

    /***
     * Set height of current BoardCell to 0 (0 floor), 1 (1st floor), 2 (2nd floor), 3 (3rd floor) or 4 (dome)
     * @param height
     */
    public void setHeight(int height){
        this.height=height;
    }

    /***
     * Constructor for this class
     */
    public BoardCell(){
        this.height=0;
        this.workerBuilder=null;
    }

}
