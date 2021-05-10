package server.service.impl.Command;

import server.handler.SQLHandler;
import server.service.FileProcessable;
import service.Command;
import service.FileUpload;

import java.nio.file.Paths;

public class MoveFileFromServer implements FileProcessable {

    public MoveFileFromServer() {
    }

    @Override
    public FileUpload processCommand(String command) {
        System.out.println("move");
        String [] strings = command.split("\\s");
        String out = strings[1];
        String in = strings[2];
        String nickname = out.split("\\\\")[0];
        String name_file = Paths.get(out).getFileName().toString();
        String path = Paths.get(out).getParent().toString()+ "\\";
        byte[] bytes = SQLHandler.getFileFromStorage(nickname,name_file,path);
        SQLHandler.getDeleteFromStorage(nickname,name_file,path);
        return new FileUpload(name_file,in,bytes);
    }
    @Override
    public String getCommand() {
        return Command.MOVE.getInstruction();
    }
}
