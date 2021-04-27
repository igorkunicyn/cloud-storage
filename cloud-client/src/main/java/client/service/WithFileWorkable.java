package client.service;

public interface WithFileWorkable {

    String  openDirectoryService(String path);
    void createDirectoryAndFileService(String path);
    void deleteDirectoryAndFileService(String path);
    void copyDirectoryAndFileService(String source, String target);
}
