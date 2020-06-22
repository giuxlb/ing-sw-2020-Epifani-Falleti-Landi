package Model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class BoardCell implements Serializable {

    private static final long serialVersionUID = 5796216832741310505L;
    private int height;
    private Worker workerBuilder;




    /***
     * Put a worker on this BoardCell
     * @param workerBuilder the worker to set
     */
    public void setBoardCellWorker(Worker workerBuilder){
        this.workerBuilder=workerBuilder;
    }

    /***
     * Set height of current BoardCell to 0 (0 floor), 1 (1st floor), 2 (2nd floor), 3 (3rd floor) or 4 (dome)
     * @param height the value of height to set
     */
    public void setBoardCellHeight(int height){
        this.height=height;
    }

    /***
     * Increments the cell height
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
     * @return the worker on this cell
     */
    public Worker getWorkerBuilder(){
        return this.workerBuilder;
    }

    /***
     * @return the height of this cell
     */
    public int getHeight() {
        return height;
    }

    public void readObject(ObjectInputStream stream) throws IOException,ClassNotFoundException
    {
        stream.defaultReadObject();
    }

    public void writeObject(ObjectOutputStream stream) throws IOException
    {
        stream.defaultWriteObject();
    }

}
