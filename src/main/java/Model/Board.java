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
                this.boardGame[i][j].setBoardCellHeight(0);
                this.boardGame[i][j].setBoardCellWorker(null);
            }
        }
    }

    /***
     * Call setBoardCellHeight to modify the cell's height in (i,j) on board
     * @param i
     * @param j
     * @param height
     */
    public void setBoardHeight(int i, int j, int height){
        this.boardGame[i][j].setBoardCellHeight(height);
    }

    /***
     * Call setBoardCellWorker to modify the cell's worker in (i,j) on board
     * @param i
     * @param j
     * @param w
     */
    public void setBoardWorker(int i, int j, Worker w){
        this.boardGame[i][j].setBoardCellWorker(w);
    }

    /***
     * For each BoardCell of our Board print height and if there is a worker on
     */
    public void printBoard(){
        for(int i=0;i<DIM;i++){
            for(int j=0;j<DIM;j++){
                System.out.print(" "+this.boardGame[i][j].getHeight()+" ");
                if(this.boardGame[i][j].getWorkerBuilder()!=null){
                    System.out.print("x");
                }
            }
            System.out.println();
        }
    }
}