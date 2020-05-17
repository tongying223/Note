# RabbitMQ

[TOC]

## 消息队列解决哪些问题

异步处理

应用解耦

流量削峰

日志处理



## RabbitMQ安装与配置

### Windows安装

下载安装[Erlang](https://www.erlang.org/downloads)

下载安装[RabbitMQ](<https://www.rabbitmq.com/install-windows.html>)

```
cd E:\SoftWare\RabbitMQ\rabbitmq_server-3.7.17\sbin
rabbitmq-plugins enable rabbitmq_management
```

浏览器打开localhost:15672进入RabbitMQ管理控制台



### 用户及vhost配置

添加用户

![1566390771655](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505181255.png)

vitual hosts管理

```
vitual hosts:相当于数据库
一般以/开头
添加成功后需对用户授权
```

![1566391096306](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505181256.png)



## 简单队列（Simple Queue）



![(assets/python-one.png) -> [|||] -> (C)](https://www.rabbitmq.com/img/tutorials/python-one.png)

引入RabbitMQ Java Client

创建获取RabbitMQ连接的工具类

```java
/**
 * 获取RabbitMQ连接
 */
public class ConnectionUtil {

    public static Connection getConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setVirtualHost("/vhost_tonyne");
        factory.setUsername("tonyne");
        factory.setPassword("123456");
        return factory.newConnection();
    }
}
```

创建Producer

```java
public class Send {
    private static final String QUEUE_NAME = "test_simple_queue";
    public static void main(String[] args) throws IOException, TimeoutException {
        // 1. 获取连接
        Connection connection = ConnectionUtil.getConnection();
        // 2. 获取Channel
        Channel channel = connection.createChannel();
        // 3. 队列声明
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String msg = "Hello RabbitMQ";
        channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
        System.out.println("msg sent: " + msg);
        channel.close();
        connection.close();
    }
}
```

创建Consumer

```java
public class Receive {
    private static final String QUEUE_NAME = "test_simple_queue";
    public static void main(String[] args) throws IOException, TimeoutException {
        // 1. 获取连接
        Connection connection = ConnectionUtil.getConnection();
        // 2. 获取channel
        Channel channel = connection.createChannel();
        // 3.队列声明
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        DefaultConsumer consumer = new DefaultConsumer(channel){

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "utf-8");
                System.out.println("msg received: " + msg);

            }
        };
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}
```

生产者发送一条消息，消费者消费

```bash
# Send Server
msg sent: Hello RabbitMQ

# Receive Server
msg received: Hello RabbitMQ
```



简单队列的不足

```
1.耦合性高，生产者消费者一一对应，无法多个消费者同时消费
2.队列名变更，则消费者和生产者都需要同时变更
```



## 工作队列（Work Queues）

### 轮询分发（Round-Robin）

![img](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505181257.png)

为什么需要工作队列？

```
Simple Queue生产者消费者一一对应，消费者消费能力跟不上生产者速度，导致消息积压
```



创建生产者1

```java
public class Send {
    private static String QUEUE_NAME = "test_work_queue";
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        // 获取连接
        Connection connection = ConnectionUtil.getConnection();
        // 获取Channel
        Channel channel = connection.createChannel();
        // 队列声明
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        // 发送消息
        for(int i = 0; i < 50; i++){
            String msg = "Message" + i;
            channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
            System.out.println("msg sent: " + msg);
            Thread.sleep(10 * i);
        }
        channel.close();
        connection.close();
    }
}
```

创建消费者1

```java
public class Receive1 {
    private static String QUEUE_NAME = "test_work_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        //1.获取连接
        Connection connection = ConnectionUtil.getConnection();
        //2.获取Channel
        Channel channel = connection.createChannel();
        //3.队列声明
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //4.定于消费者
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "utf-8");
                System.out.println("Consumer[1] received msg: " + msg);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("Consumer[1] consumed");
                }
            }
        };
        boolean autoAck = true;
        channel.basicConsume(QUEUE_NAME, autoAck, consumer);
    }
}
```

创建消费者2

```java
public class Receive2 {
    private static String QUEUE_NAME = "test_work_queue";
    public static void main(String[] args) throws IOException, TimeoutException {
      	// ...
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "utf-8");
                System.out.println("Consumer[2] received msg: " + msg);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("Consumer[2] consumed");
                }
            }
        };
        boolean autoAck = true;
        channel.basicConsume(QUEUE_NAME, autoAck, consumer);
    }
}
```

```
Send Server:
msg sent: Message0
msg sent: Message1
msg sent: Message2
msg sent: Message3
...
---------------------------------------------------------------------------------------------------------------------
Receive1 Server:
Consumer[1] received msg: Message0
Consumer[1] consumed
Consumer[1] received msg: Message2
Consumer[1] consumed
Consumer[1] received msg: Message4
Consumer[1] consumed
...
---------------------------------------------------------------------------------------------------------------------
Receive2 Server:
Consumer[2] received msg: Message1
Consumer[2] consumed
Consumer[2] received msg: Message3
Consumer[2] consumed
Consumer[2] received msg: Message5
Consumer[2] consumed
...
```

> Receive1和Revceive2处理消息数量相同，交替消费，不关心当前消费者的消费能力。这种模式叫做轮询分发（rounf-robin）



### 公平分发（Fair Dispatch）

Send

```
// 消费者发送确认消息之前，消息队列不发送下一个消息给消费者，一次只处理一个消息
// 限制发送给消费者不得超过一条消息
int perfetchCount = 1;
channel.basicQos(perfetchCount);
```

Receive1

```java
//4.一次只消费一条消息
int perfetchCount = 1;
channel.basicQos(perfetchCount);
//5.定义消费者
Consumer consumer = new DefaultConsumer(channel) {
    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        String msg = new String(body, "utf-8");
        System.out.println("Consumer[1] received msg: " + msg);
        try {
        	Thread.sleep(1000);
        } catch (InterruptedException e) {
        	e.printStackTrace();
        } finally {
        	System.out.println("Consumer[1] consumed");
        	channel.basicAck(envelope.getDeliveryTag(), false);	// 手动应答
        }
    }
};
// 6.关闭自动应答
boolean autoAck = false;
channel.basicConsume(QUEUE_NAME, autoAck, consumer);
```

Receive2

```java
//4. 一次只消费一条消息
int perfetchCount = 1;
channel.basicQos(perfetchCount);
//5.定义消费者
Consumer consumer = new DefaultConsumer(channel) {
    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        String msg = new String(body, "utf-8");
        System.out.println("Consumer[2] received msg: " + msg);
    try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Consumer[2] consumed");
            channel.basicAck(envelope.getDeliveryTag(), false);	// 手动应答
        }
    }
};
//6.关系自动应答
boolean autoAck = false;
channel.basicConsume(QUEUE_NAME, autoAck, consumer);
```

```
Send Server：
msg sent: Message0
msg sent: Message1
msg sent: Message2
msg sent: Message3
msg sent: Message4
msg sent: Message5
...

Receive1 Server:
Consumer[1] received msg: Message0
Consumer[1] consumed
Consumer[1] received msg: Message2
Consumer[1] consumed
Consumer[1] received msg: Message4
Consumer[1] consumed
Consumer[1] received msg: Message5
...

Receive2 Server:
Consumer[2] received msg: Message1
Consumer[2] consumed
Consumer[2] received msg: Message3
Consumer[2] consumed
Consumer[2] received msg: Message6

现象：Receive2比Receive1处理消息多，能者多劳动
```

> 消费者每次只接收一条消息，消费完成后手动反馈，再接收下一条消息。用`chanel.basicQos(perfetch = 1)`设置。==使用公平分发，必须关闭自动应答，改为手动==







## 消息应答与消息持久化

### 消息应答（Message Acknowledgement）

- 自动确认模式

```java
boolean autoAck = true;
channel.basicConsume(QUEUE_NAME, autoAck, consumer);
```

一旦RabbitMQ将消息分发给消费者，就会从内存中删除

==弊端：如果正在执行的消费者挂了，就会丢失消息==



- 手动确认模式

```java
boolean autoAck = false;
channel.basicConsume(QUEUE_NAME, autoAck, consumer);
```

消费者处理完完成后发送一个消息应答，告知RabbitMQ可以删除消息，如果消费者挂掉，消息会被交付给其他消费者，RabbiltMQ判断是否将消息重新投递给消费者的唯一依据是消费该消息的消费者是否已经连接断开。应答默认是打开的，即`autoAck = false`

==弊端：如果RabbitMQ挂掉了，消息还是会丢失==

### 消息持久化

第一步，交换器的持久化

```java
// 参数1 exchange ：交换器名
// 参数2 type ：交换器类型
// 参数3 durable ：是否持久化
channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);
```

第二步，队列的持久化

```java
// 参数1 queue ：队列名
// 参数2 durable ：是否持久化
// 参数3 exclusive ：仅创建者可以使用的私有队列，断开后自动删除
// 参数4 autoDelete : 当所有消费客户端连接断开后，是否自动删除队列
// 参数5 arguments
channel.queueDeclare(QUEUE_NAME, true, false, false, null);
```

第三步，消息的持久化

```java
// 参数1 exchange ：交换器
// 参数2 routingKey ： 路由键
// 参数3 props ： 消息的其他参数,其中 MessageProperties.PERSISTENT_TEXT_PLAIN 表示持久化
// 参数4 body ： 消息体
channel.basicPublish("", queue_name, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
```

> rabbitMQ不允许重新定义（参数不同）一个已经存在的队列，所以在队列声明后直接改变参数是不可以的





## 订阅模式（Publish/Subscribe）

![img](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505181258.png)



```
1.一个生产者，多个消费者
2.每个消费者都有自己的队列
3.生产者没有将消息直接发送到队列，而是发送到交换器（exchange）
4.每个队列都要绑定到交换器上
5.生产者发送的消息经由交换器到达队列，实现一个消息被多个消费者消费
```

==交换器没有存储能力，在RabbitMQ里面只有队列有存储能力==

创建消费者并声明交换器

```java
public class Send {
    private static String EXCAHNGE_NAME = "test_exchange_fanout";
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        // 声明交换器
        channel.exchangeDeclare(EXCAHNGE_NAME, "fanout");
        String msg = "hello publish subscribe";
        // 发送消息
        channel.basicPublish(EXCAHNGE_NAME, "", null, msg.getBytes());
        System.out.println("Send msg: " + msg);
        channel.close();
        connection.close();
    }
}
```

创建消费者，声明队列，绑定交换器

```java
public class Recv1 {
    private static String EXCAHNGE_NAME = "test_exchange_fanout";
    private static String QUEUE_NAME = "test_queue_fanout_email";
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionUtil.getConnection();
        final Channel channel = connection.createChannel();
        // 队列声明
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        // 绑定到交换器
        channel.queueBind(QUEUE_NAME, EXCAHNGE_NAME, "");
		// 监听队列，打印接收到的消息
        // ...
    }

}
```

创建另一个消费者，声明队列，绑定交换器

```bash
#Send Server Console:
Send msg: hello publish subscribe

#Recv1 Server Console:
Recv1 received: hello publish subscribe
Recv1 consumed

#Recv2 Server Console:
Recv2 received: hello publish subscribe
Recv2 consumed
```

![1566549727517](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505181259.png)







## 路由模式（Routing）

### 交换器（Exchange）

- 扇形交换器：Fanout exchange
- 直连交换器：Direct exchange
- 主题交换器：Topic exchange
- 首部交换器：Headers exchange

#### 扇形交换器

扇形交换器会把接收到的消息全部发送给绑定到自己身上的消息队列，速度是所有交换机类型里最快的

![1566558586519](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505181300.png)





#### 直连交换器

直连交换器根据消息携带的路由键(`routing key`)将消息投递给对应的队列

```
1.将一个队列绑定到一个交换器，同时赋予该绑定一个路由键(routing key)
2.当一个携带着路由值为R的消息被发送到直连交换器，交换器会把消息路由给绑定值同样为R的队列
```

![1566558698443](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505181301.png)

使用场景：有优先级的任务，可以根据任务的优先级将任务消息送到对应的优先级，可以指派更多的资源去处理高优先级的队列。



#### 主题交换器

在直连交换器中，如果希望一条消息发送发给多个队列，那么这个交换器就需要绑定上非常多的`routing_key`，假设每个交换器都绑定一堆的`routing_key`到各个队列上，消息的管理就会很困难。

主题交换器的`routing_key`需要有一定的规则，交换机和队列的`binding_key`需要采用`*.#.*.....`的格式，每个部分用`.`分开，其中：

- `*`表示一个单词
- `#`表示任意数量（零个或多个）个单词

假设有一条消息的`routing_key`为`fast.rabbit.white`,那么带有以下`binding_key`的几个队列都会收到这个消息

`fast.*.*`

`*.*.white`

`fast.#`

`*.*.*`

![img](assets/1479657-48e5409a26f0c75b.webp)





#### 首部交换器

首部交换器是忽略`routing_key`的一种路由方法。路由器和交换器路由的规则是通过Header信息来交换的，这个有点像`HTTP`的`Headers`。将一个交换器声明为首部交换器，绑定一个队列的时候，定义一个Hash的数据结构，消息发送的时候，会携带一组hash数据结构的信息，当Hash的内容匹配上的时候，消息就会被写入队列。

绑定交换器和队列的时候，Hash结构中要求携带一个键"x-match"，这个键的`Value`可以是`any`或者`all`，这代表消息携带的`Hash`是需要全部匹配（all），还是仅匹配一个键（any）就可以了。相比直连交换器，首部交换器的优势是匹配的规则不被限定为限定字符串（String）

### 路由模式

![img](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505181302.png)

创建生产者，声明直连交换器，发送指定`routingKey`的消息

```java
public class Send {
    private static String EXCHANGE_NAME = "test_routing_exchange";
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        // 声明交换器，直连模式
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        String msg = "Hello Routing";
        System.out.println("Producer send msg: "+ msg);
        // routingKey
        String routingKey = "info";
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, msg.getBytes());
        channel.close();
        connection.close();
    }
}
```

消费者1，将队列绑定到直连交换器，指定多个`routingKey`

```java
public class Recv1 {
    private static String EXCAHNGE_NAME = "test_routing_exchange";
    private static String QUEUE_NAME = "test_routing_queue1";
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionUtil.getConnection();
        final Channel channel = connection.createChannel();
        // 声明队列,绑定到直连交换器
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.basicQos(1);
        channel.queueBind(QUEUE_NAME, EXCAHNGE_NAME, "info");
        channel.queueBind(QUEUE_NAME, EXCAHNGE_NAME, "warning");
        channel.queueBind(QUEUE_NAME, EXCAHNGE_NAME, "error");

        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body);
                System.out.println("Recv1 received msg: " + msg);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    System.out.println("Recv1 consumed");
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, consumer);
    }
}
```

消费者2，将队列绑定到直连交换器，指定一个`routingKey`

```java
public class Recv2 {
    private static String EXCAHNGE_NAME = "test_routing_exchange";
    private static String QUEUE_NAME = "test_routing_queue2";
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionUtil.getConnection();
        final Channel channel = connection.createChannel();
        // 声明队列,绑定到直连交换器
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.basicQos(1);
        channel.queueBind(QUEUE_NAME, EXCAHNGE_NAME, "error");
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body);
                System.out.println("Recv2 received msg: " + msg);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    System.out.println("Recv2 consumed");
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, consumer);
    }
}
```

```bash
#Send Server Console:
Producer send msg: Hello Routing

#Recv1 Server Console:
Recv1 received msg: Hello Routing
Recv1 consumed

#Recv Sever Console:

```





## 主题模式（topic）

![img](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505181303.png)

```
Topic Exchange 将路由键（rounting_key）与某模式（binding_key）匹配
```



创建Producer，声明主题交换器，发送`routing_key=goods.delete`的消息

```java
public class Send {
    private static String EXCHANGE_NAME = "test_exchange_topic";
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        // 声明Topic Exchange
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        String msg = "Goods delete";
        System.out.println("Producer send msg: " + msg);
        // 发送消息 routingKey=goods.delete
        channel.basicPublish(EXCHANGE_NAME, "goods.delete", null, msg.getBytes());
        channel.close();
        connection.close();
    }
}
```

创建消费者1，将队列绑定到主题交换器，监听`routing_key=goods.add`的消息

```java
public class Recv1 {
    private static String EXCHANGE_NAME = "test_exchange_topic";
    private static String QUEUE_NAME = "test_queue_topic_1";
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionUtil.getConnection();
        final Channel channel = connection.createChannel();
        // 声明队列,绑定到topic交换器
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.basicQos(1);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "goods.add");

        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body);
                System.out.println("Recv1 received msg: " + msg);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    System.out.println("Recv1 consumed");
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, consumer);
    }
}
```

创建消费者1，将队列绑定到主题交换器，监听`routing_key=goods.*`的消息

```java
public class Recv2 {
    private static String EXCHANGE_NAME = "test_exchange_topic";
    private static String QUEUE_NAME = "test_queue_topic_2";
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionUtil.getConnection();
        final Channel channel = connection.createChannel();
        // 声明队列,绑定到topic交换器
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.basicQos(1);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "goods.*");

        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body);
                System.out.println("Recv1 received msg: " + msg);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    System.out.println("Recv1 consumed");
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, consumer);
    }
}

```

```bash
#Send Server Console:
Producer send msg: Goods delete

#Recv1 Server Console:

#Recv2 Server Console:
Recv1 received msg: Goods delete
Recv1 consumed
```



## RPC模式

![img](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505181304.png)

使用 RabbitMQ 实现 RPC，相应的角色是由生产者来作为客户端，消费者作为服务端。

RPC 调用一般是同步的，客户端和服务器也是紧密耦合的。即客户端通过 IP/域名和端口链接到服务器，向服务器发送请求后等待服务器返回响应信息。

但 MQ 的生产者和消费者是完全解耦的，那么如何用 MQ 实现 RPC 呢？很明显就是把 MQ 当作中间件实现一次双向的消息传递：



**请求信息的队列**

我们需要一个队列来存放请求信息，客户端向这个队列发布请求信息，服务端消费该队列处理请求。该队列不需要复杂的路由规则，直接使用 RabbitMQ 默认的 direct exchange 来路由消息即可。

**响应信息的队列**

存放响应信息的队列不应只有一个。如果存在多个客户端，不能保证响应信息被发布请求的那个客户端消费到。所以应为每一个客户端创建一个响应队列，这个队列应该由客户端来创建且只能由这个客户端使用并在使用完毕后删除，这里可以使用 RabbitMQ 提供的排他队列（Exclusive Queue）：

```
channel.queueDeclare(queue:"", durable:false, exclusive:true, autoDelete:false, new HashMap<>())
```

并且要保证队列名唯一，声明队列时名称设为空 RabbitMQ 会生成一个唯一的队列名。

`exclusive`设为`true`表示声明一个排他队列，排他队列的特点是只能被当前的连接使用，并且在连接关闭后被删除。

## RabbitMQ的消息确认机制

在RabbitMQ中，通过持久化数据解决RabbitMQ服务器异常的数据丢失问题



如何确保生产者将消息送出后一定到达RabbitMQ服务器

### 事务机制

```
txSelect: 开启事务
txCommit: 提交事务
txRollback: 回滚事务
```

创建生产生产者`Send.java`，采用事务机制管理消息发送

```java
public class Send {
    private static String QUEUE_NAME = "test_queue_tx";
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String msg = "Transaction test";
        try{
            channel.txSelect();
            channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
            System.out.println("send msg: " + msg);
            channel.txCommit();
        }catch (Exception e){
            channel.txRollback();
        }finally {
            channel.close();
            connection.close();
        }
    }
}
```

==不足：消息吞吐量低==

### Confirm模式

生产者将信道设置成confirm模式，一旦信道进入confirm模式，所有在该信道上面发布的消息都会被指派成一个唯一的ID（从1开始），一旦消息被投递到所有匹配的队列后，broker就会发送一个确认给生产者（包含消息的唯一ID），这就使得生产者知道消息已经到达目的队列了，如果消息和队列是可持久化的，那么确认消息会在消息写入磁盘后发出，broker回传给生产者的确认消息中deliver-tag域包含了确认消息的序列号，此外broker也可以设置basic.ack的multiple域，表示到这个序列号之前的所有消息都已经得到了处理

在channel 被设置成 confirm 模式之后，所有被 publish 的后续消息都将被 confirm（即 ack） 或者被nack一次。但是没有对消息被 confirm 的快慢做任何保证，并且同一条消息不会既被 confirm又被nack 。

==优势：异步==



**开启confirm模式**

生产者通过调用channel的confirmSelect方法将channel设置为confirm模式，如果没有设置no-wait标志的话，broker会返回confirm.select-ok表示同意发送者将当前channel信道设置为confirm模式（no-wait默认为false）

```java
// 开启confirm模式
channel.confirmSelect()  
```

> 已经在transaction事务模式的channel是不能再设置成confirm模式的，即这两种模式是不能共存的。



**编程模式**

- 普通confirm模式。每发送一条消息后，调用waitForConfirms()方法，等待服务端confirm，实际上是一种串行的confirm了
- 批量confirm模式。每发送一批消息后，调用waitForConfirms()方法，等待服务端confirm
- 异步confirm模式。提供一个回调方法，服务端confirm了一条或多条消息后Client会回调这个方法

普通confirm模式。

```java
public class SingleSender {
    private static String QUEUE_NAME = "test_queue_single";
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        // 开启Confirm模式
        channel.confirmSelect();
        String msg = "Single Confirm";
        channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
        if(channel.waitForConfirms()){
            System.out.println("Send msg: [" + msg + "] success");
        }else{
            System.out.println("Send msg: [" + msg + "] failed");
        }
    }
}
```

```bash
# SingleSender Server:
Send msg: [Single Confirm] success
```



批量confirm模式。

能极大提升confirm效率，但是一旦出现confirm返回false或者超时的情况事时，客户端需要将这一批的消息全部重发，会带来明显的重复消息数量，当消息频繁丢失时，批量confirm的效率应该是不升反降的

```java
public class BatchSender {
    private static String QUEUE_NAME = "test_queue_batch";
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        // 开启Confirm模式
        channel.confirmSelect();
        for(int i = 0; i < 10; i++){
            String msg = "Batch Confirm (" + i + ")";
            channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
        }

        if(channel.waitForConfirms()){
            System.out.println("Send msgs success");
        }else{
            System.out.println("Send msgs  failed");
        }
    }
}
```



异步confirm模式。

异步confirm模式的编程实现最复杂，Channel对象提供的ConfirmListener()回调方法只包含deliveryTag（当前Chanel发出的消息序号），我们需要自己为每一个Channel维护一个unconfirm的消息序号集合，每publish一条数据，集合中元素加1，每回调一次handleAck方法，unconfirm集合删掉相应的一条（multiple=false）或多条（multiple=true）记录。从程序运行效率上看，这个unconfirm集合最好采用有序集合SortedSet存储结构。实际上，SDK中的waitForConfirms()方法也是通过SortedSet维护消息序号的。

```java
public class AsycSender {
    private static String QUEUE_NAME = "test_queue_async";
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        // 开启Confirm模式
        channel.confirmSelect();
        final SortedSet<Long> confirmSet = Collections.synchronizedSortedSet(new TreeSet<Long>());
        channel.addConfirmListener(new ConfirmListener() {
            // 发送成功的
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                if(multiple){
                    System.out.println("--handleAck--multiple");
                    confirmSet.headSet(deliveryTag+1).clear();
                }else{
                    System.out.println("--handleAck--multiple false");
                    confirmSet.remove(deliveryTag);
                }
            }

            // 发送失败的
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                if(multiple){
                    System.out.println("--handleAck--multiple");
                    confirmSet.headSet(deliveryTag+1).clear();
                }else{
                    System.out.println("--handleAck--multiple false");
                    confirmSet.remove(deliveryTag);
                }
            }
        });

        String msg = "Aync confirm";
        for(int i = 0; i < 10; i++){
            long seqNo = channel.getNextPublishSeqNo();
            channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
            confirmSet.add(seqNo);
        }
    }
}
```



broker将在下面的情况中对消息进行confirm

```
1.broker发现当前消息无法被路由到指定的queue中（如果设置了mandatory属性，则broker会发送basic.return）
2.非持久属性的消息到达了其所应该到达的所有queue中(和镜像queue中)
3.持久消息到达了其所应该到达的所有queue中（和镜像queue中），并被持久化到了磁盘
4.持久消息从其所在的所有queue中被consume了（如果必要则会被ack）
```



## Spring集成RabbitMQ-client

引入依赖。

```java
<dependency>
	<groupId>org.springframework.amqp</groupId>
	<artifactId>spring-rabbit</artifactId>
	<version>2.1.8.RELEASE</version>
</dependency>
```



Spring配置。

```java
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:rabbit="http://www.springframework.org/schema/rabbit"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/rabbit http://www.springframework.org/schema/rabbit/spring-rabbit.xsd"> <!-- bean definitions here -->


    <!--定义RabbitMQ的连接工厂-->
    <rabbit:connection-factory id="connectionFactory" host="127.0.0.1" port="5672" username="tonyne" password="123456" virtual-host="/vhost_tonyne"/>

    <!--定义Rabbit模板，指定连接工厂以及定义exchange-->
    <rabbit:template id="amqpTemplate" connection-factory="connectionFactory" exchange="fanoutExchange"/>

    <!--MQ的管理，包括队列，交换器声明等-->
    <rabbit:admin connection-factory="connectionFactory"/>

    <!--定义队列，自动声明-->
    <rabbit:queue name="myQueue" auto-declare="true"/>

    <!--定于交换器，自动声明-->
    <rabbit:fanout-exchange name="fanoutExchange" auto-declare="true">
        <rabbit:bindings>
            <rabbit:binding queue="myQueue"/>
        </rabbit:bindings>
    </rabbit:fanout-exchange>

    <!--队列监听-->
    <rabbit:listener-container connection-factory="connectionFactory">
        <rabbit:listener ref="myConsumer" method="listen" queue-names="myQueue"/>
    </rabbit:listener-container>

    <!--消费者-->
    <bean id="myConsumer" class="com.java.rabbitmq.spring.MyConsumer"/>
</beans>
```



Producer

```java
public class SpringMain {
    public static void main(String[] args) throws InterruptedException {
        AbstractApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
        RabbitTemplate template = applicationContext.getBean(RabbitTemplate.class);
        template.convertAndSend("Hello Spring Rabbit");
        Thread.sleep(1000);
    }
}
```



Consumer

```java
public class MyConsumer {
    public void listen(String msg){
        System.out.println("Consumer Received :" + msg);
    }
}
```

