package Controller;

import Client.View.Data;
import Model.Game;
import Model.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TurnControlTest {
    Player player;
    Game game;
    TurnControl turnControl;
    VirtualView virtualView;

    @Before
    public void setUp(){
        game = new Game();
        player = new Player("giux", new Data(30, 11, 1998));
        player.chooseCard("");
        game.chooseInitialPosition(player,0,0,0);
        game.chooseInitialPosition(player,4,4,1);
        virtualView = new VirtualViewTesting();

        turnControl = new TurnControl(player,0,game.getBoardGame(),game,virtualView);

    }

    @Test
    public void startTest(){
        int effect = turnControl.start();
        assertEquals(0,effect);

        assertEquals(player.getWorker(0),game.getBoardGame().getBoardWorker(0,1));
        assertEquals(1,game.getBoardGame().getBoardHeight(0,0));
    }
}
