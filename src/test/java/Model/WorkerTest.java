package Model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.*;

class WorkerTest {

    public static Worker worker = null;

    @BeforeAll
    public static void setUp() {
        worker = new Worker(0,0,Color.WHITE);
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void moveTo() {
        worker.moveTo(2,2);
        assertEquals(worker.getPositionX(),2);
        assertEquals(worker.getPositionY(),2);
    }

}