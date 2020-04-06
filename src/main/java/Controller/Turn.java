package Controller;

import Model.Game;
import Model.Player;
import Model.Board;

import java.util.ArrayList;

public interface Turn {

    public void checkTurnPhase();
    public void move(int x, int y, Player p, int worker_index, Board boardGame);
    public void build(int x, int y, Player p, int worker_index, Board boardGame);

}
