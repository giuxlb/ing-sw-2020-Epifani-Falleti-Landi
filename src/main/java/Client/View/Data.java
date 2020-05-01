package Client.View;

public class Data {
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
}
