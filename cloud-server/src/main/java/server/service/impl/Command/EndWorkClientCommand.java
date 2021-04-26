package server.service.impl.Command;


import server.service.CommandService;
import service.Command;

public class EndWorkClientCommand implements CommandService {


    public EndWorkClientCommand() {
    }

    @Override
    public String processCommand(String str) {
        return Command.END.getInstruction();
    }

    @Override
    public String getCommand() {
        return Command.END.getInstruction();
    }

}
