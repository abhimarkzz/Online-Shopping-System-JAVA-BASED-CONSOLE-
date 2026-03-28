package com.shopping.datastructures;

import com.shopping.models.Product;
import java.util.*;

public class SortingAlgorithms {

    public enum Algorithm { BUBBLE, SELECTION, INSERTION, MERGE, QUICK, HEAP, SHELL }

    public static List<Product> sort(List<Product> products, Comparator<Product> cmp, Algorithm algo) {
        List<Product> list = new ArrayList<>(products);
        switch (algo) {
            case BUBBLE    -> bubbleSort(list, cmp);
            case SELECTION -> selectionSort(list, cmp);
            case INSERTION -> insertionSort(list, cmp);
            case MERGE     -> { return mergeSort(list, cmp); }
            case QUICK     -> quickSort(list, 0, list.size() - 1, cmp);
            case HEAP      -> heapSort(list, cmp);
            case SHELL     -> shellSort(list, cmp);
        }
        return list;
    }

    public static void bubbleSort(List<Product> l, Comparator<Product> cmp) {
        int n = l.size();
        for (int i = 0; i < n - 1; i++) {
            boolean sw = false;
            for (int j = 0; j < n - i - 1; j++)
                if (cmp.compare(l.get(j), l.get(j+1)) > 0) { swap(l,j,j+1); sw=true; }
            if (!sw) break;
        }
    }

    public static void selectionSort(List<Product> l, Comparator<Product> cmp) {
        int n = l.size();
        for (int i = 0; i < n-1; i++) {
            int m = i;
            for (int j = i+1; j < n; j++) if (cmp.compare(l.get(j), l.get(m)) < 0) m = j;
            if (m != i) swap(l, i, m);
        }
    }

    public static void insertionSort(List<Product> l, Comparator<Product> cmp) {
        int n = l.size();
        for (int i = 1; i < n; i++) {
            Product key = l.get(i); int j = i-1;
            while (j >= 0 && cmp.compare(l.get(j), key) > 0) { l.set(j+1, l.get(j)); j--; }
            l.set(j+1, key);
        }
    }

    public static List<Product> mergeSort(List<Product> l, Comparator<Product> cmp) {
        if (l.size() <= 1) return l;
        int mid = l.size() / 2;
        List<Product> L = mergeSort(new ArrayList<>(l.subList(0, mid)), cmp);
        List<Product> R = mergeSort(new ArrayList<>(l.subList(mid, l.size())), cmp);
        List<Product> res = new ArrayList<>();
        int i=0, j=0;
        while (i < L.size() && j < R.size()) {
            if (cmp.compare(L.get(i), R.get(j)) <= 0) res.add(L.get(i++));
            else res.add(R.get(j++));
        }
        while (i < L.size()) res.add(L.get(i++));
        while (j < R.size()) res.add(R.get(j++));
        return res;
    }

    public static void quickSort(List<Product> l, int lo, int hi, Comparator<Product> cmp) {
        if (lo < hi) {
            int p = partition(l, lo, hi, cmp);
            quickSort(l, lo, p-1, cmp);
            quickSort(l, p+1, hi, cmp);
        }
    }

    private static int partition(List<Product> l, int lo, int hi, Comparator<Product> cmp) {
        Product pivot = l.get(hi); int i = lo-1;
        for (int j = lo; j < hi; j++)
            if (cmp.compare(l.get(j), pivot) <= 0) { i++; swap(l,i,j); }
        swap(l, i+1, hi); return i+1;
    }

    public static void heapSort(List<Product> l, Comparator<Product> cmp) {
        int n = l.size();
        for (int i = n/2-1; i >= 0; i--) heapify(l, n, i, cmp);
        for (int i = n-1; i > 0; i--) { swap(l, 0, i); heapify(l, i, 0, cmp); }
    }

    private static void heapify(List<Product> l, int n, int r, Comparator<Product> cmp) {
        int lg = r, lt = 2*r+1, rt = 2*r+2;
        if (lt < n && cmp.compare(l.get(lt), l.get(lg)) > 0) lg = lt;
        if (rt < n && cmp.compare(l.get(rt), l.get(lg)) > 0) lg = rt;
        if (lg != r) { swap(l, r, lg); heapify(l, n, lg, cmp); }
    }

    public static void shellSort(List<Product> l, Comparator<Product> cmp) {
        int n = l.size();
        for (int gap = n/2; gap > 0; gap /= 2)
            for (int i = gap; i < n; i++) {
                Product tmp = l.get(i); int j = i;
                while (j >= gap && cmp.compare(l.get(j-gap), tmp) > 0) { l.set(j, l.get(j-gap)); j -= gap; }
                l.set(j, tmp);
            }
    }

    private static void swap(List<Product> l, int i, int j) {
        Product t = l.get(i); l.set(i, l.get(j)); l.set(j, t);
    }

    public static final Comparator<Product> BY_PRICE_ASC   = Comparator.comparingDouble(Product::getPrice);
    public static final Comparator<Product> BY_PRICE_DESC  = (a,b) -> Double.compare(b.getPrice(), a.getPrice());
    public static final Comparator<Product> BY_NAME_ASC    = Comparator.comparing(p -> p.getName().toLowerCase());
    public static final Comparator<Product> BY_RATING_DESC = (a,b) -> Double.compare(b.getRating(), a.getRating());
    public static final Comparator<Product> BY_CATEGORY    = Comparator.comparing(Product::getCategory);
    public static final Comparator<Product> BY_POPULARITY  = (a,b) -> Integer.compare(b.getReviewCount(), a.getReviewCount());
}