package Model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BoardTest {

    private static Board board = null;

    @Before
    public void setUp(){
        board = new Board();
    }

    @Test
    public void build(){
        board.buildOnBoard(0,0);
        assertEquals(board.getBoardHeight(0,0),1);
        board.setBoardHeight(0,0,4);
        assertEquals(board.getBoardHeight(0,0),4);
    }

    @Test
    public void clearBoard(){
        Worker worker = new Worker(1,1,Color.ANSI_YELLOW);
        board.setBoardHeight(0,0,3);
        board.setBoardWorker(0,0,worker);
        board.reset();
        assertEquals(board.getBoardHeight(0,0),0);
        assertEquals(board.getBoardWorker(0,0),null);
    }
}
