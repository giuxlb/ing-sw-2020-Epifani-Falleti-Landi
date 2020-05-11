package Controller;

import Client.View.Data;
import Model.*;

import java.net.Socket;
import java.util.*;
import java.util.concurrent.TimeUnit;

/***
 * @author Giuseppe Epifani
 */

public class GameControl {
    private Game game;
    private Socket[] sockets;
    private ArrayList<Player> players;
    private boolean athenaEffectTurn;
    private VirtualView virtualView;
    public Thread thread;
    private boolean exit;



    /***
     * Costructor of GameControl, it creates a new Game and,
     * after receiving the names from the clients, it creates and inserts the player
     */
    public GameControl() {
         this.game = new Game();
         this.virtualView = new VirtualView();
         //this.virtualView.setUndoOn(true);
        exit=false;

        players = new ArrayList<Player>();

        //waitForOk();

        int x = virtualView.playerNumber();
        //aspetto che SetupItReady sia true
        while (x <= 1) {
                x = virtualView.playerNumber();
        }
        while(virtualView.isSetUpisReady() == false){
            System.out.println("Qui");
            try{
                TimeUnit.MILLISECONDS.sleep(10);}
            catch (InterruptedException e){System.out.println("Interrupted exception");};
        }

        //prendo nome, data, e numero giocatori dal client 0
        int player_number = virtualView.getNumberOfPlayers();


        String player_name_0 = this.virtualView.askForUsername(0,false);
        if(player_name_0==null){
            exit=true;
            return;
        }
        Data player_date_0 = virtualView.askForDate(0);
        if(player_date_0==null){
            exit=true;
            return;
        }
        System.out.println(player_date_0.toString());

        //aggiungo il player 0

        this.addPlayer( new Player(player_name_0,player_date_0));

        boolean flag = false;
        String player_name_1 = null;
        virtualView.sendColor("yellow",0);
       // waitForOk();
        //continuo a chiedere il nome al secondo giocatore finchè non è diverso dal primo
        boolean first_time = true;
        while (!flag) {
            //ricevo dal client nome 2
            player_name_1 = virtualView.askForUsername(1,!first_time);
            if(player_name_1==null){
                exit=true;
                return;
            }
            if(!player_name_1.equals(player_name_0)){ flag=true;}
            first_time = false;

        }

        Data player_date_1 = virtualView.askForDate(1);
        if(player_date_1==null){
            exit=true;
            return;
        }
        System.out.println(player_date_1.toString());
        this.addPlayer(new Player(player_name_1,player_date_1));
        virtualView.sendColor("green",1);
        //se il numero di player è 3, chiedo i dati del terzo giocatore
        if(player_number==3){
           // waitForOk();
            flag=false;
            String player_name_2 = null;

            first_time = true;
            while (!flag){
                player_name_2 = virtualView.askForUsername(2,!first_time);
                if(player_name_2==null){
                    exit=true;
                    return;
                }
                if(!player_name_2.equals(player_name_1) && !player_name_2.equals(player_name_0)){flag=true;}
                first_time = false;
            }
            Data player_date_2 = virtualView.askForDate(2);
            if(player_date_2==null){
                exit=true;
                return;
            }
            this.addPlayer(new Player(player_name_2,player_date_2));
            virtualView.sendColor("purple",2);
        }

        //mando alla virtualview l'array dei player non ordinato
        virtualView.setPlayers(players);
        System.out.println("Array della virtual view");
        for (int i = 0; i < player_number; i++) {
            System.out.println(virtualView.getPlayers().get(i).getBirthDate().toString());
        }
        System.out.println("Array del game control");
        for (int i = 0; i < player_number; i++) {
            System.out.println(players.get(i).getBirthDate().toString());
        }
        //riordino i giocatori in base all'età
        this.sortPlayersByAge();
        System.out.println("Array della virtual view");
        for (int i = 0; i < player_number; i++) {
            System.out.println(virtualView.getPlayers().get(i).getBirthDate().toString());
        }
        System.out.println("Array del game control post ordinamento");
        for (int i = 0; i < player_number; i++) {
            System.out.println(players.get(i).getBirthDate().toString());
        }
        virtualView.sendNumberOfPlayer();

    }


    public void startGame(){

        if(this.exit==true) return;

        while(!exit) {
            game.startGame();
            virtualView.upload(game.getBoardGame());

            //scelta delle carte del primo player
            System.out.println(players.get(game.getTurnNumber()).getBirthDate().toString());
            ArrayList<String> allCards = game.getAvailableCards();
            //rimuovo le carte non censentite nelle partite con 3 giocatori
            if(players.size()==3){
                allCards.remove("CHRONUS");
            }
            ArrayList<String> chosenCards = virtualView.sendAllCards(players.get(game.getTurnNumber()), allCards);
            if(chosenCards==null) return;
            game.setChosenCards(chosenCards);
            game.nextTurnNumber();


            String cardChoice;

            //mando array delle carte scelte dal primo player al secondo player
            while (game.getTurnNumber() != 0) {
                cardChoice = virtualView.sendChosenCards(players.get(game.getTurnNumber()), chosenCards);
                if (cardChoice==null) return;
                players.get(game.getTurnNumber()).chooseCard(cardChoice);
                chosenCards.remove(cardChoice);
                game.nextTurnNumber();
            }

            cardChoice = virtualView.sendChosenCards(players.get(game.getTurnNumber()), chosenCards);
            if(cardChoice==null) return;

            //setto l'ultima carta rimasta al primo player
            players.get(game.getTurnNumber()).chooseCard(cardChoice);

            //creo e riempio una lista con tutte le posizioni valide
            ArrayList<Coordinates> initial_valid_pos = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    initial_valid_pos.add(new Coordinates(i, j));
                }
            }
            int index;
            //chiedo a tutti i player le posizioni iniziali
            boolean reDoTurn;
            do {
                reDoTurn = false;
                Board copy = new Board();
                copy.deepCopy(game.getBoardGame());
                ArrayList<Coordinates> copyCoordinates = new ArrayList<>();
                for (int i = 0; i < initial_valid_pos.size(); i++) {
                    copyCoordinates.add(initial_valid_pos.get(i));
                }
                for (int worker_index = 0; worker_index < 2; worker_index++) {

                    //mando al current player la lista delle posizioni valide e ricevo l'indice della posizione scelta
                    index = virtualView.sendAvailableMove(players.get(game.getTurnNumber()), initial_valid_pos);
                    if(index==-1) return;

                    //inserisco il worker nella la posizione scelta

                    insertInitialPosition(game.getTurnNumber(), initial_valid_pos.get(index).getX(), initial_valid_pos.get(index).getY(), worker_index);

                    System.out.println(game.getBoardGame().getBoardWorker(1, 1));

                    System.out.println(game.getBoardGame().getBoardHeight(1, 1));

                    virtualView.upload(game.getBoardGame());

                    System.out.println(game.getBoardGame().getBoardWorker(1, 1));
                    //rimuovo la posizione scelta dall'array
                    initial_valid_pos.remove(index);


                }
                //chiedo di fare l'undo e se è true devo saltare la game.nextTurnNumber, chiamare deepCopy sulla board del game
                //devo riportare l'array delle initial_valid_pos alla situazione precedente
                if (this.virtualView.isUndoOn())
                {
                    int response = virtualView.sendUndoRequest(players.get(game.getTurnNumber()));
                    if (response == 1)
                    {
                        reDoTurn = true;

                        game.setBoardGame(copy); //ripristino la board del game alla situazione precedente al turno
                        for (int i = 0; i <initial_valid_pos.size() ; i++) {
                            initial_valid_pos.remove(i); //tolgo tutte le posizione valide
                        }
                        for (int i = 0; i <copyCoordinates.size() ; i++) { //inserisco tutte le posizioni valide prima del turno
                            initial_valid_pos.add(copyCoordinates.get(i));
                        }

                        System.out.println("Sto per andare a rifare il turno");
                    }
                    else if (response == 0)
                    {
                        game.nextTurnNumber();
                        virtualView.upload(game.getBoardGame());
                    }
                    else{ // se response è -1
                        System.out.println("Finisco la partita");
                        return;
                    }
                }
                else {
                    game.nextTurnNumber();
                    System.out.println("Non rifaccio il turno");
                }
                System.out.println("Rifaccio il turno...");
            } while (game.getTurnNumber() != 0 || reDoTurn) ;


            //loop della partita:
            //next_turn_effect è il valore di ritorno di startNextTurn, e sta ad indicare che nel ciclo di turno successivo ci sarà un effetto extra (tipo Athena)
            //extra_turn_effect è il parametro passato a startNextTurn, che indica se e quali effetti extra ci saranno in  questo turno
            //effect_duration indica per quanti turni l'effetto si applica, viene settato al numero di giocatori-1 quando next_turn_effect indica che c'è un effetto
            int next_turn_effect = 0;
            int extra_turn_effect = 0;
            int effect_duration = 0;
            while (!game.isGameStopped()) {
                if (effect_duration > 0) {
                    next_turn_effect = startNextTurn(extra_turn_effect);
                    effect_duration--;
                } else {
                    next_turn_effect = startNextTurn(0);
                }
                if(next_turn_effect == -1){
                    return;
                }

                if (game.getLastLostPlayer() == null) {
                    game.nextTurnNumber();
                } else {
                    players.remove(game.getLastLostPlayer());
                    virtualView.youLost(game.getLastLostPlayer(), true, null);
                    game.clearLastLostPlayer();
                }

                if (next_turn_effect != 0) {
                    extra_turn_effect = next_turn_effect;
                    effect_duration = players.size() - 1;
                }
            }

            Player winner = game.getWinner();

            if (winner != null) {
                //manda il messaggio ai giocatori con scritto il vincitore
                virtualView.youWon(winner);
                exit = true;
                //manda a tutti i giocatori non winner il messaggio di perdita
                for (Player p : players) {
                    if (!p.getUsername().equals(winner.getUsername())) {
                        virtualView.youLost(p, false, winner);
                    }
                }
            } else {
                //virtualView.playerHasDisconnected();
            }
        }
        System.out.println("Server Stopped");
    }

    /***
     * Method that starts the currentPlayer's turn, this
     * method will be inserted in a loop in startGame
     */
    public int startNextTurn(int extra_turn_effect){
        TurnControl turn = new TurnControl(players.get(game.getTurnNumber()),extra_turn_effect,game.getBoardGame(),game,virtualView);
        return turn.start();
    }

    /***
     * Check if it is possible to insert a worker on the position (x,y)
     * @param x pos x
     * @param y pos y
     * @return true if it is possible
     */
    private boolean checkValidInitialPosition(int x, int y){
        if(x>4 || x<0 || y>4 || y<0) return false;

        Player[] players = this.game.getPlayers();

        for ( Player p : players)
        {
            for(int i=0;i<2;i++){
                if(p.getWorker(i)!=null) {
                    if (p.getWorker(i).getPositionX() == x && p.getWorker(i).getPositionY() == y) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private void insertInitialPosition(int currentPlayer, int x, int y, int index){
        game.chooseInitialPosition(players.get(currentPlayer),x,y,index);
    }

    /***
     * Adds a player in both the model and the controller
     * @param player the player to be added
     */
    public void addPlayer(Player player){
        this.players.add(player);
        this.game.addPlayer(player);
    }


    /***
     * Sorts the Arraylist of players in the Controller by age, the younger first
     */
    public void sortPlayersByAge(){
        if(this.players.size()==3){
            if(!this.players.get(0).getBirthDate().isGreaterthan(this.players.get(1).getBirthDate())){
                Collections.swap(players,0,1);
            }
            if (!this.players.get(1).getBirthDate().isGreaterthan(this.players.get(2).getBirthDate())){
                Collections.swap(players,1,2);
            }
            if (!this.players.get(0).getBirthDate().isGreaterthan(this.players.get(1).getBirthDate())){
                Collections.swap(players,0,1);
            }
        }
        else {
            if (!this.players.get(0).getBirthDate().isGreaterthan(this.players.get(1).getBirthDate())) {
                Collections.swap(players,0,1);
            }
        }
    }

    public static void main(String[] args){
        GameControl partita;

        while (true) {

            partita = new GameControl();
            partita.startGame();
            while (!partita.checkNewGame()){
                //System.out.println("IN ATTESA");
            }
        }
    }


    public boolean checkNewGame(){
       return this.virtualView.isStartNewGame();
    }

    public void closeAll(){
        this.virtualView.closeAll();
    }
}


