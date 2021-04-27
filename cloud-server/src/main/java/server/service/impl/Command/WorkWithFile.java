package server.service.impl.Command;

import server.factory.Factory;
import server.service.CommandFileProcessable;
import server.service.FileProcessable;
import service.FileUpload;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WorkWithFile implements CommandFileProcessable {
    private final Map<String, FileProcessable> commandProcessFile;

    public WorkWithFile() {
        commandProcessFile = Collections.unmodifiableMap(getCommandProcessFile());
    }

    private Map<String, FileProcessable> getCommandProcessFile() {
        List<FileProcessable> processFilesList = Factory.getFileProcessable();
        Map<String, FileProcessable> commandProcessFile = new HashMap<>();
        for (FileProcessable fileProcessable : processFilesList) {
            commandProcessFile.put(fileProcessable.getCommand(), fileProcessable);
        }
        return commandProcessFile;
    }

    @Override
    public FileUpload commandProcessFile(String command) {
        String[] commandParts = command.split("\\s");
        return commandProcessFile.get(commandParts[0]).processCommand(command);
    }

}
