package ru.nsu.bogomolov;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class MainTest {

    @Test // проверка пустого массива
    void heapSortSortsEmptyArray() {
        Main main = new Main();
        int[] arr = {};
        int[] expect = {};

        main.heapSort(arr);

        assertArrayEquals(expect, arr);
    }

    @Test // проверка массива из 1 элемента
    void heapSortSortsArrayWithOneElement() {
        Main main = new Main();
        int[] arr = {50};
        int[] expect = {50};

        main.heapSort(arr);

        assertArrayEquals(expect, arr);
    }

    @Test // проверка уже отсортированного массива
    void heapSortSortsAlreadySortedArray() {
        Main main = new Main();
        int[] arr = {1,2,3,4,5,6,7,8,9};
        int[] expect = {1,2,3,4,5,6,7,8,9};

        main.heapSort(arr);

        assertArrayEquals(expect, arr);
    }

    @Test  // проверка обычного массива
    void heapSortSortsDefaultArray() {
        Main main = new Main();
        int[] arr = {12,11,13,5,6,7};
        int[] expect = {5,6,7,11,12,13};

        main.heapSort(arr);

        assertArrayEquals(expect, arr);
    }

    @Test  // проверка массива в худшем случае
    void heapSortSortsWorstArray() {
        Main main = new Main();
        int[] arr = {9,8,7,6,5,4,3,2,1};
        int[] expect = {1,2,3,4,5,6,7,8,9};

        main.heapSort(arr);

        assertArrayEquals(expect, arr);
    }

    @Test // проверка массива с одинаковыми элементами
    void heapSortSortsArrayWithEqualElement() {
        Main main = new Main();
        int[] arr = {4,4,3,2,1,2,2,2,10,10,2,3,4,5,10};
        int[] expect = {1,2,2,2,2,2,3,3,4,4,4,5,10,10,10};

        main.heapSort(arr);

        assertArrayEquals(expect, arr);
    }

    @Test // проверка массива с нулями и отрицательными числами
    void heapSortSortsArrayWithZeroAndNegativeNumber() {
        Main main = new Main();
        int[] arr = {-30,12,3,2,4,21,0,0,0,-42,-3,-31,0,0,-30};
        int[] expect = {-42,-31,-30,-30,-3,0,0,0,0,0,2,3,4,12,21};

        main.heapSort(arr);

        assertArrayEquals(expect, arr);
    }
}