package Controller;

import Model.Board;
import Model.Player;

public abstract class turnDecorator implements Turn {
    protected Turn component;

    public turnDecorator(Turn component){
        this.component=component;
    }

    @Override
    public abstract void checkTurnPhase();

    @Override
    public abstract void move(int x, int y, Player p, int worker_index, Board boardGame);

    @Override
    public abstract void build(int x, int y, Player p, int worker_index, Board boardGame);
}
