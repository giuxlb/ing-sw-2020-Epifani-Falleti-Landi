package Client.View;

public class Elements {
    private String[] buildings={"\u2584","\u2580","\u2588","\u06E9"};

    private String worker="\u06DE";

    public String getBuilding(int index){
        return buildings[index];
    }

    public String getWorker() {
        return worker;
    }
}
