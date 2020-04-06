/*package Model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerTest {

    private static Player player = null;

    @Before
    public void setUp() throws Exception {
        player = new Player("Giux");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void setCard(){
        Card card = new Card("Artemis");
        player.chooseCard(card);
        assertEquals(card,player.getGameCard());
    }

    @Test
    public void setWorkers(){
        player.assignWorker1(0,0);
        player.assignWorker2(4,4);
        assertEquals(player.getWorker1().getPositionX(),0);
        assertEquals(player.getWorker1().getPositionY(),0);
        assertEquals(player.getWorker2().getPositionX(),4);
        assertEquals(player.getWorker2().getPositionY(),4);
    }

    @Test
    public void moveWorker(){
        player.assignWorker1(0,0);
        player.assignWorker2(4,4);
        player.moveWith(player.getWorker1(),1,1);
        player.moveWith(player.getWorker2(),2,2);
        assertEquals(player.getWorker1().getPositionX(),1);
        assertEquals(player.getWorker1().getPositionY(),1);
        assertEquals(player.getWorker2().getPositionX(),2);
        assertEquals(player.getWorker2().getPositionY(),2);
    }
}*/