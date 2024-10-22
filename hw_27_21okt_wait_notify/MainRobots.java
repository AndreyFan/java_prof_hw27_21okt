package de.telran.hw_27_21okt_wait_notify;

import java.util.Scanner;

import static java.lang.Thread.sleep;

public class MainRobots {
    //  1 уровень сложности: 1. У вас есть стол, на который один
    //  робоманипулятор ложит деталь, а второй забирает эту деталь.
    //Когда Робот1 положит деталь на стол, он должет обязательно ждать,
    // пока Робот2 заберет эту деталь
    //и только тогда ложить следующую. Постройте программу, которая
    // автоматизирует эту работу.
    //Количество деталей, которые должны обработать манипуляторы,
    // пользователь задает с клавиатуры.
    //(wait - notify)

    private static final Object workTable = new Object(); // стол - объект, синхронизирующий работу
    private static boolean flag = false; // флаг наличия детали на столе

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите количество деталей, которые должны обработать манипуляторы_  ");
        int countDetails = scanner.nextInt();

        Thread robot1 = new Thread(new Robot1(countDetails), "Робот1");
        Thread robot2 = new Thread(new Robot2(countDetails), "Робот2");

        robot1.start();
        robot2.start();

        try {
            robot1.join();
            robot2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scanner.close();
        System.out.println();
        System.out.println(" ***** Все детали обработаны ***** ");
    }

    static class Robot1 implements Runnable {   //Робот 1 (который кладет детали на стол)
        private final int totalDetails;

        public Robot1(int totalDetails) {
            this.totalDetails = totalDetails;
        }

        @Override
        public void run() {
            for (int i = 1; i <= totalDetails; i++) {
                synchronized (workTable) {
                    // Ждем, пока Робот2 не заберет деталь
                    while (flag) {
                        try {
                            workTable.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println();
                    System.out.println("Робот1 кладет деталь " + i + " на стол.");
                    flag = true;

                    workTable.notify(); // Уведомляем Робот2, что деталь на столе
                }
            }
        }
    }

    static class Robot2 implements Runnable {         // Робот 2 (который забирает детали со стола)
        private final int totalDetails;

        public Robot2(int totalDetails) {
            this.totalDetails = totalDetails;
        }

        @Override
        public void run() {
            for (int i = 1; i <= totalDetails; i++) {
                synchronized (workTable) {

                    while (!flag) {
                        try {
                            workTable.wait(); // Ждем, пока Робот1 не положит деталь на стол
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    System.out.println("Робот2 забирает деталь " + i + " со стола.");
                    flag = false;
                    workTable.notify();
                }
            }
        }
    }
}
