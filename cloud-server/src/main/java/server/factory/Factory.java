package server.factory;


import server.service.*;
import server.service.impl.Command.*;
import server.service.impl.CommandAuthAndRegServiceI;
//import Server.Service.impl.IOClientService;
import server.service.impl.NettyServerService;

import java.util.Arrays;
import java.util.List;

public class Factory {

    public static ServerService getServerService() {
        return NettyServerService.getInstance();
    }

    public static CommandAuthAndRegService getCommandDirectoryService() {
        return new CommandAuthAndRegServiceI();
    }

    public static List<CommandService> getCommandServices() {

        return Arrays.asList(new AuthClientCommand(), new RegClientCommand(), new EndWorkClientCommand());
    }
    public static CommandFileProcessable getCommandFileProcessable() {
        return new WorkWithFile();
    }

    public static List<FileProcessable> getFileProcessable() {

        return Arrays.asList(new CopyFileFromServer(), new MoveFileFromServer(), new DeleteFileInServer());
    }


}
