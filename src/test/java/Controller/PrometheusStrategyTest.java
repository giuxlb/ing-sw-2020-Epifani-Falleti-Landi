package Controller;

import Client.View.Data;
import Controller.DivinityStrategies.DefaultStrategy;
import Model.Game;
import Model.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PrometheusStrategyTest {
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
        player.chooseCard("PROMETHEUS");
        game.addPlayer(player);
        game.chooseInitialPosition(player,0,0,0);
        strategy = new DefaultStrategy();
        context = new DivinityContext();
        context.selectStrategy("PROMETHEUS");
    }

    @Test
    public void turnTest(){
        context.turn(player,player.getWorker(0),game.getBoardGame(),game,0,virtualView);
        assertEquals(player.getWorker(0),game.getBoardGame().getBoardWorker(1,0));
        assertEquals(1,game.getBoardGame().getBoardHeight(0,0));
        assertEquals(1,game.getBoardGame().getBoardHeight(0,1));

    }
}
