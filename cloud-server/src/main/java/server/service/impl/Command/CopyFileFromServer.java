package server.service.impl.Command;

import server.handler.SQLHandler;
import server.service.FileProcessable;
import service.Command;
import service.FileUpload;

import java.nio.file.Paths;

public class CopyFileFromServer implements FileProcessable {
    public CopyFileFromServer() {
    }

    @Override
    public FileUpload processCommand(String command) {
        System.out.println("copy");
        String [] strings = command.split("\\s");
        String source = strings[1];
        String target = strings[2];
        String nickname = source.split("\\\\")[0];
        String name_file = Paths.get(source).getFileName().toString();
        String path = Paths.get(source).getParent().toString()+"\\";
        byte[] bytes = SQLHandler.getFileFromStorage(nickname,name_file,path);
        return new FileUpload(name_file,target,bytes);
    }

    @Override
    public String getCommand() {
        return Command.COPY.getInstruction();
    }
}
