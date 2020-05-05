# Java面试

## 一、 JVM

### 1. 平台无关性

> Java源码（.java）首先被编译成字节码（.class)，再由不同平台的JVM进行解析，Java语言在不同的平台上运行时不需要进行重新编译，Java虚拟机在执行字节码的时候，把字节码转换成具体平台上的机器指令。



### 2. 类加载机制

#### 2.1 Java运行时数据区



![JVM运行时数据区域](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171249.svg)

- **Class Loader**：依据特定格式，加载Class文件到内存
- **Execution Engine**：对命令进行解析
- **Native Interface**：融合不同开发语言的原生库为Java所用
- **Runtime Data Area**：JVM内存空间结构模型

#### 2.2 反射

1. 概念

Java反射是在运行状态中，对于任意一个类，都能够知道这个类的所有的属性和方法，对于任意一个对戏那个，都能够调用它的所有属性和方法，这种动态获取信息以及动态调用对象方法的功能称为Java语言的反射机制。

2. 常用对象

- `Class`

```
Class类实例表示正在运行的Java应用程序中的类和接口
```

- `Constructor`

```
关于类的单个构造方法的信息以及对它的访问权限
```

- `Field`

```
Field提供有关类或接口的单个字段信息，以及对它的动态访问权限
```

- `Method`

```
Method类提供关于类或接口上单独某个方法的信息
```

3. API

- 获取Class对象

> 1. 已知类和对象的情况下
>
> ```
> 类名.class
> 对象名.getClass
> ```
>
> 2. 未知类和对象的情况下
>
> ```
> Class.forName("包名.类名")（推荐）
> ```

- Constructor类

> 1. 得到某个类的所有构造方法
>
> ```java
> Constructor[] constructors = Class.forName(Java.lang.String).getConstructors();
> ```
>
> 2. 得到指定的构造方法并调用
>
> ```java
> Constructor constructor = Class.forName("java.lang.String").getConstructor(String.class)
> String str = constrctor.newInstance("abc");
> ```
>
> 3. Class类的getInstance()用来调用类的默认构造方法
>
> ```java
> String obj = Class.forName("java.lang.String").newInstance();
> ```

- Field类

> 1. 得到所有的成员变量
>
> ```java
> // 获取所有的public属性，包括父类继承
> Field[] fields = c.getField();
> // 或得所有声明的属性
> Field[] fields = c.getDeclaredField(); 
> ```
>
> 2. 获取指定的成员变量
>
> ```java
> // 共有属性
> Field field = c.getField("name");
> // 任何属性
> Field field = c.getDeclardField("name");
> ```
>
> 3. 设置field变量是否可以访问
>
> ```java
> // 默认是false
> field.setAccessible(true);
> ```
>
> 4. field变量的读取、设置
>
> ```java
> field.get(obj);
> field.set(obj, val);
> ```

- Method类

> 1. 获得所有方法
>
> ```java
> // 获得所有的共有成员方法
> getMethods()
> // 获得所有声明的成员方法
> getDeclaredMethod()
> ```
>
> 2. 获得指定的方法
>
> ```java
> getMethod(String name, Class<?>...parameterTypes);
> getDeclaredMethod(String name, Class<?>...parameterTypes);
> ```
>
> 3. 通过反射执行方法
>
> ```java
> invoke(Object obj, Object...args);
> ```

4. 反射举例

```java
public class Robot {
    private String name;

    public void sayHello2Sb(String sb) {
        System.out.println("Hello, " + sb + "!");
    }

    private String whoAmI() {
        return "I'm " + name;
    }
}
```

```java
public class ReflectSample {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        // 获取class对象
        Class robotClazz = Class.forName("com.interview.reflect.Robot");
        System.out.println("Class name is: " + robotClazz.getName());

        // 获取构造方法进行实例化
        Robot robot = (Robot)robotClazz.getConstructor().newInstance();

        // 获取并调用公有方法sayHello2Sb
        Method getSayHello = robotClazz.getMethod("sayHello2Sb", String.class);
        getSayHello.invoke(robot,"Ann");

        // 获取并设置私有属性name
        Field filed = robotClazz.getDeclaredField("name");
        filed.setAccessible(true);
        filed.set(robot, "Bob");

        // 获取并调用私有方法whoAmI
        Method getWho = robotClazz.getDeclaredMethod("whoAmI");
        getWho.setAccessible(true);
        String myName = (String)getWho.invoke(robot);
        System.out.println("My name is: " + myName);
    }
}
```

总结：反射就是把java类中的成分映射成一个个的对象

#### 2.3 类加载机制

1. 类从编译到执行的过程

- 编译器将Robot.java源文件编译为Robot.class字节码文件
- ClassLoader将字节码转换为JVM中的Class\<Robot>对象
- JVM利用Class<Robot\>对象实例化为Robot对象

2. ClassLoader的种类

- BootStrapClassLoader：C++编写，加载核心库java.*

- ExtClassLoader：Java编写，加载扩展库javax.*

- AppClassLoader：Java编写，加载程序所在目录

- 自定义ClassLoader：Java编写，定制化加载

3. 类加载器的双亲委派机制

![双亲委派机制](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171324.svg)

当需要加载类对象时，当前类加载器会把加载动作委派给父加载器，最后直到Bootstrap ClassLoader，当父加载器无法完成类的加载时，当前类加载器才会尝试自己去加载类。

4. 为什么要使用双亲委派机制去加载类？

    避免多份同样字节码的加载，减少不必要的内存浪费，以及避免产生安全问题。当我们自己写了一个java.lang.Object类，其在加载时会被加载器委派给父加载器，在某一个父加载器中发现已经存在java.lang.Object类，就不会进行加载，从而保证同样的类只有一个能存在于内存中。



#### 2.4 loadClass与forName的区别

1. 类的加载方式

- 隐式加载 new
- 显式加载 loadClass与forName等

2. 类的装载过程

（1）**加载**

- 通过ClassLoader加载class字节码文件，生成Class对对象

（2）**链接**

- 校验：检查加载的class的正确性和安全性
- 准备：为类变量分配存储空间并设置类变量初始值
- 解析：JVM将常量池内的符号引用转换为直接引用

（3）**初始化**

- 执行类变量赋值和静态代码块

3. loadClass与forName的区别

- Class.forName()得到的class是已经初始化完成的
- Classloader.loadClass()得到的是还没有链接的

> 应用举例：
>
> （1）`Class.forName(“com.mysql.jdbc.Driver”);`这里的Driver类就需要进行初始化，以创建Driver对象来创建数据库驱动
>
> （2）在Spring的lazy loading机制中，为了加快Spring容器的启动速度，只需要加载类对象，而不需要进行链接和初始化。

### 3. Java内存模型

![Java内存模型](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171334.svg)



- 线程私有：程序计数器、虚拟机栈、本地方法栈
- 线程共享：MetaSpace、Java堆 

#### 3.1 线程独占部分

1. 程序计数器（Program Counter Register）

- 当前线程所执行的字节码行号指示器（逻辑）
- 改变计数器的值来选取下一条需要执行的字节码指令
- 和线程是一对一的关系即“线程私有”
- 对Java方法记数，如果是Native方法则计数器值为Undefined
- 不会发生内存泄漏

2. Java虚拟机栈（Stack）

![Java内存模型-第 2 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171342.svg)

- Java方法执行的内存模型
- 包含多个栈帧



局部变量表与操作数栈

> 局部变量表：包含方法执行过程中的所有变量
>
> 操作数栈：入栈、出栈、复制、交换、产生消费变量

例子：

```java
public class ByteCodeSample {
    public static int add(int a, int b) {
        int c = 0;
        c = a + b;
        return c;
    }
}
```

进行编译和反编译后截取add方法的JVM指令集：

```assembly
  public static int add(int, int);
    descriptor: (II)I
    flags: (0x0009) ACC_PUBLIC, ACC_STATIC
    Code:
      # 操作数栈深度为2，本地变量容量为3，参数个数为2
      stack=2, locals=3, args_size=2
         0: iconst_0
         1: istore_2
         2: iload_0
         3: iload_1
         4: iadd
         5: istore_2
         6: iload_2
         7: ireturn
      LineNumberTable:
        line 5: 0
        line 6: 2
        line 7: 6
```

执行add(1, 2)

![Java内存模型-第 3 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171350.svg)

- 递归为什么会引发java.lang.StackOverflowError异常

> 递归过深，栈帧超出虚拟机栈深度



- 虚拟机栈过多会引发java.lang.OutOfMemoryError异常

```java
public void stackLeakbyThread(){
	while(true){
		new Thread(){
			public void run(){
				while(true){
				
				}
			}
		}.start();
	}
}
```



3. 本地方法栈

- 与虚拟机栈相似，主要作用于标注了native的方法



#### 3.2 线程共享部分

1. 元空间（MetaSpace）和永久代（PermGen）的区别

- 元空间使用本地内存，而永久代使用的是jvm的内存

    ```
    java.lang.OutOfMemoryError: PermGen space不复存在
    ```



2. MetaSpace相比PermGen的优势

- 字符串常量池存在永久代中，容易出现性能问题和内存溢出
- 类和方法的信息大小难以确定，给永久代的大小指定带来困难
- 永久代会为GC带来不必要的复杂性
- 方便HotSpot与其他JVM如Jrokit的集成



3. Java堆（Heap）

- 对象实例的分配区域
- GC管理的主要区域



#### 3.3 常考题

1. JVM三大性能调优参数-Xms -Xmx -Xss的含义

```shell
java -Xms128m -Xmx128m -Xss256k -jar xxxx.jar
```

- -Xss：规定每个线程虚拟机栈（堆栈）的大小
- -Xms：堆的初始容量
- -Xmx：堆能达到的最大值



2. Java内存模型中堆和栈的区别——内存分配策略

    （1）内存分配策略

- 静态存储：编译时确定每个数据目标在运行时的存储空间需求

- 栈式存储：数据区需求在编译时未知，运行时模块入口前确定

- 堆式存储：编译时或运行时模块入口处都无法确定，动态分配

    （2） 堆和栈的联系

- 联系：引用对象、数组时，栈里定义变量保存堆中目标的首地址

![堆和栈的联系-第 4 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171358.svg)

​	（3）Java内存模型中堆和栈的区别

- 管理方式：栈自动释放，堆需要GC
- 空间大小：栈比堆小
- 碎片相关：栈产生的碎片远小于堆
- 栈支持静态分配和动态分配，而堆仅支持动态分配
- 栈的效率比堆高



3. 元空间、堆、线程独占部分间的联系——内存角度

```java
public class HelloWorld {
    private String name;

    public void sayHello() {
        System.out.println("Hello " + name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public static void main(String[] args) {
        int a = 1;
        HelloWorld hw = new HelloWorld();
        hw.setName("test");
        hw.sayHello();
    }
}
```

- 元空间

```
· Class: HelloWorld - Method: sayHello\setName\main - Field: name
· Class: System
```

- Java堆

```
· Object: String("test")
· Object: HelloWorld
```

- 线程独占

``` 
· Parameter reference: "test" to String object
· Variable reference: "hw" to HelloWorld object
· Local Variables: a with 1, lineNo
```



4. 不同JDK版本之间的intern()方法的区别——JDK6 VS JDK6+

```java
String s = new String("a");
s.intern();
```

- JDK6：当调用`intern`方法时，如果字符串常量池先前已经创建出该字符串对象，则返回池中的该字符串的引用，否则将此字符串对象添加到字符串常量池中，并且返回该字符串对象的引用。
- JDK6+：当调用`intern`方法时，如果字符串常量池先前已创建出该字符串对象，则返回池中该字符串的引用。否则，如果该字符串对象已经存在于Java堆中，则将堆中对此对象的引用添加到字符串常量池中，并且返回该引用，如果堆中不存在，则在池中创建该字符串并返回其引用。

```java
public class InternDifference {
    public static void main(String[] args) {
        String s1 = new String("a");
        s1.intern();
        String s2 = "a";
        System.out.println(s1 == s2);

        String s3 = new String("a") + new String("a");
        s3.intern();
        String s4 = "aa";
        System.out.println(s3 == s4);
    }
}
```

运行结果：

> JDK6
>
> ```
> false
> false
> ```
>
> JDK6+
>
> ```
> false
> true
> ```

Java中用引号声明的字符串都是放在常量池中，而new出来的String对象是在Java堆中。

JDK1.6内存模型

![堆和栈的联系-第 5 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171406.svg)

> (1) `String s1 = new String(“a”);`声明了字符串”a”并将其放在了常量池中，又new了一个String对象放在堆中，`s1.intern()`企图放一个副本到常量池中，因为`”a”`已经存在，所以失败，于是s1引用的是堆中的`”a”`，而s2引用的是常量池中的`”a”`，于是返回`false`
>
> (2)`String s3 = new String(“a”) + new String(“a”);`在堆中生成了一个字符串对象，调用`s3.intern()`将副本`“aa”`放到常量池中，所以s3指向堆中String对象，而s4指向常量池中的`”aa"`



JDK7及以上内存模型

![堆和栈的联系-第 5 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171416.svg)

> (1)`s1=s2`返回false的情况与JDK6类似
>
> (2)`String s3 = new String("a") + new String("a”);`在堆中new了一个对象，调用s3.intern()将其副本放入常量池，并返回其在常量池中的引用，而s4指向的也是常量池中的“aa”，故`s3=s4`返回值为true





## 二、GC

### 1. 标记算法

1. 对象被判定为垃圾的标准

- 没有被其他对象引用



2. 引用计数法

- 通过判断对象的引用数量来决定对象是否可以被回收
- 每个对象实例都有一个引用计数器，被引用则+1，完成引用则-1
- 任何引用计数为0的对象实例可以被当作垃圾收集



优点：

- 执行效率高，程序执行受影响小

缺点：

- 无法检测出循环引用的情况，导致内存泄漏



3. 可达性分析算法

- 通过判断对象的引用链是否可达来决定对象是否可以被回收



**可以作为GC Root的对象**

- 虚拟机栈中引用的对象
- 方法去中的常量引用的对象
- 方法区中的类静态属性引用的对象
- 本地方法栈中JNI（Native方法）的引用对象

- 活跃线程的引用对象

### 2. 回收算法

##### 2.1 标记-清除算法（Mark and Sweep）

- 标记：从根集合进行扫描，对存活对象进行标记
- 清除：对堆内存从头到尾进行线性遍历，回收不可达对象内存

![标记清除算法](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171424.svg)

 **缺点：**

> 碎片化。堆内存中存在大量不连续的碎片。



#### 2.2 复制算法（Copying）

- 分为对象面和空闲面
- 对象在对象面上创建
- 存活的对象被从对象面复制到空闲面
- 将对象面所有对象内存清除



![复制算法](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171431.svg)

**优点：**

> （1）解决碎片化的问题
>
> （2）顺序分配内存，简单高效
>
> （3）适用于对象存活率低的场景



#### 2.3 标记-整理算法（Compacting）

- 标记：从根集合进行扫描，对存活对象进行标记
- 清除：移动所有存活的对象，且按照内存地址次序依次排列，然后将末端内存地址以后的内存全部回收。 

![标记整理算法-第 8 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171437.svg)



**优点：**

> （1）避免内存的不连续性
>
> （2）不用设置两块内存互换
>
> （3）适用于对象存活率高的场景



#### 2.4 分代收集算法（Generational Collector）

- 垃圾回收算法的组合拳
- 按照对象生命周期的不同划分区域以采用不同的垃圾回收肃算法

![标记整理算法-第 9 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171443.svg)





**GC的分类**

- Minor GC
- Full GC



**年轻代**

尽可能快速收集掉那些生命周期短的对象

- Eden区
- 两个Survivor区（From和To）

![GC-第 10 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171449.svg)

对象如何晋升到老年代

- 经历一定Minor次数依然存活的对象
- Survivor区中存放不下的对象
- 新生成的大对象

常用的调优参数

- Eden和Survivor的比例，，默认8:1

```
-XX:SurvivorRatio
```

- 老年代和年轻代内存大小的比例

```
-XX:NewRatio
```

- 对象从年轻代晋升到老年代经过GC次数的最大阈值

```
-XX:MaxTenuringThreshold
```



**老年代**

存放生命周期较长的对象

- 标记-清除算法
- 标记-整理算法
- Full GC和Major GC
- Full GC比Minor GC慢，但执行频率低

触发Full GC的条件

- 老年代空间不足
- 永久代空间不足
- CMS GC时出现promotion failed，concurrent mode failure
- Minor GC晋升到老年代的平均大小大于老年代的剩余空间
- 调用System.gc()
- 使用RMI来进行RPC或管理的JDK应用，每小时执行一次Full GC



### 3. 新生代垃圾回收器

 **Stop-the-World**

- JVM由于要执行GC而停止了应用程序的执行
- 任何一种GC算法中都会发生
- 多数GC优化通过减少Stop-the-world发生的时间来提高程序性能



**Safepoint**

- 分析过程中对象引用关系不会发生变化的点
- 产生Safepoint的地点：方法调用；循环跳转；异常跳转等
- 安全点数量得适中



**JVM的运行模式**

- Server
- Client

![image-20200422012723103](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171456.jpg)



**垃圾收集器之间的联系**

![垃圾收集器关系-第 11 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171526.svg)



**Serial收集器（-XX:+UseSerialGC，复制算法）**

- 单线程收集，进行垃圾收集时，必须暂停所有工作线程
- 简单高效，Clien模式下默认的年轻代收集器

![Serial收集器-第 12 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171534.svg)



**ParNew收集器（-XX:+UseParNewGC，复制算法）**

- 多线程收集，其余的行为、特点和Serial收集器一样
- 单核执行效率不如Serial，在多核下执行才有优势

![ParNewGC-第 13 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171539.svg)



**Parallel Scavenge收集器（-XX:+UseParallelGC，复制算法）**

- 吞吐量=运行用户代码时间/（运行用户代码时间+垃圾收集时间）
- 比起关注用户线程停顿时间，更关注系统的吞吐量
- 在多核下执行才有优势，Server模式下默认的年轻代收集器
- `-XX:+UseAdaptiveSizePolicy`将内存调优交给JVM执行

![ParNewGC-第 13 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171545.svg)



### 4. 老年代垃圾收集器

**Serial Old收集器（-XX:+UseSerialOldGC，标记-整理算法）**

- 单线程执行，进行垃圾收集时，必须暂停所有的工作线程
- 简单高效，Client模式下默认的老年代收集器

![SerialoldGC-第 12 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171552.svg)



**Parallel Old收集器（-XX:+UseParallelOldGC，标记整理算法)**

- 多线程，吞吐量优先

![ParallelOld-第 13 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171558.svg)



**CMS收集器（-XX:+UseConcMarkSWeepGC，标记-清除算法）**

- 初始标记：stop-the-world
- 并发标记：并发追溯标记，程序不会停服
- 并发预清理：查找执行并发标记阶段从年轻代晋升到老年代的对象
- 重新标记：暂停虚拟机，扫描CMS堆中的剩余对象
- 并发清理：清理垃圾对象，程序不会停顿
- 并发重置：重置CMS收集器的数据结构

![CMS收集器](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171603.svg)



**G1收集器（-XX:+UseG1GC，复制+标记-整理算法）**

Garbage First收集器的特点

- 分代和并发
- 分代收集

- 空间整合
- 可预测和停顿

- 将整个Java堆内存划分为多个大小相等的Region
- 年轻代和老年代不再物理隔离

![G1](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171609.svg)



### 5. GC相关的面试题

**1. Object的finalize()方法的作用是否与C++的析构函数作用相同**

- 与C++的析构函数不同，析构函数调用确定，而它的是不确定的
- 将未被引用的对象放置于F-Queue队列
- 方法执行随时可能会被终止
- 给予对象最后一次重生的机会

```java
public class Finalization {
    public static Finalization finalization;

    @Override
    protected void finalize() throws Throwable {
        System.out.println("Finalized");
        finalization = this;
    }

    public static void main(String[] args) {
        Finalization f = new Finalization();
        System.out.println("First print: " + f);
        f = null;
        System.gc();
        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Second print: " + f);
        System.out.println(f.finalization);
    }
}
```

> First print: com.interview.jvm.model.Finalization@2d6e8792
> Finalized
> Second print: nullcom.interview.jvm.model.Finalization@2d6e8792



**2. Java中的强引用，软引用，若引用，虚引用有什么用**

 强引用（Strong Reference）

- 最普遍的引用：Object obj = new Object()
- 抛出OutOfMemoryError终止程序也不会回收具有强引用的对象
- 通过将对象设置为null来弱化引用，使其被回收



软引用（Soft Reference）

- 对象处在有用但非必须的状态
- 只有当内存空间不足时，GC会回收该引用的对象的内存
- 可以用来实现高速缓存

```java
// 强引用
String str = new String("abc");
// 软引用
SoftReference<String> softRef = new SoftRefrence<String>(str);
```



弱引用（Weak Reference)

- 非必须的对象，比软引用更弱一些
- GC时被回收
- 被回收的概率也不大，因为GC线程优先级比较低
- 适用于引用偶尔被使用且不影响垃圾收集的对象

```java
String str = new String("abc");
WeakRefrence<String> abcWeakRef = new WeakRefrence<String>(str);
```



虚引用（PhantomRefrence）

- 不会决定对象的生命周期
- 任何时候都可能被垃圾收集器回收
- 跟踪对下岗被垃圾收集器回收的活动，其哨兵作用
- 必须和引用队列RefenceQueue联合使用

```java
String str = new String("abc");
ReferenceQueue queue = new ReferenceQueue();
PhantomRefrence ref = new PhantomReference(str, queue);
```



总结：

强引用 > 软引用 > 弱引用 > 虚引用

| 引用类型 | 被垃圾回收时间 | 用途           | 生存时间          |
| -------- | -------------- | -------------- | ----------------- |
| 强引用   | 从来不会       | 对象的一般状态 | JVM停止运行时终止 |
| 软引用   | 在内存不足时   | 对象缓存       | 内存不足时终止    |
| 弱引用   | 在垃圾回收时   | 对象缓存       | gc运行后终止      |
| 虚引用   | Unknown        | 标记、哨兵     | Unknown           |



类层次结构

![引用间类关系图](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171620.svg)



引用队列（ReferenceQueue）

- 无实际存储结构，存储逻辑依赖内部节点之间的关系来表达
- 存储关联的且被GC的软引用，若引用以及虚引用



## 三、多线程与并发（基础）

### 1. 进程与线程的区别

**1. 进程和线程的由来**

 ![进程和线程的由来-第 6 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171625.svg)

**2. 进程和线程的区别**

- 进程是资源分配的最小单位，线程是CPU调度的最小单位

    - 所有与进程相关的资源，都被记录在进程PCB中

    - 进程是抢占处理机的调度单位；线程属于某个进程，共享其资源

        ![进程和线程的由来-第 7 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171631.svg)

    - 线程只由堆栈寄存器、程序计数器和TCB组成

        ![进程和线程的由来-第 8 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171637.svg)

- 总结

    ```
    · 线程不能看作独立应用，而进程可以看作独立应用
    · 进程有独立的地址空间，相互不影响，线程只是进程的不同执行路径
    · 线程没有独立的地址空间，多进程的程序比多线程程序健壮
    进程的切换比线程的切换开销大
    ```



**3. Java进程和线程的关系**

- Java对操作系统提供的功能进行封装，包括进程和线程
- 运行一个程序会产生一个进程，进程包含至少一个线程
- 每个进程对应一个JVM实例，多个线程共享JVM里的堆
- Java采用单线程编程模型，程序会自动创建主线程
- 主线程可以创建自线程，原则上要后于自线程完成执行



### 2. 进程的start与run方法的区别

- 调用start()方法会创建一个新的子线程并启动
- run()方法只是Thread的一个普通方法的调用



### 3. Thread与Runnable的关系

- Thread是实现了Runnable接口的类，使得run支持多线程
- 因单一继承原则，推荐多使用Runnable接口



### 4. 如何实现处理线程的返回值

**1. 如何给run()方法传参**

- 构造函数传参
- 成员变量传参
- 回调函数穿参



**2. 如何实现处理线程的返回值**

（1）主线程等待法

```java
public class CycleWait implements Runnable {

    private String value;

    @Override
    public void run() {
        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        value = "Face strong wind!";
    }

    public static void main(String[] args) {
        CycleWait cycleWait = new CycleWait();
        new Thread(cycleWait).start();
        while (cycleWait.value == null) {
            try {
                Thread.currentThread().sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("value = " + cycleWait.value);
    }

```

> value = Face strong wind!

（2）使用Thread类的join()方法阻塞当前线程以等待子线程处理完毕

```java
public class CycleWait implements Runnable {

    private String value;

    @Override
    public void run() {
        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        value = "Face strong wind!";
    }

    public static void main(String[] args) {
        CycleWait cycleWait = new CycleWait();
        Thread t = new Thread(cycleWait);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("value = " + cycleWait.value);
    }
}
```

> value = Face strong wind!

（3）通过callable接口实现：通过FutureTask或者线程池获取

- 通过FutureTask获取

```java
public class MyCallable implements Callable<String> {
    @Override
    public String call() throws Exception {
        String value = "Face strong wind!";
        System.out.println("Ready to work.");
        Thread.currentThread().sleep(5000);
        System.out.println("Work done!");
        return value;
    }
}
```

```java
public class FutureTaskDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask futureTask = new FutureTask(new MyCallable());
        new Thread(futureTask).start();
        System.out.println("FutureTask returned: " + futureTask.get());
    }
}
```

> Ready to work.
> Work done!
> FutureTask returned: Face strong wind!

- 通过线程池获取

```java
public class ThreadPoolDemo {
    public static void main(String[] args) {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        Future<String> future = cachedThreadPool.submit(new MyCallable());
        if (!future.isDone()) {
            System.out.println("Task is not finished, please wait!");
        }
        try {
            System.out.println(future.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            cachedThreadPool.shutdown();
        }
    }
}
```

> Task is not finished, please wait!
> Ready to work.
> Work done!
> Face strong wind!



### 5.线程的状态

- 新建（NEW）：创建后尚未启动的线程的状态

- 运行（RUNNABLE）：包含Running和Ready

- 无限期等待（WAITING）：不会被分配CPU执行时间，需要显式被唤醒

    ```
    没有设置Timeout参数的Object.wait()方法。
    没有设置Timeout参数的Thread.join()方法
    LockSupport.park()方法
    ```

- 限期等待（TIMED_WAITING）：在一定时间后会系统自动唤醒

    ```
    Thread.sleep()方法
    设置了Timeout参数的Object.wait()方法
    设置了Timeout参数的Thread.join()方法
    LockSupport.parkNanos()方法
    LockSupport.parkUntil()方法
    ```

- 阻塞（BLOCKED）：等待获取排它锁

- 结束（TERMINATED）：已终止线程的状态，线程已经结束执行



### 6. sleep与wait的区别

**1. 基本差别**

- sleep是Thread类的方法，wait是Object类中的方法
- sleep()可以在任何地方使用
- wait()方法只能在synchronized方法或synchronized块中使用

**2. 最主要的本质区别**

- Thread.sleep只会让出CPU，不会导致锁行为的改变
- Object.wait不仅让出CPU，还会释放已经占有的同步资源锁



### 7. notify与notifyAll的区别

**1. 锁池**

 假设线程A已经拥有了某个对象（不是类）的锁，而其他线程B、C想要调用这个对象的某个synchronized方法（或者块），由于B、C线程在进入对象的synchronized方法（或者块）之前必须先获得该对象锁的拥有权，而恰巧该对象锁目前正被线程A所占用，此时B、C线程就会被阻塞，进入一个地方等待锁的释放，这个地方便是对象的锁池。



**2. 等待池**

假设线程A调用了某个对象的wait()方法，线程A就会释放该对象的锁，同时线程A就进入到了该对象的等待池中，进入到等待池中的线程不会去竞争该对象的锁。



**3. notify和notifyAll的区别**

- notifyAll会让所有处于等待池的线程全部进入锁池去竞争获取锁的机会
- notify只会随机选取一个处于等待池中的线程进入锁池去竞争获取锁的机会



### 8. yield函数

当调用Thread.yield()函数时，会给线程调度器一个当前线程愿意让出CPU使用的暗示，但是线程调度器可能会忽略这个暗示

 yield函数不会改变锁的行为



#### 9. Interrupt函数

- 调用interrupt()，通知线程应该中断了

    ① 如果线程处于被阻塞状态，那么线程将立即退出被阻塞状态，并抛出一个InterruptedException异常

    ② 如果线程处于正常活动状态，那么会将该线程的中断标志设置为true。被设置中断标志的线程将继续正常运行，不受影响。

- 需要被调用的线程配合中断

    ① 在正常运行任务时，经常检查本线程的中断标志位，如果被设置了中断标志就立即停止线程

    ② 如果线程处于正常活动状态，那么会将该线程的中断标志设置为true。被设置中断标志的线程将继续正常运行，不受影响。

```java
public class InterruptDemo {
    public static void main(String[] args) throws InterruptedException {
        Runnable interruptTask = new Runnable() {
            @Override
            public void run() {
                int i = 0;
                try {
                    // 在正常运行时任务时，经常检查本线程的中断标志位，如果被设置了中断标志位就立即停止线程
                    while (!Thread.currentThread().isInterrupted()) {
                        Thread.sleep(100);
                        i++;
                        System.out.println(Thread.currentThread().getName() + "(" + Thread.currentThread().getState() + ") + loop " + i);
                    }
                } catch (InterruptedException e) {
                    // 在调用阻塞方法时正确处理InterruptedException异常。（例如：catch异常就结束线程）
                    System.out.println(Thread.currentThread().getName() + "(" + Thread.currentThread().getState() + ") + catch InterruptedException.");
                }
            }
        };

        Thread t1 = new Thread(interruptTask, "t1");
        System.out.println(t1.getName() + "(" + t1.getState() + ") is new.");

        t1.start();
        System.out.println(t1.getName() + "(" + t1.getState() + ") is started.");

        // 主线程休眠300ms，然后给t1发送"中断"指令
        Thread.sleep(300);
        t1.interrupt();
        System.out.println(t1.getName() + "(" + t1.getState() + ") is interrupted.");

        // 主线程休眠300，然后查看t1状态
        Thread.sleep(300);
        System.out.println(t1.getName() + "(" + t1.getState() + ") is interrupted now.");
    }
}
```





## 四、多线程与并发（原理）

### 1. synchronized

#### 1.1 synchronized

**1. 线程安全问题的主要诱因**

- 存在共享数据（也称临界资源）
- 存在多条线程共同操作这些共享数据

解决的根本办法：

> 同一时刻有且只有一个线程在操作共享数据，其他线程必须等到该线程处理完数据后再对共享数据进行操作



**2. 互斥锁的特征**

互斥性：即在同一时间只允许一个线程持有某个对象锁，通过这些特性来实现多线程的协调机制，这样在同一时间只有一个线程对需要同步的代码块（符合操作）进行访问。互斥性也称为操作的原子性。

可见性：必须确保在锁被释放之前，对共享变量所做的修改，对于随后获得该锁的另一个线程是可见的（即在获得锁时应该获得最新共享变量的值），否则另一个线程可能是在本地缓存的某个副本继续操作，从而引起不一致。

> synchronized锁的不是代码，锁的是对象



**3. 根据获取的锁的分类：获取对象锁和获取类锁**

获取<u>对象锁</u>的两种方法

- 同步代码块（`synchronized(this)`, `synchronized(类实例对象)`），锁是小括号()中的实例对象
- 同步非静态方法（`synchronized method`），锁是当前对象的实例对象



获取<u>类锁</u>的两种用法

- 同步代码块（`synchronized(类.class)`），锁是小括号（）重的类对象（Class对象）
- 同步静态方法（synchronized static method），锁是当前对象的类对象（Class对象）





对象锁和类锁的总结

1. 有线程访问对象的同步代码块时，另外的线程可以访问该对象的非同步代码块。
2. 若锁住的是同一个对象，一个线程在访问对象的同步代码块时，另外一个访问对象的同步代码块的线程会被阻塞。
3. 若锁住的是同一个对象，一个线程在访问对象的同步方法时，另一个访问对象同步方法的线程会被阻塞。
4. 若锁住的是同一个对象，一个线程在访问对象的同步代码块时，另一个访问对象同步方法的线程会被阻塞，反之亦然。
5. 同一个类的不同对象的对象锁互不干扰。
6. 类锁由于也是一种特殊的对象锁，因此表现与上述1，2，3，4一致，而由于一个类只有一把对象锁，所以同一个类的不同对象使用类锁将会是同步的。
7. 类锁和对象锁互不干扰。

#### 1.2 synchronized底层实现原理

**1. 实现synchronized的基础**

- Java对象头
- Monitor



**2. 对象在内存中的布局**

- 对象头
- 实例数据
- 对齐填充



3. **对象头的结构**

- MarkWord，默认存储对象的hashCode，分代年龄，锁类型，锁标志位等信息
- klass，klass类型指针，即对象指向它的类元数据的指针，虚拟机通过这个指针来确定这个对象是哪个类的实例。
- 数组长度（只有数组对象有），如果对象是一个数组，那在对象头中还必须有一块数据用于记录数组长度。



**4. Mark Word**

因为JVM的空间有限，Mark Word被设计成可变的数据结构，可以根据对象的状态复用之前的存储空间。

![Java多线程](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171655.svg)



**5. Monitor**

每个Java对象天生自带了一把看不见的锁

```c++
ObjectMonitor() {
    _header       = NULL;
    _count        = 0;		// 计数器
    _waiters      = 0,
    _recursions   = 0;
    _object       = NULL;
    _owner        = NULL;	// 指向当前持有锁的线程
    _WaitSet      = NULL;	// 等待池
    _WaitSetLock  = 0 ;
    _Responsible  = NULL ;
    _succ         = NULL ;
    _cxq          = NULL ;
    FreeNext      = NULL ;
    _EntryList    = NULL ;	// 锁池
    _SpinFreq     = 0 ;
    _SpinClock    = 0 ;
    OwnerIsThread = 0 ;
    _previous_owner_tid = 0;
  }
```



**6. Monitor锁的竞争、获取与释放**

![Java线程池-第 5 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171702.svg)

**7. 什么是重入**

从互斥锁的设计来看，当一个线程试图操作一个由其他线程持有的对象锁的临界资源时，将会处于阻塞状态，但当一个线程再次请求自己持有的临界资源时，这种情况属于重入。



**8. 自旋锁与自适应自旋锁**

自旋锁

- 许多情况下，共享数据的锁定状态持续时间较短，切换线程不值得

- 通过让线程执行忙循环等待锁的释放，不让出CPU

- 缺点：若锁被其他线程长时间占用，会带来许多性能上的开销

自适应自旋锁

- 自旋的次数不再固定
- 由前一次在同一个锁上的自旋时间即锁的拥有者的状态来决定



**9. 锁消除**

- JIT编译时，对运行上下文进行扫描，去除不可能存在竞争的锁

```java
public class StringBufferWithoutSync {
    /**
     * StringBuffer是线程安全的，由于sb只会在append()方法中使用，
     * 不可能被其他线程引用，因此sb属于不可能共享资源，JVM会自动消除内部的锁
     */
    public void add(String str1, String str2){
        StringBuffer sb = new StringBuffer();
        sb.append(str1).append(str2);
    }

    public static void main(String[] args) {
        StringBufferWithoutSync withoutSync = new StringBufferWithoutSync();
        for(int i = 0; i < 100; i++){
            withoutSync.add("aaa", "bbb");
        }
    }
}
```



**10. 锁粗化**

- 通过扩大加锁的范围，避免反复加锁和解锁

```java
public class StringBufferWithoutSync {
    /**
     * StringBuffer是线程安全的，由于sb只会在append()方法中使用，
     * 不可能被其他线程引用，因此sb属于不可能共享资源，JVM会自动消除内部的锁
     */
    public void add(String str1, String str2){
        StringBuffer sb = new StringBuffer();
        sb.append(str1).append(str2);
    }

    public static void main(String[] args) {
        StringBufferWithoutSync withoutSync = new StringBufferWithoutSync();
        for(int i = 0; i < 100; i++){
            withoutSync.add("aaa", "bbb");
        }
    }
}
```



**11. synchronized的四种状态**

- 无锁、偏向锁、轻量级锁、重量级锁

锁膨胀的方向：无锁 → 偏向锁 → 轻量级锁 → 重量级锁



**12. 偏向锁：减少同一线程获取锁的代价**

- 大多数情况下，锁不存在多线程竞争，总是由同一线程多次获得

核心思想：

如果一个线程获得了锁，那么锁就进入偏向模式，此时Mark Word的结构也变为偏向锁结构，当该线程再次请求锁时，无需再做任何同步操作，即获取锁的过程只需要检查Mark Word的锁标记位为偏向锁及当前线程ID等于Mark Word的ThreadID即可，这样就省去了大量有关锁申请的操作。

不适用于锁竞争比较激烈的多线程场合。



**13. 轻量级锁**

轻量级锁是由偏向锁升级来的，偏向锁运行在一个线程进入同步块的情况下，当第二个线程加入锁争用的时候，偏向所就会升级为轻量级锁。

适应的场景：线程交替执行同步块。



**14. 锁的内存语意**

当线程释放锁时，Java内存模型会把该线程对应的本地内存中的共享变量刷新到主内存中

而当线程获取锁时，Java内存模型会把该线程对应的本地内存置为无效，从而使得被监视器保护的临界区代码必须从住内存中读取共享变量。



**15. 偏向锁、轻量级锁、重量级锁的汇总**

![Java多线程-第 2 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171711.svg)



#### 1.3 synchronized与ReentrantLock的区别

**1. ReentrantLock**

- 位于java.util.concurrent.locks包
- 和CountDownLatch、FutureTask、Semaphore一样基于AQS实现

- 能够实现比synchronized更细粒度的控制，如控制fairness
- 调用lock()之后，必须调用unlock释放锁

- 性能未必比sychronized高，并且也是可重入的



**2. ReentrantLock公平性设置**

```java
ReentrantLock fairLock = new ReentrantLock(true)
```

- 参数为true时，倾向于将锁赋予等待时间最久的线程
- 公平锁：获取锁的顺序按先后调用lock方法的顺序（慎用）
- 非公平锁：抢占的顺序不一定，看运气
- synchronized是非公平锁



**3. ReentrantLock将锁对象化**

- 判断是否有线程，或者某个特定线程，在排队等待获取锁
- 带超时的获取锁的尝试

- 感知有没有成功获取锁



**4. 总结**

- synchronized是关键字，ReentranLock是类
- ReentranLock可以获取锁的等待时间进行设置，避免死锁
- ReentranLock可以获取各种锁的信息
- ReentranLock可以灵活地实现多路通知
- 机制：synchronized操作Mark Word，lock调用Unsafe类的park()方法

### 2. jmm的内存可见性

**1. Java内存模型**

Java内存模型（Java Memory Model，简称JMM）本身是一种抽象的概念，并不真实存在，它描述的是一组规则或规范，通过这组规范定义了程序中各个变量（包括实例字段，静态字段和构成数组对象的元素）访问方式。

![JMM](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171717.svg)



**2. JMM中的主内存**

- 存储Java实例对象
- 包括成员变量、类信息、常量、静态变量等
- 属于数据共享的区域，多线程并发操作时会引发线程安全问题



**3. JMM的工作内存**

- 存储当前方法的所有本地变量信息，本地变量对其他线程不可见
- 字节码行号指示器、Native方法信息
- 属于线程私有数据区域，不存在线程安全问题



**4. JMM与Java内存区域划分是不同的概念层次**

- JMM描述的是一组规则，围绕原子性、有序性、可见性展开
- 相似点：存在共享区域和私有区域



**5. 主内存与工作内存的数据存储类型以及操作方式归纳**

- 方法里的基本数据类型本地变量将直接存储在工作内存的栈帧结构中
- 引用类型的本地变量：引用存储在工作内存中，实例存储在主内存中
- 成员变量、static变量、类信息均会被存储在主内存中
- 主内存共享的方式是线程各拷贝一份数据到工作内存，操作完成后刷新回主内存



**6. 指令重排序需要满足的条件**

- 在单线程环境下不能改变程序运行的结果
- 存在数据依赖关系的不允许重排序

> 无法通过happens-before原则推导出来的，才能进行指令的重排序



**7. A操作的结果需要对B操作可见，则A与B存在happens-befoere关系**

```java
i = 1;	// 线程A执行
j = i;	// 线程B执行
```



**8. happens-before的八大原则**

1. 程序次序规则：一个线程内，按照代码顺序，书写在前面的操作先行发生于书写在后面的操作；
2. 锁定规则：一个unlock操作先行发生于后面对同一个锁的lock操作；
3. volatile变量规则：对一个变量的写操作先行发生于后面对这个变量的读操作；
4. 传递规则：如果操作A先行发生于操作B，而操作B又先行发生于操作C，则可以得出操作A先行发生于操作C；
5. 线程启动规则：Thread对象的start()方法先行发生于此线程的每一个动作；
6. 线程中断规则：对线程interrupt()方法的调用先行发生于被中断线程的代码检测到中断事件的发生；
7. 线程终结规则：线程中所有的操作都先行发生于线程的终止检测，我们可以通过Thread.join()方法结束、Thread.isAlive()的返回值手段检测线程已经终止执行；
8. 对象终结规则：一个对象的初始化完成先行发生于它的finalize()方法的开始；



**9. happens-befor的概念**

如果两个操作不满足上述任意一个happens-before规则，那么这两个操作就没有顺序的保障，JVM可以对这两个操作进行重排序；

如果操作A happens-before操作B，那么操作A在内存上所做的操作对操作B都是可见的。

```java
// 无法通过happens-before推导出write() happens-before read()，故下面的代码不是线程安全的
private int value = 0;
public void write(int input){
    value = input;
}
public int read(){
    return value;
}
```



**10. volatile：JVM提供的轻量级同步机制**

- 保证volatile修饰的共享变量对所有线程总是可见的
- 禁止指令重排序优化



**11. volatile的可见性**

```java
// 以下代码在多线程环境下并不是线程安全的
// 因为value++操作并不是原子性的
public class VolatileVisibility{
	public static volatile int value = 0;
    
    pubic static void increase(){
        value++;
    }
}

// 修改为线程安全的
// synchronized保证所有线程的执行结果直接刷到主存，可以省去volatile
public class VolatileVisibility{
	public static int value = 0;
    
    pubic synchronized static void increase(){
        value++;
    }
}
```



```java
// 用volatile达到线程安全的目的
public class VolatileSafe{
	volatile boolean shutdown;
    
    public void close(){
        shutdown = true;
    }
    
    public void doWork(){
        while(!shutdown){
            System.out.println("safe...")
        }
    }
}
```



**11. volatile变量为何立即可见**

- 当写一个volatile变量时，JMM会把该线程对应的工作内存中的共享变量值刷新到主内存中
- 当读取一个volatile变量时，JMM会把该线程对应的工作内存置为无效，并从主内存中重新读取



**12. volatile如何禁止指令重排**

- 内存屏障（Memory Barrier）
    - 保证特定操作的执行顺序
    - 保证某些变量的内存可见性

- 通过插入内存屏障指令禁止在内存屏障前后的指令执行重排序优化
- 强制刷出各种CPU的缓存数据，因此任何CPU的线程都能读取到这些数据的最新版本



**13. 单例的双重检测实现**

```java
public class Singleton{
	private volatile static Singleton instance;
	
	private Singleton(){}
	
	public static Singleton getInstance(){
		if(instance == null){
			synchronized(Singleton.class){
				if(instance == null){
                    // 多线程环境下可能会出现问题的地方
					instance == new Singleton();
				}
			}
		}
		return instance;
	}
}
```



上述多线程环境下出现问题的地方

```
memory = allocate();	// 1.分配对象空间
instance(memory); 		// 2.初始化对象
instance = memory(); 	// 3. 设置instance指向刚分配的内存地址，此时instance!=null

重排序：

memory = allocate();	// 1.分配对象空间
instance = memory(); 	// 3. 设置instance指向刚分配的内存地址，此时instance!=null
instance(memory); 		// 2.初始化对象
```



**14. volatile和synchronized的区别**

- volatile本质是告诉JVM当前变量在寄存器（工作内存）中的值是不正确的，需要从主存中读取；synchronized则是锁定当前变量，只有当前线程可以访问该变量，其他线程被阻塞住直到该线程完成变量的操作为止
- volatile仅能使用在变量级别；synchronized则可以使用在变量、方法和类级别
- volatile仅能实现变量的修改可见行，不能保证原子性；而synchronized则可以保证便来给你修改的可见性与原子性
- volatile不会造成线程的阻塞；synchronized则可能造成线程的阻塞
- volatile标记的变量不会被编译起优化；synchronized标记的变量可以被编译器优化



### 3. CAS

**1. 一种高效实现线程安全性的方法**

- 支持原子更新操作，适用于计数器，序列发生器等场景
- 属于乐观锁机制，号称lock-free
- CAS操作失败时由开发者决定是继续尝试还是执行别的操作



**2. CAS思想**

- 包含三个操作数——内存位置（V）、预期原值（A）和新值（B）



**3. CAS多数情况下对开发者来说是透明的**

- J.U.C的atomic包提供了常用的原子性数据类型以及引用、数组等相关原子类型和更新操作工具，是很多线程安装程序的首选
- Unsafe类虽提供CAS服务，但因能够操作任意内存地址读写而有隐患
- Java9后，可以用Variable Handle API来代替Unsafe



**4. 缺点**

- 若循环时间长，则开销很大

- 只能保证一个共享变量的原子操作

- ABA操作

    J.U.C使用AtomicStampedReference来解决。



### 4. Java线程池

**1. 利用Executors创建不同的线程池满足不同场景的需求**

1. `newFixedThreadPool(int nThread)`	

    指定工作线程数量的线程池

2. `newCachedThreadPool()`

    处理大量短时间工作任务的线程池

    （1）试图缓存线程并重用，当无缓存线程可用时，就会创建新的工作线程

    （2）如果线程闲置的时间超过阈值，则会被终止并移出缓存

    （3）系统长时间闲置的时候，不会消耗什么资源

3. `newSingleThreadExecutor()`

    创建唯一的工作线程来执行任务，如果线程异常结束，会有另一个线程取代它。

4. `newSingleThreadScheduledExecutor()`与`newScheduledThreadPool()`

    定时或周期性的工作调度，两者的区别在于单一工作线程还是多线程

5. `newWordStealingPool`

    内部会构建ForkJoinPool，利用working-stealing算法，并行地处理任务，不保证出力顺序



**2. Fork/join框架 **

- 把大任务分割成若干个小任务并执行，最终汇总每个小任务结果后得到大任务结果的框架

    > Work-Stealing算法：某个线程从其他队列里窃取任务来执行

![Java线程池-第 4 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171730.svg)



**3. 为什幺使用线程池**

- 降低资源消耗
- 提高线程的可管理性



**4. Executor的框架**

![Java多线程-第 3 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171736.svg)



**5. J.U.C的三个Executor接口**

- `Executor`：运行新任务的简单接口，将新任务提交和任务执行的细节解耦

    ```java
    Thread t = new Thread();
    executor.execute(t);
    ```

- `ExecutorService`：具备管理执行器和任务生命周期的方法，提交任务机制更完善

-  `ScheduledExecutorService`：支持Future和定期执行任务



**6. ThreadPoolExecutor**

![Java线程池](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171742.svg)



**7. ThreadPoolExecutor的构造函数**

- `corePoolSize`：核心线程数量
- `maximumPoolSize`：线程不够用时能够创建的最大线程数
- `keepAlivetTime`： 最大空闲时间
- `timeUnit`：表示keepAliveTime的单位
- `workQueue`：任务等待队列
- `threadFactory`：创建新线程，`Executors.defaultTreadFActory()`

- `handler`：线程池的饱和策略

    ```
    AbortPolicy：直接抛出异常，这是默认策略
    
    CallerRunsPlicy：用调用者所在的线程来执行任务
    
    DiscardOldestPolicy：丢弃队列中靠最前的任务，并执行当前任务
    
    DiscardPolicy：直接丢弃任务
    
    还可以实现RejectExecutionHandler接口的自定义handler
    ```

    

**8. 新任务提交execute执行后的判断**

- 如果运行的线程少于corePoolSize，则创建新线程来处理任务，即使线程池中的其他线程是空闲的
- 如果线程池中的线程数量大于等于corePoolSize且小于maximumPoolSize，则只有当workQueue满时才创建新的线程去处理任务
- 如果设置corePoolSize和maximumPoolSize相同，则创建的线程池的大小是固定的，这时如果有新任务提交，若workQueue未满，则将请求放入workQueue中，等待有空闲的线程去从workQueue中取任务并处理
- 如果运行的线程数量大于等于maximumPoolSize，这时workQueue已经满了，则通过handler所指定的策略来处理任务

![Java线程池-第 2 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171747.svg)



9. **线程池的状态**

- RUNNING：能接受新提交的任务，并且也能处理阻塞队列中的任务
- SHUTDOWN：不 再接受新提交的任务，但可以处理存量任务
- STOP：不再接受新提交的任务，也不处理存量任务
- TIDYING：所有的任务都已终止
- TERMINATED：terminated()方法执行完后进入该状态



![image-20200426011408067](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171754.jpg)



![Java线程池-第 3 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171804.svg)



**10. 线程池的大小如何选定**

- CPU密集型：线程池=按照核数或者核数+1设定
- I/0密集型：线程数=CPU核数*（1+平均等待时间/平均工作时间）



## 五、Java常用类库

### 1. Java异常体系

**1. Java的异常体系**

![Java异常体系](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171810.svg)



**2. 概念角度**

- Error：程序无法处理的系统错误，编译器不做检查
- Exception：程序可以处理的错误，捕获后可能恢复
    - RuntimeException：不可预知的，程序应当自行避免
    - 非RuntimeException：可预知的，从编译器校验的异常
- 总结：前者是程序无法处理的错误，后者是可以处理的异常



**3. 从责任角度**

- Error属于JVM需要负担的责任
- RuntimeException是程序应该负担的责任
- Checked Exception可检查异常是Java编译器应该负担的责任

```java
public class ErrorAndException {
    private void throwError() {
        throw new StackOverflowError();
    }

    private void throwRuntimeException() {
        throw new RuntimeException();
    }

    // 将异常向上抛出，本层不做处理
    private void throwCheckedException() throws FileNotFoundException {
        throw new FileNotFoundException();
    }

    public static void main(String[] args) {
        ErrorAndException eae = new ErrorAndException();
        eae.throwError();
        eae.throwRuntimeException();
        // 最上层跑不掉，必须进行处理，直接抛出则会导致程序终止
        try {
            eae.throwCheckedException();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
```



**4. 常见Error以及Exception**

- RuntimeException

```
1. NullPointerException - 空指针异常
2. ClassCastException - 类型强制转换异常
3. IllegalArgumentException - 传递非法参数异常
4. IndexOutOfBoundsException - 下标越界异常
5. NumberFotmatException - 数字格式异常
```

- 非RuntimeException

```
1. ClassNotFoundException - 找不到指定的class的异常
2. IOEXception - IO操作异常
```

- Error

```
1. NoClassDefFoundError - 找不到class定义的异常
2. StackOverflowError - 深递归导致栈被耗尽而抛出异常
3. OutOfMemoryError - 内存溢出异常
```

​	NoClassDefFoundError的成因

> 1.类依赖的class或者jar不存在
>
> 2.类文件存在，但是存在不同的域中
>
> 3.大小写问题，javac编译的时候是无视大小写的，很可能编译出来的class文件就与想要的不一样



**5. Java异常处理机制**

- 抛出异常：创建异常对象，交由运行时系统处理
- 捕获异常：寻找合适的异常处理器处理异常，否则终止运行

```java
public class ExceptionHandleMechanism {
    public static int doWork(){
        try{
            int i = 10 / 0;
            System.out.println("i = " + i);
        }catch (ArithmeticException e){
            System.out.println("ArithmeticException: " + e);
            return 0;
        }catch (Exception e){
            System.out.println("Exception: " + e);
            return 1;
        }finally {
            System.out.println("Finally");
            return 2;
        }
    }

    public static void main(String[] args) {
        System.out.println("执行后的值为：" + doWork());
        System.out.println("Mission Complete");
    }
}
```

> ArithmeticException: java.lang.ArithmeticException: / by zero
> Finally
> 执行后的值为：2
> Mission Complete

`finally`里面的`return`会在`catch`语句的`return`之前执行，如果`finally`里面没有`return`语句，则该函数的返回值应该为`0`。

**6. Java异常的处理原则**

- 具体明确：抛出的异常应能通过异常类名和message准确说明异常的类型和产生异常的原因。
- 提早捕获：应尽可能早地发现并抛出异常，便于精确定位问题。
- 延迟捕获：异常的捕获和处理应尽可能延迟，让掌握更多信息的作用域来处理异常。



**7. 高效主流的异常处理框架**

在用户看来，应用系统发生的所有异常都是应用系统内部的异常

- 设计一个通用的继承自RuntimeException的异常来统一处理
- 其余异常都统一转译为上述异常AppException
- 在catch之后，抛出上述异常的子类，并提供足以定位的信息
- 由前端接受AppException做统一处理

![Exception](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171818.svg)

参考学习：org.springframework.core.NestedRuntimeException



**8. Java异常处理消耗性能的地方**

- try-catch快影响JVM的优化
- 异常对象实例需要保存栈快照信息，开销较大



### 2. Collection体系

![Collection](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171823.jpg)

**1. TreeSet**

- 自然排序，实现Comparable接口，实现compareTo方法

```java
public class Customer implements Comparable {
    private String name;
    private int age;

    public Customer(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return age == customer.age &&
                Objects.equals(name, customer.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    @Override
    public int compareTo(Object o) {
        Customer other = (Customer) o;
        if (this.name.compareTo(other.name) > 0) {
            return 1;
        } else if (this.name.compareTo(other.name) < 0) {
            return -1;
        }
        if (this.age > other.age) {
            return 1;
        } else if (this.age < other.age) {
            return -1;
        }
        return 0;
    }

```

- 客户化排序，集合中传入实现了Comparator接口的类对象作为参数进行排序

```java
public class CustomerComparator implements Comparator<Customer> {
    @Override
    // 降序
    public int compare(Customer o1, Customer o2) {
        if (o1.getName().compareTo(o2.getName()) > 0) {
            return -1;
        }
        if (o1.getName().compareTo(o2.getName()) < 0) {
            return 1;
        }
        if (o1.getAge() < o2.getAge()) {
            return 1;
        }
        if (o1.getAge() > o2.getAge()) {
            return -1;
        }
        return 0;
    }

    public static void main(String[] args) {
        Set<Customer> set = new TreeSet<>(new CustomerComparator());
        set.add(new Customer("tom", 5));
        set.add(new Customer("tom", 9));
        set.add(new Customer("tom", 2));
        Iterator<Customer> it = set.iterator();
        while (it.hasNext()) {
            Customer customer = it.next();
            System.out.println(customer.getName() + " " + customer.getAge());
        }
    }
}
```



（1）用自定义的类实现Comparable接口，那么这个类就具有排序的功能（静态绑定排序）。

（2）Comparator不任何类绑定，实现了它的自定义类仅仅定义了一种排序方式或排序规则（动态绑定排序）。



### 3. HashMap

![Map](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171830.svg)



**1. HashMap: put方法的逻辑**

1. 若HashMap未被初始化，则进行初始化操作
2. 对Key求Hash值，依据Hash值计算下标
3. 若未发生碰撞，则直接放入桶中
4. 若发生碰撞，则以链表的方式链接到后面
5. 若链表长度超过阈值，且HashMap元素超过最低树化容量，则将链表转成红黑树
6. 若节点已经存在，则用新值替换旧值
7. 若桶满了（默认容量16*扩容因子0.75），就需要resize（扩容2倍后重排）



**2. HashMap：如何减少碰撞**

- 扰动函数：促使元素位置均匀分布，减少碰撞几率
- 使用final对象，并采用合适的equals()和hashCode()方法



 **3. HashMap：从获取hash到散列的过程**

 ![Hash](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171835.svg)

**4. HashMap：扩容的问题**

- 多线程环境下，调整大小会存在条件竞争，容易造成死锁
- rehashing是一个比较耗时的过程



**5. HashMap：复习重点**

- 成员变量：数据结构，树化阈值
- 构造函数：延迟创建
- put和get的六层
- 哈希算法，扩容，性能

**6. HashMap：为什么HashMap线程不安全**

- JDK1.7 

扩容时采用头插法，多线程环境下形成环。

```java
void transfer(Entry[] newTable, boolean rehash) {
        int newCapacity = newTable.length;
        for (Entry<K,V> e : table) {
            while(null != e) {
                Entry<K,V> next = e.next;
                if (rehash) {
                    e.hash = null == e.key ? 0 : hash(e.key);
                }
                int i = indexFor(e.hash, newCapacity);
                e.next = newTable[i];
                newTable[i] = e;
                e = next;
            }
        }
    }
```



![jdk7-resize](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171841.svg)

- JDK1.8

扩容时不采用头插法，而是先分裂成两个链表，分别放入相应的桶中，但是仍然线程不安全。

put()函数：

```java
final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        if ((tab = table) == null || (n = tab.length) == 0)
            n = (tab = resize()).length;
    	// 线程不安全的地方，桶中链表为空，直接插入元素
        if ((p = tab[i = (n - 1) & hash]) == null)
            tab[i] = newNode(hash, key, value, null);
        else {
            Node<K,V> e; K k;
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
            else if (p instanceof TreeNode)
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            else {
                for (int binCount = 0; ; ++binCount) {
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            treeifyBin(tab, hash);
                        break;
                    }
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;
                }
            }
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        if (++size > threshold)
            resize();
        afterNodeInsertion(evict);
        return null;
    }
```

线程A进行put()操作时检测到`tab[i]`处链表为空，还未插入数据就挂起，线程B在桶中`tab[i]`插入了数据，线程A继续执行，则会覆盖线程B插入的数据。



### 4. ConcurrentHashMap

**1. Hashtable**

- 早起Java类库提供的哈希表的实现
- 线程安全：涉及到修改Hashtable的地方，使用synchronized修饰
- 串行化的方式运行



**2. ConcurrentHashMap：CAS + synchronized使锁更细化**

![ConcurrentHashMap](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171847.svg)



**3. ConcurrenHashMap：put方法的逻辑**

1. 判断Node[]数组是否初始化，没有则进行初始化

2. 通过hash定位数组的索引坐标，是否有Node结点，如果没有则使用CAS进行添加（链表的头结点），添加失败则进入下次循环。

3. 检查到内部正在扩容，就帮助它一块扩容

4. 如果f != null， 则使用synchronized锁住f元素（链表/红黑树的头结点）

    4.1 如果是Node（链表结构）则执行链表的添加操纵

    4.2 如果是TreeNode（树形结构）则执行树添加操作

5. 判断链表长度已经达到临界值8（默认树化长度），链表树化为二叉树。



**4. ConcurrentHashMap：别的需要注意的地方**

- size()方法和mappingCount()方法的异同，两者计算是否准确
- 多线程环境下如何进行扩容



### 5. J.U.C

**1. java.util.concurrent：提供了并发编程的解决方案**

- CAS是java.util.concurrent.atomic包的基础
- AQS是java.util.concurrent.locks包以及一些常用类比如Semophore，ReentrantLock等类的基础

**2. J.U.C包的分类**

- 线程执行器executor
- 锁locks
- 原子变量类atomic
- 并发工具类tools
- 并发集合collections



**3. 并发工具类**

- 闭锁 CountDownLatch
- 栅栏 CyclicBarrier
- 信号量 Semaphore
- 交换器 Exchanger



**4. CountDownLatch：让主线程等待一组事件发生后继续执行**

- 事件指的是CountDownLatch里的countDown()方法

![CountDownLatch](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171853.svg)

```java
public class CountDownLatchDemo {

    public static void main(String[] args) throws InterruptedException {
        new CountDownLatchDemo().go();
    }

    private void go() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(3);
        // 依次创建3个线程并启动
        new Thread(new Task(countDownLatch), "Thread1").start();
        Thread.sleep(1000);
        new Thread(new Task(countDownLatch), "Thread2").start();
        Thread.sleep(1000);
        new Thread(new Task(countDownLatch), "Thread3").start();
        countDownLatch.await();
        System.out.println("所有线程已到达，主线程开始执行" + System.currentTimeMillis());

    }


    private class Task implements Runnable {

        private CountDownLatch countDownLatch;

        public Task(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            System.out.println("线程" + Thread.currentThread().getName() + "已经到达" + System.currentTimeMillis());
            countDownLatch.countDown();
        }
    }
}
```

> ```
> 线程Thread1已经到达1588096238656
> 线程Thread2已经到达1588096239661
> 线程Thread3已经到达1588096240666
> 所有线程已到达，主线程开始执行1588096240667
> ```



**5. CyclicBarrier：阻塞当前线程，等待其他线程**

- 等待其他线程，且会阻塞当前线程，所有线程必须同时到达栅栏位置后，才能继续执行
- 所有线程到达栅栏处，可以触发执行另外一个预先设置的线程

![CyclicBarrier](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171859.svg)

```java
/**
 * CountDownLatch是主线程等待其他自线程执行完之后执行
 * CyclicBarrier是参与的线程之间互相等待，到达栅栏处一起执行
 */
public class CyclicBarrierDemo {

    public static void main(String[] args) throws InterruptedException {
        new CyclicBarrierDemo().go();
    }

    public void go() throws InterruptedException {
        // 初始化栅栏的参与者数量为3
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3);
        // 依次创建3个线程并开始
        new Thread(new Task(cyclicBarrier), "Thread1").start();
        Thread.sleep(1000);
        new Thread(new Task(cyclicBarrier), "Thread2").start();
        Thread.sleep(1000);
        new Thread(new Task(cyclicBarrier), "Thread3").start();
    }

    class Task implements Runnable {
        private CyclicBarrier cyclicBarrier;

        public Task(CyclicBarrier cyclicBarrier) {
            this.cyclicBarrier = cyclicBarrier;
        }

        @Override
        public void run() {
            System.out.println("线程" + Thread.currentThread().getName() + "已经到达" + System.currentTimeMillis());
            try {
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.println("线程" + Thread.currentThread().getName() + "开始处理" + System.currentTimeMillis());
        }
    }
}
```

> ```
> 线程Thread1已经到达1588097517153
> 线程Thread2已经到达1588097518156
> 线程Thread3已经到达1588097519161
> 线程Thread3开始处理1588097519162
> 线程Thread2开始处理1588097519162
> 线程Thread1开始处理1588097519162
> ```

**6. Semaphore：控制某个资源可以被多少个线程同时访问**

![Semaphore](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171904.svg)

```java
public class SemaphoreDemo {
    public static void main(String[] args) {
        // 线程池
        ExecutorService threadPool = Executors.newCachedThreadPool();
        // 只能5个线程同时访问
        final Semaphore semaphore = new Semaphore(5);
        // 模拟20个客户端进行访问
        for (int idx = 0; idx < 20; idx++) {
            final int THREAD_NO = idx;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    try {
                        // 获取许可
                        semaphore.acquire();
                        System.out.println("Accessing: " + THREAD_NO);
                        Thread.sleep((long) Math.random() * 10000);
                        // 访问完成后释放信号量
                        semaphore.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            threadPool.execute(task);
        }
        // 退出线程池
        threadPool.shutdown();
    }
}
```



**7. Exchanger：两个线程到达同步点后，相互交换数据**

![Exchanger](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171911.svg)

```java
public class ExchangerDemo {
    private static Exchanger<String> exchanger = new Exchanger<>();

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        threadPool.execute(() -> {
            try {
                String girlSay = "我其实暗恋你很久了";
                String girlGet = exchanger.exchange(girlSay);
                System.out.println("女生听说：" + girlGet);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        threadPool.execute(() -> {
            System.out.println("女生慢慢从教师走出来......");
            String boySay = "我喜欢你";
            try {
                String boyGet = exchanger.exchange(boySay);
                System.out.println("男生听说：" + boyGet);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
```

> ```
> 女生慢慢从教师走出来......
> 男生听说：我其实暗恋你很久了
> 女生听说：我喜欢你
> ```



**8. BlockingQueue：提供可阻塞的入队和出队操作**

![BlockingQueue](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171916.svg)

主要用于生产者-消费者模式，在多线程场景时生产者线程在队列尾部添加元素，而消费者则在队列头部消费元素，通过这种方式能够达到将任务的生产和消费进行隔离的目的

1. ArrayBlockingQueue：一个由数组结构组成的有界阻塞队列
2. LinkedBlockingQueue：一个由链表结构组成的有界/无界阻塞队列
3. PriorityBlockingQueue：一个支持优先级排序的无界阻塞队列
4. DelayQueue：一个使用优先级队列实现的无界阻塞队列
5. SynchronousQueue：一个不存储元素的阻塞队列
6. LinkedtransferQueue：一个由链表组成的无界区阻塞队列
7. LinkedBlockingDque：一个由链表结构组成的双向阻塞队列

### 6. IO机制

**1. Bock-IO：InputStream和OutputStream，Reader和Writer**

![BIO](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171921.svg)

 

**2. NonBlock-IO：构建多路复用的、同步非阻塞IO操作**

![NIO](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171928.svg)

 

- NIO的核心

```
Channels
Buffers
Selectors
```

![JavaIO-第 3 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171933.svg)

- NIO-Channels

```
FileChannel
DatagramChannel
SocketChannel
ServerSocketChannel
```

```
transferTo: 把FileChannel中的数据拷贝到另一个Channel
transferFrom: 把另一个Channel中的数据拷贝到FileChannel
避免了两次用户态和内核态间的上下文切换，即“零拷贝”，效率较高
```

- NIO-Buffers

```
ByteBuffer
CharBuffer
DoubleBuffer
FloatBuffer
IntBuffer
LongBuffer
ShortBuffer
MappedByteBuffer
```

- NIO-Selector

![JavaIO-第 4 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171939.svg)

**3. IO多路复用：调用系统级别的select/poll/epoll**

![JavaIO-第 5 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171945.svg)

select，poll和epoll都是IO多路复用的机制。IO多路复用就通过一种机制，可以监视多个描述符，一旦某个描述符就绪（一般是读就绪或者写就绪），它们能够通知程序进行响应的读写操作。select、poll和epoll本质上都是同步IO，因为它们都需要在读写事件就绪后自己负责读写，也就是说读写过程是阻塞的。

（1）支持一个进程所能打开的最大连接数

- select

```
单个进程所能打开的最大连接数由FD_SETSIZE宏定义，其大小是32个整数的大小（32位机器上是32*32，64位机器上是32*64），可以对其进行修改，但是性能无法保证，需要做进一步的测试
```

- poll

```
本质上与select没有区别，但是它没有最大连接数的限制，原因是它是基于链表来存储的
```

- epoll

```
虽然打开连接数有上限，但是很大，1G内存的机器上可以打开10万左右的连接
```

（2） FD（文件句柄）剧增后带来的IO效率问题

- select

```
因为每次调用都会对连接进行现行遍历，所以随着FD的增加会造成遍历速度的“线性下降”的性能问题
```

- poll

```
同上
```

- epoll

```
由于epoll是根据每个fd上的callback函数来实现的，只有活跃的socket才会主动调用callback，所以活跃socket较少的情况下，使用epoll不会有“线程下降”的性能问题，但是所有socket都很活跃的情况下，可能会有性能问题
```

（3）消息传递方式

- select

```
内核需要将消息传递到用户空间，需要内核的拷贝动作
```

- poll

```
同上
```

- epoll

```
通过内核和用户空间共享一块内存来实现，性能较高
```



**4. Asynchronous IO：基于事件和回调机制**

![JavaIO-第 6 页](https://gitee.com/tongying003/MapDapot/raw/master/img/20200505171951.svg)



**5. AIO如何进一步加工处理结果**

- 基于回调：实现CompletionHandler接口，调用时触发回调函数
- 返回Future：通过isDone()查看是否准备好



**6. BIO实例**

```java
public class BIOPlainEchoServer {
    public void server(int port) throws IOException {
        // 将ServerSocket绑定到指定的端口里
        final ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            // 阻塞直到收到客户端请求
            final Socket clientSocket = serverSocket.accept();
            System.out.println("Accepted connection from: " + clientSocket);
            // 创建一个子线程去处理客户端请求
            new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                    // 将客户端读到的数据写回
                    while (true) {
                        writer.println(reader.readLine());
                        writer.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    /**
     * 上面的程序中系统创建和销毁线程都是很大的开销，用线程池处理客户端请求
     */
    public void improvedServer(int port) throws IOException {
        // 将ServerSocket绑定到指定的端口
        final ServerSocket server = new ServerSocket(port);
        // 线程池
        ExecutorService executor = Executors.newFixedThreadPool(6);
        while (true) {
            // 阻塞直到收到客户端请求
            final Socket client = server.accept();
            System.out.println("Accepted connection from: " + client);
            // 将请求提交给线程池去执行
            executor.execute(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
                    while (true) {
                        // 将收到的客户端数据写回
                        writer.println(reader.readLine());
                        writer.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
```



如果请求实在太多，建立的连接数急剧上升，很多请求就得到不响应，这时想到了多路复用，NIO

**7. NIO实例**

```java
public class NIOPlainEchoServer {
    public void server(int port) throws IOException {
        System.out.println("Listening for connection on port: " + port);
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        ServerSocket serverSocket = serverChannel.socket();
        InetSocketAddress address = new InetSocketAddress(port);
        // 将ServerSocket绑定到指定的端口
        serverSocket.bind(address);
        serverChannel.configureBlocking(false);
        Selector selector = Selector.open();
        // 将Channel注册到selector里，并说明让Selector关注的点，这里是关注建立连接这个事件
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            try {
                // 阻塞等待就绪的Channel，即没有与客户端建立连接前就一直轮询
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
                // 处理异常的逻辑
                break;
            }
            // 获取到Selector里面所有就绪的SelectedKey实例，每将一个channel注册到一个selector就会产生一个selectedKey
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // 将selectedKey从selector中移除，因为马上就要去处理它，防止重复执行
                iterator.remove();
                try {
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        // 接受客户端连接
                        SocketChannel client = server.accept();
                        System.out.println("Accepted connection from: " + client);
                        client.configureBlocking(false);
                        // 向selector注册socketChannel，主要关注读写，并传入一个ByteBuffer实例供读写缓存
                        client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, ByteBuffer.allocate(100));
                    }
                    if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer output = (ByteBuffer) key.attachment();
                        // 从channel里读取数据到ByteBuffer里
                        client.read(output);
                    }
                    if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer output = (ByteBuffer) key.attachment();
                        output.flip();
                        // 将ByteBuffer的数据写入到channel里
                        client.write(output);
                        output.compact();
                    }
                } catch (IOException e) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException IOE) {
                    }
                }
            }
        }
    }
}
```



**8. AIO实例**

```java
public class AIOPlainEchoServer {
    public void server(int port) throws IOException {
        System.out.println("Listening connections on port: " + port);
        AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(port);
        // 将ServerSocket绑定到指定的端口里
        serverChannel.bind(address);
        final CountDownLatch latch = new CountDownLatch(1);
        // 开始接收新的客户端请求，一旦一个客户端被请求，CompletionHandler就会被调用
        serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {

            @Override
            public void completed(AsynchronousSocketChannel channel, Object attachment) {
                // 一旦完成请求，再次接收新的客户端请求
                serverChannel.accept(null, this);
                ByteBuffer buffer = ByteBuffer.allocate(100);
                // 在channel里植入一个读操作EchoCompletionHandler，一旦buffer有数据写入，EchoCompletionHandler就会被唤醒
                channel.read(buffer, buffer, new EchoCompletionHandler(channel));
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                try {
                    // 若遇到异常，关闭channel
                    serverChannel.close();
                } catch (IOException e) {
                    // ignore on close
                } finally {
                    latch.countDown();
                }
            }
        });
    }

    private final class EchoCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

        private final AsynchronousSocketChannel channel;

        EchoCompletionHandler(AsynchronousSocketChannel channel) {
            this.channel = channel;
        }

        @Override
        public void completed(Integer result, ByteBuffer buffer) {
            buffer.flip();
            // 在channel里植入一个读操作CompletionHandler，一旦Channel有数据写入，CompletionHandler就会被唤醒
            channel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer buffer) {
                    if (buffer.hasRemaining()) {
                        // 如果buffer里还有内容，则再次触发写入操作将buffer里的内容写入channel
                        channel.write(buffer, buffer, this);
                    } else {
                        // 如果channel还有内容要读入buffer里，则再次触发写入操作将channel里的内容读如buffer
                        buffer.compact();
                        channel.read(buffer, buffer, EchoCompletionHandler.this);
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        // ignore on close
                    }
                }
            });
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            try {
                channel.close();
            } catch (IOException e) {
                // ignore on close
            }
        }
    }
}
```



**9. BIO、NIO、AIO对比**

| 属性/模型                | 阻塞BIO    | 非阻塞NIO    | 异步AIO      |
| ------------------------ | ---------- | ------------ | ------------ |
| blocking                 | 阻塞并同步 | 非阻塞但同步 | 非阻塞并异步 |
| 线程数（server : client) | 1: 1       | 1: N         | 0: N         |
| 复杂度                   | 简单       | 较复杂       | 复杂         |
| 吞吐量                   | 低         | 高           | 高           |

