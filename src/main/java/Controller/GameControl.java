package Controller;

import Model.Card;
import Model.Game;
import Model.Player;

import java.net.Socket;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GameControl {
    private Game game;
    private Socket[] sockets;
    private ArrayList<Player> players;
    private boolean athenaEffectTurn;
    private VirtualView virtualView;


    /***
     * Costructor of GameControl, it creates a new Game and,
     * after receiving the names from the clients, it creates and inserts the player
     */
    public GameControl() {
        Game game = new Game();
        VirtualView virtualview = new VirtualView();
        players = new ArrayList<Player>();

        //aspetto che SetupItReady sia true

        while(!virtualView.isSetUpisReady()){
            try{
                TimeUnit.MILLISECONDS.sleep(10);}
            catch (InterruptedException e){System.out.println("Interrupted exception");};
        }

        //prendo nome, data, e numero giocatori dal client 0
        int player_number = virtualView.getNumberOfPlayers();


        String player_name_0 = virtualview.askForUsername(0,false);
        Calendar player_date_0 = virtualView.askForDate(0);

        //aggiungo il player 0
        this.addPlayer(new Player(player_name_0,player_date_0));

        boolean flag = false;
        String player_name_1 = null;


        //continuo a chiedere il nome al secondo giocatore finchè non è diverso dal primo
        boolean first_time = true;
        while (!flag) {
            //ricevo dal client nome 2
            player_name_1 = virtualView.askForUsername(1,!first_time);
            if(!player_name_1.equals(player_name_0)){ flag=true;}
            first_time = false;

        }
        Calendar player_date_1 = virtualView.askForDate(1);
        this.addPlayer(new Player(player_name_1,player_date_1));

        //se il numero di player è 3, chiedo i dati del terzo giocatore
        if(player_number==3){
            flag=false;
            String player_name_2 = null;

            first_time = true;
            while (!flag){
                player_name_2 = virtualView.askForUsername(2,!first_time);
                if(!player_name_2.equals(player_name_1) && !player_name_2.equals(player_name_0)){flag=true;}
                first_time = false;
            }
            Calendar player_date_2 = virtualView.askForDate(2);
            this.addPlayer(new Player(player_name_2,player_date_2));
        }

        //mando alla virtualview l'array dei player non ordinato
        virtualView.setPlayers(players);

        //riordino i giocatori in base all'età
        this.sortPlayersByAge();

    }

//TODO creare test per classi in model e controller ove possibile
//TODO gestione dello scollegamento di un client

    public void startGame(){
        game.startGame();

        //scelta delle carte del primo player

        ArrayList<Card> chosenCards = virtualView.sendAllCards(players.get(game.getTurnNumber()),game.getAvailableCards());
        game.setChosenCards(chosenCards);
        game.nextTurnNumber();



        Card cardChoice;

        //mando array delle carte scelte dal primo player al secondo player
        while(game.getTurnNumber()!=0) {
            cardChoice = virtualView.sendChosenCards(players.get(game.getTurnNumber()), chosenCards);
            players.get(game.getTurnNumber()).chooseCard(cardChoice);
            chosenCards.remove(cardChoice);
            game.nextTurnNumber();
        }

        //setto l'ultima carta rimasta al primo player
        players.get(game.getTurnNumber()).chooseCard(chosenCards.get(0));

        //mando a tutti i player le loro carte
        do{
            virtualView.sendYourCard(players.get(game.getTurnNumber()),players.get(game.getTurnNumber()).getGameCard());
            game.nextTurnNumber();
        }while (game.getTurnNumber()!=0);

        //chiedo a tutti i player le posizioni iniziali
        do{
            //creo e riempio una lista con tutte le posizioni valide
            ArrayList<Coordinates> initial_valid_pos = new ArrayList<>();
            for(int i = 0;i < 5;i++){
                for(int j = 0;j < 5;j++){
                    initial_valid_pos.add(new Coordinates(i,j));
                }
            }
            int index;
            for(int worker_index = 0;worker_index<2;worker_index++) {

                //mando al current player la lista delle posizioni valide e rcevo l'indice della posizione scelta
                index = virtualView.sendAvailableMove(players.get(game.getTurnNumber()),initial_valid_pos);

                //inserisco il worker nella la posizione scelta
                insertInitialPosition(game.getTurnNumber(),initial_valid_pos.get(index).getX(),initial_valid_pos.get(index).getY(),worker_index);

                //rimuovo la posizione scelta dall'array
                initial_valid_pos.remove(index);


            }

            game.nextTurnNumber();
        }while(game.getTurnNumber()!=0);


        //loop della partita:
        //next_turn_effect è il valore di ritorno di startNextTurn, e sta ad indicare che nel ciclo di turno successivo ci sarà un effetto extra (tipo Athena)
        //extra_turn_effect è il parametro passato a startNextTurn, che indica se e quali effetti extra ci saranno in  questo turno
        //effect_duration indica per quanti turni l'effetto si applica, viene settato al numero di giocatori-1 quando next_turn_effect indica che c'è un effetto
        int next_turn_effect = 0;
        int extra_turn_effect = 0;
        int effect_duration = 0;
        while(!game.isGameStopped()) {
            if (effect_duration > 0) {
                next_turn_effect = startNextTurn(extra_turn_effect);
                effect_duration--;
            } else {
                next_turn_effect = startNextTurn(0);
            }

            if(game.getLastLostPlayer()==null){
                game.nextTurnNumber();
            }
            else{
                players.remove(game.getLastLostPlayer());
                virtualView.sendLostMessage(game.getLastLostPlayer());
                game.clearLastLostPlayer();
            }

            if (next_turn_effect != 0) {
                extra_turn_effect = next_turn_effect;
                effect_duration = players.size() - 1;
            }
        }

        Player winner = game.getWinner();

        if(winner!=null){
            //manda il messaggio ai giocatori con scritto il vincitore
            virtualView.sendWinMessage(winner);
        }
        else{
            virtualView.sendInterruptedMessage();
        }
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

    /*public void win(Player player){
        virtualView.sendWinMessage(player);
        game.win(player);
    }*/

    /*public void lose(Player player){
        virtualView.sendLoseMessage(player);
        game.lose(player);
    }*/

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
            if(this.players.get(0).getBirthDate().compareTo(this.players.get(1).getBirthDate()) < 0){
                Collections.swap(players,0,1);
            }
            if (this.players.get(1).getBirthDate().compareTo(this.players.get(2).getBirthDate()) < 0){
                Collections.swap(players,1,2);
            }
            if (this.players.get(0).getBirthDate().compareTo(this.players.get(1).getBirthDate()) < 0){
                Collections.swap(players,0,1);
            }
        }
        else {
            if (this.players.get(0).getBirthDate().compareTo(this.players.get(1).getBirthDate()) < 0) {
                Collections.swap(players,0,1);
            }
        }
    }

    public static void main(String[] args){
        GameControl partita = new GameControl();
    }
}
