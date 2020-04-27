package Controller;

import Model.*;

public class TurnControl {
    private Player player;
    private boolean athenaEffect;
    private Card card;
    private DivinityContext ctx;
    private Board boardGame;
    private Game game;
    private VirtualView virtualView;

    /***
     * Constructor of TurnControl, which is created every time a turn starts
     * @param player the player that his starting their turn
     * @param athenaEffect boolean that indicates the fact that another player
     *                     can interfere in this turn with the Athena effect
     */
    public TurnControl(Player player,boolean athenaEffect,Board boardGame,Game game,VirtualView virtualView){
        this.player = player;
        this.athenaEffect = athenaEffect;
        this.card = this.player.getGameCard();
        this.ctx = new DivinityContext();
        this.boardGame = boardGame;
        this.game = game;
        this.virtualView = virtualView;
    }

    public void start(){

        //ask the player if they want to use the card effect

        ctx.selectStrategy(this.card);

        //ask the player which worker they want to use
        Worker selectedWorker = player.getWorker(0);

        ctx.turn(player,selectedWorker,boardGame,game,athenaEffect,virtualView);

    }


}
