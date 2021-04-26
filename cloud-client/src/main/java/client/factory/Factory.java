package client.factory;

import client.service.impl.IONetworkService;
import client.service.impl.WorkWithFiles;


public class Factory {

        public static IONetworkService getNetworkService() {
            return IONetworkService.getInstance();
        }
        public static WorkWithFiles getWithFileWorkable() {
        return WorkWithFiles.getInstance();
    }

}
