package Controller;

import Client.View.Data;
import Controller.DivinityStrategies.DefaultStrategy;
import Model.Game;
import Model.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AresStrategyTest {
    private static Game game;
    private static Player player = null;
    private static Player player_1 = null;
    private static DefaultStrategy strategy;
    private static DivinityContext context;
    private static VirtualView virtualView;

    @Before
    public void setUp(){
        game = new Game();
        virtualView = new VirtualViewTesting();
        player = new Player("Giux",new Data(30,11,1998));
        player.chooseCard("ARES");
        game.addPlayer(player);
        game.chooseInitialPosition(player,0,0,0);
        game.chooseInitialPosition(player,4,4,1);
        game.buildTo(3,3,1);
        strategy = new DefaultStrategy();
        context = new DivinityContext();
        context.selectStrategy("ARES");

    }

    @Test
    public void turnTest(){
        context.turn(player,player.getWorker(0),game.getBoardGame(),game,0,virtualView);
        assertEquals(player.getWorker(0),game.getBoardGame().getBoardWorker(0,1));
        assertEquals(1,game.getBoardGame().getBoardHeight(0,0));
        assertEquals(0,game.getBoardGame().getBoardHeight(3,3));
    }
}
