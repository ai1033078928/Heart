package com.hrbu.javase;

import org.junit.Test;
import java.util.Random;
import java.util.Scanner;

public class 排序算法 {
    private static Scanner scanner;

    @Test
    public void Main() {
        int arr[]= getArr(18); //{95,27,90,49,80,58,6,9,18,50};
        //e.printArr(arr);
        int ahb = 0;
        long starttime;
        long endtime;
        System.out.println("请输入排序方式：");
        System.out.println("1.冒泡排序");
        System.out.println("2.选择排序");
        System.out.println("3.插入排序");
        System.out.println("4.希尔(Shell)排序");
        scanner = new Scanner(System.in);
        while((ahb = scanner.nextInt())!=-1){
            switch(ahb){
                case 1:
                    starttime = System.currentTimeMillis();
                    sortBub(arr);
                    endtime = System.currentTimeMillis();
                    System.out.println("冒泡排序时间" + (endtime - starttime) + "ms");
                    //e.printArr(arr);
                    break;
                case 2:
                    starttime = System.currentTimeMillis();
                    sortsel(arr);
                    endtime = System.currentTimeMillis();
                    System.out.println("选择排序时间" + (endtime - starttime) + "ms");
                    //e.printArr(arr);
                    break;
                case 3:
                    starttime = System.currentTimeMillis();
                    sortins(arr);
                    endtime = System.currentTimeMillis();
                    System.out.println("选择排序时间" + (endtime - starttime) + "ms");
                    //e.printArr(arr);
                    break;
                case 4:
                    starttime = System.currentTimeMillis();
                    sortShell(arr);
                    endtime = System.currentTimeMillis();
                    System.out.println("Shell排序时间" + (endtime - starttime) + "ms");
                    //e.printArr(arr);
                    break;
                default:
                    break;
            }
        }

//		starttime=System.currentTimeMillis();
//		sortBub2(arr);
//		endtime=System.currentTimeMillis();
//		System.out.println("改进冒泡排序时间" + (endtime - starttime) + "ms");
//		e.printArr(arr);

    }

    public int[] getArr(int len){	//随机生成长度为len的数组
        int arr[] = new int[len];
        Random r = new Random();
        //r.setSeed(100);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = r.nextInt();
        }
        return arr;
    }

    public void printArr(int arr[]){		//打印数组
        for (int i = 0; i < arr.length; i++) {
            if(i!=0 && i%9==0){
                System.out.println();
            }
            System.out.print(arr[i] + "\t");
        }
        System.out.println();
    }

    public static void sortBub(int arr[]){		//冒泡排序  （10万个数15716ms）
        for (int i = 0; i < arr.length-1; i++) {
            for (int j = 0; j < arr.length-i-1; j++) {
                if(arr[j]>arr[j+1]){
                    int temp=arr[j];
                    arr[j]=arr[j+1];
                    arr[j+1]=temp;
                }
            }
        }
    }

    public static void sortBub2(int arr[]){		//改进冒泡排序  （10万个15874ms 若原来有序会更快）
        int i,j,flag=1;
        for (i = 0; i < arr.length-1 && flag==1; i++) {
            flag=0;
            for (j = 0; j < arr.length-i-1; j++) {
                if(arr[j]>arr[j+1]){
                    int temp=arr[j];
                    arr[j]=arr[j+1];
                    arr[j+1]=temp;
                    flag=1;
                }
            }
        }
    }

    public static void sortsel(int arr[]){//选择排序  10万个数2671ms
        int i,j,m;
        for (i = 0; i < arr.length-1; i++) {
            m = i;
            for (j = i+1 ; j < arr.length; j++) {
                if(arr[j]<arr[m]){
                    m=j;
                }
            }
            if(i!=m){
                int temp=arr[i];
                arr[i]=arr[m];
                arr[m]=temp;
            }
        }
    }

    public static void sortins(int arr[]){//插入排序  10万个数3260ms
        int i,j,temp;
        for (i = 1; i < arr.length; i++) {
            temp=arr[i];
            j=i-1;
            while(temp<arr[j]){
                arr[j+1]=arr[j];
                j--;
                if(j==-1){
                    break;
                }
            }
            arr[j+1]=temp;

        }
    }

    public static void sortShell(int arr[]) {//Shell排序=》改良的插入排序   10万个数21ms
        int i, j, k, gap;
        gap = arr.length / 2;
        while (gap > 0) {
            for (k = 0; k < gap; k++) {
                for (i = k + gap; i < arr.length; i += gap) {
                    for (j = i - gap; j >= k; j -= gap) {
                        if (arr[j] > arr[j + gap]) {
                            int temp = arr[j];
                            arr[j] = arr[j + gap];
                            arr[j + gap] = temp;
                        } else {
                            break;
                        }
                    }
                }
            }
            gap /= 2;
        }
    }

}
