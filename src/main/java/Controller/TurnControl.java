package Controller;

import Model.*;

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

    public int start(){

        //ask the player if they want to use the card effect

        ctx.selectStrategy(this.card);

        //ask the player which worker they want to use
        Worker selectedWorker = player.getWorker(0);

        return ctx.turn(player,selectedWorker,boardGame,game,extraEffect,virtualView);
    }


}
