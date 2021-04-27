package server.service;

import service.FileUpload;

public interface CommandFileProcessable {

    FileUpload commandProcessFile(String command);

}
