package Controller;

import Model.*;

public class TurnControl {
    private Player player;
    private boolean athenaEffect;
    private Card card;

    /***
     * Constructor of TurnControl, which is created every time a turn starts
     * @param player the player that his starting their turn
     * @param athenaEffect boolean that indicates the fact that another player
     *                     can interfere in this turn with the Athena effect
     */
    public TurnControl(Player player,boolean athenaEffect){
        this.player = player;
        this.athenaEffect = athenaEffect;
        this.card = this.player.getGameCard();
    }

    public void start(){
        DivinityContext ctx = new DivinityContext();

        ctx.selectStrategy(this.card);

        ctx.move();

        ctx.build();
    }

}
