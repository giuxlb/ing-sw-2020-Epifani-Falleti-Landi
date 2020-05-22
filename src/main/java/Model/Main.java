package Model;
import Client.View.CLI;
import Controller.Coordinates;

import java.util.ArrayList;
import java.util.Calendar;

public class Main {
    public static void main(String[] args){
        /*Player p=new Player("alf", new Calendar.Builder().setCalendarType("gregorian").setFields().build());
        p.chooseCard(Card.APOLLO);
        p.setColor(Color.ANSI_YELLOW);
        System.out.println("Il tuo colore è: " + p.getColor());*/

        //Prova del motore grafico della CLI
        /*
        Board boardGame= new Board();
        boardGame.setBoardHeight(0,0, 4);
        Worker w= new Worker(1,1, Color.ANSI_YELLOW);
        boardGame.setBoardWorker(1,1, w);
        CLI prova = new CLI();
        prova.printBoard(boardGame);
        ArrayList<Coordinates> c = new ArrayList<Coordinates>();
        Coordinates c1=new Coordinates(0,0);
        Coordinates c2=new Coordinates(0,1);
        Coordinates c3 = new Coordinates(1,0);
        Coordinates c4 = new Coordinates(1,1);
        c.add(c1);
        c.add(c2);
        c.add(c3);
        c.add(c4);
        System.out.println();
        prova.paintBoardCell(boardGame,c);

         */

       // System.out.println(Color.ANSI_GREEN+"hello"+ Color.ANSI_PURPLE+"world"+Color.ANSI_YELLOW+"ciao");
    }

}
