package client.service.impl;

import service.Command;
import service.FileUpload;
import client.service.WithFileWorkable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class WorkWithFiles implements WithFileWorkable {
    private static WorkWithFiles instance;

    public static WorkWithFiles getInstance() {
        if (instance == null){
            instance = new WorkWithFiles();
        }
        return instance;
    }
    public static FileUpload createFileForCopyOrMoveToServer(String source, String target,String nickname) {
        String nameFile = Paths.get(target).getFileName().toString();
        String path = Paths.get(target.replace(Command.DIR_STORAGE.getInstruction(), nickname)).getParent().toString()+"\\";
        System.out.println(nameFile + " name");
        System.out.println(path + " path");
        byte[] bytes = createByteArrayFromFile(Paths.get(source));
        return new FileUpload(nameFile, path, bytes);
    }

    private  static byte[] createByteArrayFromFile(Path path){
        byte[] bytes = {};
        try {
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }
    public static void createByteArrayToFile(FileUpload fileUpload){
        try {
            Files.write(Paths.get(fileUpload.getPath()),fileUpload.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String openDirectoryService(String s) {
        File directory = new File(s);
        if (!directory.exists()) {
            return "Directory is not exists";
        }
        StringBuilder builder = new StringBuilder();
        for (File childFile : Objects.requireNonNull(directory.listFiles())) {
            if (childFile.isDirectory()){
                builder.append("<DIR>");
            }else {
                builder.append("           ");
            }
            builder.append(childFile.getName()).append(System.lineSeparator());
        }
        return builder.toString();
    }

    @Override
    public void deleteDirectoryAndFileService(String path) {
        try {
            Files.delete(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void createDirectoryAndFileService(String s) {
        try {
            if (!Paths.get(s).getFileName().toString().contains(".")){
                Files.createDirectory(Paths.get(s));
            }else {
                Files.createFile(Paths.get(s));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void copyDirectoryAndFileService(String source, String target) {
        try {
            Files.copy(Paths.get(source), Paths.get(target));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}