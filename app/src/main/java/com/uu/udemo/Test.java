package com.uu.udemo;

/**
 * description：
 * autohor：zhangguiyou
 * date: 2017/11/10.
 */

public class Test {

    {
        System.out.println("构造代码块一");
    }

    public Test() {
        System.out.println("构造方法");
    }

    public static void main(String[] args){
        System.out.println("main方法");

        {
            System.out.println("普通代码块一");
        }
        new Test();
        {
            System.out.println("普通代码块二");
        }
    }

    {
        System.out.println("构造代码块二");
    }

    static{
        System.out.println("静态构造代码块");
    }
}
