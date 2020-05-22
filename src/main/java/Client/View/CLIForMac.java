package Client.View;

import Model.Board;
import Model.Color;

public class CLIForMac extends CLI {

    public CLIForMac(String OS){
        super(OS);
    }

    @Override
    public String turnWorkerIntoColoredImage(Board b, int i, int j){
        if(b.getBoardWorker(i,j)!=null){
            switch (b.getBoardWorker(i,j).getColor()){
                case ANSI_YELLOW:
                    return "  " + Color.ANSI_YELLOW + getE().getWorker() + " ";
                case ANSI_WHITE:
                    return "  " + Color.ANSI_WHITE + getE().getWorker() + " ";
                case ANSI_PURPLE:
                    return "  " + Color.ANSI_PURPLE + getE().getWorker() + " ";
            }
        }else{
            return "    ";
        }

        return "Renderizzazione e colorazione del worker non riuscita";
    }
}
