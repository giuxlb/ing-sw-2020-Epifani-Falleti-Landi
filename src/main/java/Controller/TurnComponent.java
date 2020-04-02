package Controller;

import Model.TurnPhases;

public abstract class TurnComponent {

    private TurnPhases phase;


    public abstract void move();
    public abstract void build();
    public abstract void startPhase();
}