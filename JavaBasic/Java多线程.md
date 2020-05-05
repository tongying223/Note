## Java多线程（基础）

[TOC]

#### 1.  创建线程的方式

1. 继承Thread类，重写run()方法

```java
public class MyThread extends Thread {
    @Override
    public void run(){
        System.out.println("Thread body");
    }

    public static void main(String[] args) {
        MyThread thread = new MyThread();
        thread.start();
    }
}
```

2. 实现Runnable接口，实现run()方法

```java
public class MyThread implements Runnable {
    @Override
    public void run() {
        System.out.println("Thread body");
    }

    public static void main(String[] args) {
        MyThread thread = new MyThread();
        // 创建代理类对象
        Thread t = new Thread(thread);
        t.start();
    }
}
```

- 推荐用法，避免Java单继承的局限性
- 方便共享资源，一份资源，多个代理

共享资源——抢票

```java
public class Web12306 implements Runnable {
    private int tickets = 100;
    @Override
    public void run() {
        while (tickets > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "---->" + tickets--);
        }
    }

    public static void main(String[] args) {
        Web12306 web = new Web12306();
        Thread thread1 = new Thread(web, "AAA");
        Thread thread2 = new Thread(web, "BBB");
        Thread thread3 = new Thread(web, "CCC");

        thread1.start();
        thread2.start();
        thread3.start();
    }
}
```

> ...
>
> CCC---->4
> BBB---->4
> AAA---->3
> BBB---->2
> CCC---->2
> AAA---->1
> CCC---->0
> BBB---->-1

从运行结果可以看出，有的线程抢到了同一张票，有的线程抢到了不存在的票，引起了线程不安全问题，也就是数据不准确

3. 实现Callable接口，重写call()方法

```java
import java.util.concurrent.*;
public class MyThread implements Callable {
    @Override
    public String call(){
        return "Hello Java multithreading!";
    }

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        // 启动线程
        Future<String> future = threadPool.submit(new MyThread());
        System.out.println("waiting thread to finish");
        try {
            System.out.println(future.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        threadPool.shutdownNow();
    }
}
```

- Callable可以在任务结束后提供一个返回值
- Callable中的call()方法可以抛出异常
- 运行Callable可以拿到一个Future对象，Future对象表示异步计算的结果。当调用Future的get()方法以获取结果时，当前线程就会阻塞，直到call()方法结束返回。

> 一个类是否可以同时继承Thread并实现Runnable？
>
> 可以

```java
public class Test extends Thread implements Runnable{
    public static void main(String[] args) {
        new Thread(new Test()).start();
        //new Test().start();       // 也可以启动线程
    }
}
```

继承的run()方法可以当作对Runnable接口的实现



#### 2. Lamda表达式推导

- 避免匿名内部类定义过多

- 其实质是函数式编程

1. 静态内部类实现

```java
public class LamdaThread{
    static class StaticClass implements Runnable{
        // 静态内部类
        @Override
        public void run() {
            for(int i = 0; i < 20; i++){
                System.out.println("i = " + i);
            }
        }
    }
    
    public static void main(String[] args) {
        new Thread(new StaticClass()).start();
    }
}
```

2. 局部内部类实现

```java
public class LamdaThread{
    public static void main(String[] args) {
        class localClass implements Runnable{
            // 局部内部类
            @Override
            public void run() {
                System.out.println("局部内部类实现");
            }
        }

        new Thread(new localClass()).start();
    }
}
```

3. 匿名内部类实现

```java
public class LamdaThread{
    public static void main(String[] args) {
       new Thread(new Runnable() {
           // 匿名内部类必须借助接口或父类
           @Override
           public void run() {
               System.out.println("匿名内部类实现");
           }
       }).start();
    }
}
```

4. Lamda简化

```java
public class LamdaThread{
    public static void main(String[] args) {
        new Thread(() -> {
            System.out.println("Lamda简化");
        }).start();
         // new Thread(() -> System.out.println("Lamda简化")).start();
    }
}
```

- 接口只能有一个方法
- Lamda推导必须存在类型

```java
interface Ilike{
    void lamda();
}

public class LamdaTest {
    public static void main(String[] args) {
        // Lamda实现，必须存在类型
        Ilike like = () -> {
            System.out.println("I like Lamda5!");
        };
        like.lamda();
    }
}
```

- 带参数的Lamda

```java
public class LamdaTest {
    public static void main(String[] args) {
        Ilove love = (String str) ->{
            System.out.println(str);
        };
        love.love("I love coding");
    }
}

interface Ilove{
    void love(String str);
}
```

- 参数类型可以省略

```java
Ilove love = (str) ->{
	System.out.println(str);
};
```

- 若参数只有一个，括号也能省略

```java
Ilove love = str -> {
    System.out.println(str);
};
```

- 若实现语句只有一行代码，连花括号都能省略

```java
Ilove love = str -> System.out.println(str);
```

- 带返回值的Lamda，实现语句只有一个return，把return、参数类型、花括号全部省略

```java
public class LamdaTest {
    public static void main(String[] args) {
        IAdd iAdd = (a, b) -> a + b;
        System.out.println(iAdd.add(1, 1));
    }
}

interface IAdd{
    int add(int a, int b);
}
```

#### 3. 线程状态

![1557052359277](C:\Users\tongy\AppData\Roaming\Typora\typora-user-images\1557052359277.png)

- 阻塞状态不能直接回到运行状态
- 进入死亡状态的线程不能重新开启

![1557052397070](C:\Users\tongy\AppData\Roaming\Typora\typora-user-images\1557052397070.png)

- 线程进入就绪状态的情况：

> start()
>
> 阻塞状态解除
>
> yield()
>
> JVM将CPU从本地线程切换到其他线程

- 线程进去阻塞状态的情况

> sleep()
>
> wait()
>
> join()
>
> I/O

- 线程进入死亡状态的情况

> 线程运行结束
>
> 强制结束(stop()  不推荐使用)

#### 4. 多线程 终止

- 不使用JDK提供的stop()/destroy()方法
- 提供一个bool型的终止变量，当这个变量重置为false，则终止线程的运行

```java
class Study implements Runnable{
    // 1. 线程类中定义线程体使用的标识
	private boolean flag = true;
	@Override
	public void run(){
        // 2. 线程体使用该标识
		while(flag){
			System.out.println("Study Thread");
		}
	}
	// 3. 对外提供方法改变标识
	public void terminate(){
		this.flag = false;
	}
}
```

#### 5. 多线程 暂停

- sleep()指定当前线程阻塞的毫秒数
- sleep存在异常InterruptedException
- sleep时间达到后线程进入就绪状态
- sleep可以模拟网络延时、倒计时等
- 每个对象都有一个锁，**sleep不会释放锁**

```java
public class Web12306 implements Runnable {
    private int tickets = 100;
    @Override
    public void run() {
        while (tickets > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "---->" + tickets--);
        }
    }
}
```

#### 6. 多线程 礼让

- yield礼让线程，让当前正在执行的线程暂停
- 不是阻塞线程，而是将线程从**运行状态**转入**就绪状态**

- 让CPU重新调度，不一定礼让成功

```java
public class YieldDemo{
    public static void main(String[] args) {
        new Thread(new MyYieldThread(), "Thread1").start();
        new Thread(new MyYieldThread(), "thread2").start();
    }
}

class MyYieldThread implements Runnable{

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + "-->start");
        Thread.yield();
        System.out.println(Thread.currentThread().getName() + "-->end");
    }
}
```

> thread2-->start
> Thread1-->start
> thread2-->end
> Thread1-->end

#### 7. 多线程 插队

- join合并线程，待此线程执行完成后，再执行其他线程，其他线程阻塞

```java
public class JoinDemo implements Runnable{
    public static void main(String[] args) {
        JoinDemo joinDemo = new JoinDemo();
        Thread t = new Thread(joinDemo);
        t.start();
        for(int i = 0; i < 100; i++){
            System.out.println("main ==>" + i);
            if(i == 50){
                try {
                    t.join();   // t插队，main阻塞
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
       for(int i = 0; i < 100; i++){
           System.out.println("join-->" + i);
       }
    }
}
```

#### 8. 深入线程状态

（1）**新建（NEW）**：创建后尚未启动的线程的状态

（2）**运行（RUNNABLE）**：包含Running和Ready

（3）**无限期等待（WAITING）**：不会被分配CPU执行时间，需要显式被唤醒

```java
没有设置Timeout参数的Object.wait()方法

没有设置Timeout参数的Thread.join()方法

LockSupport.park()方法
```

（4）**限期等待（TIMED_WAITING）**：在一定时间后会由系统自动唤醒

```java
Thread.sleep()方法

设置了Timeout参数的Object.wait()方法

设置了Timeout参数的Thread.join()方法

LockSupport.parkNanos()方法

LockSupport.parkUntil()方法
```

（5）**阻塞（BLOCKED）**：等待获取排他锁

（6）**结束（TERMINATED）**：已中止线程的状态，线程已经结束执行

```java
Thread.activeCount();	// 当前活动的线程数
```

#### 9. 多线程 优先级

线程的优先级用数字表示，范围1-10

- Thread.MIN_PRIORITY = 1
- Thread.MAX_PRIORITY = 10
- Thread.NORM_PRIORITY = 5

使用下述方法获得或设置线程对象的优先级

- int getPriority()
- void setPriority(int newPriority)

**优先级的设定建议在start()调用前**

> 优先级低只是意味着获得调度的概率低，并不是绝对先调用优先级高，再调用优先级低的线程

```java
public class PriorityThread implements Runnable {
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + "-->" + Thread.currentThread().getPriority());
    }

    public static void main(String[] args) {
        PriorityThread pt = new PriorityThread();
        Thread t1 = new Thread(pt, "A");
        Thread t2 = new Thread(pt, "B");
        Thread t3 = new Thread(pt, "C");
        Thread t4 = new Thread(pt, "D");
        Thread t5 = new Thread(pt, "E");
        Thread t6 = new Thread(pt, "F");

        t1.setPriority(Thread.MIN_PRIORITY);
        t2.setPriority(Thread.MIN_PRIORITY);
        t3.setPriority(Thread.NORM_PRIORITY);
        t4.setPriority(Thread.NORM_PRIORITY);
        t5.setPriority(Thread.MAX_PRIORITY);
        t6.setPriority(Thread.MAX_PRIORITY);

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
    }
}
```

> D-->5
> E-->10
> F-->10
> A-->1
> B-->1
> C-->5

#### 10. 守护线程

- 线程分为用户线程和守护线程

- 虚拟机必须确保用户线程执行完毕

- 虚拟机不用等待守护线程执行完毕

- 如记录操作日志、监控内存使用等

  

```java
public class DaemonThread implements Runnable {
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + ": begin");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + ": end");
    }

    public static void main(String[] args) {
        System.out.println("main: begin");
        Thread t = new Thread(new DaemonThread(), "daemon thread");
        t.setDaemon(true);
        t.start();
        System.out.println("main: end");
    }
}
```

> main: begin
> main: end
> daemon thread: begin

控制台并未打印daemon: end，因为当JVM中只有守护线程运行时，JVM会自动关闭。

- 守护线程的一个典型例子就是垃圾回收器，只要JVM启动，它始终在运行，时时监控和管理系统中可以被回收的资源

#### 11.  线程的其他方法

| 方法          | 功能                                         |
| ------------- | -------------------------------------------- |
| isAlive()     | 判断线程是否还活着，集是否还未终止           |
| setName()     | 设置线程名称                                 |
| getName()     | 获取线程名称                                 |
| currentThread | 取得当前正运行的线程对象，也就是获取自己本身 |

#### 12. 线程不安全

- 并发

  同一个对象多个线程同时操作，如，同时操作同一账户、同时购买同一车次的票

- 线程不安全

  数据出现错误

- 线程不安全——购票

```java
public class BuyTicket implements Runnable{
    private boolean flag = true;
    private int tickets = 10;
    @Override
    public void run() {
        while(flag){
            buy();
        }
    }

    private void buy(){
        if(tickets <= 0){
            flag = false;
            return;
        }
        try {
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() + "-->" + tickets--);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        BuyTicket buyTicket = new BuyTicket();
        new Thread(buyTicket, "A").start();
        new Thread(buyTicket, "B").start();
        new Thread(buyTicket, "C").start();
    }
}
```

> C-->3
> B-->2
> A-->2
> C-->1
> A-->0
> B-->-1

- 线程不安全——集合操作

```java
import java.util.ArrayList;
import java.util.List;

public class CollectionsOperation{
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        for(int i = 0; i < 10000; i++){
            new Thread(() ->{
                list.add(Thread.currentThread().getName());
            }).start();
        }
        System.out.println(list.size());
    }
}
```

> 9983

有些数据被覆盖了

#### 13. 多线程 队列与锁

- 线程同步

  线程同步，即一种等待机制，多个需要同时访问此对象的线程进入这个对象的等待池形成队列，等待前面的线程使用完毕后，下一个线程再使用

- 锁机制

  由于同一进程的多个线程共享同一块存储空间，在带来方便的同时，也带来了访问冲突的问题。为了保证数据在方法中被访问时的正确性，在访问时加入锁机制（synchronized），当一个线程获得对象的排他锁，独占资源，其他线程必须等待，使用后释放锁即可。存在以下问题

  - 一个线程持有锁会导致其他所有需要此锁的线程挂起
  - 在多线程竞争下，加锁、释放锁会导致比较多的上下文切换和调度延迟，引起性能问题
  - 如果一个优先级高的线程等待一个优先级低的线程会导致优先级倒置，引起性能问题。

  

- synchronized关键字

  - synchronized方法

  ```java
  public synchronized void method(int args){}
  ```

  synchronized方法控制对"成员变量|类变量"对象的访问：每个对象对应一把锁，每个synchronized方法都必须获得调用该方法的对象的锁才能执行，否则所属线程阻塞，方法一旦执行，就独占该锁，直到从该方法返回时才将锁释放，此后被阻塞的线程方能获得该锁

  **缺陷：**若将一个大的方法声明为synchronized将会大大影响效率

  - synchronized块

  

#### 14. 多线程 synchronized方法

- 买票问题优化

```java
public class BuyTicket implements Runnable{
    private boolean flag = true;
    private int tickets = 10;
    @Override
    public void run() {
        while(flag){
            buy();
        }
    }

    private synchronized void buy(){
        if(tickets <= 0){
            flag = false;
            return;
        }
        try {
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() + "-->" + tickets--);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        BuyTicket buyTicket = new BuyTicket();
        new Thread(buyTicket, "A").start();
        new Thread(buyTicket, "B").start();
        new Thread(buyTicket, "C").start();
    }
}
```

- 并不是简单的把synchronized加到方法上就行，会存在锁错对象而加锁失败的情况

```java
public class UnsafeDrawingMoney{
    public static void main(String[] args) {
        Account account = new Account(100, "工资");
        new DrawingMoney(account, 80, "A").start();
        new DrawingMoney(account, 60, "B").start();
    }
}

class DrawingMoney extends Thread {

    Account account;    // 取钱的账户
    int drawingMoney;   // 要取多少钱
    int packetMoney;    // 口袋里的钱

    public DrawingMoney(Account account, int drawingMoney, String name){
        super(name);
        this.account = account;
        this.drawingMoney = drawingMoney;
    }

    @Override
    public void run() {
        draw();
    }

    public synchronized void draw(){
        if(account.money - drawingMoney < 0){
            return;
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        account.money -= drawingMoney;
        packetMoney += drawingMoney;
        System.out.println(this.getName() + "-->账户余额为:" + account.money);
        System.out.println(this.getName() + "-->口袋里的钱为:" + packetMoney);
    }
}

class Account{
    int money;
    String name;
    public Account(int money, String name){
        this.money = money;
        this.name = name;
    }
}
```

> A-->账户余额为:-40
> B-->账户余额为:-40
> A-->口袋里的钱为:80
> B-->口袋里的钱为:60

这里synchhronized锁的对象为this，即DrawingMoney对象，然而实际上应该锁Account才对



#### 15. synchronized块

```java
同步块： synchronizd (obj) {},obj称之为同步监视器
```

- obj可以是任何对象，但是推荐使用共享资源作为同步监视器
- 同步方法中无需指定同步监视器，因为同步方法的同步监视器是this即该对象本身，或class即类的模子

- 取钱问题优化

```java
public class SafeDrawingMoney{
    public static void main(String[] args) {
        Account account = new Account(100, "工资");
        new DrawingMoney(account, 80, "A").start();
        new DrawingMoney(account, 60, "B").start();
    }
}

class DrawingMoney extends Thread {

    Account account;    // 取钱的账户
    int drawingMoney;   // 要取多少钱
    int packetMoney;    // 口袋里的钱

    public DrawingMoney(Account account, int drawingMoney, String name){
        super(name);
        this.account = account;
        this.drawingMoney = drawingMoney;
    }

    @Override
    public void run() {
        draw();
    }

    public void draw(){
        // 性能优化
        if(account.money <= 0){
            return;
        }
        // 同步块
        synchronized (account) {
            if (account.money - drawingMoney < 0) {
                return;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            account.money -= drawingMoney;
            packetMoney += drawingMoney;
            System.out.println(this.getName() + "-->账户余额为:" + account.money);
            System.out.println(this.getName() + "-->口袋里的钱为:" + packetMoney);
        }
    }


}

class Account{
    int money;
    String name;
    public Account(int money, String name){
        this.money = money;
        this.name = name;
    }
}

```

> A-->账户余额为:20
> A-->口袋里的钱为:80



- 集合操作线程安全优化

```java
public class CollectionsOperation{
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        for(int i = 0; i < 10000; i++){
            new Thread(() ->{
                // 同步块，list作为同步监视器
                synchronized (list) {
                    list.add(Thread.currentThread().getName());
                }
            }).start();
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(list.size());
    }
}
```

> 10000

#### 16. synchronized同步性能分析

- 将买票问题由同步方法修改为同步块，但是粒度太大，性能偏低

```java
public class BuyTicket implements Runnable{
    private boolean flag = true;
    private int tickets = 10;
    @Override
    public void run() {
        while(flag){
            buy();
        }
    }

    private void buy(){
        synchronized (this) {
            if (tickets <= 0) {
                flag = false;
                return;
            }
            try {
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() + "-->" + tickets--);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        BuyTicket buyTicket = new BuyTicket();
        new Thread(buyTicket, "A").start();
        new Thread(buyTicket, "B").start();
        new Thread(buyTicket, "C").start();
    }
}
```

- 锁资源时，对象不能变（对象属性可以变）

```java
private void buy(){
    // 加锁失败，tickets对象一直在变
    synchronized ((Integer)tickets) {
        if (tickets <= 0) {
            flag = false;
            return;
        }
        try {
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() + "-->" + tickets--);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

> ...
>
> C-->3
> B-->4
> C-->2
> A-->2
> C-->1
> B-->1

- 锁定范围过小也会存在锁不住的情况

```java
private void buy(){
    // 加锁失败，加锁的范围太小
    synchronized (this) {
        if (tickets <= 0) {
            flag = false;
            return;
        }
    }
    try {
        Thread.sleep(1000);
        System.out.println(Thread.currentThread().getName() + "-->" + tickets--);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
```

> ...
>
> A-->2
> B-->3
> A-->0
> B-->1
> C-->0

- synchronized同步块应该尽可能锁定合理的范围（不是指代码，指数据的完整性）

```java
private void buy(){
    // double check
    if (tickets <= 0) { // 考虑没有票的情况，不然没有票也要排队等待，降低了系统性能
        flag = false;
        return;
    }
    synchronized (this) {
        if (tickets <= 0) { // 考虑临界值的问题
            flag = false;
            return;
        }
        try {
            Thread.sleep(100);
            System.out.println(Thread.currentThread().getName() + "-->" + tickets--);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

> C-->10
> C-->9
> C-->8
> C-->7
> C-->6
> C-->5
> C-->4
> C-->3
> C-->2
> C-->1

#### 17. 多线程 购买电影票

```java
public class HappyCinema {
    public static void main(String[] args) {
        List<Integer> available = new ArrayList<>();
        available.add(1);
        available.add(2);
        available.add(3);
        available.add(4);
        available.add(5);
        available.add(6);
        Cinema cinema = new Cinema(available, "HappyCinema");
        List<Integer> annaSeats = new ArrayList<>();
        annaSeats.add(1);
        annaSeats.add(3);
        List<Integer> bellaSeats = new ArrayList<>();
        bellaSeats.add(3);
        bellaSeats.add(4);
        new Thread(new Customer(cinema, annaSeats), "Anna").start();
        new Thread(new Customer(cinema, bellaSeats), "Bella").start();
    }
}

// 影院
class Cinema{
    List<Integer> available;  // 剩余电影票数量
    String name;       // 名称
    public Cinema(List<Integer> available, String name){
        this.available = available;
        this.name = name;
    }

    // 购票
    boolean bookTicket(List<Integer> seats){
        System.out.println("可用位置为" + available);
        List<Integer> temp = new ArrayList<>();
        temp.addAll(available);
        temp.removeAll(seats);
        if(available.size() - seats.size() != temp.size()){
            return false;
        }
        available = temp;
        return true;
    }
}

// 顾客
class Customer implements Runnable{

    Cinema cinema;
    List<Integer> seats;

    public Customer(Cinema cinema, List<Integer> seats) {
        this.cinema = cinema;
        this.seats = seats;
    }

    @Override
    public void run() {
        synchronized (cinema) {
            boolean flag = cinema.bookTicket(seats);
            if (flag) {
                System.out.println(Thread.currentThread().getName() + "购票成功,位置为" + seats);
            } else {
                System.out.println(Thread.currentThread().getName() + "购票失败");
            }
        }
    }
}
```



#### 18. 多线程 购买火车票

```java
package com.interview.javabasic.syn;

public class HappyWeb12306 {

    public static void main(String[] args) {
        Web12306 web12306 = new Web12306(2);
        new Passenger(web12306, "Anna", 1).start();
        new Passenger(web12306, "Bella", 2).start();
    }
}

// 购票网站，用同步方法只能锁定this
class Web12306 implements Runnable{

    private int available;

     Web12306(int available) {
        this.available = available;
    }

    // 购票
    private synchronized boolean bookTicket(int num){
        System.out.println("当前余票: " + available + "张");
        if(available < num){
            return false;
        }
        available -= num;
        return true;
    }

    @Override
    public void run() {
        Passenger passenger = (Passenger)Thread.currentThread();
        boolean flag = bookTicket(passenger.num);
        if(flag){
            System.out.println(Thread.currentThread().getName() + "--> 成功购票" + passenger.num + "张");
        }else{
            System.out.println(Thread.currentThread().getName() + "-->购票失败，余票不足");
        }
    }
}

// 乘客
class Passenger extends Thread{
    int num;
    Passenger(Runnable target, String name, int num) {
        super(target, name);
        this.num = num;
    }
}
```



#### 19. 多线程 并发容器

- ArrayList对应的线程安全容器CopyOnWriteArrayList

```java
import java.util.concurrent.CopyOnWriteArrayList;

public class SynContainer {
    public static void main(String[] args) {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
        for(int i = 0; i < 10000; i++){
            new Thread(() ->{
                list.add(Thread.currentThread().getName());
            }).start();
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(list.size());
    }
}
```

#### 20. 多线程 死锁

- 某一个同步块同时拥有“两个以上对象的锁”时，就可能发生“死锁”的问题

```java
public class DeadBlock {
    public static void main(String[] args) {
        new Makeup(0, "Anna").start();
        new Makeup(1, "Belle").start();
    }
}

// 镜子
class Mirror{

}

// 口红
class Lipstick{

}

// 化妆
class Makeup extends Thread{

    // 一面镜子，一根口红
    static Lipstick lipstick = new Lipstick();
    static Mirror mirror = new Mirror();

    int choice;
    String girlName;

    public Makeup(int choice, String girlName) {
        this.choice = choice;
        this.girlName = girlName;
    }

    @Override
    public void run(){
        makeup();
    }

    private void makeup(){
        if(choice == 0){
            synchronized (lipstick){
                System.out.println(this.girlName + "拥有口红");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (mirror){
                    System.out.println(this.girlName + "拥有镜子");
                }
            }
        }else{
            synchronized (mirror){
                System.out.println(girlName + "拥有镜子");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lipstick){
                    System.out.println(this.girlName + "拥有口红");
                }
            }
        }
    }
}
```

> Belle拥有镜子
> Anna拥有口红
>
> (程序无法正常结束)

- 解决办法

  不要在一个代码块中同时持有多个对象的锁（不要嵌套锁）

```java
// 避免死锁：不要在一个代码块中同时持有多个对象的锁
public class DeadBlock {
    public static void main(String[] args) {
        new Makeup(0, "Anna").start();
        new Makeup(1, "Belle").start();
    }
}

// 镜子
class Mirror{

}

// 口红
class Lipstick{

}

class Makeup extends Thread{

    // 一面镜子，一根口红
    static Lipstick lipstick = new Lipstick();
    static Mirror mirror = new Mirror();

    int choice;
    String girlName;

    public Makeup(int choice, String girlName) {
        this.choice = choice;
        this.girlName = girlName;
    }

    @Override
    public void run(){
        makeup();
    }

    private void makeup(){
        if(choice == 0){
            synchronized (lipstick){
                System.out.println(this.girlName + "拥有口红");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            synchronized (mirror){
                System.out.println(this.girlName + "拥有镜子");
            }
        }else{
            synchronized (mirror){
                System.out.println(girlName + "拥有镜子");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            synchronized (lipstick){
                System.out.println(this.girlName + "拥有口红");
            }
        }
    }
}
```

> Belle拥有镜子
> Anna拥有口红
> Anna拥有镜子
> Belle拥有口红

#### 21. 多线程 生产者消费者模式

> 问题描述

生产者和消费者线程并发执行，在两者之间设置了一个具有n个缓冲区的缓冲池，生产者进程将其所生产的产品放入一个缓冲区中，消费者线可从一个缓冲区中取走产品去消费，它们之间的同步方式为既不允许消费者进程到一个空缓冲区去取产品，也不允许生产者进程向一个已装满产品且尚未被取走的缓冲区中投放产品

Java提供了三个方法解决线程之间的通信问题

| 方法名                        | 作用                                                         |
| ----------------------------- | ------------------------------------------------------------ |
| final void wait()             | 表示线程一直等待，直到其他线程通知，与sleep不同，会释放锁    |
| fibal void wait(long timeout) | 指定等待的毫秒数                                             |
| final void notify()           | 唤醒一个处于等待状态的线程                                   |
| final void notifyAll()        | 唤醒同一个对象上所有调用wait()方法的线程，优先级别高的线程优先调度 |

- 均是java.lang.Object类的方法，都只能在同步方法或者同步代码块中使用，否则会抛出异常

#### 22. 多线程 管程法解决生产者消费者问题

```java
public class ProducerAndCustomerBuffer {
    public static void main(String[] args) {
        Buffer buffer = new Buffer();
        new Thread(new Producer(buffer)).start();
        new Thread(new Customer(buffer)).start();
    }
}

// 生产者
class Producer implements Runnable{

    private Buffer buffer;

    Producer(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        for(int i = 0; i < 100; i++){
            System.out.println("==>生产商品" + i);
            buffer.push(new Good(i));
        }
    }
}
// 消费者
class Customer implements Runnable{

    private Buffer buffer;

    Customer(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        for(int i = 0; i < 100; i++){
            System.out.println("消费商品" + buffer.pop().id);
        }
    }
}

// 缓冲区
class Buffer{
    private Good [] goods = new Good[10];
    private int count = 0;  // 计数器

    // 生产商品
    synchronized void push(Good good){
        // 缓冲区满了，则等待
        if(count == goods.length){
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        goods[count] = good;
        count++;
        // 商品放入缓冲区，通知消费者
        this.notifyAll();
    }

    // 消费商品
    synchronized Good pop(){
        // 缓冲区为空，等待
        if(count == 0){
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        count--;
        Good good = goods[count];
        // 消费后缓冲区存在空间，通知生产者
        this.notifyAll();
        return good;
    }
}

// 商品
class Good{
    int id;

     Good(int id) {
        this.id = id;
    }
}
```



#### 23. 多线程 信号灯法解决生产者消费者问题

```java
public class ProducerAndCustomerSignature {
    public static void main(String[] args) {
        Container container = new Container();
        new Thread(new Producer(container)).start();
        new Thread(new Customer(container)).start();
    }
}

// 生产者
class Producer implements Runnable{

    private Container container;

    Producer(Container container) {
        this.container = container;
    }

    @Override
    public void run() {
        for(int i = 0; i < 100; i++){
            container.produce(new Good(i));
        }
    }
}

// 消费者
class Customer implements Runnable{
    private Container container;

    Customer(Container container) {
        this.container = container;
    }

    @Override
    public void run() {
        for(int i = 0; i < 100; i++){
            container.consume();
        }
    }
}

// 共享资源——一个容器
class Container{
    private Good good;              // 商品
    private boolean flag = true;    // 标志位，true——消费者等待， false——生产者等待

    // 生产商品
    synchronized void produce(Good good){
        // flag == false 生产者等待
        if(!flag){
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 生产商品
        this.good = good;
        System.out.println("生产商品" + good.id);
        // 生产好之后通知消费者消费
        flag = false;
        this.notifyAll();
    }

    // 消费商品
    synchronized void consume(){
        // flag == true 消费消费者等待
        if(flag){
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 消费
        System.out.println("消费商品" + good.id);
        // 消费完成，通知生产者
        flag = true;
        this.notifyAll();
    }
}


// 商品
class Good{
    int id;

    Good(int id) {
        this.id = id;
    }
}
```



#### 24 多线程 定时调度

- `java.util.Timer:` 类似闹钟的功能，本身实现的就是一个线程
- `java.util.TimerTask:` 一个抽象类，该类实现了runnable接口，所以该类具备多线程的能力

```java
import java.util.*;

public class TimerTest {
    public static void main(String[] args) {
        Timer timer = new Timer();
        //timer.schedule(new Task(), 2000);                  // 执行一次
        //timer.schedule(new Task(), 2000, 1000);     // 执行多次
        Calendar calendar = new GregorianCalendar(2019,5,12,21,53,00);
        timer.schedule(new Task(), calendar.getTime(), 1000);
    }
}

// 任务继承TimerTask, TimerTask已经实现了Runnable接口
class Task extends TimerTask{
    @Override
    public void run() {
        System.out.println("doing task!");
    }
}
```

#### 25 多线程 quartz

- Scheduler 调度器，控制所有调度
- Trigger 触发条件，采用DSL模式
- JobDetail 需处理的Job
- Job 执行逻辑

DSL: Domain-specific language领域特定语言，针对一个特定领域，具有受限表达式的一种计算机程序语言，即领域专用语言，声明式编程：

1. Method Chaining 方法链 Fluent Style流程风格， builder模式构建器
2. Nested Functions 嵌套函数
3. Lamda Expression/Closures
4. Function Sequence

#### 26 多线程 HappenBefore

- **计算机执行指令的步骤**

  ①Fetch指令

  ②解码，取操作数

  ③计算

  ④写回



- **指令重排**

  执行代码的顺序可能与编写代码不一致，即虚拟机优化代码顺序，则为指令重排



- **happenbefore**

  编译器或运行时环境为了优化程序性能而采取的对指令进行重新排序执行的一种手段



- **虚拟机层面**

  为了尽可能减少内存操作速度远慢于CPU运行速度所带来的CPU空置的影响，虚拟机会按照自己的一些规则将程序编写顺序打乱，尽可能充分利用CPU

  ```java
  a = new byte[1024 * 1024]
  ```

  像这种执行速很慢的语句，CPU不会等待它执行结束，可能会先执行后面的代码，并且后面的代码有可能提前结束



- **硬件层面**

  CPU会接收到一批指令按照其规则重排序，同样是基于CPU速度比缓存速度快的原因

- **数据依赖**

如果两个操作数访问同一个变量，且这两个操作数中有一个为写操作，此时这两个操作之间就存在数据依赖。数据依赖分为下列三种类型：

------

 **名称**						**代码示例**											**说明**

写后读					a = 1;b = a;					写一个变量之后，再读这个位置

写后写					a = 1;a = 2;					写一个变量之后，再写这个变量

读后写					a = b;b = 1;					读一个变量之后，再写这个变量

------

上面三种情况，只要重排序两个操作的执行顺序，程序的执行结果将会被改变。所以编译器和处理器在重排时。会遵守数据依赖性，编译器和处理器不会改变存在数据依赖关系的两个操作的执行顺序



```java
/**
 * 指令重排：代码执行顺序与预期的不一致
 */
public class HappenBefore {
    private static int a = 0;
    private static boolean flag = false;
    public static void main(String[] args) {
        for(int i = 0; i < 1000; i++){
            Thread thread1 = new Thread(()->{
                a = 1;
                flag = true;
            });

            Thread thread2 = new Thread(()->{
                if(flag){
                    a *= 1;
                }

                if(a == 0){
                    System.out.println("发生了指令重排：a = " + a);
                }
            });

            thread1.start();
            thread2.start();

            try {
                thread1.join();
                thread2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

> 发生了指令重排：a = 1
>
> （一种可能的结果，未观察到）

#### 27 多线程 volatile

- volatile保证线程间变量的可变性
  - 线程对变量进行修改后，要立刻回写到主内存
  - 线程对变量读取的时候，要从主内存中读，而不是缓存
- volatile是不错的机制，但是volatile不能保证原子性
- 例1：保证可见性

```java
public class VolatileTest {
    static volatile boolean flag = false;

    public static void main(String[] args) {
        new Thread(()->{
            while(!flag){

            }
        }).start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        flag = true;
    }
}
```

> 若不加volatile，程序不会停止
>
> 加了volatile，程序会自动停止

- 例2： 不能保证原子性

```java
public class VolatileAtom {
    public volatile int inc = 0;

    public void increase() {
        inc++;
    }
    public static void main(String[] args) {
        final VolatileAtom volatileAtom = new VolatileAtom();
        for(int i=0;i<10;i++){
            new Thread(() -> {
                for(int j=0;j<1000;j++)
                    volatileAtom.increase();
            }).start();
        }
        while(Thread.activeCount()>2)  //保证前面的线程都执行完
            Thread.yield();
        System.out.println(volatileAtom.inc);
    }
}
```

> 6503

按照代码逻辑，保证每个线程都运行完，又是对volatile修饰的变量进行访问，是不是应该是10000

- volatile不能保证原子性解释

> 例1中，在flag被更新后，因为任务线程的下一个操作是获取数据，所以会从内存获取最新的数据，为false，于是中断了
>
> 例2中，i++ → （获取i, i + 1, 写回），在加入线程1获取i后阻塞，线程2更新i, 线程1虽然知道i被更新了，但是下一个操作是i+1，并不是重新取出i，所以两个线程加完之后i只增了1

#### 28. 多线程 dcl单例模式

- double-checked locking
- volatile

```java
/**
 * 单例模式
 */
public class DoubleCheckedLocking {
    // 1. 提供私有的静态属性
    private static volatile DoubleCheckedLocking instance;

    // 2. 构造器私有化
    private DoubleCheckedLocking(){

    }

    // 3. 提供公共的静态方法获取属性
    public static DoubleCheckedLocking getInstance(){
        if(instance != null){
            return instance;
        }
        synchronized (DoubleCheckedLocking.class) {
            if (instance == null) {
                // new对象的过程： 1. 开辟空间 2. 初始化对象信息 3. 返回对象的地址给引用
                // 可能发生指令重排， 下面的return先执行，于是需要将instance设置为volatile
                instance = new DoubleCheckedLocking();
            }
            return instance;
        }
    }

    public static void main(String[] args) {
        new Thread(()->{
            System.out.println(DoubleCheckedLocking.getInstance());
        }).start();

        System.out.println(DoubleCheckedLocking.getInstance());
    }
}
```

> com.interview.javabasic.syn.DoubleCheckedLocking@214c265e
> com.interview.javabasic.syn.DoubleCheckedLocking@214c265e

#### 29. 多线程 ThreadLocal

- ThreadLocal能够放一个线程级别的变量，其本身能够被多个线程共享使用，并且能够对达到线程安全的目的。也就是，ThreadLocal想在多线程的环境下去保证成员变量的安全，常用的方法，get/set/initialVlaue

- JDK建议ThreadLocal定义为private static

- ThreadLocal最常用的地方就是为每个线程绑定一个数据库连接，HTTP请求，用户身份信息等，这样一个线程的所有调用到的方法都可以非常方便地访问这些资源

  - Hibernate的Session 工具类HibernateUtil
  - 通过不同的线程对象设置Bean属性，保证各个线程Bean对象的独立性

  ```java
  public class ThreadLocalTest01 {
      
      private static ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
      
      public static void main(String[] args) {
          // 设置值
          threadLocal.set(100);
          // 获取值
          System.out.println(Thread.currentThread().getName() + "-->" + threadLocal.get());
          new Thread(new MyRun()).start();
  
      }
  
      public static class MyRun implements Runnable{
  
          @Override
          public void run() {
              threadLocal.set((int)(Math.random() * 100));
              System.out.println(Thread.currentThread().getName() + "-->" + threadLocal.get());
          }
      }
  }
  ```

  > main-->100
  > Thread-0-->14

  可以在初始化ThreadLocal时给它一个初始值

```java
// 匿名内部类方式初始化
private static ThreadLocal<Integer> threadLocal = new ThreadLocal<Integer>(){
    @Override
    protected Integer initialValue() {
        return 200;
    }
};
```



```java
// 调用ThreadLocal的静态方法withInitial()初始化
private static ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(()->200);
```



- 分析上下文环境

```java
public class ThreadLocalTest02 {

    private static ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(()->200);

    public static void main(String[] args) {
        new Thread(new MyRun()).start();

    }

    public static class MyRun implements Runnable{
		// MyRun()是在main线程调用的，所以对threadlocal的改变也只是影响main线程里的threadLocal
        public MyRun(){
            threadLocal.set(100);
            System.out.println(Thread.currentThread().getName() + "-->" + threadLocal.get());
        }

        @Override
        public void run() {
            threadLocal.set((int)(Math.random() * 100));
            System.out.println(Thread.currentThread().getName() + "-->" + threadLocal.get());
        }
    }
}
```

> main-->100
> Thread-0-->21

- InheritableThreadLocal

可以继承上下文环境的数据，将数据拷贝一份给子线程

```java
public class ThreadLocalTest03 {

    private static ThreadLocal<Integer> threadLocal = new InheritableThreadLocal<>();

    public static void main(String[] args) {

        threadLocal.set(100);
        System.out.println(Thread.currentThread().getName() + "-->" + threadLocal.get());

        // 子线程在main线程里面创建，所以会拷贝一份threadLocal的数据给到子线程，所以子线程的值取出来不是null
       new Thread(() -> {
           System.out.println(Thread.currentThread().getName() + "-->" + threadLocal.get());
       }).start();
    }
}
```

> main-->100
> Thread-0-->100





#### 30. 可重入锁

**可重入锁：**

​		如果一个线程试图获取一个已经由它自己持有的锁，那么这个请求会立刻成功并且会将这个锁的计数值加1，而当线程退出同步代码块时，计数器的值将会递减，当计数值等于0时，锁释放。如果没有可重入锁的支持，在第二次企图获得锁时将会进入死锁状态。可重入锁随处可见：

```java
// 第一次获得锁
synchronized(this){
	while(true){
        // 第二次获得同样的锁
		synchronized(this){
			System.out.println("ReentrantLock!");
		}
        try{
            Thread.sleep(1000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
	}
}
```

```java
class ReentrantLockTest{
	public synchhronized void a(){}
	public synchronized void b(){}
	
	public synchronized void all(){
		this.a();	// 此对象的锁计数值已经达到2了
		this.b();
	}
}
```

**可重入锁原理：**

​		不可重入的锁

```java
public class LockTest {
    Lock lock = new Lock();
    // 方法a
    public void a(){
        lock.lock();
        b();
        lock.unlock();
    }

    // 方法b
    public void b(){
        lock.lock();
        System.out.println("do something!");
        lock.unlock();
    }

    public static void main(String[] args) {
        new LockTest().a();
    }
}


class Lock{
    // 是否占用
    private boolean isLocked = false;

    // 加锁
    public synchronized void lock(){
        while(isLocked){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isLocked = true;
    }

    // 释放锁
    public synchronized void unlock(){
        isLocked = false;
        notifyAll();
    }
}
```

> 导致死锁

可重入锁

```java
public class LockTest {
    private ReLock lock = new ReLock();
    // 方法a
    public void a(){
        lock.lock();
        System.out.println("holdCount : " + lock.getHoldCount());
        b();
        lock.unlock();
        System.out.println("holdCount : " + lock.getHoldCount());
    }

    // 方法b
    public void b(){
        lock.lock();
        System.out.println("holdCount : " + lock.getHoldCount());
        System.out.println("do something!");
        lock.unlock();
        System.out.println("holdCount : " + lock.getHoldCount());
    }

    public static void main(String[] args) {
        new LockTest().a();
    }
}


class ReLock{
    // 是否占用
    private boolean isLocked = false;
    // 计数器
    private int holdCount = 0;

    // 持锁线程
    private Thread lockedThread = null;
    // 加锁
    public synchronized void lock(){
        while(isLocked && lockedThread != Thread.currentThread()){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isLocked = true;
        lockedThread = Thread.currentThread();
        holdCount++;
    }

    // 释放锁
    public synchronized void unlock(){
        if(lockedThread == Thread.currentThread()){
            holdCount--;
            if(holdCount == 0){
                isLocked = false;
                lockedThread = null;
                notifyAll();
            }
        }
    }

    public int getHoldCount() {
        return holdCount;
    }
}
```

> holdCount : 1
> holdCount : 2
> do something!
> holdCount : 1
> holdCount : 0

**可重入锁原理：**

​		如果持锁线程为当前线程，则无需等待，直接进行使用，锁计数器加1就行

**ReentrantLock**

```java
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockTest {
    private ReentrantLock lock = new ReentrantLock();
    // 方法a
    public void a(){
        lock.lock();
        System.out.println("holdCount : " + lock.getHoldCount());
        b();
        lock.unlock();
        System.out.println("holdCount : " + lock.getHoldCount());
    }

    // 方法b
    public void b(){
        lock.lock();
        System.out.println("holdCount : " + lock.getHoldCount());
        System.out.println("do something!");
        lock.unlock();
        System.out.println("holdCount : " + lock.getHoldCount());
    }

    public static void main(String[] args) {
        new LockTest().a();
    }
}
```

ReentrantLock实现原理与上述相似

#### 31. CAS

**锁分为两类：**

- 悲观锁：synchronized时独占锁即悲观锁，会导致其他所有需要锁的线程挂起，等待持有锁的线程释放锁
- 乐观锁：每次不加锁而是假设没有冲突而去完成某项操作，如果因为冲突失败就重试，直到成功为止

Compare and Swap 比较并交换

- 乐观锁的实现
- 有三个值：一个当前内存值V、旧的预期值A、将更新的值B。先获取到内存中当前的内存值V。再将内存值V与原值A进行比较，要是相等就修改为B并返回true，否则什么都不做并返回false
- CAS是一组原子操作，不会被外部打断；
- 属于硬件级别的操作（利用CPU的CAS指令，同时借助JNI来完成的非阻塞算法），效率比加锁效率高
- ABA问题：如果变量V初次读取的时候是A。并且再准备赋值的时候检查到它仍然是A，那能说明它的值没有被其他线程修改过吗？如果在这段期间曾经被改成B。然后又改回A，那CAS操作就会误认为它从来没有被修改过

```java
import java.util.concurrent.atomic.AtomicInteger;

public class CAS {
    private static AtomicInteger stock = new AtomicInteger(5);

    public static void main(String[] args) {
        for(int i = 0; i < 5; i++){
            new Thread(()->{
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int leave = stock.decrementAndGet();
               if(leave < 0){
                   System.out.println("抢完了...");
                   return;
               }
                System.out.println(Thread.currentThread().getName() + "抢了一件商品，还剩下" + leave + "件");
            }).start();
        }
    }
}
```



