package Controller;

import Client.View.Data;
import Model.*;
import Controller.DivinityStrategies.DefaultStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

public class DefaultStrategyTest {
    private static Game game;
    private static Player player =null;
    private static DefaultStrategy strategy;
    private static DivinityContext context;
    private static VirtualView virtualView;

    @Before
    public void setUp(){
        game = new Game();
        virtualView = new VirtualViewTesting();
        player = new Player("Giux",new Data(30,11,1998));
        game.addPlayer(player);
        game.chooseInitialPosition(player,0,0,0);
        strategy = new DefaultStrategy();
        context = new DivinityContext();
        context.selectStrategy("");

    }

    @Test
    public void checkValidMoveSpots_corners(){
        ArrayList<Coordinates> move_spots = context.checkAvailableMoveSpots(player,player.getWorker(0),game.getBoardGame(),false);
        assertEquals(3,move_spots.size());
        game.moveWorker(player.getWorker(0),0,4);
        move_spots = context.checkAvailableMoveSpots(player,player.getWorker(0),game.getBoardGame(),false);
        assertEquals(3,move_spots.size());
        game.moveWorker(player.getWorker(0),4,0);
        move_spots = context.checkAvailableMoveSpots(player,player.getWorker(0),game.getBoardGame(),false);
        assertEquals(3,move_spots.size());
        game.moveWorker(player.getWorker(0),4,4);
        move_spots = context.checkAvailableMoveSpots(player,player.getWorker(0),game.getBoardGame(),false);
        assertEquals(3,move_spots.size());
    }




    @Test
    public void moveWorkerTest() {
        ArrayList<Coordinates> move_spots = context.checkAvailableMoveSpots(player, player.getWorker(0), game.getBoardGame(), false);
        assertEquals(player.getWorker(0),game.getBoardGame().getBoardGame()[0][0].getWorkerBuilder());

        context.move(player.getWorker(0),move_spots,0,game,game.getBoardGame());
        assertEquals(player.getWorker(0),game.getBoardGame().getBoardGame()[0][1].getWorkerBuilder());

    }

    @Test
    public void buildTest(){
        ArrayList<Coordinates> build_spots = context.checkAvailableBuildSpots(player,player.getWorker(0),game.getBoardGame());
        assertEquals( 0,game.getBoardGame().getBoardGame()[0][1].getHeight());

        context.build(player.getWorker(0),build_spots,0,game,game.getBoardGame());
        assertEquals(1,game.getBoardGame().getBoardGame()[0][1].getHeight());
    }

    @Test
    public void checkValidMoveSpots_Athena(){
        game.buildTo(1,0,1);
        game.buildTo(0,1,1);
        game.buildTo(1,1,1);
        ArrayList<Coordinates> move_spots = context.checkAvailableMoveSpots(player,player.getWorker(0),game.getBoardGame(),true);
        assertEquals(0,move_spots.size());
    }

    @Test
    public void checkWinCondition_Test(){
        game.buildTo(0,0,2);
        game.buildTo(0,1,3);
        game.moveWorker(player.getWorker(0),0,1);
        assertTrue(context.checkWinCondition(new Coordinates(0, 0), new Coordinates(0, 1), game.getBoardGame()));

    }

    @Test
    public void turnTest(){
        int effect = context.turn(player,player.getWorker(0),game.getBoardGame(),game,0,virtualView);
        assertEquals(0,effect);

        assertEquals(player.getWorker(0),game.getBoardGame().getBoardWorker(0,1));
        assertEquals(1,game.getBoardGame().getBoardHeight(0,0));
    }
}
