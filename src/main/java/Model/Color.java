package Model;

import java.io.Serializable;

public enum Color {
    ANSI_RED("\u001B[31m"),
    ANSI_GREEN("\u001B[32m"),
    ANSI_YELLOW("\u001B[33m"),
    ANSI_BLUE("\u001B[34m"),
    ANSI_PURPLE("\u001B[35m"),
    ANSI_WHITE("\u001B[37m");

    static final String RESET="\u001B[0m";
    private String escape;

    /***
     * Constructor for this class
     * @param escape String representation of a color
     */
     Color(String escape) {
        this.escape=escape;
    }

    /***
     * Get escape from the current object
     * @return String representation of a color
     */
    public String getEscape() {
        return escape;
    }

    /***
     * Override toStricg() methods
     * @return String representation of the current object
     */
    @Override
    public String toString(){
        return escape;
    }

}
