package Client.View.GUI;

import Controller.Coordinates;

import javax.swing.*;

public class BoardCellWorker extends SwingWorker {
    @Override
    protected Object doInBackground() {
        return null;
    }

    public BoardCellWorker(int i, int j){
        GUIHandler.currentCoordinate = new Coordinates(i,j);
        GUIHandler.ready=true;
    }
}
