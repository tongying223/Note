## Java内部类

[TOC]

#### 1. 内部类基础

##### 1.1 成员内部类

- 成员内部类：定义在另一个类内部的类

```java
class Circle{
    private double radius = 0;

    public Circle(double radius){
        this.radius = radius;
    }

    class Draw{
        // 内部类
        public void drawShape(){
            System.out.println("draw a shape");
        }
    }
}
```

- 成员内部类可以无条件访问外部类的所有属性和方法（包括private成员和static成员）

```java
class Circle{
    private double radius = 0;
    public static int count = 1;

    public Circle(double radius){
        this.radius = radius;
    }


    class Draw{
        // 内部类
        public void drawShape(){
            System.out.println(radius); // 访问私有成员
            System.out.println(count);  // 访问静态成员
        }
    }
}
```

- 当成员内部类拥有和外部类同名的属性和方法时，会发生隐藏现象，即默认情况下访问的是成员成员内部类的成员，如果要访问外部内部类的同名成员，需要以下面的形式访问

```java
外部类.this.成员变量
外部类.this.成员方法
```

- 外部类想要访问内部类的成员，必须先创建一个成员内部类对象，再通过指向这个对象的引用来访问

```java
class Circle{
    private double radius = 0;

    public Circle(double radius){
        this.radius = radius;
        getDrawInstance().drawShape(); // 必须先创建内部类的对象，再进行访问
    }

    private Draw getDrawInstance(){
        return new Draw();
    }

    class Draw{
        // 内部类
        public void drawShape(){
            System.out.println("draw a circle: radius = " + radius);    // 访问外部类的private成员
        }
    }
}
```

- 成员内部类是依附于外部类而存在的，也就是说，如果要创建成员内部类的对象，前提是必须存在一个外部类的对象。创建成员内部类对象的一般方式如下：

```java
public class Test {
    public static void main(String[] args) {
        // 第一种方式
        Outer outer = new Outer();
        Outer.Inner inner = outer.new Inner();

        //第二种方式
        Outer.Inner inner = new Outer().getInnerInstance();

    }
}

class Outer{
    // 外部类
    private Inner inner = null;
    public Outer(){

    }

    public Inner getInnerInstance(){
        if(inner == null){
            inner = new Inner();
        }
        return inner;
    }

    class Inner{
        // 成员内部类
        public Inner(){

        }
    }
}
```

- 成员内部类可以拥有public、protected、private和包访问权限

> `public：`任何地方都能访问
>
> `protected:` 同包或子类
>
> `private：`只能在外部类的内部访问
>
> `default：`同包

- 外部类只能被public或包访问权限修饰符修饰，可以理解为成员内部类看起来像是外部类的一个成员，所以可以像类的成员一样拥有多种权限修饰符

##### 1.2 局部内部类

```java
class People{

}

class Man{
    public Man(){

    }

    public People getWoman(){
        class Woman extends People{
            // 局部内部类
            int age = 0;
        }
        return new Woman();
    }
}
```

- 局部内部类是定义在一个方法或一个作用域里面的类，它和成员内部类的区别在于局部内部类的访问权限仅限于方法内或者该作用域内
- 局部内部类就像是方法里面的一个局部变量一样，是不能有public、protected、private及static修饰符的

##### 1.3 匿名内部类

```java
public class Test {
    public static void main(String[] args) {
        Person person  = new Person() {
            // 内部类
            @Override
            public void sayHello() {
                System.out.println("Hello world!");
            }
        };
        person.sayHello();
    }
}

interface Person{
    // 接口
    void sayHello();
}
```

- 使用匿名内部类时，必须继承一个类或者实现一个接口，但是两者不可兼得，同时也只能继承一个类或者实现一个接口
- 匿名内部类内部是不能定义构造函数的
- 匿名内部类中不能存在任何的静态成员变量或静态方法
- 匿名内部类为局部内部类，所以局部内部类的所有限制同样对匿名内部类生效
- 匿名内部类不能是抽象的，它必须实现继承的类或者实现的接口的所有抽象方法

##### 1.4 静态内部类

```java
class Outer{
    // 外部类
    public Outer(){

    }
    
    static class Inner{
        // 静态内部类
        public Inner(){

        }
    }
}
```

- 静态内部类也是定义在另一个类里面的类，只不过在类的前面多了一个关键字static。静态内部类是不依赖于外部类对象的，这点和类的静态成员属性有点类似
- 静态内部类不能使用外部类的非static成员变量或方法，因为在没有外部类对象的情况下，可以创建静态内部类的对象，如果允许访问外部类的非static成员就会产生矛盾，外部类的非static成员必须依附于具体的对象

#### 2. 深入理解内部类

##### 2.1 为什么内部类可以无条件访问外部类的成员

```java
public class Outer {

    private Inner inner;

    public Outer(){

    }

    public Inner getInnerInstance(){
        if(inner == null){
            inner = new Inner();
        }
        return inner;
    }


    protected class Inner{
        public Inner(){}

    }
}
```

```java
E:\IntelliJ IDEA Project\JavaBasic\src>javap -v com/interview/javabasic/innerclass/Outer$Inner.class
Classfile /E:/IntelliJ IDEA Project/JavaBasic/src/com/interview/javabasic/innerclass/Outer$Inner.class
  Last modified 2019-5-4; size 435 bytes
  MD5 checksum 4dd6c2deac4d611bba7e9f9cc83a8ae2
  Compiled from "Outer.java"
public class com.interview.javabasic.innerclass.Outer$Inner
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:
   #1 = Fieldref           #3.#13         // com/interview/javabasic/innerclass/Outer$Inner.this$0:Lcom/interview/javabasic/innerclass/Outer;
   #2 = Methodref          #4.#14         // java/lang/Object."<init>":()V
   #3 = Class              #16            // com/interview/javabasic/innerclass/Outer$Inner
   #4 = Class              #19            // java/lang/Object
   #5 = Utf8               this$0
   #6 = Utf8               Lcom/interview/javabasic/innerclass/Outer;
   #7 = Utf8               <init>
   #8 = Utf8               (Lcom/interview/javabasic/innerclass/Outer;)V
   #9 = Utf8               Code
  #10 = Utf8               LineNumberTable
  #11 = Utf8               SourceFile
  #12 = Utf8               Outer.java
  #13 = NameAndType        #5:#6          // this$0:Lcom/interview/javabasic/innerclass/Outer;
  #14 = NameAndType        #7:#20         // "<init>":()V
  #15 = Class              #21            // com/interview/javabasic/innerclass/Outer
  #16 = Utf8               com/interview/javabasic/innerclass/Outer$Inner
  #17 = Utf8               Inner
  #18 = Utf8               InnerClasses
  #19 = Utf8               java/lang/Object
  #20 = Utf8               ()V
  #21 = Utf8               com/interview/javabasic/innerclass/Outer
{
  final com.interview.javabasic.innerclass.Outer this$0;
    descriptor: Lcom/interview/javabasic/innerclass/Outer;
    flags: ACC_FINAL, ACC_SYNTHETIC

  public com.interview.javabasic.innerclass.Outer$Inner(com.interview.javabasic.innerclass.Outer);
    descriptor: (Lcom/interview/javabasic/innerclass/Outer;)V
    flags: ACC_PUBLIC
    Code:
      stack=2, locals=2, args_size=2
         0: aload_0
         1: aload_1
         2: putfield      #1                  // Field this$0:Lcom/interview/javabasic/innerclass/Outer;
         5: aload_0
         6: invokespecial #2                  // Method java/lang/Object."<init>":()V
         9: return
      LineNumberTable:
        line 20: 0
}
SourceFile: "Outer.java"
InnerClasses:
     protected #17= #3 of #15; //Inner=class com/interview/javabasic/innerclass/Outer$Inner of class com/interview/javabasic/innerclass/Outer
```

反编译

![1556979888401](C:\Users\tongy\AppData\Roaming\Typora\typora-user-images\1556979888401.png)

```java
 final com.interview.javabasic.innerclass.Outer this$0;
```

指向外部类对象的指针。也就是编译器会默认为成员内部类添加一个指向外部类对象的引用，下面是赋值语句

```java
 public com.interview.javabasic.innerclass.Outer$Inner(com.interview.javabasic.innerclass.Outer);
```

虽然定义的成员内部类的构造器是无参的，但是编译器还是会默认添加一个参数，该参数的类型为指向外部类对象的一个引用。所以成员内部类中的`Outer this$0`指针指向了外部类对象，因此可以在成员内部类中随意访问外部类的成员。这里也说明了成员内部类是依赖于外部类的，如果没有创建外部类对象，则无法对`Outer this$0`引用进行初始化赋值，也就无法创建成员内部类的对象

##### 2.1  为什么局部内部类和匿名内部类只能访问局部final变量

> JDK < 8

```java
public class Test {
    public static void main(String[] args) {

    }

    public void test(final int b){
       final int a = 10;
       new Thread(){
         public void run(){
             System.out.println(a);
             System.out.println(b);
         }
       }.start();
    }
}
```

编译

![1556980731221](C:\Users\tongy\AppData\Roaming\Typora\typora-user-images\1556980731221.png)

默认情况下，编译器会将匿名内部类和局部内部类起名为Outerx.class(x为正数)。

反编译

```java
 public void run();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=2, locals=1, args_size=1
         0: getstatic     #4                  // Field java/lang/System.out:Ljava/io/PrintStream;
         3: bipush        10				 //	在匿名内部类内部创建一个值的拷贝
         5: invokevirtual #5                  // Method java/io/PrintStream.println:(I)V
         8: getstatic     #4                  // Field java/lang/System.out:Ljava/io/PrintStream;
        11: aload_0
        12: getfield      #2                  // Field val$b:I
        15: invokevirtual #5                  // Method java/io/PrintStream.println:(I)V
        18: return
      LineNumberTable:
        line 12: 0
        line 13: 8
        line 14: 18
```



- 如果局部变量的值可以在编译期间确定，则直接在匿名内部类内创建一个拷贝

  ```java
   bipush        10
  ```

  如果局部变量的值无法在编译期间确定，则通过构造器传参的方式对拷贝进行初始化赋值 

  ```java
  com.interview.javabasic.innerclass.Test$1(com.interview.javabasic.innerclass.Test, int);
  ```

  如果不将局部变量的值限制为final，则在run()方法运行过程中可造成数据不一致，run()中的局部变量与Test中的局部变量不是同一个，run()方法中的只是一个拷贝

##### 2.3 静态内部类有特殊的地方吗？

静态内部类不依赖于外部类，可以在不创建外部类的情况下创建静态内部类，静态内部类是不持有指向外部类的对象的引用的。

#### 3. 内部类的使用场景和好处

> 为什么在Java中需要内部类

```java
1. 每个内部类都能独立的继承一个接口的实现，所以无论外部类是否已经继承了一个接口的实现，对于内部类都没有影响。内部类使多继承的解决方案变得完成

2. 方便存在一定逻辑关系的类组织在一起，又可以对外界隐藏

3. 方便编写事件驱动程序

4. 方便编写线程代码
```

> 内部类相关笔试面试题

1. 根据注释填写(1)、（2）、（3）处的代码

```java
public class Test{
	public static void main(String[] args){
		// 初始化Bean1
		(1)
		bean1.I++;
		// 初始化Bean2
		(2)
		bean2.J++;
		// 初始化Bean3
		(3)
		bean3.K++;
	}
	
	class Bean1{
		public int I = 0;
	}
	
	static class Bean2{
		public int J = 0;
	}
}

class Bean{
    class Bean3{
        public int K = 0;
    }
}
```

(1)处，创建成员内部类对象  `外部类名.内部类名 xxx = 外部类对象名.new 内部类类名()`

```java
Test test = new Test();
Test.Bean1  bean1 = test.new Bean1();
```

(2)处，创建静态内部类对象 `外部类类名.内部类类名 xxx = new 外部类类名.内部类类名()`

```java
Test.Bean2 bean2 = new Test.Bean2();
```

(3)处，成员内部类

```java
Bean bean = new Bean();
Bean.Bean3 bean3 = bean.new Bean3();
```

2. 下面这段代码的输出结果是

```java
public class Test{
	public static void main(String[] args){
		Outer outer = new Outer();
		outer.new Inner().print();
	}
}

class Outer{
	private int a = 1;
	class Inner{
		private int a = 2;
		public void print(){
			int a = 3;
			System.out.println(a);					// 局部便变量
			System.out.println(this.a);				// 内部类变量
			System.out.println(Outer.this.a);		// 外部类变量
		}
	}
}
```

```
3
2
1
```

3. 内部类的继承

- 成员内部类的引用必须为Outer.Inner
- 构造器中必须有指向外部类对象的引用，并通过这个引用调用super()。

```java
public class WithInner {
    class Inner{

    }
}

class InheritInner extends WithInner.Inner{
    // InheritInner()是不能通过编译的，一定要加上形参
    InheritInner(WithInner wi){
        wi.super();	// 必须有这句调用
    }

    public static void main(String[] args) {
        WithInner wi = new WithInner();
        InheritInner obj = new InheritInner(wi);
    }
}
```

参考：

[[Java内部类详解](https://www.cnblogs.com/dolphin0520/p/3811445.html)](<https://www.cnblogs.com/dolphin0520/p/3811445.html>)  