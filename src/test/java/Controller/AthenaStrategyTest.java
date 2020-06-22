package Controller;

import Client.View.Data;
import Controller.DivinityStrategies.DefaultStrategy;
import Model.Game;
import Model.Player;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class AthenaStrategyTest {
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
        player.chooseCard("ATHENA");
        game.addPlayer(player);
        game.chooseInitialPosition(player,0,0,0);
        strategy = new DefaultStrategy();
        context = new DivinityContext();
        context.selectStrategy("ATHENA");
    }

    @Test
    public void turnTest(){
        int effect = context.turn(player,player.getWorker(0),game.getBoardGame(),game,0,virtualView);
        assertEquals(player.getWorker(0),game.getBoardGame().getBoardWorker(0,1));
        assertEquals(1,game.getBoardGame().getBoardHeight(0,0));
    }

    @Test
    public void turnTest_giveEffect(){
        game.buildTo(0,1,1);
        int effect = context.turn(player,player.getWorker(0),game.getBoardGame(),game,0,virtualView);
        assertEquals(1,effect);
    }
}
