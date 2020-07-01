package Client.View.GUI;

import jdk.tools.jlink.internal.Jlink;

import javax.swing.*;
import java.util.ArrayList;

public class GodsWorker extends SwingWorker {
    private JLabel upperLabel;

    public GodsWorker(String god, JLabel upperLabel){
        this.upperLabel = upperLabel;
        GUIHandler.chosenGods.add(god);
        upperLabel.setText("You have selected god: " + god + ", You have to choose "+ (GUIHandler.playersNumber-GUIHandler.chosenGods.size()) + "god");
        GUIHandler.ready=true;
    }

    @Override
    protected Object doInBackground() throws Exception {
        return null;
    }
}
