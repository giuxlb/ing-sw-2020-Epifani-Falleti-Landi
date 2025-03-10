package Client.View;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Data implements Serializable {
    private static final long serialVersionUID = 6609982820955625848L;
    private int giorno;
    private int mese;
    private int anno;

    /***
     *
     * @param giorno
     * @param mese
     * @param anno
     */
    public Data(int giorno, int mese, int anno){
        this.giorno=giorno;
        this.mese=mese;
        this.anno=anno;
    }

    /***
     *
     * @return
     */
    public int getGiorno() {
        return giorno;
    }

    /***
     *
     * @param giorno
     */
    public void setGiorno(int giorno) {
        this.giorno = giorno;
    }

    /***
     *
     * @return
     */
    public int getMese() {
        return mese;
    }

    /***
     *
     * @param mese
     */
    public void setMese(int mese) {
        this.mese = mese;
    }

    /***
     *
     * @return
     */
    public int getAnno() {
        return anno;
    }

    /***
     *
     * @param anno
     */
    public void setAnno(int anno) {
        this.anno = anno;
    }

    /***
     *
     * @param d
     * @return
     */
    public boolean isGreaterthan(Data d){
        if(this.anno>d.getAnno()){
            return true;
        }else if(this.anno==d.getAnno()){
            if(this.mese>d.getMese()){
                return true;
            }else if(this.getMese()==d.getMese()){
                if(this.giorno>=d.getGiorno()){
                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    public void readObject(ObjectInputStream stream) throws IOException,ClassNotFoundException
    {
        stream.defaultReadObject();
    }

    public void writeObject(ObjectOutputStream stream) throws IOException
    {
        stream.defaultWriteObject();
    }

    @Override
    public String toString() {
        return "Data{" +
                "giorno=" + giorno +
                ", mese=" + mese +
                ", anno=" + anno +
                '}';
    }
}
