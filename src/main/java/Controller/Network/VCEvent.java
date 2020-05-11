package Controller.Network;

import Model.Board;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * @author Adriano Falleti
 */
public class VCEvent implements Serializable {


    private static final long serialVersionUID = 6833487609956296120L;

    public enum Event{
        setup_request,
        username_request,
        wrong_username,
        date_request,
        not_your_turn,
        number_of_players,
        update,
        send_cells_move,
        send_cells_build,
        send_cells_remove,
        you_lost,
        game_ended_foryou,
        you_won,
        send_all_cards,
        send_chosen_cards,
        ping,
        player_disconnected_game_ended,
        id,
        send_your_card,
        ask_for_worker,
        ask_for_divinity_activation,
        send_color,
        undo_request
    }

    private Object box; // potrà essere o una stringa o un array di celle, o un array di carte, o la board
    private Event command;

    /**
     * Constructor for the VCEvent
     * @param arg
     * @param command
     */
    public VCEvent(Object arg, Event command)
    {
        this.box = arg;
        this.command = command;
        this.boardImmutable = null;

    }
    public VCEvent(List<Board> b)
    {
        this.boardImmutable = b;
        this.command = Event.update;
    }
    public void setCommand(Event event) {
        this.command = event;
    }

    public Event getCommand() {
        return command;
    }

    private final List<Board> boardImmutable;

    public void setBox(Object box) {
        this.box = box;
    }

    public Object getBox() {
        return box;
    }

    public List<Board> getBoardImmutable() {
        return boardImmutable;
    }




    public void readObject(ObjectInputStream stream) throws IOException,ClassNotFoundException
    {
        stream.defaultReadObject();
    }

    public void writeObject(ObjectOutputStream stream) throws IOException
    {
        stream.defaultWriteObject();
    }

}
