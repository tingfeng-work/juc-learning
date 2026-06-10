# Thread Basic 与线程安全问题初探

## 学习目标

理解：

- Thread 与 run() 的关系
- start() 与 run() 的区别
- Thread 生命周期中的限制
- 什么是竞态条件（Race Condition）
- 为什么 count++ 线程不安全
- join() 的作用

------

# 一、Thread 的基本使用

## 创建线程

通过继承 Thread：

```java
class MyThread extends Thread {
    @Override
    public void run() {
        // 线程执行任务
    }
}
```

启动线程：

```java
MyThread thread = new MyThread();
thread.start();
```

------

## run() 方法是什么

run() 中定义线程要执行的任务。

例如：

```java
@Override
public void run() {
    for(int i=0;i<10;i++){
        ...
    }
}
```

线程启动后最终执行的就是 run() 方法中的内容。

------

# 二、start() 与 run() 的区别

## 实验

### 情况1

```java
thread.start();
```

现象：

- 创建新的执行线程
- 由 JVM 和操作系统调度
- 输出线程名为：

```text
Thread-0
```

（或自定义线程名）

------

### 情况2

```java
thread.run();
```

现象：

- 不创建新线程
- 本质是普通方法调用

输出线程名：

```text
main
```

------

## 结论

start()：

```text
创建新线程
并由新线程执行 run()
```

run()：

```text
普通成员方法
由当前线程直接执行
```

------

# 三、start() 为什么只能调用一次

实验：

```java
thread.start();
thread.start();
```

结果：

```java
IllegalThreadStateException
```

------

查看源码：

```java
if (threadStatus != 0)
    throw new IllegalThreadStateException();
```

------

## 原因

Thread 对象生命周期：

```text
NEW

↓

RUNNABLE

↓

RUNNING

↓

TERMINATED
```

只有：

```text
NEW
```

状态允许调用 start()。

线程启动后已经脱离 NEW 状态。

即使线程执行结束：

```text
TERMINATED
```

也不能再次 start()。

只能重新：

```java
new Thread(...)
```

创建新的线程对象。

------

# 四、线程调度现象

实验：

两个线程同时打印。

现象：

```text
线程1

线程2

线程1

线程1

线程2
...
```

顺序每次运行都不同。

------

## 原因

线程执行顺序由：

```text
操作系统调度器
```

决定。

线程之间会发生：

```text
CPU时间片切换
```

因此：

```text
start顺序 ≠ 执行顺序
```

------

# 五、Race Condition（竞态条件）

## 实验代码

共享变量：

```java
private static int count = 0;
```

10个线程：

```java
for (...) {
    count++;
}
```

每个线程执行：

```text
10000次
```

理论结果：

```text
100000
```

------

## 实验结果

多次运行：

```text
25395
25093
18537
21976
23024
```

远小于：

```text
100000
```

------

## 为什么会出错

count++ 并不是原子操作。

本质上包含：

```text
读取 count

↓

修改

↓

写回 count
```

等价于：

```java
temp = count;

temp = temp + 1;

count = temp;
```

------

## 错误示例

初始：

```text
count = 0
```

线程A：

```text
读取 count

得到 0
```

切换。

------

线程B：

```text
读取 count

得到 0

+1

写回 1
```

切换。

------

线程A：

```text
0+1

写回 1
```

最终：

```text
count = 1
```

理论：

```text
count = 2
```

发生：

```text
丢失更新（Lost Update）
```

------

# 六、问题的本质

问题不是：

```text
++
```

导致的。

也不是：

```text
+
-
*
/
```

导致的。

真正的问题是：

```text
多个线程

同时修改

共享变量
```

且修改过程：

```text
不是原子操作
```

------

例如：

```java
count++;

count--;

count += 100;

count *= 2;
```

都可能出现线程安全问题。

------

# 七、join() 的作用

实验：

如果直接打印：

```java
System.out.println(count);
```

可能很多线程还没执行结束。

实验结果失去意义。

------

解决：

```java
thread.join();
```

------

## join 的含义

当前线程：

```text
等待目标线程执行结束
```

例如：

```java
thread.join();
```

表示：

```text
main线程等待thread执行结束
```

------

## 注意

join 并不是：

```text
等待当前线程结束
```

而是：

```text
等待被 join 的线程结束
```

------

# 八、面试回答

## start() 与 run() 的区别

run() 只是普通方法调用，不会创建新线程；start() 会创建新的线程，并由该线程执行 run() 方法。

------

## 为什么 start() 只能调用一次

Thread 对象只有处于 NEW 状态时才能调用 start()。线程启动后即使执行结束，也不能再次启动，只能重新创建 Thread 对象。

------

## 为什么 count++ 线程不安全

count++ 不是原子操作，底层包含读取、修改、写回三个步骤。多个线程同时执行时可能发生丢失更新，导致最终结果小于理论值。

------

## 什么是竞态条件

多个线程同时访问并修改共享资源，而程序执行结果依赖于线程执行顺序时，就发生了竞态条件（Race Condition）。

# 九、synchronized 的本质

## 实验1

共享锁：

```java
synchronized(MyThread.class){
    count++;
}
```

实验结果：

```text
100000
100000
100000
```

始终正确。

------

## 实验2

每个线程使用自己的锁对象：

```java
synchronized(this){
    count++;
}
```

实验结果：

```text
38109
22966
...
```

结果再次错误。

------

## 结论

synchronized 并不是锁代码。

真正锁的是：

```text
锁对象（Monitor）
```

代码块只是被锁保护的区域。

------

例如：

```java
synchronized(lock){
    ...
}
```

锁的是：

```text
lock对象
```

------

例如：

```java
synchronized(this){
    ...
}
```

锁的是：

```text
当前对象
```

------

例如：

```java
synchronized(MyThread.class){
    ...
}
```

锁的是：

```text
MyThread对应的Class对象
```

------

## 判断是否互斥的核心

不要看：

```text
是不是同一个方法
```

不要看：

```text
是不是同一段代码
```

只看：

```text
锁对象是否相同
```

如果：

```text
锁对象相同
```

↓

```text
互斥
```

------

如果：

```text
锁对象不同
```

↓

```text
不互斥
```

------

# 十、synchronized 普通方法与静态方法

## 普通方法

```java
public synchronized void add(){
    count++;
}
```

等价于：

```java
public void add(){
    synchronized(this){
        count++;
    }
}
```

锁的是：

```text
当前对象(this)
```

------

实验：

```java
MyThread t1 = new MyThread();
MyThread t2 = new MyThread();
```

结果：

```text
22966
```

原因：

```text
t1和t2是两个不同对象

因此是两把锁
```

不互斥。

------

## 静态同步方法

```java
public static synchronized void add(){
    count++;
}
```

等价于：

```java
synchronized(MyThread.class){
    count++;
}
```

锁的是：

```text
MyThread.class
```

------

实验结果：

```text
100000
```

原因：

```text
整个JVM中

一个类只对应一个Class对象
```

因此：

```text
所有线程竞争同一把锁
```

实现互斥。

------

# 十一、Runnable

## 为什么有Thread还需要Runnable

Thread表示：

```text
线程执行者
```

Runnable表示：

```text
线程任务
```

------

如果只继承Thread：

```java
class MyThread extends Thread
```

任务和线程耦合在一起。

------

Runnable将两者分离：

```java
class MyTask implements Runnable
```

------

执行：

```java
Runnable task = new MyTask();

Thread t1 = new Thread(task);
Thread t2 = new Thread(task);
```

------

此时：

```text
两个线程

执行同一个任务
```

------

## 面试回答

Runnable实现了任务与线程的解耦。

Thread负责执行任务，

Runnable负责描述任务。

同一个Runnable可以被多个Thread执行，同时避免了继承Thread导致的单继承限制。

------

# 十二、Callable

## Runnable的问题

Runnable定义：

```java
void run()
```

特点：

```text
没有返回值

不能抛出受检异常
```

------

例如：

子线程计算：

```text
1+2+...+100
```

结果：

```text
5050
```

无法直接返回给主线程。

------

因此Java设计：

```java
Callable<V>
```

------

定义：

```java
V call() throws Exception
```

特点：

```text
支持返回值

支持异常传递
```

------

示例：

```java
public class CallableDemo
        implements Callable<Integer>{

    @Override
    public Integer call(){
        return 5050;
    }

}
```

------

# 十三、FutureTask

## Callable的问题

Callable虽然可以返回值：

```java
call()
```

但是：

```java
Thread
```

只能执行：

```java
run()
```

因此：

```java
new Thread(callable)
```

无法编译。

------

## FutureTask出现的原因

FutureTask同时实现：

```java
RunnableFuture
```

而：

```java
RunnableFuture
```

继承：

```java
Runnable

Future
```

因此：

```text
既能被Thread执行

又能保存结果
```

------

## 执行流程

```text
Callable

↓

FutureTask(包装Callable)

↓

Thread(FutureTask)

↓

start()

↓

FutureTask.run()

↓

Callable.call()

↓

得到结果

↓

保存到outcome

↓

futureTask.get()

↓

获取结果
```

------

## 实验结论

FutureTask内部持有：

```java
private Callable<V> callable;
```

执行时调用：

```java
callable.call()
```

获取结果。

------

FutureTask内部通过：

```java
private Object outcome;
```

保存执行结果。

------

主线程：

```java
futureTask.get()
```

最终从：

```text
outcome
```

中获取结果。

------

# 十四、Thread Basic知识图谱

```text
Thread
│
├── start()
├── run()
├── join()
│
├── Runnable
│     ↓
│   定义任务
│
├── Callable
│     ↓
│   支持返回值
│
├── FutureTask
│     ↓
│ Runnable + Future
│
├── Race Condition
│
└── synchronized
      ↓
  通过竞争同一把锁实现互斥
```

------

# Thread Basic面试高频问题

### start()与run()区别

start()创建新线程并执行run()；run()只是普通方法调用。

------

### 为什么start()只能调用一次

Thread对象只能在NEW状态下启动一次，线程结束后不能再次启动，只能重新创建Thread对象。

------

### 为什么count++线程不安全

count++不是原子操作，底层包含读取、修改、写回三个步骤，多线程情况下会发生丢失更新。

------

### Runnable与Callable区别

Runnable没有返回值且不能抛出受检异常；Callable支持返回值和异常传递。

------

### FutureTask的作用

FutureTask实现了Runnable和Future，既能交给Thread执行，又能保存并获取Callable的执行结果。

------

### synchronized锁的是什么

锁的是对象（Monitor），而不是代码块。判断是否互斥的核心标准是多个线程是否竞争同一个锁对象。
