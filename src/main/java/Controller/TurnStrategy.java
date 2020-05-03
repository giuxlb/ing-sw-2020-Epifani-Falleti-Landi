package Controller;

import Model.Board;
import Model.Game;
import Model.Player;
import Model.Worker;

import java.util.ArrayList;

public interface TurnStrategy {
    public int turn(Player player,Worker worker,Board board,Game game,int extraEffect,VirtualView virtualView);
    public void move(Worker worker, ArrayList<Coordinates> valid_positions, int index, Game game, Board board);
    public void build(Worker worker, ArrayList<Coordinates> valid_positions, int index, Game game, Board board);
    public ArrayList<Coordinates> checkAvailableMoveSpots(Player player, Worker worker, Board board, boolean athenaeffect);
    public ArrayList<Coordinates> checkAvailableBuildSpots(Player player,Worker worker, Board board);
    public boolean checkWinCondition(Coordinates starting_position,Coordinates final_position, Board board);
}
