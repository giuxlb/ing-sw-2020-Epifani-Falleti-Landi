package Model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Board implements Serializable {
    public final static int DIM=5;
    private static final long serialVersionUID = 1216730102358212421L;
    private BoardCell[][] boardGame;


    /***
     * Constructor for this class
     */
    public Board(){
        this.boardGame= new BoardCell[DIM][DIM];

        for(int i=0;i<DIM;i++){
            for(int j=0;j<DIM;j++){
                this.boardGame[i][j]= new BoardCell();
            }
        }
    }

    /***
     * Reset boardGame to initial conditions
     */
    public void reset(){
        for(int i=0;i<DIM;i++){
            for(int j=0;j<DIM;j++){
                this.boardGame[i][j].setBoardCellHeight(0);
                this.boardGame[i][j].setBoardCellWorker(null);
            }
        }
    }

    /***
     * Call setBoardCellHeight to modify the cell's height in (i,j) on board
     * @param i i-position of needed boardCell
     * @param j j-position of needed boardCell
     * @param height new height value of boardCell height
     */
    public void setBoardHeight(int i, int j, int height){
        this.boardGame[i][j].setBoardCellHeight(height);
    }

    /***
     * Build a new level on boardCell (i,j)
     * @param i i-position of needed boardCell
     * @param j j-position of needed boardCell
     */
    public void buildOnBoard(int i, int j){
        boardGame[i][j].buildOnBoarCell();
    }

    /***
     * Put a worker on boardCell (i,j)
     * @param i i-position of needed boardCell
     * @param j j-position of needed boardCell
     * @param w worker reference for needed worker
     */
    public void setBoardWorker(int i, int j, Worker w){
        this.boardGame[i][j].setBoardCellWorker(w);
    }

    /***
     * Get reference of boardGame
     * @return reference of boardGame
     */
    public BoardCell[][] getBoardGame() {
        return boardGame;
    }

    /***
     * Get height from boardCell (i,j)
     * @param i i-position of needed boardCell
     * @param j j-position of needed boardCell
     * @return height of boardCell(i,j)
     */
    public int getBoardHeight(int i, int j) {
        return this.boardGame[i][j].getHeight();
    }

    /***
     * Get worker from boardCell (i,j)
     * @param i i-position of needed boardCell
     * @param j j-position of needed boardCell
     * @return pointer to needed worker
     */
    public Worker getBoardWorker(int i, int j){
        return this.boardGame[i][j].getWorkerBuilder();
    }

    public void deepCopy(Board b)
    {
        int counter = 0;
        for (int i = 0; i <5 ; i++) {
            for (int j = 0; j < 5; j++) {
                this.boardGame[i][j].setBoardCellHeight(0);
                this.boardGame[i][j].setBoardCellHeight(b.getBoardGame()[i][j].getHeight());
                this.boardGame[i][j].setBoardCellWorker(null);
                if (b.getBoardGame()[i][j].getWorkerBuilder() != null) {

                    this.boardGame[i][j].setBoardCellWorker(new Worker(i,j,b.getBoardGame()[i][j].getWorkerBuilder().getColor()));
                }
                else {
                    this.boardGame[i][j].setBoardCellWorker(null);
                }
                counter++;
            }
        }
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