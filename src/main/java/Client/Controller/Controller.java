//Controllo delle carte
//Controllo della data in formato stringa
package Client.Controller;

import Client.View.Data;
import Controller.Coordinates;
import java.util.ArrayList;

public class Controller {

    /***
     *
     * @param choice
     * @return
     */
    public boolean checkLimits(int choice){
        if(choice>=0 && choice<=4){
            return true;
        }else{
            return false;
        }
    }

    /***
     *
     * @param n
     * @return
     */
    public boolean checkNumberOfPlayers(int n){
        if(n==2 || n==3){
            return true;
        }

        return false;
    }

    /***
     *
     * @param validPositions
     * @param requestedPosition
     * @return
     */
    public boolean checkRequestedPosition(ArrayList<Coordinates> validPositions, Coordinates requestedPosition){
        for(Coordinates c:validPositions){
            if(c.getX() == requestedPosition.getX() && c.getY() == requestedPosition.getY()){
                return true;
            }
        }

        return false;
    }

    //ATTENZIONE: questo metodo non provvede al caso dell'inserimento di una stringa da parte dell'utente, ci penserà l'opportuna eccezione (da implementare)

    /***
     *
     * @param d
     * @return
     */
    public boolean controllaData(Data d){
        if(d.getGiorno()<=0 || d.getGiorno()>=31){
            System.out.println("Errore! Giorno non valido");
            return false;
        }

        if(d.getAnno()<=1921 || d.getAnno()>=2012){
            System.out.println("Errore! Anno non valido");
            return false;
        }

        if((d.getMese()==4 || d.getMese()==6 || d.getMese()==9 || d.getMese()==11) && d.getGiorno()>30){
            System.out.println("Errore! Il mese " + d.getMese() + " non può avere più di 30 giorni");
            return false;
        }else if((d.getMese()==1 || d.getMese()==3 || d.getMese()==5 || d.getMese()==7 || d.getMese()==8 || d.getMese()==10 || d.getMese()==12) && d.getGiorno()>31){
            System.out.println("Errore! Il mese " + d.getMese() + " non può avere più di 31 giorni");
            return false;
        }else if(d.getMese()==2 && isLeapYear(d.getAnno())==false && d.getGiorno()>28) {
            System.out.println("Errore! Il mese " + d.getMese() + " non può avere più di 28 giorni");
            return false;
        }else if(d.getMese()==2 && isLeapYear(d.getAnno())==true && d.getGiorno()>29){
            System.out.println("Errore! Il mese " + d.getMese() + " non può avere più di 29 giorni");
            return false;
        }else if (d.getMese()<=0 || d.getMese()>12){
            System.out.println("Errore! Mese non valido");
            return false;
        }

        return true;
    }

    /***
     * Check if a year is leap or not
     * @param year to check if it's a leap year or not
     * @return true if param year represents a leap year or false if param year doesn't represent a leap year
     */
    public boolean isLeapYear(int year){
        if(year%400==0){
            return true;
        }else {
            if (year % 4 == 0 && year% 100 != 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    /***
     *
     * @param insertion
     * @param gods
     * @return
     */
    public boolean isInArrayListOfGods(String insertion, ArrayList<String> gods){
        for(String god: gods){
            if(insertion.equals(god)){
                return true;
            }
        }

        return false;
    }

    public boolean isCardCorrect(String card, ArrayList<String> gods){
        boolean isCorrect = false;
        for(String p : gods){
            if (p.toUpperCase().equals(card.toUpperCase())) {
                isCorrect = true;
                break;
            }
        }
        return isCorrect;
    }


}