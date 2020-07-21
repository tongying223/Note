# 	 Spring Boot基础

## 一、Spring Boot入门

### 1. Spring Boot简介

简化Spring应用开发的一个框架，整个Spring技术栈的一个大整合，J2EE开发的一站式解决方案。

### 2. 微服务

 每个功能元素最终都是一个可独立替换和独立升级的软件单元。

![20200518121536](https://gitee.com/tongying003/MapDapot/raw/master/img/20200521174713.png)

###   3. Hello World探究

#### 3.1  POM文件

1. 父项目

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.12.RELEASE</version>
</parent>
```

它的父项目是：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-dependencies</artifactId>
    <version>2.1.12.RELEASE</version>
    <relativePath>../../spring-boot-dependencies</relativePath>
</parent>
```

由该项目来真正管理Spring Boot应用里面的所有依赖版本。可以称为Spring Boot版本仲裁中心，对于在其dependencies里面管理的依赖，我们在导入时不再需要写版本号。



2. 导入的依赖

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

- Spring-boot-starter-web

     Spring Boot场景启动器，帮助我们导入了web模块正常运行所依赖的组件。

Spring Boot将所有的功能场景都抽取出来，做成一个个的starter（启动器），只需要在项目里引入这些starter，相关场景的所有依赖都会导入锦进来，可以根据场景选择导入相应的启动器。



#### 3.2 主程序类

```java
@SpringBootApplication
```

Spring Boot应用标注在某个类上说明这个类是Spring Boot的主配置类，Spring Boot就应该运行这个类的main方法来启动SpringBoot应用。

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(
    excludeFilters = {@Filter(
    type = FilterType.CUSTOM,
    classes = {TypeExcludeFilter.class}
), @Filter(
    type = FilterType.CUSTOM,
    classes = {AutoConfigurationExcludeFilter.class}
)}
)
public @interface SpringBootApplication {
```

（1）`@SpringBootConfiguration`

标注在某个类上，表示这是一个Spring Boot的配置类。其底层是`@Configuration`，用来标注一个配置类，配置类也是容器中的一个组件`@Component`

（2）`@EnableAutoConfiguration`

该注解告诉Spring Boot开启自动配置功能，由Spring Boot帮我们进行自动配置。

```java
@AutoConfigurationPackage
@Import({AutoConfigurationImportSelector.class})
public @interface EnableAutoConfiguration {
```

- `@AutoConfigurationPackage`

    将主配置类（`@SpringBootApplication`标注的类）所在的包及下面所有子包里里面的所有组件扫描到Spring容器。底层是`@Import({Registrar.class})`，Spring的底层注解`@Import`给容器中导入一个组件，导入的组件由`{Registrar.class}`获取。

- `Import({AutoConfigurationImportSelector.class})`

    导入哪些组件的选择器，将所有需要导入的组件以全类名的方式返回，这些组件就会被添加到容器中。

    会给容器中导入非常多的自动配置类（`xxxAutoConfiguration`）；就是给容器中导入这个场景需要的所有组件，并配置好这些组件。  

![20200518191037](https://gitee.com/tongying003/MapDapot/raw/master/img/20200521180230.png)

​		有了自动配置类，免去了我们手动编写配置注入功能组件等的工作。其底层调用了`SpringFactoriesLoader.loadFactoryNames(EnableAutoConfiguration.class, classLoader);`

​		Spring Boot在启动时从类路径下的`META-INF/spring.factories` 中获取`EnableAutoConfiguration`指定的值，将这些值作为自动配置类导入到容器中，自动配置类就生效，帮我们进行自动配置工作。

​		J2EE的整体解决方案和自动配置都在spring-boot-autoconfigure-2.1.12.RELEASE.jar

## 二、配置

### 1. 配置文件

 Spring Boot使用一个全局的配置文件，配置文件名是固定的

```
application.properties
application.yml
```

配置文件的作用：修改Spring Boot自动配置的默认值。

`.yml`是YAML（YAML Ain’t Markup Language）语言的文件，以数据为中心，比json、xml等更适合做配置文件

### 2. YAML语法

1. YAML基本语法

- 使用缩进表示层级关系
- 缩进时不允许使用Tab键，只允许只用空格
- 缩进的空格数目不重要，只要相同层级的元素左对齐即可
- 大小写敏感

2. YAML支持三种数据结构

1. 对象（属性和值）：健值对的集合

```yaml
friends:
	name: tom
	age: 20
```

​				行内写法：

```yaml
friends: {name: tom,age: 20}
```

2. 数组（List、Set）：一组按次序排列的值

    用`-值`表示数组中的一个元素

```yaml
pets:
	- cat
	- pig
	- dog
```

​				行内写法：

```yaml
pets: [cat,pig,dog]			
```

3. 字面量（数字、字符串、布尔）：单个的、不可再分的值

```
k: v：字面直接写

“”：双引号，会引起转义，如"hello \n world"输出为hello 换行 world。

‘’：单引号，可以避免转义，如'hello \n world'输出为hello \n world
```

### 3. 配置文件值注入

1. 配置文件

```yml
person:
  lastName: tom
  age: 18
  birthDay: 2012/09/01
  info: {height: 170, motto: Good morning!}
  friends: [Anna, Bella, Coco]
  dog:
    name: Cindy
    age: 2
```

2. JavaBean

```java
/**
 * Person类
 * 将配置文件中的值映射到当前组件中
 * @ConfigurationProperties： 告诉Spring Boot将本类中的属性和配置文件中相关的配置进行绑定
 * prefix = "person"：将配置文件中person下面的属性进行一一映射
 */
@Component
@Data
@ConfigurationProperties(prefix = "person")
public class Person {
    String lastName;
    Integer age;
    Date birthDay;
    private Map<String, Object> info;
    private List<Object> friends;
    private Dog dog;
}
```

3. 需要导入配置文件处理器，以后编写配置就有提示了

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-configuration-processor</artifactId>
	<optional>true</optional>
</dependency>
```

### 4. properties配置文件编码问题

Preferences -> Editor -> File Encodings，将编码方式改为UTF-8。

设置后还乱码的话可以删除application.properties，再新建一个相同的。



### 5. `@ConfigurationProperties`获取值与`@Value`获取值的区别

| 注解                  | `@ConfigurationProperties` | `@Value`     |
| --------------------- | -------------------------- | ------------ |
| 功能                  | 批量注入配置文件中的属性   | 注入单个属性 |
| 松散绑定（松散语法）  | 支持                       | 不支持       |
| SpEL                  | 不支持                     | 支持         |
| JSR303数据校验        | 支持                       | 不支持       |
| 复杂类型封装（如map） | 支持                       | 不支持       |



### 6. `@PropertySource`和`@ImportResource`

1. `@PropertySource`

    `@ConfigurationProperties`注解从配置文件中获取值，默认是全局配置文件，配合`@PropertySource`加载指定的配置文件。

```java
@PropertySource(value = {"classpath:person.properties"})
@ConfigurationProperties(prefix = "person")
```

2. `@ImportResource`

导入Spring的配置文件，让配置文件里面的内容生效。 

```java
@ImportResource(locations = {"classpath:beans.xml"})
```

3. Spring Boot推荐给容器中添加组件的方式

Spring Boot推荐使用全注解的方式。

例如：给容器中添加组件，可以使用配置类代替Spring的配置文件，使用`@Bean`给容器中添加组件。

```java
/**
 * @configuration: 指明当前类是一个配置类，用来代替前面的Spring的配置文件
 */
@Configuration
public class MyConfig {

    // 将方法的返回值添加到容器中，容器中这个组件的id就是方法名
    @Bean
    public HelloService helloService() {
        return new HelloService();
    }
}
```

### 7.  配置文件占位符

1. RandomValuePropertySource：配置文件中可以使用随机数

```properties
${random.value}、${random.int}、${random.int}
${random.int(10)}、${random.int[1024,65536]}
```

2. 属性配置占位符

- 可以在配置文件中引用前面配置过的属性
- 如果没有配置可以用`${app.name:默认值}`的形式指定默认值

```proper
app.name=MyApp
app.description={app.name} is a Spring Boot application
```



### 8.  Profile

1. 多profile文件支持

在主配置文件编写的时候，文件名可是是application-{profile}.properties/yml

默认使用application.properties的配置。



2. 多profile文档快模式

```yaml
spring:
  profiles:
    active: dev
---
server:
  port: 8081

---
server:
  port: 8082
```

3. 激活方式

- 在配置文件中指定

```properties
spring.profiles.active=dev
```

- 命令行

```shell
--spring.profiles.active=dev
```

- JVM参数

```shell
-Dspring.profiles.active=dev
```



### 9. 配置文件的加载位置

Spring Boot启动会扫描以下位置的application.properties或者application.yml文件作为Spring Boot的默认配置文件

```
-file: ./config/
-file: ./
-classpath: /config/
-classpath: /
```

（1）优先级从高到低，高优先级配置内容会覆盖低优先级配置内容

（2）可以通过配置`spring.config.location`来改变默认配置文件位置，项目打包好以后，可以使用命令行参数的形式，在启动项目时指定配置文件的新位置，指定的和默认的配置文件会互补配置

```shell
--spring.config.location=xxx/application.properties
```



### 10.  外部配置加载顺序

Spring Boot可以从以下位置加载配置，优先级从高到低，高优先级配置覆盖低优先级配置，所有的配置会形成互补配置。

1. 命令行参数

```shell
java -jar myProject.jar --server.port=8080 --server.context-path=/boot
```

2. 来自java:comp/evn的JDNI属性
3. Java系统属性（`System.getProperties()`)
4. 操作系统环境变量
5. RandomValuePropertySource配置的`random.*`属性值
6. jar包外部的application-{profile}.properties或application.yml（带`spring.profile`）配置文件
7. jar包内部的application-{profile}.properties或application.yml（带`spring.profile`）配置文件
8. jar包外部的application.properties或application.yml（不带`spring.profile`）配置文件
9. jar包内部的application.properties或application.yml（不带`spring.profile`）配置文件
10. `@Configuration`注解类上的`@PropertySource`
11. 通过`SpringApplication.setDefaultProperties`指定的默认属性



### 11. 自动配置原理

[能配置的属性参照](https://docs.spring.io/spring-boot/docs/2.3.1.RELEASE/reference/html/appendix-application-properties.html#common-application-properties)

1. 自动配置原理

    1）Spring Boot启动的时加载主配置类，开启了自动配置功能`@EnableAutoConfiguration`

    2）`@EnableAutoConfiguration`的作用:

    - 利用`AutoConfigurationImportSelector`给容器中导入一些组件

    - 获取候选配置的关键方法：

        ```java
        List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);
        ```

    - 该方法调用`SpringFactoriesLoader.loadFactoryNames()`

        

        ```java
        // SpringFactoriesLoader.loadFactoryNames()
        扫描所有jar包类路径下META-INF/spring.factories，把扫描到的这些文件的内容包装成properties对象，从properties中取得EnableAutoConfiguration.class类名对应的值，然后把它们添加到容器中
        ```

    - 即将类路径META-INF/spring.facories里面配置的所有`EnableAutoConfiguration`的值加入到了容器中

    ![image-20200703203903550](https://gitee.com/tongying003/MapDapot/raw/master/img/20200703203903.png)

    每一个这样的xxxAutoConfiguration类都是容器中的一个组件，都加入到容器中，用他们来做自动配置；

    3）每一个自动配置类进行自动配置功能

    4）以HttpEncodingAutoConfiguration为例解释自动配置原理：

    ```java
    // 表示这是一个配置类，也可以给容器中添加组件
    @Configuration(proxyBeanMethods = false)
    // 启用指定类的ConfigurationProperties功能，从配置文件中，将配置文件中对应的值和ServerProperties绑定起来，并把ServerProperties加入到IOC容器中
    @EnableConfigurationProperties(ServerProperties.class)
    // Spring底层@Conditional注解，如果满足指定的条件，整个配置类里的配置就会生效；这里是判断当前配置类在Web应用生效
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    // 判断当前项目有没有这个类，CharacterEncodingFilter是Spring MVC中解决乱码的过滤去
    @ConditionalOnClass(CharacterEncodingFilter.class)
    // 判断配置文件中是否存在某个配置server.servlet.encoding.enabled，matchIfMissing = true，如果不存在，判断也是成立的
    @ConditionalOnProperty(prefix = "server.servlet.encoding", value = "enabled", matchIfMissing = true)
    public class HttpEncodingAutoConfiguration {
    
        // 它已经和Spring Boot的配置文件映射了
    	private final Encoding properties;
    
        // 只有一个有参构造器的情况下，参数的值就会从容器中拿
    	public HttpEncodingAutoConfiguration(ServerProperties properties) {
    		this.properties = properties.getServlet().getEncoding();
    	}
    
        // 给容器中添加一个组件，这个组件的某些值需要从properties中获取
    	@Bean
        // 判断容器中不存在这个Bean
    	@ConditionalOnMissingBean
    	public CharacterEncodingFilter characterEncodingFilter() {
    		CharacterEncodingFilter filter = new OrderedCharacterEncodingFilter();
    		filter.setEncoding(this.properties.getCharset().name());
    		filter.setForceRequestEncoding(this.properties.shouldForce(Encoding.Type.REQUEST));
    		filter.setForceResponseEncoding(this.properties.shouldForce(Encoding.Type.RESPONSE));
    		return filter;
    	}
    
    ```

    根据当前不同的条件判断，决定这个配置类是否生效。一旦这个配置类生效，这个配置类就会给容器中添加组件，这些组件的属性是从对应的properties类中获取的，这些类中的每一个属性又是和配置文件绑定的。

    

    5）所有能在配置文件中配置的属性都封装在xxxProperties类中，配置文件中

    能配置的属性就可以参照该功能对应的这个属性类

    ```java
    // 从配置文件中获取指定的值和bean的属性进行绑定
    @ConfigurationProperties(prefix = "server", ignoreUnknownFields = true)
    public class ServerProperties {
    ```

    **- Spring Boot精髓 -**

    1）Spring Boot启动会加载大量的自动配置类

    2）我们先查看需要的功能是否有Spring Boot写好的自动配置类

    3）再看自动配置类中配置了哪些组件，如果已经存在我们要用的组件，就不需要再进行配置

    4）给容器中自动配置类添加组件的时候会从xxxProperties类中获取某些组件，我们就可以在配置文件中指定这些属性的值。

    > `xxxAutoConfiguration`：自动配置类，给容器中添加组件
    >
    > `xxxProperties`：封装配置文件中相关属性

2. 细节

（1）`@Conditional`派生注解

作用：必须是`@Conditional`指定的条件成立，才给容器中添加组件，配置里面的所有内容才生效

| `@Conditional`扩展注解            | 作用（判断是否满足当前指定的条件）               |
| --------------------------------- | ------------------------------------------------ |
| `@ConditionalOnJava`              | 系统的Java版本是否符合要求                       |
| `@ConditionalOnBean`              | 系统中存在指定的Bean                             |
| `@ConditionalOnMissingBean`       | 容器中不存在指定的Bean                           |
| `@ConditionalOnExpression`        | 满足SpEl表达式指定                               |
| `@ConditionalOnClass`             | 系统中有指定的类                                 |
| `@ConditionalOnMissingClass`      | 系统中没有指定的类                               |
| `@ConditionalOnSingleCandidate`   | 系统中只有一个指定的Bean，或者这个Bean是首选Bean |
| `@ConditionalOnProperty`          | 系统中指定的属性是否有指定的值                   |
| `@ConditionalOnSource`            | 类路径下是否存在指定的资源文件                   |
| `@ConditionalOnWebApplication`    | 当前是Web环境                                    |
| `@ConditionalOnNotWebApplication` | 当前不是Web环境                                  |
| `@ConditionalOnJndi`              | JNDI存在指定项                                   |

自动配置类必须在一定的条件下才能生效，如何知道哪些自动配置类生效？



（2）自动配置报告

通过启用`debug=true`属性，在控制台打印自动配置报告，这样就可以很方便的知道哪些自动配置类生效。

```properties
# application.properties
debug=true
```

```verilog
============================
CONDITIONS EVALUATION REPORT
============================
    
Positive matches:(自动配置类启用的)
-----------------

   AopAutoConfiguration matched:
      - @ConditionalOnProperty (spring.aop.auto=true) matched (OnPropertyCondition)

   AopAutoConfiguration.ClassProxyingConfiguration matched:
      - @ConditionalOnMissingClass did not find unwanted class 'org.aspectj.weaver.Advice' (OnClassCondition)
      - @ConditionalOnProperty (spring.aop.proxy-target-class=true) matched (OnPropertyCondition)
          
          
          ...
          

Negative matches:（没有启用，没有匹配成功的）
-----------------

   ActiveMQAutoConfiguration:
      Did not match:
         - @ConditionalOnClass did not find required class 'javax.jms.ConnectionFactory' (OnClassCondition)

   AopAutoConfiguration.AspectJAutoProxyingConfiguration:
      Did not match:
         - @ConditionalOnClass did not find required class 'org.aspectj.weaver.Advice' (OnClassCondition)   

```

## 三、日志

### 1. 日志框架分类和选择

**1. 常见的日志门面**

JCL（Jakarta Commons Logging）

SLF4j（Simple Logging Facade for Java）

jboss-logging

**2. 常见的日志实现**

Log4j

JUL（java.util.logging）

log4j2

Logback

**3. 日志框架选择**

Spring Boot在框架内容部使用JCL，spring-boot-starter-logging采用了==slf4j + logback==的形式，Spring Boot也能自动适配（JUL、Log4j2、logback）并简化配置。

### 2. SLF4j使用原理

**1. 如何在系统中使用SLF4j**

在开发的时候，日志记录方法的调用，不应该直接调用日志的实现类，而是调用日志抽象层里面的方法。

给系统导入slf4j和logback的jar包

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorld {
  public static void main(String[] args) {
    Logger logger = LoggerFactory.getLogger(HelloWorld.class);
    logger.info("Hello World");
  }
}
```

图示：

![click to enlarge](https://gitee.com/tongying003/MapDapot/raw/master/img/20200704114947.png)

每一个日志的实现框架都有自己的配置文件，使用slf4j以后，**配置文件还是做成日志实现框架的配置文件**。



**2. 遗留问题**

统一日志记录：即使项目中使用到了其他框架也统一使用slf4j进行输出

![click to enlarge](https://gitee.com/tongying003/MapDapot/raw/master/img/20200704124001.png)

如何让系统中所有的日志都统一到slf4j？

1）将系统中其他日志框架先排除出去

2）用中间包来替换原有的日志框架

3）导入slf4j其他的实现



### 3. Spring Boot日志关系

Spring Boot日志功能：

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-logging</artifactId>
  <version>2.3.1.RELEASE</version>
  <scope>compile</scope>
</dependency>
```

底层依赖关系：

![image-20200704131902902](https://gitee.com/tongying003/MapDapot/raw/master/img/20200704131903.png)

1）Spring Boot底层也是使用slf4j + logback的方式进行日志记录

2）Spring Boot也把其他的日志都替换成了slf4j

3）中间替换包`jul-to-slf4j`、`log4j-to-slf4j`

4）如果在项目中引入了其他框架，一定要把这个框架的默认日志依赖移除掉，不然中间替换包与默认日志依赖不就冲突了吗？

例如：

Spring5并没有使用`commons-logging`，而是仿照`commons-logging`实现的`spring-icl`，只不过包名是`org.apache.commons.logging`而已，使其可以支持`slf4j`，所以Spring5并不需要排除掉`commons.logging`

![image-20200704132720699](https://gitee.com/tongying003/MapDapot/raw/master/img/20200704132720.png)

### 4. 日志使用

**1. 默认配置**

Spring Boot默认帮我们配置好了日志

```java
@SpringBootTest
class SpringBoot03LoggingApplicationTests {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Test
    void contextLoads() {
        logger.trace("这是trace日志");
        logger.debug("这是debug日志");
        logger.info("这是info日志");
        logger.warn("这是warn日志");
        logger.error("这是error日志");
    }
}
```

- 日志级别：`trace < debug < info < warn < error`
- Spring默认规定的日志级别（root）：`info`

- 日志的路径

```properties
# 在指定的路径下生成日志文件，若不指定路径，则在项目路径下生成
logging.file.name=mylog.log

# 在指定路径下生成springboot.log日志文件
logging.file.path=/springboot/log

# 若都不指定，只在控制台输出
# 若都指定，logging.file.name生效
```

| `logging.file.name` | `logging.file.path` | Example    | Description                                                  |
| :------------------ | :------------------ | :--------- | :----------------------------------------------------------- |
| *(none)*            | *(none)*            |            | Console only logging.                                        |
| Specific file       | *(none)*            | `my.log`   | Writes to the specified log file. Names can be an exact location or relative to the current directory. |
| *(none)*            | Specific directory  | `/var/log` | Writes `spring.log` to the specified directory. Names can be an exact location or relative to the current directory. |

- 日志模版

```properties
# 控制台日志输出的格式
logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n

# 日志文件中输出个格式
logging.pattern.file=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```

```properties
%d：时间
%thread：线程名
%-5level：级别从左显示5个字符宽度
%logger{36}：表示logger名字最长50个字符，否则按照句点分割
%msg：日志信息
%n：换行符
```



**2. 指定配置**

[官网文档](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-logging)

给类路径下放上每个日志框架自己的配置文件即可，Spring Boot就不使用默认配置了。

| Logging System          | Customization                                                |
| :---------------------- | :----------------------------------------------------------- |
| Logback                 | `logback-spring.xml`, `logback-spring.groovy`, `logback.xml`, or `logback.groovy` |
| Log4j2                  | `log4j2-spring.xml` or `log4j2.xml`                          |
| JDK (Java Util Logging) | `logging.properties`                                         |

`logback.xml`：配置文件直接被日志框架识别

`logback-spring.xml`：日志框架不直接加载日志的配置项，由Spring Boot解析日志的配置，推荐使用。可以使用Spring Boot的高级特性，如`<springProfile>`标签。

```xml
<springProfile name="staging">
    <!-- configuration to be enabled when the "staging" profile is active -->
</springProfile>

<springProfile name="dev | staging">
    <!-- configuration to be enabled when the "dev" or "staging" profiles are active -->
</springProfile>

<springProfile name="!production">
    <!-- configuration to be enabled when the "production" profile is not active -->
</springProfile>
```

logback.xml配置示例：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--
scan：当此属性设置为true时，配置文件如果发生改变，将会被重新加载，默认值为true。
scanPeriod：设置监测配置文件是否有修改的时间间隔，如果没有给出时间单位，默认单位是毫秒当scan为true时，此属性生效。默认的时间间隔为1分钟。
debug：当此属性设置为true时，将打印出logback内部日志信息，实时查看logback运行状态。默认值为false。
-->
<configuration scan="false" scanPeriod="60 seconds" debug="false">
    <!-- 定义日志的根目录 -->
    <property name="LOG_HOME" value="/Users/tongying/Workspace/springboot" />
    <!-- 定义日志文件名称 -->
    <property name="appName" value="javalearning-springboot"></property>
    <!-- ch.qos.logback.core.ConsoleAppender 表示控制台输出 -->
    <appender name="stdout" class="ch.qos.logback.core.ConsoleAppender">
        <!--
        日志输出格式：
			%d表示日期时间，
			%thread表示线程名，
			%-5level：级别从左显示5个字符宽度
			%logger{50} 表示logger名字最长50个字符，否则按照句点分割。
			%msg：日志消息，
			%n是换行符
        -->
        <layout class="ch.qos.logback.classic.PatternLayout">
            <!--使用logback-spring.xml时，支持Spring Boot高级特性springProfile，在不同的Profile生效-->
            <springProfile name="dev">
                <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} --> [%thread] %-5level %logger{50} - %msg%n</pattern>
            </springProfile>
            <springProfile name="!dev">
                <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} == [%thread] %-5level %logger{50} - %msg%n</pattern>
            </springProfile>

        </layout>
    </appender>

    <!-- 滚动记录文件，先将日志记录到指定文件，当符合某个条件时，将日志记录到其他文件 -->
    <appender name="appLogAppender" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 指定日志文件的名称 -->
        <file>${LOG_HOME}/${appName}.log</file>
        <!--
        当发生滚动时，决定 RollingFileAppender 的行为，涉及文件移动和重命名
        TimeBasedRollingPolicy： 最常用的滚动策略，它根据时间来制定滚动策略，既负责滚动也负责出发滚动。
        -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!--
            滚动时产生的文件的存放位置及文件名称 %d{yyyy-MM-dd}：按天进行日志滚动
            %i：当文件大小超过maxFileSize时，按照i进行文件滚动
            -->
            <fileNamePattern>${LOG_HOME}/${appName}-%d{yyyy-MM-dd}-%i.log</fileNamePattern>
            <!--
            可选节点，控制保留的归档文件的最大数量，超出数量就删除旧文件。假设设置每天滚动，
            且maxHistory是365，则只保存最近365天的文件，删除之前的旧文件。注意，删除旧文件是，
            那些为了归档而创建的目录也会被删除。
            -->
            <MaxHistory>365</MaxHistory>
            <!--
            当日志文件超过maxFileSize指定的大小是，根据上面提到的%i进行日志文件滚动 注意此处配置SizeBasedTriggeringPolicy是无法实现按文件大小进行滚动的，必须配置timeBasedFileNamingAndTriggeringPolicy
            -->
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
        <!-- 日志输出格式： -->
        <layout class="ch.qos.logback.classic.PatternLayout">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [ %thread ] - [ %-5level ] [ %logger{50} : %line ] - %msg%n</pattern>
        </layout>
    </appender>

    <!--
		logger主要用于存放日志对象，也可以定义日志类型、级别
		name：表示匹配的logger类型前缀，也就是包的前半部分
		level：要记录的日志级别，包括 TRACE < DEBUG < INFO < WARN < ERROR
		additivity：作用在于children-logger是否使用 rootLogger配置的appender进行输出，
		false：表示只用当前logger的appender-ref，true：
		表示当前logger的appender-ref和rootLogger的appender-ref都有效
    -->
    <!-- hibernate logger -->
    <logger name="com.atguigu" level="debug" />
    <!-- Spring framework logger -->
    <logger name="org.springframework" level="debug" additivity="false"></logger>



    <!--
    root与logger是父子关系，没有特别定义则默认为root，任何一个类只会和一个logger对应，
    要么是定义的logger，要么是root，判断的关键在于找到这个logger，然后判断这个logger的appender和level。
    -->
    <root level="info">
        <appender-ref ref="stdout" />
        <appender-ref ref="appLogAppender" />
    </root>
</configuration>
```



**5. 切换日志框架**

可以按照slf4j的日志适配图，进行相关的切换

- slf4j + log4j

    这样做并没有什么意义

    排除掉`logback`、`log4j-to-slf4j`，引入`slf4j-log4j12`

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <artifactId>logback-classic</artifactId>
            <groupId>ch.qos.logback</groupId>
        </exclusion>
        <exclusion>
            <artifactId>log4j-to-slf4j</artifactId>
            <groupId>org.apache.logging.log4j</groupId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-log4j12</artifactId>
</dependency>
```

- 切换为log4j2

    排除掉`spring-boot-starter-logging`，引入`spring-boot-starter-log4j2`即可

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <artifactId>spring-boot-starter-logging</artifactId>
            <groupId>org.springframework.boot</groupId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>
```

## 四、Web开发

### 1. 简介

### 2. 静态资源的映射规则

```java
// 可以设置和静态资源有关的参数，缓存时间等
@ConfigurationProperties(prefix = "spring.resources", ignoreUnknownFields = false)
public class ResourceProperties {
```



```java

// 配置webjars
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    if (!this.resourceProperties.isAddMappings()) {
        logger.debug("Default resource handling disabled");
        return;
    }
    Duration cachePeriod = this.resourceProperties.getCache().getPeriod();
    CacheControl cacheControl = this.resourceProperties.getCache().getCachecontrol().toHttpCacheControl();
    if (!registry.hasMappingForPattern("/webjars/**")) {
        customizeResourceHandlerRegistration(registry.addResourceHandler("/webjars/**")
                                             .addResourceLocations("classpath:/META-INF/resources/webjars/")
                                             .setCachePeriod(getSeconds(cachePeriod)).setCacheControl(cacheControl));
    }
    String staticPathPattern = this.mvcProperties.getStaticPathPattern();
    if (!registry.hasMappingForPattern(staticPathPattern)) {
        customizeResourceHandlerRegistration(registry.addResourceHandler(staticPathPattern)
                                             .addResourceLocations(getResourceLocations(this.resourceProperties.getStaticLocations()))
                                             .setCachePeriod(getSeconds(cachePeriod)).setCacheControl(cacheControl));
    }
}



// 配置欢迎页
@Bean
public WelcomePageHandlerMapping welcomePageHandlerMapping(ApplicationContext applicationContext,
                                                           FormattingConversionService mvcConversionService, ResourceUrlProvider mvcResourceUrlProvider) {
    WelcomePageHandlerMapping welcomePageHandlerMapping = new WelcomePageHandlerMapping(
        new TemplateAvailabilityProviders(applicationContext), applicationContext, getWelcomePage(),
        this.mvcProperties.getStaticPathPattern());
    welcomePageHandlerMapping.setInterceptors(getInterceptors(mvcConversionService, mvcResourceUrlProvider));
    welcomePageHandlerMapping.setCorsConfigurations(getCorsConfigurations());
    return welcomePageHandlerMapping;
}

// 
```

1）所有的`/webjars/**`，都去`classpath:/META-INF/resources/webjars/`找资源

`webjars`：以jar包的方式引入静态资源

[WebJars查询](https://www.webjars.org)

```xml
<!-- 引入jquery-webjar-->
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>jquery</artifactId>
    <version>3.5.1</version>
</dependency>
```

![image-20200705110257138](https://gitee.com/tongying003/MapDapot/raw/master/img/20200705110257.png)

在访问的时候只需要写webjars下面资源的名称即可。

例如：访问jquery.js：[localhost:8080/webjars/jquery/3.5.1/jquery.js]()



2） `/**`访问当前项目的任何资源

```java
	private static final String[] CLASSPATH_RESOURCE_LOCATIONS = { "classpath:/META-INF/resources/",
			"classpath:/resources/", "classpath:/static/", "classpath:/public/" };

	/**
	 * Locations of static resources. Defaults to classpath:[/META-INF/resources/,
	 * /resources/, /static/, /public/].
	 */
	private String[] staticLocations = CLASSPATH_RESOURCE_LOCATIONS;
```

 访问静态资源：[localhost:8080/my.js]()



3） 欢迎页：静态资源文件夹下所有的index.html页面，被`/**`映射

访问欢迎页：[localhost:8080]()



4）favicon.ico

2.2.x前：在静态资源文件下放置favicon.icon即可

2.2.x后：在html中配置，并将favicon放置在指定的路径

```html
 <link rel="icon" type="image/x-icon" href="/favicon.ico" />
```



### 3. 模版引擎

![模版引擎](https://gitee.com/tongying003/MapDapot/raw/master/img/20200705194120.png)

**1. 引入thymeleaf**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

<!-- 切换版本 -->
<properties>
    <java.version>1.8</java.version>
    <thymeleaf.version>3.0.11.RELEASE</thymeleaf.version>
    <thymeleaf-layout-dialect.version>2.4.1</thymeleaf-layout-dialect.version>
</properties>
```



**2. thymeleaf语法**

```java
public class ThymeleafProperties {

	private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

	public static final String DEFAULT_PREFIX = "classpath:/templates/";

	public static final String DEFAULT_SUFFIX = ".html";
```

只需要把html页面放在`classpath:/templates/`，thymeleaf就能自动渲染

1. 导入thymeleaf的名称空间

```html
<html lang="en" xmlns:th="http://www.thymeleaf.org">
```

2. 语法规则

 1）`th:text`：改变当前元素里面的文本内容

  	 `th:任意html属性`：替换原生属性的值

属性优先级：

| Order |              Feature               | Explanation                         | Attribute                                             |
| :---: | :--------------------------------: | :---------------------------------- | ----------------------------------------------------- |
|   1   |       **Fragment inclusion**       | 片段包含：jsp:include               | `th:insert`<br/>`th:replace`                          |
|   2   |      **Fragment Interation**       | 遍历：c:foreach                     | `th:each`                                             |
|   3   |      **Fragment evaluation**       | 条件判断：c:if                      | `th:if`<br/>`th:unless`<br/>`th:switch`<br/>`th:case` |
|   4   |   **Local variable definition**    | 声明变量：c:set                     | `th:object`<br>`th:with`                              |
|   5   | **General attribute modification** | 任意属性修改<br>支持prepend、append | `th:attr`<br>`th:attrprepend`<br>`th:attrapend`       |
|   6   | **Special attribute modification** | 修改指定属性默认值                  | `th:value`<br>`th:href`<br>`th:src`<br>...            |
|   7   |  **Text(tag body modification)**   | 修改标签体内容                      | `th:text`<br>`th:utext`                               |
|   8   |     **Fragment specification**     | 声明片段                            | `th:fragment`                                         |
|   9   |        **Fragment removal**        | 移除片段                            | `th：remove`                                          |

2）表达式

- **Simple expressions（表达式语法）**

    - **Variable Expressions: `${…}`**：获取变量值，OGNL

    （1）获取对象的属性、调用方法

    （2）使用内置的基本对象

    ```
    #ctx : the context object. 
    #vars: the context variables. 
    #locale : the context locale. 
    #request : (only in Web Contexts) the HttpServletRequest object. 
    #response : (only in Web Contexts) the HttpServletResponse object. 
    #session : (only in Web Contexts) the HttpSession object. 
    #servletContext : (only in Web Contexts) the ServletContext object.
    ```

    ```html
    <span th:text="${#locale.country}">US</span>.
    ```

    （3）内置的一些工具对象

    ```
    #execInfo : information about the template being processed. #messages : methods for obtaining externalized messages inside variables expressions, in the same way as they would be obtained using #{…} syntax. 
    #uris : methods for escaping parts of URLs/URIs
    #conversions : methods for executing the configured conversion service (if any). 
    #dates : methods for java.util.Date objects: formatting, component extraction, etc. 
    #calendars : analogous to #dates , but for java.util.Calendar objects. 
    #numbers : methods for formatting numeric objects. 
    #strings : methods for String objects: contains, startsWith, prepending/appending, etc. 
    #objects : methods for objects in general. #bools : methods for boolean evaluation. 
    #arrays : methods for arrays. 
    #lists : methods for lists. 
    #sets : methods for sets.
    #maps : methods for maps. 
    #aggregates : methods for creating aggregates on arrays or collections. 
    #ids : methods for dealing with id attributes that might be repeated (for example, as a result of an iteration).
    ```

    - **Selection Variable Expressions: `*{…}`**：选择表达式，在功能上和`${}`是一样的，补充：配合`th:object`使用。

    如，下面的两种写法功能相同

    ```html
    <div th:object="${session.user}"> 
        <p>Name: <span th:text="*{firstName}">Sebastian</span>.</p> 
        <p>Surname: <span th:text="*{lastName}">Pepper</span>.</p> 
        <p>Nationality: <span th:text="*{nationality}">Saturn</span>.</p> 
    </div>
    ```

    ```html
    <div> 
        <p>Name: <span th:text="${session.user.firstName}">Sebastian</span>.</p> 
        <p>Surname: <span th:text="${session.user.lastName}">Pepper</span>.</p> 
        <p>Nationality: <span th:text="${session.user.nationality}">Saturn</span>.</p> 
    </div>
    ```

    - **Message Expressions:** **`#{…}`** ：获取国际化内容
    - **Link URL Expressions:** **`@{…}`** ：定义URL

    ```html
    <!-- Will produce 'http://localhost:8080/gtvg/order/details?orderId=3' (plus rewriting) --> 
    <a href="details.html" th:href="@{http://localhost:8080/gtvg/order/details(orderId=${o.id})}">view</a> 
    <!-- Will produce '/gtvg/order/details?orderId=3' (plus rewriting) --> 
    <a href="details.html" th:href="@{/order/details(orderId=${o.id})}">view</a> 
    <!-- Will produce '/gtvg/order/3/details' (plus rewriting) --> 
    <a href="details.html" th:href="@{/order/{orderId}/details(orderId=${o.id})}">view</a>
    <!--多个参数-->
    <a href="detail.html" th:href="@{/order/process(execId=${execId},execType='FAST')}">view</a>
    ```

    - **Fragment Expressions:** **`~{…}`** ：片段引用表达式

- **Literals（字面量）** 

    - **Text literals:** **`'one text’`** **,**` **'Another one!’`** **,…** 

    - **Number literals:** **`0`** **,** **`34`** **,** **`3.0`** **,** **`12.3`** **,…** 

    - **Boolean literals:** **`true`** **,** **`false`** 

    - **Null literal:** **`null`** 

    - **Literal tokens:** **`one`** **,** **`sometext`** **,** **`main`** **,…** 

- **Text operations:（文本操作）** 

    - **String concatenation:** **`+`** 

    - **Literal substitutions:** **`|The name is ${name}|`** 

- **Arithmetic operations:（数学运算）** 

    - **Binary operators:** **`+`** **,** **`-`** **,** **`*`** **,** **`/`** **,** **`%`** 

    - **Minus sign (unary operator):** **`-`** 

- **Boolean operations:（布尔运算）** 
    - **Binary operators:** **`and`** **,** **`or`** 
    - **Boolean negation (unary operator):** **`!`** **,** **`not`** 

- **Comparisons and equality:（比较运算）** 

    - **Comparators:** **`>`** **,** **`<`** **,** **`>=`** **,** **`<=`** **(** **`gt`** **,** **`lt`** **,** **`ge`** **,** **`le`** **)** 

    - **Equality operators:** **`==`** **,** **`!=`** **(** **`eq`** **,** **`ne`** **)** 

- **Conditional operators:（条件运算）** 

    - **If-then:** **`(if) ? (then)`** 

    - **If-then-else:** **`(if) ? (then) : (else)`** 

    - **Default:** **`(value) ?: (defaultvalue)`** 

- **Special tokens:（特殊操作）** 

    - **No-Operation:** **`_`**



### 4. SpringMVC自动配置

**[1. SpringMVC Auto-configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-spring-mvc-auto-configuration)**

Spring Boot自动配置好了SpringMVC

- 视图解析器，包括`ContentNegotiatingResolver`和`BeanNameViewResolver`
    `ContentNegotiatingResolver`：组合所有的视图解析器。如何定制？我们可以自己给容器中添加一个视图解析器，会被自动组合进来。

- 支持静态资源，包括支持WebJars

- 静态首页访问`index.html`

- 支持`Favicon`

- 自动注册`Converter`、`GenericConverter`和`Formatter`

    `Converter`：转换器，页面带来的数据类型转换

    `Formatter`：格式化器，如时间

    ```java
    @Bean
    @Override
    public FormattingConversionService mvcConversionService() {
        // 在文件中配置日期格式化规则
        Format format = this.mvcProperties.getFormat();
        WebConversionService conversionService = new WebConversionService(new DateTimeFormatters()                                                           .dateFormat(format.getDate()).timeFormat(format.getTime()).dateTimeFormat(format.getDateTime()));
        addFormatters(conversionService);
        return conversionService;
    }
    ```

    自己添加的转换器和格式化器，只需要放到容器中即可。

- 支持`HttpMessageConverters`

    `HttpMessageConverters`：SpringMVC用来转换Http请求和响应的，如对象被转换为JSON

    自定义HttpMessageConverter，只需要将自己的组件注册到容器中

    ```java
    import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
    import org.springframework.context.annotation.*;
    import org.springframework.http.converter.*;
    
    @Configuration(proxyBeanMethods = false)
    public class MyConfiguration {
    
        @Bean
        public HttpMessageConverters customConverters() {
            HttpMessageConverter<?> additional = ...
            HttpMessageConverter<?> another = ...
            return new HttpMessageConverters(additional, another);
        }
    
    }
    ```

- 自动注册`MessageCodesResolver`

    `MessageCodesResolver`：定义错误代码生成规则

- 自动使用`ConfigurableWebBindingInitializer`

    `ConfigurableWebBindingInitializer`：初始化`WebDataBinder`，请求输出—>Java bean

    可以自定义`ConfigurableWebBindingInitializer`，只需将其注册到容器中

`org.springframework.boot.autoconfigure.web`：Web的所有自动自动场景



**2. 扩展SpringMVC**

编写一个配置类（`@Configuration`）,是`WebMvcConfigurer`类型，但是不能使用`@EnableWebMvc`注解

```java
// 使用WebMvcConfigurer可以扩展SpringMVC的功能
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 浏览器发送vew请求,来到success
        registry.addViewController("/view").setViewName("success");
    }
}
```

原理：

1）WebMvcAutoConfiguration是SpringMVC的自动配置类

2）在做其他自动配置时会导入`@Import(EnableWebMvcConfiguration.class)`

```java
@Configuration(proxyBeanMethods = false)
	public static class EnableWebMvcConfiguration extends DelegatingWebMvcConfiguration implements ResourceLoaderAware {
```

```java
public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport {

	private final WebMvcConfigurerComposite configurers = new WebMvcConfigurerComposite();

	// 从容器中获取所有的WebMvcConfigurer
	@Autowired(required = false)
	public void setConfigurers(List<WebMvcConfigurer> configurers) {
		if (!CollectionUtils.isEmpty(configurers)) {
			this.configurers.addWebMvcConfigurers(configurers);
		}
	}
    
    @Override
	protected void addViewControllers(ViewControllerRegistry registry) {
		this.configurers.addViewControllers(registry);
	}
```

一个参考实现，将所有的`WebMvcConfigurer`相关配置都来一起调用

```java
class WebMvcConfigurerComposite implements WebMvcConfigurer {

	private final List<WebMvcConfigurer> delegates = new ArrayList<>();
    
    // 省略

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		for (WebMvcConfigurer delegate : this.delegates) {
			delegate.addViewControllers(registry);
		}
	}
```

3）容器中所有的`WebMvcConfigurer`都会一起起作用，包括我们的扩展配置



**3. 全面接管SpringMVC**

Spring Boot放弃SpringMVC的自动配置，所有的配置都由用户自定义。

在配置类中添加`@EnableWebMvc`注解，所有的SpringMVC自动配置都会失效。

```java
@EnableWebMvc
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {
```

原理：

1）`@EnableWebMvc`的核心

```java
@Import(DelegatingWebMvcConfiguration.class)
public @interface EnableWebMvc {
}
```

2）`DelegatingWebMvcConfiguration.class`

```java
@Configuration(proxyBeanMethods = false)
public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport {
```

3）`WebMvcConfigurationSupport.class`

```java
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class })
// 划重点：容器中没有WebMvcConfigurationSupport.class组件的时候，这个配置类才生效
@ConditionalOnMissingBean(WebMvcConfigurationSupport.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
@AutoConfigureAfter({ DispatcherServletAutoConfiguration.class, TaskExecutionAutoConfiguration.class,
		ValidationAutoConfiguration.class })
public class WebMvcAutoConfiguration {
```

4）`@EnableWebMvc`将`WebMvcConfigurationSupport`组件导入进来导致`WebMvcAutoConfiguration`配置类失效，导入的`WebMvcConfigurationSupport`只是SpringMVC最基本的功能



### 5. 如何修改Spring Boot的默认配置

1. Spring Boot在自动配置很多组件的时候，先看容器中有没有用户自己配置的，如果有就用自己配置的，如果没有，才自动配置，最后会将用户配置和默认配置组合起来。
2. 在Spring Boot中会有非常多的`xxxConfigurer`帮助我们进行扩展配置
3. 在Spring Boot中会有很多的`xxxCustomizer`帮助我们进行配置定制





### 6. RestfulCRUD

**1. 默认访问首页**

```java
// 自定义WebMvcConfigurer，所有的WebMvcConfigurer组件都会一起起作用
@Bean
public WebMvcConfigurer webMvcConfigurer(){
    return new WebMvcConfigurer() {
        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/").setViewName("login");
        }
    };
}
```



**2. 国际化**

1）编写国际化配置文件

![image-20200707143851053](https://gitee.com/tongying003/MapDapot/raw/master/img/20200707143851.png)

2）Spring Boot自动配置好了国际化资源文件的组件

```java
@Bean
public MessageSource messageSource(MessageSourceProperties properties) {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    if (StringUtils.hasText(properties.getBasename())) {
        // 设置国际化资源文件的基础名，上面是login
        messageSource.setBasenames(StringUtils                    .commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(properties.getBasename())));
    }
    if (properties.getEncoding() != null) {
        messageSource.setDefaultEncoding(properties.getEncoding().name());
    }
    messageSource.setFallbackToSystemLocale(properties.isFallbackToSystemLocale());
    Duration cacheDuration = properties.getCacheDuration();
    if (cacheDuration != null) {
        messageSource.setCacheMillis(cacheDuration.toMillis());
    }
    messageSource.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat());
    messageSource.setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage());
    return messageSource;
}
```



3）去页面获取国际化的值

```html
<form class="form-signin">
    <img class="mb-4" th:src="@{/asserts/img/bootstrap-solid.svg}" alt="" width="72" height="72">
    <h1 class="h3 mb-3 font-weight-normal" th:text="#{login.tip}">Please sign in</h1>
    <label for="inputEmail" class="sr-only" th:text="#{login.email}">Email address</label>
    <input type="email" id="inputEmail" class="form-control" placeholder="Email address" th:placeholder="#{login.email}" required autofocus>
    <label for="inputPassword" class="sr-only" th:text="#{login.password}">Password</label>
    <input type="password" id="inputPassword" class="form-control" placeholder="Password" th:placeholder="#{login.password}" required>
    <div class="checkbox mb-3">
        <label>
            <input type="checkbox" value="remember-me"> [[#{login.remember}]]
        </label>
    </div>
    <button class="btn btn-lg btn-primary btn-block" type="submit" th:text="#{login.btn}">Sign in</button>
    <p class="mt-5 mb-3 text-muted">&copy; 2017-2020</p>
</form>
```

效果：根据浏览器的语言设置自动切换页面信息。



原理：

```java
@Bean
@ConditionalOnMissingBean
@ConditionalOnProperty(prefix = "spring.mvc", name = "locale")
public LocaleResolver localeResolver() {
    if (this.mvcProperties.getLocaleResolver() == WebMvcProperties.LocaleResolver.FIXED) {
        return new FixedLocaleResolver(this.mvcProperties.getLocale());
    }
    AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
    localeResolver.setDefaultLocale(this.mvcProperties.getLocale());
    return localeResolver;
}
```

默认的就是根据请求头带来的区域信息获取Locale进行国际化。



4）点击链接切换国际化

```java
/**
 * 根据连接上携带的区域信息进行国际化
 */
public class MyLocaleResolver implements LocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String localeParamStr = request.getParameter("locale");
        Locale locale = Locale.getDefault();
        if(!StringUtils.isEmpty(localeParamStr)){
            String[] localeParamArray = localeParamStr.split("_");
            locale = new Locale(localeParamArray[0], localeParamArray[1]);
        }
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {

    }
}

/**
 * 国际化配置组件
 * @return  自定义国际化组件
 */
@Bean
public LocaleResolver localeResolver(){
    return new MyLocaleResolver();
}
```



**3. 拦截器进行登陆检查**

1）拦截器

```java
/**
 * 登录拦截器
 */
public class LoginHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object user = request.getSession().getAttribute("loginUser");
        if(user == null){
            request.setAttribute("msg", "请先登录");
            request.getRequestDispatcher("/index.html").forward(request, response);
            return false;
        }
        return true;
    }
}
```

2）注册拦截器

```java
/**
 * 注册拦截器
 * @param registry
 */
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new LoginHandlerInterceptor()).addPathPatterns("/**")
        .excludePathPatterns("/index.html", "/", "/user/login", "/asserts/**", "/webjars/**");
}
};
```



**4. 错误处理机制**

1）Spring boot默认的错误处理机制

默认效果：

- 返回一个默认的错误页面

![image-20200709012939775](https://gitee.com/tongying003/MapDapot/raw/master/img/20200709012940.png)

浏览器发送请求的请求头

![image-20200709015342520](https://gitee.com/tongying003/MapDapot/raw/master/img/20200709015342.png)

- 如果是其他客户端，默认响应一个JSON数据

```json
{
    "timestamp": "2020-07-08T17:30:29.479+00:00",
    "status": 404,
    "error": "Not Found",
    "message": "",
    "path": "/aa"
}
```

Postman发送请求的请求头

![image-20200709015629352](https://gitee.com/tongying003/MapDapot/raw/master/img/20200709015629.png)

原理：

可以参照`ErrorMvcAutoConfiguration`，错误处理的默认配置，它给容器中添加了如下组件

- `DefaultErrorAttributes`

```java
// 该组件帮我们在页面共享信息
@Override
@Deprecated
public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
    Map<String, Object> errorAttributes = new LinkedHashMap<>();
    errorAttributes.put("timestamp", new Date());
    addStatus(errorAttributes, webRequest);
    addErrorDetails(errorAttributes, webRequest, includeStackTrace);
    addPath(errorAttributes, webRequest);
    return errorAttributes;
}
```



- `BasicErrorController`

```java
// 处理默认/error请求
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class BasicErrorController extends AbstractErrorController {
    
    // 产生html类型的数据，处理浏览器请求
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = getStatus(request);
        Map<String, Object> model = Collections
            .unmodifiableMap(getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.TEXT_HTML)));
        response.setStatus(status.value());
        // 去哪个页面作为错误页面，包含页面地址和页面内容和
        ModelAndView modelAndView = resolveErrorView(request, response, status, model);
        return (modelAndView != null) ? modelAndView : new ModelAndView("error", model);
    }

    // 产生json数据，处理其他客户端请求
    @RequestMapping
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        if (status == HttpStatus.NO_CONTENT) {
            return new ResponseEntity<>(status);
        }
        Map<String, Object> body = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.ALL));
        return new ResponseEntity<>(body, status);
    }
```

- `ErrorPageCustomizer`

```java
	/**
	 * Path of the error controller.
	 * 系统出现错误后来到error请求进行处理，类似于web.xml注册的错误页面规则
	 */
	@Value("${error.path:/error}")
	private String path = "/error";
```

- `DefaultErrorViewResolver`

```java
@Override
public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
    ModelAndView modelAndView = resolve(String.valueOf(status.value()), model);
    if (modelAndView == null && SERIES_VIEWS.containsKey(status.series())) {
        modelAndView = resolve(SERIES_VIEWS.get(status.series()), model);
    }
    return modelAndView;
}

private ModelAndView resolve(String viewName, Map<String, Object> model) {
    // 默认spring boot可以去找到一个页面，如error/404
    String errorViewName = "error/" + viewName;
    // 如果模版引擎可以解析这个页面地址，就用模版引擎解析
    TemplateAvailabilityProvider provider = this.templateAvailabilityProviders.getProvider(errorViewName,this.applicationContext);
    if (provider != null) {
        // 模版引擎可用
        return new ModelAndView(errorViewName, model);
    }
    // 模版引擎不可用，就在静态资源文件夹下找ModelAndView对应的页面
    return resolveResource(errorViewName, model);
}
```

步骤：

​		一旦系统出现4xx或者5xx之类的错误，`ErrorPageCustomizer`就会定制错误的响应规则，来到`/error`请求，该请求会被`BasicErrorController`处理。分为响应页面和响应JSON数据，去哪个页面是由`DefaultErrorViewResolver`解析得到的，响应的信息由`DefaultErrorAttributes`组件进行设置。

```java
protected ModelAndView resolveErrorView(HttpServletRequest request, HttpServletResponse response, HttpStatus status,Map<String, Object> model) {
    // 遍历所有的ErrorViewResolver,得到ModelAndView
    for (ErrorViewResolver resolver : this.errorViewResolvers) {
        ModelAndView modelAndView = resolver.resolveErrorView(request, status, model);
        if (modelAndView != null) {
            return modelAndView;
        }
    }
    return null;
}
```

2）如何定制错误响应

- 如何定制错误页面

    - 有模版引擎的情况下，页面地址`error/状态码`，将错误页面放在模版引擎文件夹下的error文件夹下，优先寻找精确的`status.html`页面，也可以用`4xx.html`和`5xx.html`作为错误页面的文件名。页面能获取的信息有：

    ```
    timestamp 	时间戳
    status		状态码
    error		错误提示
    exception	异常对象
    message		异常信息
    errors		JSR303数据校验的错误
    ```

    - 没有模版引擎的情况（模版引擎找不到这个错误页面）,就在静态资源文件夹下找
    - 没有自定义的错误页面，来到Spring Boot默认的错误提示页面

- 如何定制错误响应数据

    - 自定义异常处理，返回定制JSON数据

    ```java
    /**
     * 自定义异常处理
     * 无自适应，即不论是浏览器还是客户端都返回json数据
     */
    @ControllerAdvice
    public class MyExceptionHandler{
    
        @ResponseBody
        @ExceptionHandler(UserNotExistException.class)
        public Map<String, Object> handlerException(Exception e){
            Map<String, Object> map = new HashMap<>();
            map.put("code", "user.notExist");
            map.put("message", e.getMessage());
            return map;
        }
    }
    ```

    - 自定义异常处理，自适应效果处理

    出现错误以后，会来到/error请求，被`BasicErrorController`处理，响应出去可以获取的数据是由`getErrorAttributes`得到的，该方法由`AbstractErrorController`规定。

    （1）编写一个`ErrorController`（或者编写`AbstractErrorController`的子类）的实现类，放在容器中

    （2）页面上能用的数据或者JSON数据都是通过`errorAttributes.getErrorAttributes`得到，容器中默认由`DefaultErrorAttributes`进行数据处理，可以自定义`ErrorAttribute`，实现`getErrorAttributes`方法以定制响应数据。

    ```java
    // 给容器中加入自定义的ErrorAttributes
    @Component
    public class MyErrorAttributes extends DefaultErrorAttributes {
    
        // 返回值的Map就是页面和JSON能获取的所有字段
        @Override
        public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
            Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);
            errorAttributes.put("company", "Coco");
            // 添加异常处理器携带的数据
            Map<String, Object> ext = (Map<String, Object>) webRequest.getAttribute("ext", 0);
            errorAttributes.put("ext", ext);
            return errorAttributes;
        }
    }
    ```



### 7. 配置嵌入式Servlet容器

**1. 如何定制和修改Servlet容器的相关配置**

1）修改和server有关的配置（ServerProperties），也是

```properties
server.port=8081
server.servlet.context-path=/crud
server.tomcat.uri-encoding=UTF-8
```

2）编写一个`WebServerFactoryCustomizer`

```java
    /**
     * 配置嵌入式Tomcat容器
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> webServerFactoryWebServerFactoryCustomizer() {
        return new WebServerFactoryCustomizer<TomcatServletWebServerFactory>() {
            @Override
            public void customize(TomcatServletWebServerFactory factory) {
                factory.setPort(8082);
            }
        };
    }
```



**2. 注册servlet三大组件**

1）`ServletRegistrationBean`

```java
/*
 * 注册自定义Servlet到容器中
 */
@Bean
public ServletRegistrationBean<MyServlet> myServlet(){
    return new ServletRegistrationBean<>(new MyServlet(), "/myServlet");
}
```



2）`FilterRegistrationBean`

```java
/*
 * 注册自定义Filter到容器中
 */
@Bean
public FilterRegistrationBean<MyFilter> myFilter(){
    FilterRegistrationBean<MyFilter> filter = new FilterRegistrationBean<MyFilter>();
    filter.setFilter(new MyFilter());
    filter.setUrlPatterns(Arrays.asList("/hello", "/myServlet"));
    return filter;
}
```

3）`ServletListenerRegistrationBean`

```java
/*
 * 注册自定义Listener
 */
@Bean
public ServletListenerRegistrationBean<MyListener> myListener(){
    return new ServletListenerRegistrationBean<>(new MyListener());
}
```

Spring Boot帮我们配置SpringMVC的时候，自动注册SpringMVC的前端控制器`DispatcherServlet`

```java
@Bean(name = DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME)
@ConditionalOnBean(value = DispatcherServlet.class, name = DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
public DispatcherServletRegistrationBean dispatcherServletRegistration(DispatcherServlet dispatcherServlet,
                                                                       WebMvcProperties webMvcProperties, ObjectProvider<MultipartConfigElement> multipartConfig) {
    // 默认拦截"/",包括静态资源，但是不拦截JSP,/*会拦截JSP
    // 可以通过server.
    DispatcherServletRegistrationBean registration = new DispatcherServletRegistrationBean(dispatcherServlet,                                                                webMvcProperties.getServlet().getPath());
    registration.setName(DEFAULT_DISPATCHER_SERVLET_BEAN_NAME);
    registration.setLoadOnStartup(webMvcProperties.getServlet().getLoadOnStartup());
    multipartConfig.ifAvailable(registration::setMultipartConfig);
    return registration;
}
```



**3. 替换为其他嵌入式Servlet容器**

支持以下几种Servlet容器

- Tomcat
- Jetty
- Undertow

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-web</artifactId>
	<exclusions>
	<!--排除默认的tomcat-->
		<exclusion>
			<artifactId>spring-boot-starter-tomcat</artifactId>
			<groupId>org.springframework.boot</groupId>
	</exclusion>
	</exclusions>
</dependency>

<!--引入jetty-->
<dependency>
    <artifactId>spring-boot-starter-jetty</artifactId>
    <groupId>org.springframework.boot</groupId>
</dependency>
```



**4. 嵌入式Servlet容器自动配置原理**

`ServletWebServerFactoryAutoConfituration`：Servlet容器的自动配置

```java
@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnClass(ServletRequest.class)
@ConditionalOnWebApplication(type = Type.SERVLET)
@EnableConfigurationProperties(ServerProperties.class)
@Import({ ServletWebServerFactoryAutoConfiguration.BeanPostProcessorsRegistrar.class,
         // BeanPostProcessorsRegistrar：给容器中导入一些组件
         // WebServerFactoryCustomizerBeanPostProcessor，后置处理器
         // 后置处理器：bean初始化前后（创建完对象，还没开始属性赋值）执行
		ServletWebServerFactoryConfiguration.EmbeddedTomcat.class,
		ServletWebServerFactoryConfiguration.EmbeddedJetty.class,
		ServletWebServerFactoryConfiguration.EmbeddedUndertow.class })
public class ServletWebServerFactoryAutoConfiguration {
```

先看`@Import`导入了一系列的组件，这里先跳过`BeanPostProcessorsRegistrar`，`ServletWebServerFactoryConfiguration`根据导入的依赖（Tomcat、Jetty还是Undertow）帮助我们导入Servlet容器工厂。

```java
@Configuration(proxyBeanMethods = false)
class ServletWebServerFactoryConfiguration {

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass({ Servlet.class, Tomcat.class, UpgradeProtocol.class })
	@ConditionalOnMissingBean(value = ServletWebServerFactory.class, search = SearchStrategy.CURRENT)	// 判断容器中没有用户自己定义的ServletWebServerFactory:Servlet容器工厂，其作用是创								建嵌入式的Servlet容器
	static class EmbeddedTomcat {

		@Bean
		TomcatServletWebServerFactory tomcatServletWebServerFactory(
				ObjectProvider<TomcatConnectorCustomizer> connectorCustomizers,
				ObjectProvider<TomcatContextCustomizer> contextCustomizers,
				ObjectProvider<TomcatProtocolHandlerCustomizer<?>> protocolHandlerCustomizers) {
			TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
			factory.getTomcatConnectorCustomizers()
					.addAll(connectorCustomizers.orderedStream().collect(Collectors.toList()));
			factory.getTomcatContextCustomizers()
					.addAll(contextCustomizers.orderedStream().collect(Collectors.toList()));
			factory.getTomcatProtocolHandlerCustomizers()
					.addAll(protocolHandlerCustomizers.orderedStream().collect(Collectors.toList()));
			return factory;
		}

	}
```

1）`ServletWebServerFactory`：Servlet容器工厂

```java
@FunctionalInterface
public interface ServletWebServerFactory {
    // 获取Servlet容器
    WebServer getWebServer(ServletContextInitializer... initializers);
}
```

该工厂有三个实现类。

![image-20200717143556819](https://gitee.com/tongying003/MapDapot/raw/master/img/20200717143557.png)

2）`WebServer`：Servlet容器

![image-20200717143946940](https://gitee.com/tongying003/MapDapot/raw/master/img/20200717143947.png)

3）以`TomcatServletWebServerFactory`为例

```java
public class TomcatServletWebServerFactory extends AbstractServletWebServerFactory
		implements ConfigurableTomcatWebServerFactory, ResourceLoaderAware {
    	@Override
	public WebServer getWebServer(ServletContextInitializer... initializers) {
		if (this.disableMBeanRegistry) {
			Registry.disableRegistry();
		}
        // 创建一个Tomcat
		Tomcat tomcat = new Tomcat();
        // 配置Tomcat的基本环境
		File baseDir = (this.baseDirectory != null) ? this.baseDirectory : createTempDir("tomcat");
		tomcat.setBaseDir(baseDir.getAbsolutePath());
		Connector connector = new Connector(this.protocol);
		connector.setThrowOnFailure(true);
		tomcat.getService().addConnector(connector);
		customizeConnector(connector);
		tomcat.setConnector(connector);
		tomcat.getHost().setAutoDeploy(false);
		configureEngine(tomcat.getEngine());
		for (Connector additionalConnector : this.additionalTomcatConnectors) {
			tomcat.getService().addConnector(additionalConnector);
		}
		prepareContext(tomcat.getHost(), initializers);
        // 将配置好的Tomcat传入进去，返回一个WebServer（Servlet容器），并且启动Tomcat服务器
		return getTomcatWebServer(tomcat);
	}
```

4）对嵌入式容器的配置修改是如何生效的

- 配置文件（修改ServerProperties）
- 自定义`WebServerFactorryCustomizer`

两种方式都是通过**`WebServerFactoryCustomizer(Server容器工厂定制器)`**来帮我们修改Servlet容器的配置，Spring Boot帮助我们创建了三个定制器：

- **`ServletWebServerFactoryCustomizer`：**配置tomcat的主要信息，包含remoteIpValue、connector(最大/最小可接收线程、最大可接收头部大小等等)、uriEncoding、connectionTimeout、maxConnection等属性
- **`TomcatServletWebServerFactoryCustomizer`：**配置tomcat的额外信息，redirectContextRoot(是否在请求根上下文时转发，true则转发路径为/demoWeb/)和useRelativeRedirects(是否使用相对路径)等路径跳转问题处理
- **`TomcatWebServerFactoryCustomizer`：**配置tomcat的servlet的信息，包含端口、上下文路径、应用名、Session配置、Servlet携带的初始变量等等

5）原理：由**`@Import`**引入的**`BeanPostProcessorsRegistrar`**类，注册了*`webServerFactoryCustomizerBeanPostProcessor`*类来完成相应的tomcat个性化配置

```java
// 初始化之前
@Override
public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    // 如果初始化的是Servlet容器工厂
    if (bean instanceof WebServerFactory) {
        postProcessBeforeInitialization((WebServerFactory) bean);
    }
    return bean;
}

// 获取所有的定制器，调用每一个定制器的customize方法来为Servlet容器工厂赋值
@SuppressWarnings("unchecked")
private void postProcessBeforeInitialization(WebServerFactory webServerFactory) {
    LambdaSafe.callbacks(WebServerFactoryCustomizer.class, getCustomizers(), webServerFactory)
        .withLogger(WebServerFactoryCustomizerBeanPostProcessor.class)
        .invoke((customizer) -> customizer.customize(webServerFactory));
}

// 从容器中获取所有类型为WebServerFactoryCustomizer的组件
private Collection<WebServerFactoryCustomizer<?>> getCustomizers() {
    if (this.customizers == null) {
        // Look up does not include the parent context
        this.customizers = new ArrayList<>(getWebServerFactoryCustomizerBeans());
        this.customizers.sort(AnnotationAwareOrderComparator.INSTANCE);
        this.customizers = Collections.unmodifiableList(this.customizers);
    }
    return this.customizers;
}
```

从上面的代码中可以看出，定制Servlert容器，可以给IOC容器中添加一个`WebServerFactoryCustomizer`类型的组件。

步骤：

1. Spring Boot根据导入的依赖情况给容器中添加相应的Servlet容器工厂
2. 容器中某个组件要创建对象就会惊动后置处理器
3. 后置处理器从IOC容器中获取所有的Servlet容器工厂定制器，调用定制器的定制方法



**5. 嵌入式Servlet容器启动原理**

什么时候创建Servlet容器工厂？

什么时候获取Servlet容器并启动Tomcat？

获取Servlet容器工厂：

1）Spring Boot应用启动运行run方法

2）`refreshContext(context);`Spring Boot刷新IOC容器：创建IOC容器对象，并初始化容器，创建容器中的每一个组件，如果是Servlet类型的Web应用，创建**AnnotationConfigServletWebServerApplicationContext**

3）刷新刚才创建的IOC容器

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
    synchronized (this.startupShutdownMonitor) {
        // Prepare this context for refreshing.
        prepareRefresh();

        // Tell the subclass to refresh the internal bean factory.
        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

        // Prepare the bean factory for use in this context.
        prepareBeanFactory(beanFactory);

        try {
            // Allows post-processing of the bean factory in context subclasses.
            postProcessBeanFactory(beanFactory);

            // Invoke factory processors registered as beans in the context.
            invokeBeanFactoryPostProcessors(beanFactory);

            // Register bean processors that intercept bean creation.
            registerBeanPostProcessors(beanFactory);

            // Initialize message source for this context.
            initMessageSource();

            // Initialize event multicaster for this context.
            initApplicationEventMulticaster();

            // Initialize other special beans in specific context subclasses.
            onRefresh();

            // Check for listener beans and register them.
            registerListeners();

            // Instantiate all remaining (non-lazy-init) singletons.
            finishBeanFactoryInitialization(beanFactory);

            // Last step: publish corresponding event.
            finishRefresh();
        }

        catch (BeansException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Exception encountered during context initialization - " +
                            "cancelling refresh attempt: " + ex);
            }

            // Destroy already created singletons to avoid dangling resources.
            destroyBeans();

            // Reset 'active' flag.
            cancelRefresh(ex);

            // Propagate exception to caller.
            throw ex;
        }

        finally {
            // Reset common introspection caches in Spring's core, since we
            // might not ever need metadata for singleton beans anymore...
            resetCommonCaches();
        }
    }
}
```

4）`onRefresh();`web的IOC容器重写了onRefresh方法

5）web的IOC容器会创建Servlet容器

```java
@Override
protected void onRefresh() {
    super.onRefresh();
    try {
        createWebServer();
    }
    catch (Throwable ex) {
        throw new ApplicationContextException("Unable to start web server", ex);
    }
}
```

6）获取Servlet容器工厂

```java
// ServletWebServerApplicationContext.class
ServletWebServerFactory factory = getWebServerFactory();
```

从IOC容器中获取**ServletWebServerFactory**组件，**TomcatServletWebServerFactory**创建对象，惊动后置处理器，获取所有的定制器 来先定制Servlet容器相关的配置

7）使用容器工厂获取Servlet容器

```java
// ServletWebServerApplicationContext.class
this.webServer = factory.getWebServer(getSelfInitializer());
```

8）Servlet容器创建对象并启动Servlet容器



**先启动Servlet容器，再将IOC容器中其他剩下没有创建的对象获取出来。**

总结：IOC容器启动时创建Servlet容器



### 8. 使用外置的Servlet容器

**1. 嵌入式Servlet容器：jar**

- 优点：简单、便携

- 缺点：默认不支持JSP、优化定制比较麻烦（使用定制器——配置**ServerProperties**或者自定义**WebServerFactoryCustomizer**，自己编写嵌入式Servlet容器的创建工厂——**WebServerFactory**）



**2. 外置Servlet容器：外部安装Tomcat——war**

**步骤：**

1）必须创建一个war项目

2）将嵌入式的Tomcat指定为provided

```java
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>
```

3）必须编写一个**SpringBootServletInitializer**的子类，并调用*configure*方法

```java
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        // 传入SpringBoot应用的主程序
        return application.sources(SpringBoot04WebJspApplication.class);
    }

}
```

4）启动服务器



**原理：**

jar包：执行Spring Boot主类的main方法，启动IOC容器，创建嵌入式的Servlet容器

war包：启动服务器，服务器启动Spring Boot应用（**SpringBootServletInitializer**），启动IOC容器



Servlet3.0中规则：

1）服务器（web应用）启动会创建当前web应用里面每一个jar包里**ServletContainerInitializer**实例

2）**ServletContainerInitializer**的实现放在jar包的META-INF/services文件夹下，有一个名为**javax.servlet.ServletContainerInitializer**的文件，内容是**ServletContainerInitialier**的实现类的全类名

3）还可以使用`@HandlesTypes`，在应用启动的时候加载我们感兴趣的类



流程：

1）启动tomcat

2）org/springframework/spring-web/5.2.7.RELEASE/spring-web-5.2.7.RELEASE.jar!/META-INF/services/javax.servlet.ServletContainerInitializer：

Spring的web模块里面有这个文件：**org.springframework.web.SpringServletContainerInitializer**

3）**SpringServletContainerInitializer**将`@HandlesTypes(WebApplicationInitializer.class)`标注的所有这个类型的类都传入到*onStartup*方法的*Set<Class<?>>*，为这些WebApplicationInitializer类型的类创建实例

4）每一个**WebApplicationInitializer**都调用自己的*onStartup*

![image-20200718115810266](https://gitee.com/tongying003/MapDapot/raw/master/img/20200718115810.png)

5）相当于我们的**SpringBootServletInitializer**类，并执行*onStartup*方法

6）**SpringBootServletInitializer**实例执行*onStartup*方法的时候会调用*createRootApplicationContext*，创建容器

```java
protected WebApplicationContext createRootApplicationContext(ServletContext servletContext) {
    // 1. 创建SpringApplicationBuilder
		SpringApplicationBuilder builder = createSpringApplicationBuilder();
		builder.main(getClass());
		ApplicationContext parent = getExistingRootWebApplicationContext(servletContext);
		if (parent != null) {
			this.logger.info("Root context already created (using as parent).");
			servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, null);
			builder.initializers(new ParentContextApplicationContextInitializer(parent));
		}
		builder.initializers(new ServletContextApplicationContextInitializer(servletContext));
		builder.contextClass(AnnotationConfigServletWebServerApplicationContext.class);
    // 调用configure方法，子类重写了这个方法，将SpringBoot的主程序类传入进来
		builder = configure(builder);
		builder.listeners(new WebEnvironmentPropertySourceInitializer(servletContext));
    // 使用builder创建一个Spring应用
		SpringApplication application = builder.build();
		if (application.getAllSources().isEmpty()
				&& MergedAnnotations.from(getClass(), SearchStrategy.TYPE_HIERARCHY).isPresent(Configuration.class)) {
			application.addPrimarySources(Collections.singleton(getClass()));
		}
		Assert.state(!application.getAllSources().isEmpty(),
				"No SpringApplication sources have been defined. Either override the "
						+ "configure method or add an @Configuration annotation");
		// Ensure error pages are registered
		if (this.registerErrorPageFilter) {
			application.addPrimarySources(Collections.singleton(ErrorPageFilterConfiguration.class));
		}
		application.setRegisterShutdownHook(false);
    // 启动Spring应用
		return run(application);
	}
```

7）Spring的应用就启动了，并且创建IOC容器

```java
public ConfigurableApplicationContext run(String... args) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		ConfigurableApplicationContext context = null;
		Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList<>();
		configureHeadlessProperty();
		SpringApplicationRunListeners listeners = getRunListeners(args);
		listeners.starting();
		try {
			ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
			ConfigurableEnvironment environment = prepareEnvironment(listeners, applicationArguments);
			configureIgnoreBeanInfo(environment);
			Banner printedBanner = printBanner(environment);
			context = createApplicationContext();
			exceptionReporters = getSpringFactoriesInstances(SpringBootExceptionReporter.class,
					new Class[] { ConfigurableApplicationContext.class }, context);
			prepareContext(context, environment, listeners, applicationArguments, printedBanner);
            // 刷新IOC容器
			refreshContext(context);
			afterRefresh(context, applicationArguments);
			stopWatch.stop();
			if (this.logStartupInfo) {
				new StartupInfoLogger(this.mainApplicationClass).logStarted(getApplicationLog(), stopWatch);
			}
			listeners.started(context);
			callRunners(context, applicationArguments);
		}
		catch (Throwable ex) {
			handleRunFailure(context, ex, exceptionReporters, listeners);
			throw new IllegalStateException(ex);
		}

		try {
			listeners.running(context);
		}
		catch (Throwable ex) {
			handleRunFailure(context, ex, exceptionReporters, null);
			throw new IllegalStateException(ex);
		}
		return context;
	}
```

启动Servlet容器，再启动Spring应用

## 五、Spring Boot与Docker

### 1. 简介

Docker是一个开源的应用容器引擎

Docker支持将软件编译成一个镜像，然后在镜像中各种软件做好配置，将镜像发布出去，其他使用者可以直接使用这个镜像

运行中的这个镜像称为容器，容器启动是非常快速的

![Docker](https://gitee.com/tongying003/MapDapot/raw/master/img/20200718182834.svg)

### 2. 核心概念

**Docker主机（Host）**：安装了Docker程序的主机（Docker直接安装在操作系统之上）

**Docker客户端（Client**）：连接Docker主机进行操作

**Docker仓库（Registry）**：用来保存各种打包好的软件镜像

**Docker镜像（Images）**：软件打包好的镜像，放在Docker仓库中

**Docker容器（Container）**：镜像启动后的实例称为一个容器，容器是独立运行的一个或一组应用

![Docker2](https://gitee.com/tongying003/MapDapot/raw/master/img/20200718192422.svg)

**使用Docker的步骤**：

1）安装Docker

2）去Docker仓库找到这个软件对应的镜像

3）使用Docker运行这个镜像，这个镜像就会生成一个Docker容器

4）对容器的启动停止就是对软件的启动停止



### 3. 安装Docker

[Docker官网安装教程](https://docs.docker.com/engine/install/centos/)

启动Docker

```shell
systemctl start docker
```

停止Docker

```shell
systemctl stop docker
```

更换阿里镜像源

- 进入https://cr.console.aliyun.com/
- 点击左侧`镜像加速器`

- 执行命令`vim /etc/docker/daemon.json`，加入如下内容

```json
{
  "registry-mirrors": ["https://kh4uoxfp.mirror.aliyuncs.com"]
}
```

- 重启docker

```shell
sudo systemctl daemon-reload
sudo systemctl restart docker
```

### 4. 常用操作

#### 4.1 镜像操作

| 操作 | 命令                     | 说明                                                    |
| ---- | ------------------------ | ------------------------------------------------------- |
| 检索 | `docker search 关键字`   | 在docker hub上检索镜像详细信息，如镜像的TAG             |
| 拉取 | `docker pull 镜像名:tag` | :tag是可选的，tag表示标签，多为软件的版本，默认是latest |
| 列表 | `docker images`          | 查看所有本地镜像                                        |
| 删除 | `docker rmi image-id`    | 删除指定的本地镜像                                      |

#### 4.2 容器操作

| 操作     | 命令                                                         | 说明                                                         |
| -------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 运行     | `docker run --name container-name -d image-name`<br>eg: `docker run --name myredis -d redis` | `--name`:自定义容器名<br>`-d`：后台运行<br>image-name: 指定镜像模版 |
| 列表     | `docker ps`（查看运行中的容器）                              | 加上`-a`可以查看所有容器                                     |
| 停止     | `docker stop container-name/container-id`                    | 停止当前运行的容器                                           |
| 启动     | `docker start container-name/container-id`                   | 启动容器                                                     |
| 删除     | `docker rm container-id`                                     | 删除指定容器                                                 |
| 端口映射 | -p 6379:6379<br>eg:`docker run -d -p 6379:6379 --name myredis docker.io/redis` | -p:主机端口（映射到）容器内部的端口                          |
| 容器日志 | `docker logs container-name/container-id`                    |                                                              |
| 更多命令 | https://docs.docker.com/reference/                           |                                                              |
|          |                                                              |                                                              |

软件镜像—>运行镜像—>产生一个容器（正在运行的软件）

**示例**

```shell
1. 搜索镜像
[root@Mangoo ~]# docker search tomcat
2. 拉去镜像
[root@Mangoo ~]# docker pull tomcat
3. 根据镜像启动容器
[root@Mangoo ~]# docker run --name mytomcat -d tomcat:latest
4. 查看运行中的容器
[root@Mangoo ~]# docker ps
5. 停止运行中的容器
[root@Mangoo ~]# docker stop a3ba0917b5dd
6. 查看所有的容器
[root@Mangoo ~]# docker ps -a
7. 启动容器
[root@Mangoo ~]# docker start a3ba0917b5dd
8. 删除容器
[root@Mangoo ~]# docker rm a3ba0917b5dd
9. 端口映射
[root@Mangoo ~]# docker run -p -d 8888:8080 tomcat
10. 访问tomcat首页
1) 在阿里云安全组配置中打开8888端口
2）若是启用了防火墙，配置防火墙并打开8888端口
3）在阿里源拉取的镜像webapps目录下为空，需将webapps.dist改名为webapps
4) 访问阿里云主机外网ip:8888

11. 查看容器的日志
[root@Mangoo ~]# docker logs 8428ed7be348
12.更多命令可以参考每个镜像的文档
```

## 六、Spring Boot与数据访问

### 1. JDBC

导入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>
```

数据源配置

```yml
spring:
  datasource:
    username: root
    password: 123456
    url: jdbc:mysql://47.107.230.85/jdbc
    driver-class-name: com.mysql.cj.jdbc.Driver
```

效果：

​		默认使用com.zaxxer.hikari.HikariDataSource作为数据源

​		数据源相关配置在**DataSourceProperties**中



自动配置原理：

1. 参考**DataSourceConfiguration**，根据配置创建数据源，默认使用**Hikari**连接池，可以使用`spring.datasource.type`指定自定义的数据源类型

2. Spring Boot默认支持的数据源有

    ```java
    org.apache.tomcat.jdbc.pool.DataSource
    com.zaxxer.hikari.HikariDataSource
    org.apache.commons.dbcp2.BasicDataSource
    ```

3. 自定义数据源类型

    ```java
    /**
    	 * Generic DataSource configuration.
    	 */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingBean(DataSource.class)
    @ConditionalOnProperty(name = "spring.datasource.type")
    static class Generic {
    
        @Bean
        DataSource dataSource(DataSourceProperties properties) {
            // 使用DataSourceBuilder创建数据源，利用反射创建响应type的数据源，并且绑定相关属性
            return properties.initializeDataSourceBuilder().build();
        }
    
    }
    ```

4. **DataSourceInitializer**

    方法：*runScripts*

    作用：运行sql语句

    

    方法：*createSchema*

    作用：调用*runScripts*运行建表语句

    默认文件名：`classpath*:schema-all.sql`

    也可用`spring.datasource.schema`指定路径和文件名

    

    方法：*initSchema*

    默认作用：调用*runScripts*运行插入数据的sql语句

    文件名：`classpath*:data.sql`

    也可以用`spring.datasource.data`指定路径和文件名

    

    需要打开如下配置才能自动调用

    ```properties
    initialization-mode: always
    ```

5. 操作数据库：自动配置了JdbcTemplate操作数据库。

```java
@Controller
public class HelloController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @ResponseBody
    @RequestMapping("/query")
    public Map<String, Object> query() {
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from department");
        return list.get(0);
    }

}
```



### 2. 整合Druid数据源

 ```java
@Configuration
public class DruidConfig {


    //数据源属性配置
    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource druid() {
        return new DruidDataSource();
    }

    // 配置druid的监控
    // 1. 配置一个管理后台的servlet
    @Bean
    public ServletRegistrationBean<StatViewServlet> statViewServlet() {
        ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        Map<String, String> initParams = new HashMap<>();
        initParams.put("loginUsername", "admin");
        initParams.put("loginPassword", "123456");
        initParams.put("allow", "");   // 默认允许所有访问
        initParams.put("deny", "192.168.11.101");

        bean.setInitParameters(initParams);
        return bean;
    }

    // 2. 配置一个web的监控filter
    @Bean
    FilterRegistrationBean<WebStatFilter> statViewFilter() {
        FilterRegistrationBean<WebStatFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new WebStatFilter());
        Map<String, String> initParams = new HashMap<>();
        initParams.put("exclusions", "*.js, *.css, /druid/*");
        bean.setUrlPatterns(Arrays.asList("/*"));
        bean.setInitParameters(initParams);
        return bean;
    }

}
 ```













## 七、Spring Boot启动配置原理

## 八、Spring Boot自定义starters

