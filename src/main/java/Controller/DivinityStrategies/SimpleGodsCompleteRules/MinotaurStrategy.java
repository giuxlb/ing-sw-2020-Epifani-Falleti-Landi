package Controller.DivinityStrategies.SimpleGodsCompleteRules;

import Controller.Coordinates;
import Controller.DivinityStrategies.DefaultStrategy;
import Controller.TurnStrategy;
import Controller.VirtualView;
import Model.*;

import java.util.ArrayList;

/*Il tuo spostamento: puoi spostarti nella casella occupata da un worker avversario e "spingerlo" nella casella successiva*/
public class MinotaurStrategy extends DefaultStrategy implements TurnStrategy {

    public ArrayList<Coordinates> checkAvailableMoveSpots(Player player, Worker worker, Board board,boolean athenaeffect) {
        int pos_x=worker.getPositionX();
        int pos_y=worker.getPositionY();
        BoardCell[][] boardCopy = board.getBoardGame();
        ArrayList<Coordinates> valid_positions = new ArrayList<Coordinates>();
        for(int i = pos_x-1 ; i <= pos_x+1 ; i++){
            for(int j = pos_y-1 ; j <= pos_y+1 ; j++){
                if(/*Posizione è dentro la scacchiera*/ i>=0 && i<=4 && j>=0 && j<=4) {
                    if(/*non sono nella casella del mio worker*/ !(i==pos_x && j==pos_y) &&
                            /*L'altezza della casella non è superiore di 1 rispetto a quella del worker*/ boardCopy[pos_x][pos_y].getHeight() >= boardCopy[i][j].getHeight()-1 &&
                            /*nella casella non  c'è una cupola*/ boardCopy[i][j].getHeight()<4){
                        if(boardCopy[i][j].getWorkerBuilder()!=null){
                            System.out.println("QUA CI ENTRO");
                            int dest_x;
                            int dest_y;
                            if(pos_x==i){
                                dest_x=i;
                            }else if(i>pos_x){
                                dest_x=i+1;
                            }else{
                                dest_x=i-1;
                            }

                            if(pos_y==j){
                                dest_y=j;
                            }else if(j>pos_y){
                                dest_y=j+1;
                            }else{
                                dest_y=j-1;
                            }
                            System.out.println("dest_x="+dest_x+"dest_y"+dest_y);
                            if (dest_x>=0 && dest_x<=4 && dest_y>=0 && dest_y<=4){
                                if(boardCopy[i][j].getHeight() >= boardCopy[dest_x][dest_y].getHeight()-1 && boardCopy[dest_x][dest_y].getHeight()<4 && boardCopy[dest_x][dest_y].getWorkerBuilder()==null){
                                    System.out.println("ECCOMI");
                                    if(athenaeffect){
                                        if(boardCopy[pos_x][pos_y].getHeight() >= boardCopy[i][j].getHeight()){
                                            valid_positions.add(new Coordinates(i,j));
                                        }
                                    }
                                    else {
                                        valid_positions.add(new Coordinates(i, j));
                                    }
                                }
                            }
                        }
                        else{
                            if(athenaeffect){
                                if(boardCopy[pos_x][pos_y].getHeight() >= boardCopy[i][j].getHeight()){
                                    valid_positions.add(new Coordinates(i,j));
                                }
                            }
                            else {
                                valid_positions.add(new Coordinates(i, j));
                            }
                        }
                    }
                }
            }
        }
        return valid_positions;
    }

    public void move(Worker worker, ArrayList<Coordinates> valid_positions, int index, Game game,Board board){
        //default move
        Coordinates move = valid_positions.get(index);
        if(board.getBoardGame()[move.getX()][move.getY()].getWorkerBuilder()!=null){
            int dest_x;
            int dest_y;
            if(worker.getPositionX()==move.getX()){
                dest_x=move.getX();
            }else if(move.getX()>worker.getPositionX()){
                dest_x=move.getX()+1;
            }else{
                dest_x=move.getX()-1;
            }

            if(worker.getPositionY()==move.getY()){
                dest_y=move.getY();
            }else if(move.getY()>worker.getPositionY()){
                dest_y=move.getY()+1;
            }else{
                dest_y=move.getY()-1;
            }

            game.moveWorker(board.getBoardGame()[move.getX()][move.getY()].getWorkerBuilder(),dest_x,dest_y);
            game.moveWorker(worker,move.getX(),move.getY());
        }else{
            game.moveWorker(worker, move.getX(), move.getY());
        }
    }

}
