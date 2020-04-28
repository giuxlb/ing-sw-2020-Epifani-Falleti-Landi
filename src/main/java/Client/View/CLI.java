//Manca ancora tutta la logica delle carte
package Client.View;

import Model.*;
import Client.Controller.*;

import java.util.Scanner;

public class CLI {
    public static void main(String[] args){
        boolean endGame=false; //It will be modified as soon as clientSocket will be completed
        boolean myTurn=false;//It will be modified as soon as clientSocket will be completed
        int playerID=0; //It will be modified as soon as clientSocket will be completed
        System.out.println("Santorini - Epifani Falleti Landi");
        CLI currentCLI = new CLI();
        Scanner s= new Scanner(System.in);
        Board b= new Board();
        Controller clientController = new Controller();

        //Costruzione oggetto clientNetworkHandler
        ClientNetworkHandler cnh = new ClientNetworkHandler();
        Thread clientNetworkHandlerThread = new Thread(cnh);
        clientNetworkHandlerThread.start();

        System.out.println("Inserisci il tuo nome utente");
        s.nextLine();
        //sendUsername to Server
       /* while (clientController.checkUsername(cnh)==false){ //o va implementata in modo diverso
            System.out.println("Questo username è già stato scelto! Per favore riprovare!");
            s.nextLine();

        }*/
        //getPlayerID from server
        if(playerID==0){
            System.out.println("Quanti giocatori giocheranno a questa partita?\n" +
                    "Inserire 2 per 2 giocatori, 3 per 3 giocatori"); //Controllo deve essere fatto lato client o lato server?
            s.nextInt();
            //sendPlayersNumber to server
        }

        System.out.println("Caricamento in corso...");
        //getBoard() from server
        currentCLI.printBoard(b);
        System.out.println("Dove vuoi inserire il primo worker?");
        //Chi li fa i controlli sulla posizione
        //Controlla la view se il formato che è corretto e il controller se il client non ha preso il posto di un altro?
        System.out.println("Selezionare l'i-esima posizione");
        s.nextInt();
        System.out.println("Selezionare la j-esima positione");
        s.nextInt();
        //Nel mezzo stampo la board?
        System.out.println("Dove vuoi inserire il secondo worker?");
        System.out.println("Selezionare l'i-esima posizione");
        s.nextInt();
        System.out.println("Selezionare la j-esima positione");
        s.nextInt();

        while(!endGame){ //ci sarà la getEndGame della socket
            if(myTurn){// ci sarà la getTurn della socket
                System.out.println("Fase di spostamento");
                System.out.println("Le caselle in cui puoi costruire sono in rosso");
                //cnh.getValidiPositions()
                //paintAvaibleBoardCell(Board b, validPosition)

                System.out.println("Fase di costruzione");
                //Codice per la fase di costruzione
            }
        }

        System.out.println("Partita terminata!\n Il vincitore è -> " /*+getWinner()*/);

    }



    public void printBoard(Board b){
        for(int i=0;i<5;i++){
            for(int j=0;j<5;j++)  {
                if(b.getBoardWorker(i,j)==null){
                    System.out.print( Color.ANSI_BLUE +"| worker: X" + " height: " + b.getBoardHeight(i,j) + "| ");
                }else{

                }
            }
            System.out.println();
        }
    }

    public void initialSettlemet(Controller c, int index){
        int pos;

        Scanner is= new Scanner(System.in);
        System.out.println("Selezionare l'i-esima posizione del " + index+1 + "°" +" worker");
        pos=is.nextInt();
        while(!c.checkLimits(pos)){
            System.out.println("Errore, gli indici delle posizione vanno da solo da 0 a 4, perfavore reinserire!");
            pos=is.nextInt();
        }
    }
}