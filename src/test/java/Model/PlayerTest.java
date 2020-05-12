
package Model;

import Client.View.Data;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

public class PlayerTest {

    private static Player player = null;

    @Before
    public void setUp() {
        player = new Player("Giux", new Data(30,11,1998));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void setCard(){

        player.chooseCard("ARTEMIS");
        assertEquals("ARTEMIS",player.getGameCard());
    }


    @Test
    public void setWorkers(){
        player.assignWorker(0,0,0);
        player.assignWorker(4,4,1);
        assertEquals(player.getWorker(0).getPositionX(),0);
        assertEquals(player.getWorker(0).getPositionY(),0);
        assertEquals(player.getWorker(1).getPositionX(),4);
        assertEquals(player.getWorker(1).getPositionY(),4);
    }

    @Test
    public void moveWorker(){
        player.assignWorker(0,0,0);
        player.assignWorker(4,4,1);
        player.moveWith(player.getWorker(0),1,1);
        player.moveWith(player.getWorker(1),2,2);
        assertEquals(player.getWorker(0).getPositionX(),1);
        assertEquals(player.getWorker(0).getPositionY(),1);
        assertEquals(player.getWorker(1).getPositionX(),2);
        assertEquals(player.getWorker(1).getPositionY(),2);
    }
}
