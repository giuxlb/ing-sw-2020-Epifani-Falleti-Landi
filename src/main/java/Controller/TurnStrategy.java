package Controller;

import Model.Board;
import Model.Game;
import Model.Player;
import Model.Worker;

import java.util.ArrayList;

public interface TurnStrategy {
    int turn(Player player, Worker worker, Board board, Game game, int extraEffect, VirtualView virtualView);
    void move(Worker worker, ArrayList<Coordinates> valid_positions, int index, Game game, Board board);
    void build(Worker worker, ArrayList<Coordinates> valid_positions, int index, Game game, Board board);
    ArrayList<Coordinates> checkAvailableMoveSpots(Player player, Worker worker, Board board, boolean athenaeffect);
    ArrayList<Coordinates> checkAvailableBuildSpots(Player player, Worker worker, Board board);
    boolean checkWinCondition(Coordinates starting_position, Coordinates final_position, Board board);
}
