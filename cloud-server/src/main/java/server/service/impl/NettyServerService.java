package server.service.impl;

import server.handler.SQLHandler;
import server.handler.ServerNettyHandler;
import server.service.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.net.Socket;

public class NettyServerService implements ServerService {

       private static final int SERVER_PORT = 8189;
       private AuthDBService authDBService;

       private static NettyServerService instance;

       public static NettyServerService getInstance() {
           if (instance == null) {
               instance = new NettyServerService();
           }

           return instance;
       }
    @Override
    public void startServer() {
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new ObjectEncoder());
                            pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                            pipeline.addLast(new ServerNettyHandler());
                        }
                    });
             ChannelFuture future= b.bind(8189).sync();
             System.out.println("Сервер запущен на порту " + SERVER_PORT);
            if (!SQLHandler.connect()) {
                    throw new RuntimeException("Не удалось подключиться к БД");
             }else {
                    System.out.println("База данных подключена");
                    authDBService = new AuthDBServise();
                   }
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            System.out.println("Сервер упал");
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
            SQLHandler.disconnect();
            System.out.println("Сервер завершил работу");
        }
    }
    public AuthDBService getAuthService() {
        return authDBService;
    }
}
