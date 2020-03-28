package Model;

public class Board {
    private final static int DIM=5;
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
                this.boardGame[i][j].setHeight(0);
                this.boardGame[i][j].setWorker(null);
            }
        }
    }

}