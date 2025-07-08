Tomcat和Jetty就是一个Servlet容器。为了方便使用，也具有HTTP服务器的功能。因此，Tomcat或Jetty就是一个“HTTP服务器 + Servlet容器”，我们也叫他们Web容器。

微服务需要一个轻量级的web容器，由应用本身来启动一个嵌入式的Web容器，降低应用部署的复杂度。

Tomcat和Jetty都支持 Servlet 4.0 规范。

## 1. HTTP协议

HTTP 协议和其他应用层协议一样，本质上是一种通信格式。HTTP 是通信的方式，HTML 才是通信的目的。

由于 HTTP 是无状态的协议，为了识别请求是哪个用户发过来的，出现了 Cookie 和 Session 技术。

Cookie 本质上就是一份存储在用户本地的文件，里面包含了每次请求中都需要传递的信息；

Session 可以理解为服务器端开辟的存储空间，里面保存的信息用于保持状态。

### 1.1 Session 创建与存储

作为 Web 容器，Tomcat 负责创建和管理 Session，并提供了多种持久化方案来存储 Session。

具体来说，Session 是 Web 应用程序在调用 HttpServletRequest 的 getSession 方法时创建的。

Tomcat 的 Session 管理器提供了多种持久化方案来存储 Session，通常会采用高性能的存储方式，比如 Redis，并且通过集群部署的方式，防止单点故障，从而提升高可用。同时，Session 有过期时间，因此 Tomcat 会开启后台线程定期的轮询，如果 Session 过期了就将 Session 失效。

## 2. Servlet规范和Servlet容器

### 2.1 Servlet 接口

HTTP 协议中的请求和响应就是对应了 HttpServletRequest 和 HttpServletResponse 这两个类。

可以通过 HttpServletRequest 来获取所有请求相关的信息，包括请求路径、Cookie、HTTP 头、请求参数等。还可以创建和获取Session。

而 HttpServletResponse 是用来封装 HTTP 响应的。

比如 Spring MVC 中的 DispatcherServlet，就是在 init 方法里创建了自己的 Spring 容器。

类 ServletConfig 的作用就是封装 Servlet 的初始化参数。可以在web.xml给 Servlet 配置参数，并在程序里通过 getServletConfig 方法拿到这些参数。

抽象类：实现接口和封装通用逻辑；Servlet 规范提供了 GenericServlet 抽象类，通过扩展它来实现 Servlet。虽然 Servlet 规范并不在乎通信协议是什么，但是大多数的 Servlet 都是在 HTTP 环境中处理的，因此 Servet 规范还提供了 HttpServlet 来继承 GenericServlet，并且加入了 HTTP 特性。这样我们通过继承 HttpServlet 类来实现自己的 Servlet，只需要重写两个方法：doGet 和 doPost。

### 2.2 Servlet 容器

HTTP 服务器不直接调用 Servlet，而是把请求交给 Servlet 容器。

### 2.3 Web 应用

通常我们以Web应用的方式部署Servlet，而根据Servlet规范，Web应用程序由一定的目录结构：

```
| -  MyWebApp
      | -  WEB-INF/web.xml        -- 配置文件，用来配置Servlet等
      | -  WEB-INF/lib/           -- 存放Web应用所需各种JAR包
      | -  WEB-INF/classes/       -- 存放你的应用类，比如Servlet类
      | -  META-INF/              -- 目录存放工程的一些信息
```

Servlet 容器通过读取配置文件找到并加载 Servlet。

Servlet 规范里定义了 ServletContext 这个接口来对应一个 Web 应用。

Web 应用部署好后，Servlet 容器在启动时会加载 Web 应用，并为每个 Web 应用创建唯一的 ServletContext 对象。 ServletContext 是一个全局对象，一个 Web 应用可能有多个 Servlet，这些 Servlet 可以通过全局的 ServletContext 来共享数据，这些数据包括 Web 应用的初始化参数、Web 应用目录下的文件资源等。由于 ServletContext 持有所有 Servlet 实例，还可以通过它来实现 Servlet 请求的转发。

### 2.4 扩展机制

Servlet 规范提供了两种扩展机制：Filter 和 Listener。

Web 应用部署完成后，Servlet 容器需要实例化 Filter 并把 Filter 链接成一个 FilterChain。当请求进来时，获取第一个 Filter 并调用 doFilter 方法，doFilter 方法负责调用这个 FilterChain 中的下一个 Filter。

Listener 是一种扩展机制。Servlet 容器提供了一些默认的监听器来监听这些事件，当事件发生时，Servlet 容器会负责调用监听器的方法。也可以定义自己的监听器去监听你感兴趣的事件，将监听器配置在web.xml中。

Spring 就实现了自己的监听器，来监听 ServletContext 的启动事件，目的是当 Servlet 容器启动时，创建并初始化全局的 Spring 容器。

## 3. Tomcat 目录

<img src="/Users/ellen/Library/Application Support/typora-user-images/image-20250708164248517.png" alt="image-20250708164248517" style="zoom:60%;" align="left" />

/bin：存放 Windows 或 Linux 平台上启动和关闭 Tomcat 的脚本文件。

/conf：存放 Tomcat 的各种全局配置文件，其中最重要的是server.xml。

/lib：存放 Tomcat 以及所有 Web 应用都可以访问的 JAR 文件。

/logs：存放 Tomcat 执行时产生的日志文件。

/work：存放 JSP 编译后产生的 Class 文件。

/webapps：Tomcat 的 Web 应用目录，默认情况下把 Web 应用放在这个目录下。

### 3.1 查看 Tomcat 日志

| File name                            | Comment                                                      |
| ------------------------------------ | ------------------------------------------------------------ |
| catalina.***.log                     | 记录 Tomcat 启动过程的信息，在这个文件可以看到启动的 JVM 参数以及操作系统等日志信息。 |
| catalina.out                         | Tomcat 的标准输出（stdout）和标准错误（stderr）； 应用程序打印出的日志信息 |
| localhost.**.log                     | 主要记录 Web 应用在初始化过程中遇到的未处理的异常，会被 Tomcat 捕获而输出这个日志文件。 |
| localhost_access_log.**.txt          | 存放访问 Tomcat 的请求日志，包括 IP 地址以及请求的路径、时间、请求协议以及状态码等信息。 |
| manager.***.log/host-manager.***.log | 存放 Tomcat 自带的 Manager 项目的日志信息。                  |

### 3.2 用注解的方式部署 Servlet

给 Servlet 类加上 @WebServlet 注解，表明：

- 这个 Java 类是一个 Servlet
- 这个 Servlet 对应的 URL 路径是 myAnnotationServlet

```java
@WebServlet("/myAnnotationServlet")
```

注意： **删除原来的 web.xml**
