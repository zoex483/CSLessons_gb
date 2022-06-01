package gb.dina;

/*Примеры использования Java NIO*/

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class JavaNIO {

    public static int PORT_NUMBER = 1234;

    public static void main(String[] argv) throws Exception {
        new JavaNIO().go(argv);
    }

    public void go(String[] argv) throws Exception {
        int port = PORT_NUMBER;
        if (argv.length > 0) {
            // переопределить дефолтный порт
            port = Integer.parseInt(argv[0]);
        }
        System.out.println("Listening on port " + port); // выделить несвязанный канал сокета сервера
        ServerSocketChannel serverChannel = ServerSocketChannel.open (); // создать новый канал
        ServerSocket serverSocket = serverChannel.socket (); // получить соединение с сокетом из канала
        Selector selector = Selector.open (); // создать новый селектор
        serverSocket.bind (new InetSocketAddress (port)); // привязать сокетное соединение к порту
        serverChannel.configureBlocking (false); /** установить канал в неблокирующий режим. Только неблокирующий режим может
         использовать преимущества nio. Канал сокета может быть установлен неблокирующим, а канал файла
        не может быть неблокирующим.
        */
        serverChannel.register (selector, SelectionKey.OP_ACCEPT); /** зарегистрировать этот канал в селекторе, OP_ACCEPT, если
         он заинтересован в работе, ожидает подключения */
        while (true) {/** циклически выбирайте подготовленную клавишу выбора из селектора, ключ должен
            // поддерживать соответствующие отношения между каналом и селектором */
            int n = selector.select (); // Получить количество подготовленных клавиш селектора от селектора
            if (n == 0) {
                continue; // nothing to do
            }
            Iterator it = selector.selectedKeys (). iterator (); // перебирать все подготовленные ключи выбора
            while (it.hasNext()) {
                SelectionKey key = (SelectionKey) it.next();
                if (key.isAcceptable ()) {// если ключ ожидает подключения
                    ServerSocketChannel server = (ServerSocketChannel) key.channel (); // получить готовый канал сокета сервера из смены ключа
                    SocketChannel channel = server.accept (); // получить это соединение сокета из канала сокета сервера
                    registerChannel (selector, channel, SelectionKey.OP_READ); // зарегистрировать соединение как читаемое рабочее состояние
                    sayHello (channel); // отправляем данные в это сокетное соединение
                }
                if (key.isReadable ()) {/** в следующий раз, когда ссылка будет читаемой
                    readDataFromSocket (key);  получить данные этой ссылки на сокет и отобразить их*/
                }
                it.remove (); /** удалить подготовленный ключ, который был выполнен на этот раз, и выполнить selector.select ()
                // в следующий раз, все еще можно выбрать все подготовленные ключи*/
            }
        }
    }

    protected void registerChannel(Selector selector, SelectableChannel channel, int ops) throws Exception {
        if (channel == null) {
            return;
        }
        channel.configureBlocking (false); // установить канал в неблокирующий режим
        channel.register (selector, ops); // зарегистрировать соединение как читаемое рабочее состояние
    }

    private ByteBuffer buffer = ByteBuffer.allocateDirect(1024);


    protected void readDataFromSocket(SelectionKey key) throws Exception {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        int count;
        buffer.clear();
        while ((count = socketChannel.read (buffer))> 0) {// отделить данные от канала
            buffer.flip (); // стандартные данные этого канала читаемы
            while (buffer.hasRemaining()) {
                socketChannel.write (buffer); // запись данных в канал
            }
            buffer.clear();
            if (count < 0) {
                socketChannel.close();
            }
        }
    }
    private void sayHello(SocketChannel channel) throws Exception {
        buffer.clear();
        buffer.put("Bonjour!\r\n".getBytes());
        buffer.flip();
        channel.write(buffer);
    }
}

