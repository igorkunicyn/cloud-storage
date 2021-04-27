package server.service;

import service.FileUpload;

public interface FileProcessable {

    FileUpload processCommand(String command);

    String getCommand();
}
