package Client.View.GUI;

import javax.swing.*;

public class UndoWorker extends SwingWorker {
    public UndoWorker(int choose){
        GUIHandler.choose=choose;
    }

    @Override
    protected Object doInBackground() {
        return null;
    }
}
