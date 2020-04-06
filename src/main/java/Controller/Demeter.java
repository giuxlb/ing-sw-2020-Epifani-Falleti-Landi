package Controller;

import Model.Board;
import Model.Player;

public class Demeter extends turnDecorator {
    @Override
    public void checkTurnPhase() {
        System.out.println("checkTurnPhase di Demetra");
    }

    public Demeter(Turn component){
        super(component);
    }

    @Override
    public void move(int x, int y, Player p, int worker_index, Board boardGame) {
        this.component.move(x,y,p,worker_index,boardGame);
    }

    @Override
    public void build(int x, int y, Player p, int worker_index, Board boardGame) {
        this.component.build(x,y,p,worker_index,boardGame);
        System.out.println("Build di Demetra");
    }
}
