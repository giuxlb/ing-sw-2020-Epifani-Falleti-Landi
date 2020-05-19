package Client.View.GUI;

import Client.View.GUI.GUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args){
                GUI GUI=new GUI();
                GUIHandler gh= new GUIHandler(GUI);
                gh.launchConnection();
    }

}
