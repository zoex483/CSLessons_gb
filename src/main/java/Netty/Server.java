package Netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class Server {

    public static void main(String[] args) {
        /**
         * стартовый класс
         */
        ServerBootstrap bootstrap = new ServerBootstrap();

        /**
         * можно понимать как пулы потоков, а EventLoopGroup внутренне инкапсулирует пул потоков
         */
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            // Установить пул потоков
            bootstrap.group(boss, worker);

            // Установить фабрику сокетов,
            bootstrap.channel(NioServerSocketChannel.class);

            // Настройка конвейерного завода
            bootstrap.childHandler(new ChannelInitializer<Channel>() {

                /**
                 * Конвейер в конечном итоге будет установлен в процессор
                 */
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    /**
                     * Установите процессор декодирования
                     */
                    ch.pipeline().addLast(new StringDecoder());
                    /**
                     * Установить кодировку процессора
                     */
                    ch.pipeline().addLast(new StringEncoder());
                    ch.pipeline().addLast(new ServerHandler());
                }
            });


            // Установить параметры, параметры TCP
            bootstrap.option (ChannelOption.SO_BACKLOG, 2048); // настройки serverSocketchannel, размер пула буферов ссылок
            bootstrap.childOption (ChannelOption.SO_KEEPALIVE, true); // настройки сокетного канала, держать ссылку активной, удалять мертвые ссылки
            bootstrap.childOption (ChannelOption.TCP_NODELAY, true); // настройка сокета, отключение отложенной отправки

            // Привязать порт
            ChannelFuture future = bootstrap.bind(10201);

            System.out.println("start");

            // Дождаться закрытия сервера
            future.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.print(e);
        } finally{
            // Освободить ресурсы
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}