package Model;


import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.BeforeClass;
import static org.junit.Assert.assertEquals;


public class GameTest {
/*
    static Game game;

    @BeforeClass
    public static void setUpClass()
    {
        game = new Game();
    }

    @Before

    public void setUp() throws Exception
    {
        game = new Game();
    }

    @AfterEach
    public void tearDown() throws Exception
    {
       game = null;
    }

    @Test
    public void addPlayer_player_addPayertoTheArrayAndIncrementTurnNUmber()
    {
        Player p = new Player("player1");
        game.addPlayer(p);
        assertEquals(game.getPlayers()[0],p);
        assertEquals(game.getTurnNumber(),1);


    }




    @Test
    public void nextTurnNumber_1with2players_0()
    {
        Player p = new Player("player1");
        game.addPlayer(p);

        Player p2 = new Player("player2");
        game.addPlayer(p2);

        game.startGame();
        game.nextTurnNumber();
        game.nextTurnNumber();

        assertEquals(game.getTurnNumber(),0);
    }

    @Test
    public void nextTurnNumber_2with3players_0()
    {
        Player p = new Player("player1");
        game.addPlayer(p);

        Player p2 = new Player("player2");
        game.addPlayer(p2);

        Player p3 = new Player("player3");
        game.addPlayer(p3);
        game.startGame();
        game.nextTurnNumber();
        game.nextTurnNumber();
        game.nextTurnNumber();
        assertEquals(game.getTurnNumber(),0);

    }

    @Test
    public void chooseInitialPosition_playerCoordinatesAndIndex_SetBoardAndCreateWorker()
    {
        Player p = new Player("player1");
        game.addPlayer(p);
        game.chooseInitialPosition(p,1,1,0);

        assertEquals(game.getBoardGame().getBoardGame()[1][1].getWorkerBuilder(),p.getWorker(0));

        game.chooseInitialPosition(p,1,2,1);

        assertEquals(game.getBoardGame().getBoardGame()[1][2].getWorkerBuilder(),p.getWorker(1));


    }
    /*@Test
    public void chooseInitialPosition1_playerAndCoordinates_SetTheBoardAndCreateNewWorker()
    {
        Player p = new Player("player1");
        game.addPlayer(p);
        game.chooseInitialPosition1(p,1,1);

        assertEquals(game.getBoardGame().getBoardGame()[1][1].getWorkerBuilder(),p.getWorker1());
    }

    @Test
    public void chooseInitialPosition2_playerAndCoordinates_SetTheBoardAndCreateNewWorker()
    {
        Player p = new Player("player1");
        game.addPlayer(p);
        game.chooseInitialPosition2(p,1,1);

        assertEquals(game.getBoardGame().getBoardGame()[1][1].getWorkerBuilder(),p.getWorker2());
    }

    @Test
    public void moveWorker_worker_IsInTheCorrectPositionAndThePreviousOneIsFree()
    {
        Player p = new Player("player1");
        game.addPlayer(p);
        game.chooseInitialPosition1(p,1,1);
        game.moveWorker(game.getPlayers()[0].getWorker1(),1,2);
        assertEquals(game.getBoardGame().getBoardGame()[1][1].getWorkerBuilder(),null);
        assertEquals(game.getBoardGame().getBoardGame()[1][2].getWorkerBuilder(),p.getWorker1());

    }

    @Test
    public void buildTo_LevelAndCoordinates_BuiltTheLevelPassedInTheCoordinatesPassed()
    {
        Player p = new Player("player1");
        game.addPlayer(p);
        game.chooseInitialPosition1(p,1,1);
        game.buildTo(1,2,1);
        assertEquals(game.getBoardGame().getBoardGame()[1][2].getHeight(),1);
    }

    @Test
    public void nextTurnPhase_CurrentPhase_NextPhase()
    {
        Player p = new Player("player1");
        game.addPlayer(p);
        game.startGame();
        game.nextTurnPhase();
        assertEquals(game.getTurnPhase(),TurnPhases.BUILD);
        game.nextTurnPhase();
        assertEquals(game.getTurnPhase(),TurnPhases.CHANGE_PLAYER);
        game.nextTurnPhase();
        assertEquals(game.getTurnPhase(),TurnPhases.MOVE);
        assertEquals(game.getTurnNumber(),1);
    }*/
}
