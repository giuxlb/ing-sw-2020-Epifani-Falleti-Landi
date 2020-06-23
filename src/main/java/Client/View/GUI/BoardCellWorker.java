package Client.View.GUI;

import Controller.Coordinates;

import javax.swing.*;

public class BoardCellWorker extends SwingWorker {
    public BoardCellWorker(){

    }

    public BoardCellWorker(int i, int j){
        GUIHandler.currentCoordinate = new Coordinates(i,j);
        GUIHandler.ready=true;
    }

    @Override
    protected Object doInBackground() throws Exception {
        return null;
    }
}
