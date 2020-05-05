## Redis入门

### 1. NoSQL概述

> 1. 什么是NoSQL

NoSQL = Not Only SQL

非关系型数据库

> 2. 为什么需要NoSQL

High oerformance—— 高并发读写

Huge Storage——海量数据的高效存储和访问

High Scalability && High Availability——高扩展性和高可用性

> 3. NoSQL主流产品

Redis、MongoDB

> 4. NoSQL数据库的四大分类

- 键值（Key-Value）存储（Redis）
- 列存储
- 文档数据库（MongoDB）
- 图形数据库

> 5. 四类NoSQL数据库比较

| 分类                | 相关产品                                            | 典型应用                                    | 数据模型                             | 优点                                         | 缺点                                                     |
| :------------------ | :-------------------------------------------------- | :------------------------------------------ | :----------------------------------- | :------------------------------------------- | :------------------------------------------------------- |
| 键值（Key-Value）   | Tokoy Cabinet/Tyrant、Redis、Voldemort、Berkeley DB | 内容缓存、主要用于处理大量数据的高访问负载  | 一系列键值对                         | 快速查询                                     | 存储的数据缺少结构化                                     |
| 列存储数据库        | Cassandra、HBase、Riak                              | 分布式的文件系统                            | 以列簇式存储，将同以列数据存储在一起 | 查找速度快，可扩展性强，更容易进行分布式扩展 | 功能相对局限                                             |
| 文档型数据库        | CouchDB、MongoDB                                    | Web应用（与Key-Value类似，Value是结构化的） | 一系列键值对                         | 数据结构要求不严格                           | 查询性能不高，而且缺乏统一的查询语法                     |
| 图像（Graph）数据库 | Neo4J、InfoGrid、Infinite、Graph                    | 社交网络、推荐系统等、专注于构建关系图谱    | 图结构                               | 利用图结构相关算法                           | 需要对整个图做计算才能得出结果，不容易做分布式的集群方案 |

> 6. NoSQL的特点

- 易扩展
- 大数据量
- 灵活的数据模型
- 高可用



### 2. Redis概述

- 高性能键值数据库，支持的键值数据类型

  （1）字符串类型

  （2）列表类型

  （3）有序集合类型

  （4）散列类型

  （5）集合类型

- Mysql的应用场景

  （1）缓存

  （2）任务队列

  （3）应用排行榜

  （4）网站访问统计

  （5）数据过期处理

  （6）分布式集群架构中的Session分离

### 3. Redis安装和使用

- 将redis设置为后台启动

```shell
sudo vim /developer/redis/redis.conf
```

```shell
[136]daemonize no ==> daemon yes
```

- 启动

```shell
./developer/redis/bin/redis-server ./developer/redis/redis.conf
```

- 终止

```java
./developer/redis/bin/redis-cli shutdown
```

- 本地使用

```shell
./bin/redis-cli

# 添加数据
set name imooc
# 获取数据
get name
# 查询所有的key
keys *
```

### 4. Jedis

- Jedis是Redis官方首选的Java客户端开发包

- jar包

  ```
  commons-pool2-2.6.0.jar
  jedis-3.0.1.jar
  ```

1. 单实例的测试

```java
@Test
// 单实例的测试
public void singletonTest(){
    // 1. 设置ip地址和端口
    Jedis jedis = new Jedis("192.168.116.128", 6379);
    // 2. 保存数据
    jedis.set("name", "imooc");
    // 3. 获取数据
    String value = jedis.get("name");
    System.out.println(value);
    // 4. 释放资源
    jedis.close();
}
```

```java
# 运行结果：
imooc
```



> 报错：redis.clients.jedis.exceptions.JedisConnectionException: Failed connecting to host

- 注释掉redis.conf里的 

  ```
  bind 127.2.2.1
  ```

> 报错：连接超时

- 打开防火墙配置，开放6379端口并重启防火墙

  ```
  -A INPUT -p tcp -m state --state NEW -m tcp --dport 6379  -j ACCEPT
  ```

> 报错：DENIED Redis is running in protected mode because protected mode is enabled, no bind address was specified...

- 修改redis.conf

  ```
  protected-mode yes  ==> protected-mode = no
  ```

2. 连接池方式的使用

```java
@Test
// 连接池方式的使用
public void connectionPoolTest(){
    // 获得连接池的配置对象
    JedisPoolConfig poolConfig = new JedisPoolConfig();
    // 设置最大连接数
    poolConfig.setMaxTotal(30);
    // 设置最大空连接数
    poolConfig.setMaxIdle(20);
    // 获得连接池
    JedisPool jedisPool = new JedisPool(poolConfig, "192.168.116.128", 6379);
    Jedis jedis = null;
    try{
        // 通过连接池获得连接
        jedis = jedisPool.getResource();
        // 设置数据
        jedis.set("name", "Tom");
        // 获取数据
        String value = jedis.get("name");
        System.out.println(value);
    }catch (Exception e){
        e.printStackTrace();
    }finally {
        // 释放资源
        if (jedis != null) {
            jedis.close();
        }
        if(jedisPool != null) {
            jedisPool.close();
        }
    }
}
```



### 5. Redis数据结构

- Redis数据结构
  - 字符串（String）
  - 哈希（hash）
  - 字符串列表（list）
  - 字符串集合（set）
  - 有序字符串集合（sorted set）
- key定义的注意点
  - 不要过长
  - 不要过短
  - 统一的命名规范

#### 5.1 存储String

- 二进制安全的，存入和获取的数据相同
- Value最多可以容纳的数据长度是512M



存储字符串常用命令

```mysql
set key value			# 赋值
```

```mysql
get key					# 取值
```

```mysql
getset key value		# 先获取再设置值
```

```mysql
del key					# 删除操作
```

例子

```shell
set company imooc		# OK
get company				# "imooc"
getset company baidu	# "imooc"
get company				# "baidu"
del company				# (integer) 1
get company				# (nil)
```

```mysql
incr key		# 将指定key的value递增1，若key的value不存在，将value置为0再+1，若value不能转成数值型，抛出异常
```

```mysql
decr key		# 将指定key的value递减1，若key的value不存在，将value置为0再-1，若value不能转成数值型，抛出异常
```

例子

```shell
incr num1		# (Integer) 1
get num1		# 1
decr num2		# (Integer) -1
get num2		# -1
```

```mysql
incrby key value		# 将key的value加上value，如果key不存在，将其设为0，再加上value
```

```mysql
decrby key value		# 将key的value减去value，如果key不存在，将其设为0，再减去value
```

```mysql
append key value	   # 将key的值与value进行拼接，若key不存在，则创建key的值为""，再拼接
```

例子

```shell
# num1 = 1, num2 = -1
increby num1		# (Integer) 6
get num1			# "6"
decrby num2			# (Integer) -6
get num2			# "-6"
append num1 123		# (Integer) 4  (长度)
get num2			# "6123"
append num3 123		# (Integer) 3
get num3			# "123"

```

#### 5.2 存储Hash

- String key和String Value的map容器
- 每一个Hash可以存储4294967295个键值对



存储Hash常用命令

```shell
# key 集合的键值 
# field 数据的键值
hset key field value					# 赋值
hmset key field1 value1 field2 value2...	# 存储多个键值对
hget key field							# 获取一个属性
hmget key field1 field2					# 获取多个属性
hgetall key								# 获取所有属性和属性值
hdel key field1 field2					# 删除多个属性，若删除不存在的字段
del key									# 删除整个集合
hincrby key field value					# 将集合中attr的属性的值加上value
hexists key field						# 判断集合中属性是否存在
hlen key								# 获得集合中属性个数
hkeys key								# 获取集合中所以的key
hvals key								# 获取集合中所有的value
```





```shell
hset myhash username Jack				 # (integer) 1
hset myhash age 18						 # (integer) 1
hmset myhash2 username Tom age 15		  # OK

hmget myhash2 username age
										# 1) "Tom"
										# 2) "15"
hgetall myhash
										# 1) "username"
										# 2) "Jack"
										# 3) "age"
										# 4) "18"
										
hdel myhash2 username age				  # (integer) 2
hmset myhash2 usernmae Yumy age 12		   # OK
del myhash2								 # (integer) 1
hincrby myhash age 5					  # (integer) 23 (18 + 5 = 23)
hlen myhash								 # 2	

hkeys myhash
										# 1) "username"
										# 2) "age"
hvals myhash
										# 1) "Jack"
										# 2) "23"
```

#### 5.3 存储List

- ArrayList使用数组方式
- LinkedList使用双向链表方式
- 双向链表中增加数据
- 双向链表中删除数据

存储List常用命令

```mysql
1. lpush key vlaue [value...]				# 左端添加
2. rpush key value [value...]				# 右端添加
3. lrange key start stop					# 查看List，-1表示尾部元素，0表示首部元素
4. lpop key								  	# 左边弹出  
5. rpop key								  	# 右边弹出
6. llen key								    #  返回list长度
7. lpushx key value [value...]				#  仅当key存在时左端添加
8. rpushx key value [value...]				# 仅当key存在时右端插入
9. lrem key count value			 			# 从左往右删除count个value，若count < 0，表示从右往左删除，若count为0，表示删除所有的10. value
11. set  key index value			  		# 修改index处的值为value
12. linsert key BEFORE | AFTER pivot value	# 在pivot前 | 后插入value
13. rpoplpush source destination 			# 将source的右边弹出插入到destination的左边
```



```shell
lpush mylist a b c					# mylist[c b a]
rpush mylist 1 2 3					# mylist[c b a 1 2 3]
lrange mylist 0 5					# mylist[c b a 1 2 3]
lrange mylist 0 -1					# mylist[c b a 1 2 3]
lpop mylist							# mylist[b a 1 2 3]
rpop mylist							# mylist[b a 1 2]
lpushx mylist x y z					# mylist[z y x b a 1 2]
rpushx mylist u v w					# mylist[z y x b a 1 2 u v w]
lpush mylist2 3 2 1 3 2 1 3 2 1		# mylist2[1 2 3 1 2 3 1 2 3]
lrem mylist2 2 3					# mylist2[1 2 1 2 1 2 3]
lrem mylist2 -2 1					# mylist2[1 2 2 2 3]
lrem mylist2 0 2					# mylist2[1 3]
lset mylist2 1 xxx					# mylist2[1 xxx]
linsert mylist2 BEFORE xxx 2		# mylist2[1 2 xxx]
linsert mylist2 AFTER xxx yyy		# mylsit2[1 2 xxx yyy]
lpush mylist3 3 2 1					# mylist3[1 2 3]
lpush mylist4 c b a					# mylist4[a b c]
rpoplpush mylist3 mylist4			# mylist3[1 2] mylist4[3 a b c]
```

`rpoplpush`的使用场景——消息队列

消费者从消息队列中取出元素并将之插入到备份的消息队列中，防止消息丢失，消费者完成正常的逻辑处理后弹出从备份消息队列删除，**提供一个守护线程，当主消息队列中的消息过期就将其放回到主消息队列中**。

#### 5.4 存储Set

- Set不允许出现重复的元素
- 可以完成一系列集合元素
- 可以包含的最大元素数量是4294967295

存储Set常用命令

```
sadd key member [member ...]				# 添加元素
```

```
srem key member [member ...]				# 删除元素
```

```
smembers key								# 查看集合中的所有元素
```

```
sismember key member						# 判断member在不在集合中
```

```
sdiff key [key ...]							# 进行差集运算
```

```
sinter key [key ...]						# 进行交集运算
```

```
sunion	key [key ...]						# 进行并集运算
```

```
scard key									# 获得集合中元素的个数
```

```
srandmember key [count]						# 随机获集合中的一个[或count]个元素
```

```
sdiffstore destination key [key ...]		# 进行差集运算并存储到destination
```

```
sinterstore destination key [key ...]		# 进行交集运算并存储到destination
```

```
sunionstore destination key [key ...]		# 进行并集云端并存储到destiantion
```



使用例子

```mysql
sadd myset1 a b c 1 2 3								# myset1[a b c 1 2 3]	
sadd myset2 a b c 4 5 6								# myset2[a b c 4 5 6]
sdiff myset1 myset2									# "1" "2" "3"
sinter myset1 myset2								# "a" "b" "c"
sunion myset1 myset2								# "a" "b" "c" "1" "2" "3" "4" "5" "6"
sdiffstore myset3 myset1 myset2						# myset3[1 2 3]
sinterstore myset4 myset1 myset2					# myset4[a b c]
sunionstore myset5 myset1 myset2					# myset4[a b c 1 2 3 4 5 6]
```

存储Set使用场景

- 跟踪一些唯一性数据
- 用于维护对象之间的关联关系

#### 5.5 存储Sorted-Set

- Sorted-Set的成员在集合中的位置是有序的

存储Sorted-Set常用命令

```
zadd key score member								# 添加元素
```

```
zscore member										# 获取某个成员的分数
```

```
zcard key											# 获取集合元素的数量
```

```
zrem key member [member ...]						# 删除集合中的成员
```

```
zrange key start stop [WITHSCORES] 					# 范围查询集合中的成员[带分数显示]
```

```
zrevrange key start stop [WITHSCORES]				# 倒序查询集合中的元素[带分数显示]
```

```
zremrangebyrank key start stop						# 按排名进行范围删除
```

```
zremrangebyscore key min max						# 按分数范围进行删除
```

```
zrangebyscore key min max [WITHSCORES]	[LIMIT offset count]	# 按照分数进行范围查询 [带分数显示] [显示的条数]
```

```
zincrby key increment member						# 将成员的分数值加上increment
```

```
zcount key min max									# 查询分数值在某个范围的成员数量
```

使用例子

```mysql
zadd mysort 70 Anna 80 Bella 90 Chris				# mysort[70-Anna 80-Bella 90-Chris]
zscore mysort Anna									# "70"
zcard mysort										# (integer) 3
zrange mysort 0 -1 WITHSCORES						# 1) "Anna" 2) "70" 3) "Bella" 4) "80" 5) "Chris" 6) "90"
zrevrangemysort mysort 0 -1							# 1) "Chris"2) "90" 3) "Bella" 4) "80" 5) "Anna"  6) "70"
zrangebyscore mysort 80 90 							# "Bella" "Chris"
zincrby mysort 5 Anna								# "75"
zcount mysort 70 90									# (integer) 3
```

Sorted-Set的使用场景

- 大型游戏的玩家积分排行榜。当玩家积分发生变化的时候，可以执行zadd命令更新玩家积分，再通过zrange进行排名情况查询
- 用于构建索引数据

### 6. Reids的通用命令

```
keys *												# 查询所有的key，仅限String
```

```
keys my?											# 查询my开头的key
```

```
del key [key ...]									# 删除某些key
```

```
exists key [key ...]								# 判断Key是存在,返回的是存在的个数
```

```
rename key newkey									# 重命名key
```

```
expire key seconds									# 设置过期时间
```

```
ttl key 											# 查看key所剩超时时间
```

```
type key											# 查看key所对应的数据类型
```

### 7. Redis的特性

#### 7.1 Redis多数据库

每个Redis实例最多可提供16个数据库，下标0-15，可通过`select index`指定连接的数据库

```
select index										# 选择数据库
```

```
move key db											# 将key移动到db
```

#### 7.2 Redis事务

Redis事务中所有命令都将串行化顺序执行，在事务执行期间，Redis不会再对其他客户端提供服务，保证所有命令顺序执行。

```
multi												# 开启事务
```

```
exec												# 提交事务
```

```
discard												# 回滚
```

muti开启一个事务后，将多个命令入队到事务中，最后由exec命令触发事务，一并执行事务中的所有命令。但批量指令并非原子化操作，中将某条指令的失败不会导致前面已做指令的回滚，也不会造成后续的指令不做。

```
watch key [key ...]									# 为Redis事务提供CAS行为
```

被watch的键会被监视，并会发觉这些键是否被修改过了，如果有至少一个被监视的键在exec执行前被修改了，那么整个事务将会取消

```mysql
set str 1							# 添加字符串str = "1"
watch str							# 监视str
set str 2							# 修改str = "2"
multi								# 开启事务
set str 3							# 修改str = "3"
exec								# (nil)
get str								# "2"
```

### 8. Redis的持久化

Redis效率很高的因素是所有的数据都存储在内存中，为了数据不丢失，内存中的数据需要同步到硬盘上，即进行持久化

- RDB
- AOF

#### 8.1 RDB

（默认）

在一定的时间间隔内将内存快照写入磁盘

- 整个Redis数据库只包含一个文件，便于文件备份
- 便于灾难恢复，可以轻松的将这个文件压缩后转移到其他存储介质
- 性能最大化，由子进程完成持久化操纵。
- 相比AOF，RDB的启动方式更高

缺点

- 无法保证高可用性，还没来及持久化服务器可能就宕机了
- 当数据集很大时，可能需要整个服务器暂停一段时间

配置、

```
vim redis.conf
```



```properties
...
# Dump DB per 900s if at least one key changed
save 900 1
# Dump DB per 300s if at least 10 keys changed
save 300 10
# Dump DB per 60s if ad least 10000 keys changed
save 60 10000
 
# The filename where to dump the DB
dbfilename dump.rdb

# Note that you must specify a directory here, not a file name.
dir /developer/redis/etc/

```

#### 8.2 AOF

以日志形式记录服务器所处理的所有操作，在服务器启动时，Redis读取日志文件重新构建数据库

- 更高的数据安全性，每秒同步、每修改同步和不同步
- 对日志的写入是append的方式，即使出现宕机有而不会影响以已存在的数据，即使写入一半系统崩溃，在下一次系统启动前也可以借助redis-check-aof解决数据一致性的问题
- 如果日志过大，Redis可以自动启动重写机制
- 也可以根据日志文件进行数据的重建

缺点

- 对于相同数量的数据集，AOF文件要比RDB文件大
- AOF在运行效率上要低于RDB

配置

```
vim redis.conf
```





```properties
#...
# 开启AOF方式同步
appendonly yes
# The name of the append only file (default: "appendonly.aof")
appendfilename "appendonly.aof"
#...
# 每修改同步
appendfsync always
# 每秒同步
#appendfsync everysec
# 不同步
# appendfsync no
```

测试

```
set name Anna
set age 18
# 清空数据库
flushall
```

找到appendonly.aof，删除最后一句`flushall`，重启redis服务器

```
keys *
```

> "age"
>
> "name"

