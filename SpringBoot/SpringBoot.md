# Spring Boot

# 一、Spring Boot入门

### 1. Spring Boot简介

简化Spring应用开发的框架

整个Spring技术栈的一个大整合

J2EE开发的一站式解决方案

### 2. Spring Boot Hello World

功能：

浏览器发送hello请求，服务器接受请求并相应“Hello World!”字符串。

#### 1. 创建maven工程

#### 2. 导入Spring Boot相关依赖

```xml
<!--继承是 Maven 中很强大的一种功能，继承可以使得子POM可以获得 parent 中的各项配置，
    可以对子pom进行统一的配置和依赖管理。父POM中的大多数元素都能被子POM继承。-->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.1.RELEASE</version>
    <relativePath/> <!-- lookup parent from repository -->
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

#### 3. 编写一个主程序，启动Spring Boot应用

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 标注一个主程序类，说明这是一个springboot应用
 */
@SpringBootApplication
public class HelloWorldMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(HelloWorldMainApplication.class, args);
    }
}
```

#### 4. 编写Controller、Service

```java
@Controller
public class HelloController {

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "hello World!";
    }
}
```

#### 5. 启动应用

访问http://localhost:8080/hello

![image-20200305151550745](/Users/tongying/Library/Application Support/typora-user-images/image-20200305151550745.png)

#### 6. 简化部署

```xml
<build>
    <!--这个插件可以将应用打包成一个可执行的jar包-->
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

将应用打包成jar包，使用如下命令运行：

```
java -jar xxx.jar
```

### 3. Hello World细节探究

#### 1. POM文件

##### 1. 父项目

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.1.RELEASE</version>
    <relativePath/> <!-- lookup parent from repository -->
</parent>

<!--它的父项目是-->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-dependencies</artifactId>
    <version>2.1.1.RELEASE</version>
    <relativePath>../../spring-boot-dependencies</relativePath>
</parent>
<!--它来真正管理Spring Boot应用里的所有依赖版本-->
```

`spring-boot-dependencies`：Spring Boot的版本仲裁中心，以后导入依赖默认不需要写版本号。没有在dependency里面管理的依赖自然需要写版本号。

##### 2. 启动器

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

`spring-boot-starter`：spring-boot场景启动器。

`spring-boot-starter-web`：帮助导入了web模块正常运行所需依赖的组件，包扩`spring-boot-starter`、`spring-boot-tomcat`、`spring-web`、`spring-webmvc`等。

Spring Boot将所有的功能场景都抽取出来，做成一个个starter（启动器），只需要在项目中引入这些starter，相关场景的所有依赖都会导入进来，要用什么功能就导入什么场景的启动器。

#### 2. 主程序类

```java
@SpringBootApplication
public class HelloWorldMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(HelloWorldMainApplication.class, args);
    }
}
```

**`@SpringBootApplication`**：Spring Boot标注在某个类上说明这个类是Spring Boot的主配置类，Spring Boot就应该运行这个类的`main`方法来启动Spring Boot应用。



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

**`@SpringBootConfigurarion`**：标注在某个类上，表示这是一个Spring Boot的配置类。

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
public @interface SpringBootConfiguration {
```

​		**`@Configuration`**：标注一个配置类。配置类也是容器中的一个组件。

**`@EnableAutoConfiguration`**：开启自动配置功能。

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import({AutoConfigurationImportSelector.class})
public @interface EnableAutoConfiguration {
```

​		**`@AutoConfigurarionPackage`**：自动配置包

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({Registrar.class})
public @interface AutoConfigurationPackage {
}
```

​				**`@import({Registrar.class})`**：给容器中导入一个组件，导入的组件由`{Registrar.class}`将==主配置类（@SpringBootApplication标注的类）所在包及下面所有子包==里面的所有组件扫描到Spring容器中。

​		**`@Import({AutoConfigurationImportSelector.class})`**：给容器导入组件。

​				**`AutoConfigurationImportSelector`**：将所有需要导入的组件以全类名的方式返回。会给容器中导入非常多的自动配置类（xxxAutoConfigurarion），就是给这个容器导入这个场景需要的所有组件，并配置好这些组件。![image-20200308022350902](https://tva1.sinaimg.cn/large/007S8ZIlgy1gehlc5xielj31bu0lijyd.jpg)

有了自动配置类，免去了我们手动编写配置注入功能组件等的工作。Spring Boot在启动的时候从类路径下的META-INF/spring.factories中获取EnableAutoConfiguration制定的值，将这些值作为自动配置类导入到容器中，帮我们进行自动配置工作，



J2EE的整体整合解决方案和自动配置都在org/springframework/boot/spring-boot-autoconfigure/2.1.1.RELEASE/spring-boot-autoconfigure-2.1.1.RELEASE.jar

### 4. 使用Spring Initializer快速创建Spring Boot项目

```java
@ResponseBody
@Controller
```

与如下注解相同

```java
@RestController
```

## 二、SpringBoot配置

## 三、SpringBoot与日志

## 四、SpringBoot与Web开发

## 五、SpringBoot与Docker

## 六、SpringBoot与数据访问

## 七、SpringBoot启动配置原理

## 八、SpringBoot自定义starters

## 九、SpringBoot与缓存

## 十、SpringBoot与消息

## 十一、SpringBoot与检索

## 十二、SpringBoot与任务

## 十三、Springboot与安全

## 十四、SpringBoot与分布式

## 十五、SpringBoot与开发热部署

## 十六、SpringBoot与监控管理