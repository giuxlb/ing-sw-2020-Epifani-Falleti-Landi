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
    private ArrayList<Player> players;
    private VirtualView virtualView;
    public Thread thread;
    private boolean exit;



    /***
     * Costructor of GameControl, it creates a new Game and a new VirtualView,
     * and get the number of player from the first client.
     */
    public GameControl() {
         this.game = new Game();
         this.virtualView = new VirtualView();
         Scanner scanner = new Scanner(System.in);
         System.out.println("Vuoi attivare la UNDO? Y per attivare, N per non attivare");
         if(scanner.nextLine().toUpperCase().equals("Y")){
             this.virtualView.setUndoOn(true);
             System.out.println("UNDO attivata!");
         }
        exit=false;

        players = new ArrayList<Player>();

        //waitForOk();

        int x = virtualView.playerNumber();
        //aspetto che SetupItReady sia true
        while (x <= 1) {
                x = virtualView.playerNumber();
        }
        while(!virtualView.isSetUpisReady()){
            System.out.println("Qui");
            try{
                TimeUnit.MILLISECONDS.sleep(10);}
            catch (InterruptedException e){System.out.println("Interrupted exception");}
        }

        //prendo nome, data, e numero giocatori dal client 0
        int player_number = virtualView.getNumberOfPlayers();

    }

    /***
     * Sends requests to the clients in order to receive the usernames and the dates.
     * Ensures the uniqueness of the username.
     * After adding all the player, send the unsorted array of players to the virtualview and
     * then sorts the array in gamecontrol.
     */
    private void acceptPlayers(){
        int playerNumber = virtualView.getNumberOfPlayers();
        String received_player_name;
        String color_to_send = null;
        Data received_player_date;
        boolean isWrong;

        for(int i = 0; i < playerNumber; i++){
            isWrong = false;
            do {
                received_player_name = virtualView.askForUsername(i, isWrong);
                if (received_player_name == null) {
                    exit = true;
                    return;
                }
                for (int j = 0; j < i; j++) {
                    int count = 0;
                    if (received_player_name.equals(this.players.get(j).getUsername())) {
                        isWrong = true;
                        count++;
                        break;
                    }
                    if(count==0) isWrong = false;
                }
            } while (isWrong);
            received_player_date = virtualView.askForDate(i);
            if(received_player_date == null){exit = true; return;}
            this.addPlayer(new Player(received_player_name,received_player_date));

            switch (i){
                case(0): color_to_send = "yellow";
                break;
                case(1): color_to_send = "white";
                break;
                case(2): color_to_send = "purple";
                break;
            }
            virtualView.sendColor(color_to_send,i);
        }

        virtualView.setPlayers(players);
        this.sortPlayersByAge();
        virtualView.sendNumberOfPlayer();
        virtualView.sendPlayerTurnNumber(this.players);


    }


    /**
     * the standard loop of the game
     */
    private void startGame(){

        if(this.exit) return;

        while(!exit) {
            game.startGame();
            //virtualView.upload(game.getBoardGame());

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
                chosenCards.remove(cardChoice.toUpperCase());
                game.nextTurnNumber();
            }

            cardChoice = virtualView.sendChosenCards(players.get(game.getTurnNumber()), chosenCards);
            if(cardChoice==null){
                System.out.println("STO QUI");
                return;
            }

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


                    virtualView.upload(game.getBoardGame());

                    //rimuovo la posizione scelta dall'array
                    initial_valid_pos.remove(index);


                }
                //chiedo di fare l'undo e se è true devo saltare la game.nextTurnNumber, chiamare deepCopy sulla board del game
                //devo riportare l'array delle initial_valid_pos alla situazione precedente
                /*
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

                 */
                    game.nextTurnNumber();
                    //System.out.println("Non rifaccio il turno");

              //  System.out.println("Rifaccio il turno...");
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
            }
        }
        System.out.println("Server Stopped");
    }

    /***
     * Method that starts the currentPlayer's turn, this
     * method will be inserted in a loop in startGame
     */
    private int startNextTurn(int extra_turn_effect){
        TurnControl turn = new TurnControl(players.get(game.getTurnNumber()),extra_turn_effect,game.getBoardGame(),game,virtualView);
        return turn.start();
    }

    /***
     * Calls chooseInitialPosition from this.game.
     * @param currentPlayer player that is inserting the worker
     * @param x pos x
     * @param y pos y
     * @param index index of the worker
     */
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
    private void sortPlayersByAge(){
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
            partita.acceptPlayers();
            partita.startGame();
            while (!partita.checkNewGame()){
                //System.out.println("IN ATTESA");
            }
        }
    }


    private boolean checkNewGame(){
       return this.virtualView.isStartNewGame();
    }

}


