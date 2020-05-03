package Model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SocketBoardCell extends BoardCell implements Serializable {

    private static final long serialVersionUID = 2603215813861166183L;
    private Color workerColor;

    public SocketBoardCell(int height,Worker w)
    {
        super();
        setBoardCellHeight(height);
        setBoardCellWorker(w);
        if (w != null)
            this.workerColor = w.getColor();
        else{
            this.workerColor = null;
        }
    }

    public Color getWorkerColor() {
        return workerColor;
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
