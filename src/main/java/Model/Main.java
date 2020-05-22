package Model;
import Client.View.CLI;
import Controller.Coordinates;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Calendar;

public class Main {
    public static void main(String[] args){
        /*Player p=new Player("alf", new Calendar.Builder().setCalendarType("gregorian").setFields().build());
        p.chooseCard(Card.APOLLO);
        p.setColor(Color.ANSI_YELLOW);
        System.out.println("Il tuo colore è: " + p.getColor());*/

        /*//Prova del motore grafico della CLI
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
        prova.paintBoardCell(boardGame,c);*/

        String myGod = "Athena";
        Object[] choices = {"Yes", "No"};
        int choice = JOptionPane.showOptionDialog(null,
                "Do you want to use " + myGod + "'s power ?",
                "Card activation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, //Ricordati di mettere l'icona
                choices,
                choices[1]);

        if(turnChoiceIntoCorrectOutput(choice)==0){
           System.out.println("Non hai attivato l'effetto di " + myGod);
        }else if(turnChoiceIntoCorrectOutput(choice)==1){
            System.out.println("Hai attivato l'effetto di " + myGod);
        }else if(turnChoiceIntoCorrectOutput(choice) == -1){
            System.out.println("Errore di comversione " + myGod);
        }
        else{
            System.out.println("Errore nella funzione");
        }
    }

    private static int turnChoiceIntoCorrectOutput(int choice){
        if(choice==0){
            return 1;
        }else if(choice==1){
            return 0;
        }else if(choice==-1){
            return 0;
        }else{
            return -1;
        }
    }

}
