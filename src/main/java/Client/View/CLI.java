//Manca ancora tutta la logica delle carte
package Client.View;

import Controller.Network.VCEvent;
import Model.Board;
import Model.Player;
import Client.Controller.Controller;
import Controller.Coordinates;
import Controller.Network.VCEvent;
import Model.Color;
import Model.Worker;


import java.util.ArrayList;
import java.util.Scanner;

public class CLI {
    public static void main(String[] args){
        System.out.println("Santorini - Epifani Falleti Landi");
        CLI CLI = new CLI();

        //Costruzione dello scanner e del Controlle usati nel main(Una volta finita la CLI posso unificarli con quelli del costruttore di CLI?)
        Scanner mainScanner= new Scanner(System.in);
        Controller mainController = new Controller();

        //Creazione della socket del client
        ClientNetworkHandler cnh = new ClientNetworkHandler();

        //Thread per l'esecuzione principale della CLI
        Thread game = new Thread(cnh);
        game.start();


        /*Quando Adriano ha fatto la modifica in ClientNetworkHandler implenta questo while
        * while(cnh.isIdArrived()==false){
        * //Qui bisogna aggiungere un ritardo per far aspettare il client
        * }
        * */
        CLI.setPlayerID(cnh.getPlayerID());
        if(CLI.getPlayerID()==0) {
            //Aspetta fino a quando non arriva un messaggio
            CLI.waitUpdateView(cnh);

            //Chiede numero giocatori al primo client
            System.out.println("Quanti giocatori giocheranno a questa partita?\nInserire 2 per 2 giocatori, 3 per 3 giocatori");
            int n = mainScanner.nextInt();
            while (mainController.checkNumberOfPlayers(n) == false) {
                System.out.println("Errore, impossibile avviare una partita con questo numero di giocatori, chiedere solo per 2 o 3 giocatori");
                n = mainScanner.nextInt();
            }
            CLI.buildEvent(cnh, n, VCEvent.Event.setup_request);

            //Le pedine del primo player sono gialle
            CLI.setColor(Color.ANSI_GREEN);
        }else if(CLI.getPlayerID()==1){
            //Le pedine del secondo player sono verdi
            CLI.setColor(Color.ANSI_GREEN);
        }else if(CLI.getPlayerID()==2){
            //Le pedine di un EVENTUALE terzo player sono viola
            CLI.setColor(Color.ANSI_PURPLE);
        }


        /*System.out.println("Caricamento in corso...");
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
        s.nextInt();*/



        System.out.println("Partita terminata!\n Il vincitore è -> " /*+getWinner()*/);

    }

    //Attributi grafici e testuali della CLI
    private Controller c;
    private Scanner s;
    private int playerID;
    private Color ansiColor;
    private Board b;
    private String move="dove ti vuoi muovere";
    private String build="dove vuoi costruire";


    //Costruttore della CLI
    public CLI(){
        s= new Scanner(System.in);
        c=new Controller();
        b= new Board();
    }

    //da aggiustare una volta finita l'unificazione con ClientNetworkHandler
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

    //da aggiustare una volta finita l'unificazione con ClientNetworkHandler
    public void paintBoardCell(Board b, ArrayList<Coordinates> validPositions){
        for(int i=0;i<5;i++){
            for(int j=0;j<5;j++){
                for(Coordinates c: validPositions){
                    if(c.getX()==i && c.getY()==j){
                        System.out.print( Color.ANSI_BLUE +"| worker: X" + " height: " + b.getBoardHeight(i,j) + "| ");
                    }
                }
            }
        }
    }

    //da aggiustare una volta finita l'unificazione con ClientNetworkHandler
    public void printWorker(Board b, int i, int j, Color c){
        if(b.getBoardWorker(i,j)==null){
            System.out.print( c +"| worker: X" + " height: " + b.getBoardHeight(i,j) + "| ");
        }else{
            System.out.print( Color.ANSI_BLUE +"| worker: " /*+*/  + " height: " + b.getBoardHeight(i,j) + "| ");
        }
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public void buildEvent(ClientNetworkHandler cnh, Object o, VCEvent.Event command){
        VCEvent currentEvent= new VCEvent(o, command);
        cnh.sendVCEvent(currentEvent);
    }

    public void waitUpdateView(ClientNetworkHandler cnh){
        while(cnh.isUpdateView()==false){
            //lo facciamo addormentare
        }
    }

    public void checkEvent(ClientNetworkHandler cnh){
        while(true){
            waitUpdateView(cnh);
            switch (cnh.getFromServer().getCommand()){
                case username_request :
                    System.out.println("Inserisci il tuo nome utente");
                    String username=s.nextLine();
                    buildEvent(cnh, username, VCEvent.Event.username_request);
                    break;
                /*case wrong_username*/
                /*case Date request*/
                case not_your_turn:
                    Object objectPlayer = cnh.getFromServer().getBox();
                    if(objectPlayer instanceof Player){
                        Player p = (Player)objectPlayer;
                        System.out.println("Partita in corso, sta giocando " + p.getUsername() + " con la carta " + p.getGameCard());
                    }else {
                        System.out.println("Errore, player corrotto!");
                    }
                    break;
                case update:
                    Object objectBoard = cnh.getFromServer().getBox();
                    if(objectBoard instanceof Board){
                        Board b = (Board)objectBoard;
                        printBoard(b);
                        setBoard(b);
                        //Abbiamo bisogno che update venga sempre mandato prima di send_cells_move (Da chiedere a Peppe)
                    }else {
                        System.out.println("Errore, board corrotta!");
                    }
                    break;
                case send_cells_move:
                    Object objectValidPositions = cnh.getFromServer().getBox();
                    ArrayList<Coordinates> validPositions = (ArrayList<Coordinates>)objectValidPositions;
                    paintBoardCell(b, validPositions );
                    //Attenzione qui non possiamo controllare se il dato arrivi corrotto o meno!
                    System.out.println("Selezionare l'i-esima posizione " + getStringMove());
                    s.nextInt();
                    System.out.println("Selezionare la j-esima positione" +get);
                    s.nextInt();
                        break;


            }
        }
    }

    /***
     *
     * @param c
     */
    public void setColor(Color c) {
        this.ansiColor = c;
    }

    /***
     *
     * @return
     */
    public Color getColor() {
        return ansiColor;
    }

    public Board getBoard() {
        return b;
    }

    public void setBoard(Board b) {
        this.b = b;
    }


}