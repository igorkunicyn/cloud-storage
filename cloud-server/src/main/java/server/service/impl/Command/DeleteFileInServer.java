package server.service.impl.Command;

import server.handler.SQLHandler;
import server.service.FileProcessable;
import service.Command;
import service.FileUpload;

import java.nio.file.Paths;

public class DeleteFileInServer implements FileProcessable {
    public DeleteFileInServer() {
    }

    @Override
    public FileUpload processCommand(String command) {
        System.out.println("del");
        String [] strings = command.split("\\s");
        String dirPath = strings[1];
        String nickname = dirPath.split("\\\\")[0];
        String name_file = Paths.get(dirPath).getFileName().toString();
        String path = Paths.get(dirPath).getParent().toString()+ "\\";
        byte [] bytes = {};
        SQLHandler.getDeleteFromStorage(nickname,name_file,path);
        return new FileUpload(Command.DELETE.getInstruction(),command,bytes);
    }
    @Override
    public String getCommand() {
        return Command.DELETE.getInstruction();
    }
}
