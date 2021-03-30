package Client.View.GUI;

import javax.swing.*;

public class ThreadUsername implements Runnable{
    private JTextField usernameTextField;
    private GUIHandler gh;

    public ThreadUsername(GUIHandler gh, JTextField usernameTextField){
        this.gh=gh;
        this.usernameTextField=usernameTextField;
    }

    @Override
    public void run() {
        gh.setUsername(usernameTextField);
    }
}
