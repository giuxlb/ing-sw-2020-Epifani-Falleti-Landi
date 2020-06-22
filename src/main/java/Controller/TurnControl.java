package Controller;

import Model.*;

import java.util.ArrayList;

public class TurnControl {
    private Player player;
    private int extraEffect;
    private String card;
    private DivinityContext ctx;
    private Board boardGame;
    private Game game;
    private VirtualView virtualView;

    /***
     * Constructor of TurnControl, which is created every time a turn starts
     * @param player the player that his starting their turn
     * @param extraEffect effect on this turn
     */
    public TurnControl(Player player,int extraEffect,Board boardGame,Game game,VirtualView virtualView){
        this.player = player;
        this.extraEffect = extraEffect;
        this.card = this.player.getGameCard();
        this.ctx = new DivinityContext();
        this.boardGame = boardGame;
        this.game = game;
        this.virtualView = virtualView;
    }

    /**
     * Standard flow of the turn
     * @return an extra effect to be applied on the next turn, or a special error value (-1)
     */
    public int start(){

        //ask the player if they want to use the card effect

        virtualView.notYourTurn(player);

        ctx.selectStrategy(this.card);
        //ctx.selectStrategy("");
        ArrayList<Coordinates> workers_pos = new ArrayList<>();
        for(int i = 0;i<2;i++){
            Coordinates position = new Coordinates(player.getWorker(i).getPositionX(),player.getWorker(i).getPositionY());
            workers_pos.add(position);
        }

        Coordinates worker_pos = virtualView.askForWorker(player,workers_pos);
        if(worker_pos==null) return -1;

        int temp = workers_pos.indexOf(worker_pos);

        Worker selectedWorker = player.getWorker(temp);

        return ctx.turn(player,selectedWorker,boardGame,game,extraEffect,virtualView);
    }


}
