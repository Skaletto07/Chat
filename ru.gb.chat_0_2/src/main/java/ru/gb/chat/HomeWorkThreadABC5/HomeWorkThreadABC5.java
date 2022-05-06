package ru.gb.chat.HomeWorkThreadABC5;

public class HomeWorkThreadABC5 {

    private final Object mon = new Object();
    private char currentLetter = 'A';

    public static void main(String[] args) {
        HomeWorkThreadABC5 abc = new HomeWorkThreadABC5();

        Thread tr1 = new Thread(abc::printA);
        tr1.start();
        Thread tr2 = new Thread(abc::printB);
        tr2.start();
        Thread tr3 = new Thread(abc::printC);
        tr3.start();

    }

    public void printA() {
        synchronized (mon) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (currentLetter != 'A') {
                        mon.wait();
                    }
                    System.out.print("A");
                    currentLetter = 'B';
                    mon.notifyAll();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void printB() {
        synchronized (mon) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (currentLetter != 'B') {
                        mon.wait();
                    }
                    System.out.print("B");
                    currentLetter = 'C';
                    mon.notifyAll();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void printC() {
        synchronized (mon) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (currentLetter != 'C') {
                        mon.wait();
                    }
                    System.out.print("C ");
                    currentLetter = 'A';
                    mon.notifyAll();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }



}
