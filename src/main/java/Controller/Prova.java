package Controller;

import Model.Card;
import Model.Player;

import java.util.ArrayList;

public class Prova
{
    public static void main(String[] args)
    {


        Player player = new Player("giux");
        Card card = new Card("Apollo");
        player.chooseCard(card);

        TurnControl turn = new TurnControl(player, false);

        turn.start();


        //da chiedere
        move(Player,Worker,int x,int y);

        move(Player,Worker,Coordinates);

        move(Player,Worker,ArrayList<Coordinates>,int n); //n indica l'indice della coordinata da prendere.
    }
}
