package Netty;


import java.io.BufferedReader;
import java.io.InputStreamReader;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class Client {

    public static void main(String[] args) {
        // Сервисный класс
        Bootstrap bootstrap = new Bootstrap();

        //worker
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            // Установить пул потоков
            bootstrap.group(worker);

            // Установить фабрику сокетов,
            bootstrap.channel(NioSocketChannel.class);

            // Настройка конвейера
            bootstrap.handler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(new StringDecoder());
                    ch.pipeline().addLast(new StringEncoder());
                    ch.pipeline().addLast(new ClientHandler());
                }
            });

            ChannelFuture connect = bootstrap.connect("127.0.0.1", 10201);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            while(true){
                System.out.println ("Пожалуйста, введите:");
                String msg = bufferedReader.readLine();
                connect.channel().writeAndFlush(msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            worker.shutdownGracefully();
        }
    }
}
