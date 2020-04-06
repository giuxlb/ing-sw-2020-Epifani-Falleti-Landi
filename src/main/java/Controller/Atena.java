package Controller;

import Model.Board;
import Model.Player;

public class Atena extends turnDecorator {

    public Atena(Turn component){
        super(component);
    }

    @Override
    public void checkTurnPhase() {
        System.out.println("checkTurnPhase di Atena");
    }

    @Override
    public void move(int x, int y, Player p, int worker_index, Board boardGame) {
        this.component.move(x,y,p,worker_index,boardGame);
        System.out.println("Movimento di Atena");
    }

    @Override
    public void build(int x, int y, Player p, int worker_index, Board boardGame) {
        this.component.build(x,y,p,worker_index,boardGame);
    }
}
