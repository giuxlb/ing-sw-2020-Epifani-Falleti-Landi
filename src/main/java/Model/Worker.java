package Model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Worker implements Serializable {

    private static final long serialVersionUID = 7797464638609326461L;
    private Color color;
    private int pos_x;
    private int pos_y;


    /***
     * Constructor of the Worker object
     * @param initial_x the position x on the board
     * @param initial_y the position y on the board
     * @param color the color of the worker
     */
    public Worker(int initial_x,int initial_y,Color color){
        this.color=color;
        this.pos_x=initial_x;
        this.pos_y=initial_y;
    }

    /***
     * Setter of the positions.Doesn't check if the new position is legal.
     * @param x the new x position
     * @param y the nex y position
     */
    public void moveTo(int x,int y){
        this.pos_x=x;
        this.pos_y=y;
    }

    /***
     *
     * @return position X of the Worker
     */
    public int getPositionX(){
        return this.pos_x;
    }

    /***
     *
     * @return position Y of the worker
     */
    public int getPositionY(){
        return this.pos_y;
    }

    /***
     *
     * @param c set the color
     */
    public void setColor(Color c){
        this.color=c;
    }

    public Color getColor(){return this.color;}

    public void readObject(ObjectInputStream stream) throws IOException,ClassNotFoundException
    {
        stream.defaultReadObject();
    }

    public void writeObject(ObjectOutputStream stream) throws IOException
    {
        stream.defaultWriteObject();
    }

}
