package service;

import java.io.Serializable;

public class FileUpload implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String nameFile;
    private final String path;
    private final byte[] bytes;

    public FileUpload(String nameFile, String path, byte[] bytes) {
        this.nameFile = nameFile;
        this.path = path;
        this.bytes = bytes;
    }

    public String getNameFile() {
        return nameFile;
    }

    public String getPath() {
        return path;
    }

    public byte[] getBytes() {
        return bytes;
    }
}

