package Model;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertEquals;

public class WorkerTest {

    private static Worker worker = null;

    @Before
    public void setUp() {
        worker = new Worker(0,0,Color.ANSI_YELLOW);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void moveTo() {
        worker.moveTo(2,2);
        assertEquals(worker.getPositionX(),2);
        assertEquals(worker.getPositionY(),2);
    }

    @Test
    public void setColor(){
        worker.setColor(Color.ANSI_YELLOW);
        assertEquals(worker.getColor(),Color.ANSI_YELLOW);
    }
}