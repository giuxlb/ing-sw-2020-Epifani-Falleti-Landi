package Controller;

import Model.Card;

public class DivinityContext {
    private TurnStrategy strategy;

    public void setTurnStrategy(TurnStrategy strategy){
        this.strategy=strategy;
    }

    public void move(){
        strategy.move();
    }

    public void build(){
        strategy.build();
    }

    /***
     * Select the strategy based on the card given in input
     * @param card the card associated to a player
     */
    public void selectStrategy(Card card){
        switch(card.getName()){
            case "Apollo": this.setTurnStrategy(new ApolloStrategy());
                break;
            case "Artemis": this.setTurnStrategy(new ArtemisStrategy());
                break;
            default: this.setTurnStrategy(new DefaultStrategy());

        }

    }
}
