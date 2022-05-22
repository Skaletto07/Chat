package ru.gb.HomeWork6;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HomeWork6Test {
    private static HomeWork6 hw;

    @BeforeEach
    public  void init() {
        hw = new HomeWork6();
    }

    @Test
    void equalsArrs1InMethodArr() {
        int[] a = {0,1,2,3,4,5,6,7,8,9,0};
        int[] res = hw.methodArrSearchFour(a);
        int[] eqls = {5,6,7,8,9,0};
        Assertions.assertArrayEquals(res, eqls);
    }

    @Test
    void equalsArrs2InMethodArr() {
        int[] a = {1,2,4,4,2,3,4,1,7};
        int[] res = hw.methodArrSearchFour(a);
        int[] eqls = {1,7};
        Assertions.assertArrayEquals(res, eqls);
    }

    @Test
    void equalsArrs3InMethodArr() {
        int[] a = {-17,25,4,-4,14,44,41,-14};
        int[] res = hw.methodArrSearchFour(a);
        int[] eqls = {-4,14,44,41,-14};
        Assertions.assertArrayEquals(res, eqls);
    }
    @Test
    void equalsArrs4InMethodArr() {
        int[] a = {-17,25,-4,14,44,41,-14};
        Assertions.assertThrows(RuntimeException.class, () -> hw.methodArrSearchFour(a));
    }

    @Test
    void  equalsfoundNumbersOneAndFour() {
        int[] a = {1,1,1,4,4,1,4,4};
        boolean res = hw.foundNumbersOneAndFour(a);
        Assertions.assertEquals(res, true);
    }

    @Test
    void  equalsfoundNumbersOneAndFour2() {
        int[] a = {1,1,1,1,1,1};
        boolean res = hw.foundNumbersOneAndFour(a);
        Assertions.assertEquals(res, false);
    }
    @Test
    void  equalsfoundNumbersOneAndFour3() {
        int[] a = {4,4,4,4,4,4};
        boolean res = hw.foundNumbersOneAndFour(a);
        Assertions.assertEquals(res, false);
    }
    @Test
    void  equalsfoundNumbersOneAndFour4() {
        int[] a = {1,1,-1,4,-4,1,4,4};
        boolean res = hw.foundNumbersOneAndFour(a);
        Assertions.assertEquals(res, true);
    }
    @Test
    void equalsfoundNumbersOneAndFour5() {
        int[] a = {-1,2,-4,5};
        boolean res = hw.foundNumbersOneAndFour(a);
        Assertions.assertEquals(res, false);
    }

}