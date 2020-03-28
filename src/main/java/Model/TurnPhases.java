package Model;

public enum TurnPhases {
    MOVE,BUILD,CHANGE_PLAYER;

    /**
     * it changes the TurnPhases, from MOVE to BUILD. From BUILD->CHANGE_PLAYER. From CHANGE_PLAYER->MOVE
     * @return nextPhase of the turn
     */
    public TurnPhases changeFrom()
    {
        switch (this)
        {
            case MOVE:
                return BUILD;
            case BUILD:
                return CHANGE_PLAYER;
            case CHANGE_PLAYER:
                return MOVE;
            default:
                throw new RuntimeException("Unexpected case");
        }
    }
}
