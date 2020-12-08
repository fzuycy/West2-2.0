package company;

import java.util.Scanner;

public class RunThreads implements Runnable{
    static  long ans = 0;//最终结果用一个静态变量储存
    static  int x;
    private long first;//循环分块的起点
    private long last;//循环分块的终点
    public RunThreads() {

    }
    public RunThreads(long first, long last) {
        this.first = first;
        this.last = last;
    }
    @Override
    public void run() {
        long ans1=0;
        for(long  i=first;i<last;i++)
        {
            if (contain(i, x)) {
                ans1 += i;
            }
        }
        //最开始我是打算在上面这个for循环里上锁，然后累加的，但是发现这样不停地上锁，程序运行速度反而变慢了
        // 于是就是打算在每个线程中先各自累加在ans1上，等某个线程完成分块的任务后，再进行同步化，把各自的ans1加在最终结果ans上，这样速度就大大提高了
        synchronized (RunThreads.class)//在这里进行同步化，上类的锁
        {
            ans+=ans1;
        }
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        x = scanner.nextInt();
        System.out.println("Begin!");//提示开始
//        long l = System.currentTimeMillis();
//        for (long i = 1; i < 1000000000; i++) {
//            if (contain(i, x)) ans += i;
//        }
//        long l1 = System.currentTimeMillis();
//        System.out.println(l1-l);
//
//        System.out.println(ans);

        /////////
        Thread tmp[]=new Thread[11];//开一个线程的数组
        long l = System.currentTimeMillis();//开始时计时
        for(int i=0;i<10;i++)//创建10个线程
        {
            tmp[i] = new Thread(new RunThreads(i*100000000,(i+1)*100000000));
            tmp[i].start();//线程开始
        }
        try {
            for (int i = 0; i < 10; i++) {
                tmp[i].join();//阻塞main的主线程
            }
        }
        catch (InterruptedException e)
        {
            System.out.println("Fail");
        }
        long l1 = System.currentTimeMillis();//结束时计时
        System.out.println(l1-l+" ms");//打印出耗时
        System.out.println("ans = "+ans);
    }
    private static boolean contain(long num, int x) {
        return String.valueOf(num).contains(String.valueOf(x));
    }

}
