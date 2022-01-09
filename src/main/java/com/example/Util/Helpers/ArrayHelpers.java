package com.example.Util.Helpers;

public class ArrayHelpers {
    public static int findMaxValue(int[] arr) {
        int max = Integer.MIN_VALUE;
        for (int i : arr) {
            max = Math.max(max, i);
        }
        return max;
    }
}
