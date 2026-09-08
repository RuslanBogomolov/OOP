package ru.nsu.bogomolov;

public class Main {
    /* Функция восстанавливает свойство max-heap в поддереве с корнем в индексе i
    * Сначала находит максимум, затем спускает меньший элемент и дальше по рекурсии
    * пока не станет корректной кучей*/
    void heapify(int[] arr, int n, int i) {
        int big = i;

        int l = 2 * i + 1;
        int r = 2 * i + 2;

        if (l < n && arr[l] > arr[big]) {
            big = l;
        }
        if (r < n && arr[r] > arr[big]) {
            big = r;
        }
        if (big != i) {
            int tmp = arr[i];
            arr[i] = arr[big];
            arr[big] = tmp;

            heapify(arr, n, big);
        }
    }

    /*Функция строит max-heap и после этого максимальный элемент, который находит heapify,
    * переносит в конец для каждого элемента, после чего получается отсортированный массив*/
    void heapSort(int[] arr) {
        int n = arr.length;
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }

        for (int i = n - 1; i > 0; i--) {
            int tmp = arr[0];
            arr[0] = arr[i];
            arr[i] = tmp;

            heapify(arr, i, 0);
        }
    }
}