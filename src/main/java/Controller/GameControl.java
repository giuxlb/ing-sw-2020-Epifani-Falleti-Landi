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
    private View virtualView;


    /***
     * Costructor of GameControl, it creates a new Game and,
     * after receiving the names from the clients, it creates and inserts the player
     */
    public GameControl() {
        Game game = new Game();
        //View virtualview = new View();
        players = new ArrayList<Player>();

        //aspetto che SetupItReady sia true
        while(virtualView.getSetupIsReady()==false){
            try{
                TimeUnit.MILLISECONDS.sleep(10);}
            catch (InterruptedException e){System.out.println("Interrupted exception");};
        }

        //prendo nome, data, e numero giocatori dal client 0
        String player_name_0 = virtualView.getUsername();
        Calendar player_date_0 = virtualView.getDate();
        int player_number = virtualView.getPlayerNumber();

        //aggiungo il player 0
        this.addPlayer(new Player(player_name_0,player_date_0));

        boolean flag = false;
        String player_name_1 = null;


        //continuo a chiedere il nome al secondo giocatore finchè non è diverso dal primo
        while (!flag) {
            //ricevo dal client nome 2
            player_name_1 = virtualView.askForUsername(1);
            if(!player_name_1.equals(player_name_0)){ flag=true;}

        }
        Calendar player_date_1 = virtualView.askForDate(1);
        this.addPlayer(new Player(player_name_1,player_date_1));

        //se il numero di player è 3, chiedo i dati del terzo giocatore
        if(player_number==3){
            flag=false;
            String player_name_2 = null;

            while (!flag){
                player_name_2 = virtualView.askForUsername(2);
                if(!player_name_2.equals(player_name_1) && !player_name_2.equals(player_name_0)){flag=true;}
            }
            Calendar player_date_2 = virtualView.askForDate(2);
            this.addPlayer(new Player(player_name_2,player_date_2));
        }

        //mando alla virtualview l'array dei player non ordinato
        virtualView.sendPlayerArray(players);

        //riordino i giocatori in base all'età
        this.sortPlayersByAge();

    }

//TODO creare test per classi in model e controller ove possibile
//TODO gestione dello scollegamento di un client

    public void startGame(){
        game.startGame();

        //scelta delle carte del primo player

        ArrayList<Card> chosenCards = virtualView.sendAllCards(players.get(game.getTurnNumber()),game.getAvailableCards());
        game.setChoosenCards(chosenCards);
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
            boolean valid_pos = false;
            Coordinates initial_pos;

            for(int worker = 0;worker<2;worker++) {
                while (!valid_pos) {
                    initial_pos = virtualView.askInitialPosition(players.get(game.getTurnNumber()),worker);
                    valid_pos = checkValidInitialPosition(initial_pos.getX(), initial_pos.getY());
                    if (valid_pos) {
                        insertInitialPosition(game.getTurnNumber(), initial_pos.getX(), initial_pos.getY(),worker);
                    } else {
                        virtualView.sendMessageWrongPosition(players.get(game.getTurnNumber()));
                    }
                }
            }

            game.nextTurnNumber();
        }while(game.getTurnNumber()!=0);

        while(true){


            startNextTurn();
            game.nextTurnNumber();
        }


    }

    /***
     * Method that starts the currentPlayer's turn, this
     * method will be inserted in a loop in startGame
     */
    public void startNextTurn(){
        TurnControl turn = new TurnControl(players.get(game.getTurnNumber()),athenaEffectTurn,game.getBoardGame(),game);
        turn.start();
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

    public void win(Player player){
        virtualView.sendWinMessage(player);
        game.win(player);
    }

    public void lose(Player player){
        virtualView.sendLoseMessage(player);
        game.lose(player);
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
}
