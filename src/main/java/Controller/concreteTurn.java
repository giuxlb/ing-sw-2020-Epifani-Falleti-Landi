package Controller;

import Model.Game;
import Model.Player;
import Model.Board;

import java.util.ArrayList;

public class concreteTurn implements Turn {
    private int posHeight;
    private int workerHeight;
    private ArrayList<Position> validPositions;



    @Override
    public void checkTurnPhase() {
        System.out.println("Questa funzioni controllerà le fasi del turno");
    }

    public boolean checkNeighbourhood(int x, int y, Player p, int worker_index) {
        x=p.getWorker(worker_index).getPositionX();
        y=p.getWorker(worker_index).getPositionY();
        validPositions= new ArrayList<Position>();

        if((x==0 && y==0) || (x==0 && y==4) || (x==4 && y==0) || (x==4 && y==4)){
            if(x==0 && y==0){
                for(int i=x;i<=x+1;i++){
                    for (int j=y;j<=y+1;j++){
                        validPositions.add(new Position(i,j));
                        System.out.print("(" + i + " " + " " + j + ")");
                    }
                    System.out.println("");
                }
            }else if(x==0 && y==4){
                for(int i=x;i<=x+1;i++) {
                    for (int j = -1 + y; j <= y; j++) {
                        validPositions.add(new Position(i,j));
                        System.out.print("(" + i + " " + " " + j + ")");
                    }
                    System.out.println("");
                }
            }else if(x==4 && y==0){
                for(int i=-1+x;i<=x;i++) {
                    for (int j = y; j <= y + 1; j++) {
                        validPositions.add(new Position(i,j));
                        System.out.print("(" + i + " " + " " + j + ")");
                    }
                    System.out.println("");
                }
            }else{
                for(int i=-1+x;i<=x;i++){
                    for (int j=-1+y;j<=y;j++) {
                        validPositions.add(new Position(i,j));
                        System.out.print("(" + i + " " + " " + j + ")");
                    }
                    System.out.println("");
                }
            }
        } else if (x==0 || y==0 || x==4 || y==4){
            if(x==0){
                for(int i=x;i<=x+1;i++){
                    for (int j=-1+y;j<=y+1;j++) {
                        validPositions.add(new Position(i,j));
                        System.out.print("(" + i + " " + " " + j + ")");
                    }
                    System.out.println("");
                }
            }else if(y==0){
                for(int i=-1+x;i<=x+1;i++){
                    for (int j=y;j<=y+1;j++) {
                        validPositions.add(new Position(i,j));
                        System.out.print("(" + i + " " + " " + j + ")");
                    }
                    System.out.println("");
                }
            }else if(x==4){
                for(int i=-1+x;i<=x;i++){
                    for (int j=-1+y;j<=y+1;j++) {
                        validPositions.add(new Position(i,j));
                        System.out.print("(" + i + " " + " " + j + ")");
                    }
                    System.out.println("");
                }
            }else{
                for(int i=-1+x;i<=x+1;i++){
                    for (int j=-1+y;j<=y;j++) {
                        validPositions.add(new Position(i,j));
                        System.out.print("(" + i + " " + " " + j + ")");
                    }
                    System.out.println("");
                }
            }
        }else{
            for(int i=-1+x;i<=x+1;i++){
                for (int j=-1+y;j<=y+1;j++){
                    validPositions.add(new Position(i,j));
                    System.out.print("(" + i + " " + " " + j + ")");
                }
                System.out.println("");
            }
        }

        for (Position pos: validPositions) {
            if (x==pos.getX() && pos.getY()==y) {
                return true;
            }
        }

        System.out.println("Errore: la posizione scelta non è nell'intorno del worker");
        return false;
    }

    @Override
    public void move(int x, int y, Player p, int worker_index, Board boardGame) {
        if(checkNeighbourhood(x,y,p, worker_index)==true){
            this.posHeight=boardGame.getBoardHeight(x,y);
            System.out.println("Altezza della posizione in cui mi voglio spostare: " + posHeight );
            this.workerHeight=boardGame.getBoardHeight(p.getWorker(worker_index).getPositionX(),p.getWorker(worker_index).getPositionY());
            System.out.println("Altezza della posizione in cui mi trovo: " + workerHeight);
            if(posHeight-workerHeight>=-1 && posHeight-workerHeight<=1){
                p.getWorker(worker_index).moveTo(x,y);
                System.out.println("Movimento riuscito!");
            }else{
                System.out.println("Errore: non sei all'altezza giusta per spostarti!");
            }
        }



    }

    @Override
    public void build(int x, int y, Player p, int worker_index, Board boardGame) {
        if (checkNeighbourhood(x,y,p,worker_index)==true){
            if(boardGame.getBoardHeight(x,y)<4){
                boardGame.buildOnBoard(x,y);
            }else{
                System.out.println("Errore: c'è già una cupola, non puo costruire ulteriormente qui!");
            }
        }


    }

}
