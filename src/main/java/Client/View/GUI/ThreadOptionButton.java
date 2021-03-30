package Client.View.GUI;

public class ThreadOptionButton implements Runnable{
    private GUIHandler gh;
    private int playersNumber;

    public ThreadOptionButton(GUIHandler gh, int playersNumber){
        this.gh=gh;
        this.playersNumber=playersNumber;
    }

    @Override
    public void run() {
        gh.setPlayersNumber(playersNumber);
    }
}
