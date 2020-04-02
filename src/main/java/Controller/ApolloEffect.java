package Controller;
public class ApolloEffect extends ExtraEffectDecorator{
    public ApolloEffect(TurnComponent turnComponent) {this.turnComponent = turnComponent;}

    @Override
    public String getTurnEvents(){return turnComponent.getTurnEvents()+ Actions.ApolloMove.toString();}
}
