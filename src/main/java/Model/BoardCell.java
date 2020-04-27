package Model;

public class BoardCell {
    private int height;
    private Worker workerBuilder;

    /***
     * Put a worker on this BoardCell
     * @param workerBuilder is a pointer to a worker object
     */
    public void setBoardCellWorker(Worker workerBuilder){
        this.workerBuilder=workerBuilder;
    }

    /***
     * Set height of current BoardCell to 0 (0 floor), 1 (1st floor), 2 (2nd floor), 3 (3rd floor) or 4 (dome)
     * @param height represents height of boardCell
     */
    public void setBoardCellHeight(int height){
        this.height=height;
    }

    /***
     * It builds one more level on boardCell
     */
    public void buildOnBoarCell(){
        this.height++;
    }

    /***
     * Constructor for this class
     */
    public BoardCell(){
        this.height=0;
        this.workerBuilder=null;
    }

    /***
     * Get worker on BoardCell
     * @return pointer to worker on boardCell
     */
    public Worker getWorkerBuilder(){
        return this.workerBuilder;
    }

    /***
     * Get height of boardCell
     * @return height of boarCell
     */
    public int getHeight() {
        return height;
    }
}
