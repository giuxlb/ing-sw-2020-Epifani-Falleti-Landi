package Controller;

import Client.View.Data;
import Controller.DivinityStrategies.DefaultStrategy;
import Model.Game;
import Model.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ApolloStrategyTest {
    private static Game game;
    private static Player player = null;
    private static Player player_1 = null;
    private static DefaultStrategy strategy;
    private static DivinityContext context;

    @Before
    public void setUp(){
        game = new Game();
        player = new Player("Giux",new Data(30,11,1998));
        game.addPlayer(player);
        game.chooseInitialPosition(player,0,0,0);
        strategy = new DefaultStrategy();
        context = new DivinityContext();
        context.selectStrategy("APOLLO");
        player_1 = new Player("Adrii",new Data(30, 7,1998));
        game.addPlayer(player_1);
        game.chooseInitialPosition(player_1,0,1,0);
    }

    @Test
    public void moveWithApolloSwap_Test(){
        ArrayList<Coordinates> move_spots = context.checkAvailableMoveSpots(player,player.getWorker(0),game.getBoardGame(),false);
        assertEquals(3,move_spots.size());

        assertEquals(player.getWorker(0),game.getBoardGame().getBoardWorker(0,0));
        assertEquals(player_1.getWorker(0),game.getBoardGame().getBoardWorker(0,1));

        context.move(player.getWorker(0),move_spots,0,game,game.getBoardGame());
        assertEquals(player.getWorker(0),game.getBoardGame().getBoardWorker(0,1));
        assertEquals(player_1.getWorker(0),game.getBoardGame().getBoardWorker(0,0));
    }

    @Test
    public void moveWithApolloAthena_Test(){
        game.buildTo(1,1,1);
        ArrayList<Coordinates> move_spots = context.checkAvailableMoveSpots(player,player.getWorker(0),game.getBoardGame(),true);
        assertEquals(2,move_spots.size());

        context.move(player.getWorker(0),move_spots,1,game,game.getBoardGame());
        assertEquals(player.getWorker(0),game.getBoardGame().getBoardWorker(1,0));
    }
}
