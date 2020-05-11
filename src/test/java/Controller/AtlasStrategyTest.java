package Controller;

import Client.View.Data;
import Controller.DivinityStrategies.DefaultStrategy;
import Model.Game;
import Model.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AtlasStrategyTest {
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
        context.selectStrategy("ATLAS");

    }

    @Test
    public void turnTest(){
        int effect = context.turn(player,player.getWorker(0),game.getBoardGame(),game,0,virtualView);
        assertEquals(player.getWorker(0),game.getBoardGame().getBoardWorker(0,1));
        assertEquals(4,game.getBoardGame().getBoardHeight(0,0));
    }
}
