package Controller;

import Client.View.Data;
import Model.*;
import Controller.DivinityStrategies.DefaultStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class DefaultStrategyTest {
    private static Game game;
    private static Player player =null;
    private static DefaultStrategy strategy;

    @Before
    public void setUp(){
        game = new Game();
        player = new Player("Giux",new Data(30,11,1998));
        game.addPlayer(player);
        game.chooseInitialPosition(player,0,0,0);
        strategy = new DefaultStrategy();
    }

    @Test
    public void checkValidMoveSpots_corner(){
        ArrayList<Coordinates> move_spots = strategy.checkAvailableMoveSpots(player,player.getWorker(0),game.getBoardGame(),false);

        System.out.println(move_spots.size());
        for(Coordinates c:move_spots){
            System.out.println(c.toString());
        }
    }

    @Test
    public void moveWorker(){
        for(int i = 0;i<5;i++){
            for(int j = 0;j<5;j++){
                System.out.print("| "+ game.getBoardGame().getBoardGame()[i][j].getWorkerBuilder() +" |");
            }
            System.out.println(" ");
        }

        ArrayList<Coordinates> move_spots = strategy.checkAvailableMoveSpots(player,player.getWorker(0),game.getBoardGame(),false);

        strategy.move(player.getWorker(0),move_spots,0,game,game.getBoardGame());

        System.out.println(" ");

        for(int i = 0;i<5;i++){
            for(int j = 0;j<5;j++){
                System.out.print("| "+ game.getBoardGame().getBoardGame()[i][j].getWorkerBuilder() +" |");
            }
            System.out.println(" ");
        }
    }
}
