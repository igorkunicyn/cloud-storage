package service;

public enum Command {
    END ("/end"),AUTH_OK("/authok"),AUTH("/auth"),
    DELETE("del"),MOVE("move"), COPY ("copy"),
    REG ("/reg"), REG_OK ("/regok"), REG_NO ("/regno"), CHANGE_NICK("/chnick "),
    DIR_STORAGE ("C:\\Storage"), START_DIR ("C:\\");

    private final String instruction;

    Command(String instruction){
        this.instruction=instruction;
    }
    public String getInstruction(){
        return instruction;
    }
}
