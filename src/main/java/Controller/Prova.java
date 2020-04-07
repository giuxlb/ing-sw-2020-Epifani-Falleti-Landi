package Controller;

import Model.Card;
import Model.Player;

public class Prova
{
    public static void main(String[] args)
    {


        Player player = new Player("giux");
        Card card = new Card("Apollo");
        player.chooseCard(card);

        TurnControl turn = new TurnControl(player, false);

        turn.start();

    }

}
