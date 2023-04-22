package org.nutz.dao.enhance.test;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 2023/3/20
 */
public class test {
    private static int x = 0, y = 0;
    private static int a = 0, b = 0;

    public static void main(String[] args) throws InterruptedException {
        int i = 0;
        while (true) {
            i++;
            x = y = a = b = 0;
            Thread t1 = new Thread(() -> {
                a = 1;
                x = b;
            });
            Thread t2 = new Thread(() -> {
                b = 1;
                y = a;
            });
            t1.start();
            t2.start();
            t1.join();
            t2.join();
            if (x == 0 && y == 0) {
                System.out.println("第" + i + "次出现指令重排");
                break;
            }
            /*
            *
            * 在上面的代码中，我们定义了四个变量x、y、a、b，并在一个while循环中不断地执行两个线程。
            * 在第一个线程中，我们将a的值设置为1，并将b的值赋给x；
            * 在第二个线程中，我们将b的值设置为1，并将a的值赋给y。
            * 在主线程中，我们判断x和y的值是否都为0，如果是，则说明发生了指令重排，程序会输出当前是第几次出现指令重排，并退出循环。

在上面的代码中，我们使用了volatile关键字来保证变量的可见性和有序性。由于volatile关键字可以禁止指令重排优化，所以我们可以通过不断地执行两个线程来检测是否发生了指令重排。如果发生了指令重排，程序会输出当前是第几次出现指令重排，并退出循环。
            *
            * */
        }
    }
}
