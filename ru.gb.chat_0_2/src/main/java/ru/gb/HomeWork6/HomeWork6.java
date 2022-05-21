package ru.gb.HomeWork6;

import java.util.LinkedList;
import java.util.List;

public class HomeWork6 {

    public static void main(String[] args) {
        HomeWork6 h = new HomeWork6();
    int[] a = {1,1,1,4,4,1,4,4};
    int[] a1 = {4,4,4,4,4};
        System.out.println(h.foundNumbersOneAndFour(a1));
    }

    public int[] methodArrSearchFour(int[] arr) {
        int[] res = new int[0];
        int c = 0;
        if (arr.length != 0) {
            for (int i = arr.length - 1; i >= 0; i--) {
                if (arr[i] == 4) {
                    int b = 0;
                    c = i + 1;
                    int len = arr.length - c;
                    res = new int[len];
                    for (int i1 = c; i1 < arr.length; i1++) {
                        res[b] = arr[i1];
                        b++;
                    }
                    break;
                }
            }
            if (c == 0) {
                throw new RuntimeException("Dont have number 4 in array!");
            }
        } else throw new RuntimeException("Empty array!");

        return res;
    }

    public boolean foundNumbersOneAndFour(int[] arr) {
        boolean o = false, f = false;
        for (int i : arr) {
            if (i == 4) f = true;
            if (i == 1) o = true;
        }
        return o && f;

    }





}
