package Model;

import java.util.Calendar;

public class Main {
    public static void main(String[] args){
        Player p=new Player("alf", new Calendar.Builder().setCalendarType("gregorian").setFields().build());
        p.chooseCard(Card.APOLLO);
        p.setColor(Color.ANSI_YELLOW);
        System.out.println("Il tuo colore è: " + p.getColor());
    }

}
