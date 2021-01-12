### 目录结构
#### api接口
1. 源码目录src\com\api\workflow\sgc
2. 不同的模块放在不同的文件夹下
3. 放置编译后的.class字节码到服务器对应的目录下
#### 节点前/后附加操作
1. 源码目录src\weaver\interfaces\workflow\action
2. 放置编译后的.class字节码到服务器对应的目录下
3. [流程引擎]->[路径设置]->[节点前/后附加]->添加自定义接口
### 开发环境部署
1. 配置数据库连接字符串指向测试环境
WEAVER\ecology\WEB-INF\prop\weaver.properties
2. 修改Resin的jvm参数，指定9081调试端口
WEAVER\Resin\conf\resin.properties
    ```yaml
    # Arg passed directly to the JVM
    #jvm_args  : -Xmx5550m -Xms5550m -XX:ParallelGCThreads=20 -XX:+UseConcMarkSweepGC -XX:-OmitStackTraceInFastThrow -XX:+UseParNewGC -XX:+DisableExplicitGC -javaagent:wagent.jar -Djdk.tls.ephemeralDHKeySize=2048 -Dfile.encoding=GBK
     jvm_args  : -Xdebug -Xrunjdwp:transport=dt_socket,address=9081,server=y,suspend=n -Dcom.sun.management.jmxremote -Xloggc:/var/log/gc.log -Xmx1550m -Xms1550m -XX:ParallelGCThreads=20 -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+DisableExplicitGC
     jvm_mode  : -server
    ```
3. 配置IDEA的调试参数
[OA在线开发文档](https://e-cloudstore.com/doc.html?appId=c6a9ae6e47b74d4da04c935ed51d177a&maxImgWidth=800#%E4%B8%83%E3%80%81resin%E8%BF%9C%E7%A8%8Bdebug%E9%85%8D%E7%BD%AE)
### 业务相关
1. 所有传入的参数统一使用ViewModel的形式传入，单独定义实体类
### 注意事项
1. OA**删除**的数据进入回收站30天后会彻底从数据库删除
2. OA**撤销**的数据会一直保存在数据库，仅仅是页面不显示而已
