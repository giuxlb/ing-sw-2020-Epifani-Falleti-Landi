package Controller;

import Model.Game;
import Model.Card;
import Model.Player;
import Model.Board;

public class Prova
{
    public static void main(String[] args)
    {
        Game g= new Game();
        //socket.recv();
        g.addPlayer("Alfredo");
        Player p= g.getPlayer(0);
        g.chooseInitialPosition(p, 0,0, 0);
        g.chooseInitialPosition(p, 1, 2,2);
        concreteTurn t= new concreteTurn();
        t.move(1,1, p,0,g.getBoardGame());
        Atena newTurn1=new Atena(t);
        Apollo newTurn2=new Apollo(newTurn1);
        //Ricordati di controllare che quando un worker si sposta e/o costruisce non ce ne sia un altro in quel posto
        //Ricordati di controllare che non mi posso spostare dove c'è una cupola
        newTurn2.move(3,2, p,1, g.getBoardGame());
        //in newTurn1 il worker 0 che dopo essersi spostato sta in (1, 1) prova a spostarsi fuori dal suo intorno
        newTurn1.move(1,0, p, 0, g.getBoardGame());
    }

}
