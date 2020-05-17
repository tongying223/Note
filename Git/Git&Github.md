# Git&Github



## 一、版本控制

### 1. 版本控制工具应该具备的功能

- **协同修改**

    多人并行不悖地修改服务器端的同一个文件。

- **数据备份**

    不仅保存目录和文件的当前状态，还能够保存每一个提交过的历史状态。

- **版本管理**

    在保存每一个版本的文件信息的时候要做到不保存重复的数据，以节约存储空间，提高运行效率。SVN采用增量式管理的方式，Git采用了文件系统快照的方式。

- **权限控制**

    对团队中参与开发的人员进行权限控制。

    对团队外开发者贡献的代码进行审核——Git独有。

- **历史记录**

    查看修改人、修改时间、修改内容、日志信息。

    将本地文件恢复到某一个历史状态。

- **分支管理**

    允许开发团队在功过过程中多条生产线同时推进任务，进一步提高效率。



### 2. 版本控制简介

#### 2.1 版本控制

工程设计领域中使用版本控制管理工程蓝图的设计过程。在IT开发过程中也可以使用版本控制思想管理代码的版本迭代。



#### 2.2 版本控制工具

- 集中式版本控制工具：CVS、SVN、VSS......（单点故障问题）

![集中式版本控制工具](https://gitee.com/tongying003/MapDapot/raw/master/img/20200511112733.svg)



- 分布式版本控制工具：Git、Mercurial、Bazaar、Darcs......

![分布式版本控制工具](https://gitee.com/tongying003/MapDapot/raw/master/img/20200511114116.svg)





##  二、Git简介

git官网：https://git-scm.com



### 1. Git的优势

- 大部分操作在本地完成，不需要联网
- 完整性保存
- 尽可能添加数据而不是删除或修改数据
- 分支操作非常快捷流畅
- 与Linux命令全面兼容



### 2. Git的结构

![git结构](https://gitee.com/tongying003/MapDapot/raw/master/img/20200511171226.svg)



### 3. Git和代码托管中心

 **代码托管中心的任务：维护远程库**

（1）局域网环境下

​	<u>Gitlab服务器</u>

（2）外网环境下

​	[Github](https://github.com)

​	[码云](https://gitee.com)



### 4. 本地库与远程库

1. 团队内部合作

![本地库和远程库1](https://gitee.com/tongying003/MapDapot/raw/master/img/20200511175814.svg)



2. 跨团队合作

![git跨团队合作](https://gitee.com/tongying003/MapDapot/raw/master/img/20200511182141.svg)



## 三、Git命令行操作

### 1. 本地库初始化

```bash
git init
```

![image-20200511184714896](https://gitee.com/tongying003/MapDapot/raw/master/img/20200511184714.png)

注意：.git目录中存放的是本地库相关的子目录和文件，不能删除，也不能瞎改。



### 2. 设置签名

1. 形式

```
用户名：tom
Email：xxx@163.com
```

2. 作用：区分不同开发人员身份
3. 辨析：这里设置的签名和登录远程库（代码托管中心）的账号密码没有任何关系
4. 命令

- 项目/仓库级别：仅在当前本地库范围内有效 

```shell
git config user.name tom_pro
git config user.email tom_pro@gmail.com
# 信息保存位置./git/config
```

![image-20200511190237873](https://gitee.com/tongying003/MapDapot/raw/master/img/20200511190431.png)

- 系统用户级别：登录当前操作系统的用户范围

```shell
git config --global user.name tom_glb
git config --global user.email tom_glb@gmail.com 
# 信息保存位置～/.gitconfig
```

![image-20200511191119796](https://gitee.com/tongying003/MapDapot/raw/master/img/20200511191119.png)

- 级别优先级
    - 就近原则：项目级别优先于系统用户级别
    - 如果只有系统级别，就以系统用户级别为准
    - 二者都没有不允许

### 3. 基本操作

#### 3.1 状态查看

```bash
# 查看工作区、暂存区状态
git status
```

#### 3.2 添加

```shell
# 将工作区的“新建/修改”添加到暂存区
git add [file name]
```

####  3.3提交

```shell
# 将暂存区的内容提交到本地库
git commit -m "commit message" [file name]
```

####  3.4 查看历史记录

```shell
git log
```

![image-20200511195039872](https://gitee.com/tongying003/MapDapot/raw/master/img/20200511195039.png)

```shell
git log --pretty=oneline
```

![image-20200511195304149](https://gitee.com/tongying003/MapDapot/raw/master/img/20200511195304.png)

```shell
git log --oneline
```

![image-20200511195352429](https://gitee.com/tongying003/MapDapot/raw/master/img/20200511195352.png)

```shell
# 在git log --oneline的基础上显示到某个版本需要移动几步
git reflog
```

![image-20200511195600920](https://gitee.com/tongying003/MapDapot/raw/master/img/20200511195600.png)



#### 3.5 前进后退

- **基于索引值的操作【推荐】**

```shell
git reset --hard [索引值]
```

​		![image-20200513192735176](https://gitee.com/tongying003/MapDapot/raw/master/img/20200513192735.png)



- **使用^符号【只能后退】**

```shell
# 每个^表示后退一步
git reset --hard HEAD^^^
```



- **使用~符号【只能后退】**

```shell
# 后退3步
git reset --hard HEAD~3
```



- **`reset`三个参数对比**

```
--soft
	· 仅仅在本地库移动HEAD指针
```

![reset--soft](https://gitee.com/tongying003/MapDapot/raw/master/img/20200514211506.svg)



```
--mixed
	· 在本地库移动HEAD指针
	· 重置暂存区
```

![reset--mixed](https://gitee.com/tongying003/MapDapot/raw/master/img/20200514211609.svg)



```
--hard
	· 在本地库移动HEAD指针
	· 重置暂存区
	· 重置工作区
```

![reset--hard](https://gitee.com/tongying003/MapDapot/raw/master/img/20200514211734.svg)



#### 3.5 删除文件并找回

> 前提：删除前，文件存在时的状态已经提交到了本地库

```
git reset --hard [指针位置]
	· 删除操作已经提交到本地库：指针位置指向历史记录
	· 删除操作尚未提交到本地库：指针位置使用HEAD
```



#### 3.6 比较文件

```shell
# 将工作区中的文件和暂存区进行比较
git diff [文件名]

# 将工作区中的文件和本地库历史记录比较
git diff [本里库中历史版本] [文件名]

# 不带文件名比较多个文件
git diff
```

#### 3.7 分支管理

**1. 什么是分支**

在版本控制中，使用多条线同时推进多个任务

![branch](https://gitee.com/tongying003/MapDapot/raw/master/img/20200515203910.svg)



**2. 分支的好处**

- 同时并行推进多个功能开发，提高开发效率
- 各个分支在开发过程中，如果某一个分支开发失败，不会对其他分支有任何影响。失败的分支删除重新开始即可

 

**3. 分支操作**

- 创建分支

    ```shell
    git branch [分支名]
    ```

- 查看分支

    ```shell
    git branch -v
    ```

- 切换分支

    ```shell
    git checkout [分支名]
    ```

- 合并分支

    ```shell
    # 1.切换到受修改的分支
    git checkout [接受合并的分支名]
    
    # 2. 执行merge命令
    git merge [有新内容的分支名]
    ```

- 解决合并分支后产生的冲突

    （1）冲突的表现

    ![image-20200515215854301](https://gitee.com/tongying003/MapDapot/raw/master/img/20200515215854.png)

    （2）冲突的解决

    ```
    1.编辑文件，删除特殊符号
    2.把文件修改到满意的程度
    3.git add [文件名]
    4.git commit -m "日志信息"
    	· 注意：此时commit一定不能带具体文件名
    ```

### 4. Git的原理

#### 4.1 哈希

哈希是一个系列的加密算法，各个不同的哈希算法虽然加密强度不同，但是有以下几个共同点：

1. 不管输入数据的数量有多大，输出同一个哈希算法，得到的加密结果长度固定
2. 哈希算法确定，输入数据确定，输出数据能够保证不变
3. 哈希算法确定，输入数据有变化，输出数据一定有变化，而且通常变化很大
4. 哈希算法不可逆

Git底层采用的是SHA-1算法

哈希算法可以被用来验证文件。原理如下图所示：

![哈希文件校验](https://gitee.com/tongying003/MapDapot/raw/master/img/20200515223135.svg)

Git就是靠这种机制从根本上保证数据完整性的



#### 4.2 Git保存版本的机制

**1. 集中式版本控制工具的文件管理机制**

以文件变更列表的方式存储信息，这类系统将它们保存的信息看作是一组基本文件和每个文件随时间逐步累积的差异。

![集中式版本控制原理](https://gitee.com/tongying003/MapDapot/raw/master/img/20200515230307.svg)



**2. Git的文件管理机制**

Git把数据看作是小型文件系统的一组快照。每次提交更新时Git都会对当前的全部文件制作一个快照并保存这个快照的索引。为了高效，如果文件没有修改，Git不再重新存储该文件，而是只保留一个链接指向之前存储的文件。所以Git的工作方式可以称之为快照流。

![Git保存文件的原理](https://gitee.com/tongying003/MapDapot/raw/master/img/20200516130343.svg)



**3. Git文件管理机制细节**

- Git的“提交对象”

![Git存储对象](https://gitee.com/tongying003/MapDapot/raw/master/img/20200516150644.svg)

- 提交对象及其父对象形成的链条

![提交对象及其父对象](https://gitee.com/tongying003/MapDapot/raw/master/img/20200516151918.svg)



### 5. Git分支管理机制

- 分支的创建

![分支的创建](https://gitee.com/tongying003/MapDapot/raw/master/img/20200516191907.svg)



- 分支的切换

![分支的切换](https://gitee.com/tongying003/MapDapot/raw/master/img/20200516192120.svg)



- 新分支有提交记录

![新分支提交](https://gitee.com/tongying003/MapDapot/raw/master/img/20200516192412.svg)

- 切换回master并进行了提交

![master分支进行了提交](https://gitee.com/tongying003/MapDapot/raw/master/img/20200516192904.svg)



- 分支合并

![merge之后](https://gitee.com/tongying003/MapDapot/raw/master/img/20200516193506.svg)



### 6. Github

#### 6.1 初始化本地库

```shell
git init
```



#### 6.2 在本地库创建远程库地址别名

```shell
# 为远程仓库起一个别名origin
git remote add origin https://github.com/tom/myProject.git
```

![image-20200516201611018](https://gitee.com/tongying003/MapDapot/raw/master/img/20200516201611.png)



#### 6.3 推送

```shell
# origin是远程分支别名，指定推送到master分支
git push origin master
```



#### 6.4 克隆

```shell
git clone https://github.com/tom/myProject.git
```

- 完整把远程库下载到本地
- 创建origin远程地址别名
- 初始化本地库



#### 6.5 远程库修改的拉取

- `pull = fetch + merge`

- fetch

    ```shell
    git fetch [远程库地址别名] [远程分支名]
    # git fetch origin master
    ```

    只是将远程库的内容下载下来，放在origin/master分支下，查看下载内容需先切换分支`git checkout origin/master`

- merge

    ```shell
    git merge [远程库地址别名]/[远程分支名]
    # git merge origin/master
    ```

- pull

    ```shell
    git pull [远程库地址别名] [远程分支名]
    # git pull origin master
    ```



#### 6.6 协同开发时冲突的解决

1. 如果不是基于Github的最新版所作的修改，不能推送，必须先拉取`git pull`
2. 拉取下来后如果进入冲突状态，则按照“分支解决冲突”操作解决即可





#### 6.7 SSH免密登录

1. 回到家目录

    ```shell
    cd ~
    ```

2. 生成ssh密钥

    ```shell
    ssh-keygen -t rsa -C tongying003@163.com	# 一路回车
    ```

3. 拷贝~/.ssh/id_rsa.pub的内容，并添加到Github

![image-20200516212102972](https://gitee.com/tongying003/MapDapot/raw/master/img/20200516212103.png)



4. 使用ssh链接新建远程仓库地址

    ```shell
    git remote add origin_ssh git@github.com:ty003/Note.git
    ```

5. 使用ssh推送

    ```shell
    git push origin_ssh master
    ```





### 四、git工作流

#### 1.1 概述

在开发过程中使用Git的方式

#### 1.2 分类

**1. 集中式工作流**

像SVN一样，集中式工作流以中央仓库做为项目所有修改的单点实体。所有修改都提交到Master这个分支上

这种方式与SVN的主要区别就是开发人员有本地库。Git很多特性并没有用到

![集中式](https://gitee.com/tongying003/MapDapot/raw/master/img/20200517003023.svg)



**2. GitFlow工作流**

Gitflow工作流通过为功能开发、发布准备和维护设立了独立的分支，让发布迭代过程更流畅。严格的分支模型也为大型项目提供了一些非常必要的结构。

![gitflow](https://gitee.com/tongying003/MapDapot/raw/master/img/20200517004216.svg)



**3. Forking工作流**

Forking工作流式在GitFlow基础上，充分利用了Git的Fork和pull request的功能以达到代码审核的目的。更适合安全可靠地管理大团队的开发者，而且能接受不信任贡献者的提交。

![Forking](https://gitee.com/tongying003/MapDapot/raw/master/img/20200517010125.svg)



#### 1.3 GitFlow工作流详解

**1. 分支种类**

- 主干分支 master

    主要负责管理正在运行的生产环境代码。永远保持与正在运行的生产环境完全一致。

- 开发分支 develop

    主要负责管理正在开发过程中的代码。一般情况下应该是最新的代码。

- bug修复分支 hotfix

    主要负责管理生产环境下出现的紧急修复的代码。从主干分支分出，修理完毕并测试上线后，并回主干分支。并回后，视情况可以删除该分支。

- 准生产分支（预发布分支） release

    较大版本上线前，会从开发分支中分出准生产分支，进行最后阶段的集成测试。该版本上线后，会合并到主干分支。生产环境运行一阶段比较稳定后可以视情况删除。

- 功能分支 feature

    为了不影响短周期的开发工作，一般把中长期开发模块，会从开发分支独立出来。开发完毕后会合并到开发分支。



**2. GitFlow工作流举例**

![GitFlow实例](https://gitee.com/tongying003/MapDapot/raw/master/img/20200517022849.svg)



