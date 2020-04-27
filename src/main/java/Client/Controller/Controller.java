package Client.Controller;

public class Controller {
    public boolean validOperation(){
        return false;
    }

    public boolean checkLimits(int choice){
        if(choice>=0 && choice<=4){
            return true;
        }else{
            return false;
        }
    }
}
