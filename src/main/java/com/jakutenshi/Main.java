package com.jakutenshi;

import java.io.*;
import java.lang.reflect.Array;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by JAkutenshi on 16.04.2016.
 */
public class Main {


    public static void main(String[] args) throws FileNotFoundException {
        double[][] X = new double[9][];
        double sumX = 0;
        double sumXI = 0;
        double A = 0;
        double condition = 0;

        File file = new File( "output.txt" );
        PrintStream out = new PrintStream(file);
        System.setOut(out);

        //генерация входных массивов распределений
        int i = 0;
        for (int n = 30; n >= 18 ; n -= 6) {
            X[i] = uniformDistibution(n);
            X[i + 3] = exponentialDistibution(n);
            X[i + 6] = rayleighDistibution(n);
            i++;
        }


        //сортировка массивов по возрастанию
        for (int a = 0; a < 9; a++) {
            Arrays.sort(X[a]);
        }

        //анализ
        int n = 0;
        int m;
        double fm;
        ArrayList<Double> fmList;
        double gm;
        ArrayList<Double> gmList;
        double subFG;
        ArrayList<Double> subFGList;
        int minM;
        double tmp;
        double tmpM;
        //результат
        int B = 0;
        double K = 0;
        int Tk;

        for (int a = 0; a < 9; a++) {
            System.out.println("------------------START--------------------");
            switch (a / 3) {
                case 0 :
                    System.out.printf("Uniform distribution ");
                    break;
                case 1 :
                    System.out.printf("Exponential distribution ");
                    break;
                case 2 :
                    System.out.printf("Rayleigh distribution ");
                    break;
            }
            switch (a % 3) {
                case 0 :
                    System.out.printf("n = 30\n");
                    break;
                case 1 :
                    System.out.printf("n = 24\n");
                    break;
                case 2 :
                    System.out.printf("n = 18\n");
                    break;
            }

            outArray(X[a]);
            n = X[a].length;

            //нахождение А
            //предусловия
            sumX = 0;
            sumXI = 0;
            for (i = 1; i <= n; i++) {
                sumX += X[a][i - 1];
                sumXI += i * X[a][i - 1];
            }
            A = sumXI / sumX;
            System.out.printf("Sum(Xi) = %10.4f\n", sumX);
            System.out.printf("Sum(i * Xi) = %10.4f\n", sumXI);
            System.out.printf("A = %10.4f\n", A);

            //проверка условия сходимости
            //предусловия
            m = n + 1;
            fm = 0;
            fmList = new ArrayList<Double>(100);
            gmList = new ArrayList<Double>(100);
            subFGList = new ArrayList<Double>(100);
            minM = -1;

            //расчет
            condition = (n - 1) / 2;
            if (A > condition) {
                //если ряд сходится, то анализируем дальше
                while (true) {
                    for (i = 1; i <= n; i++) {
                        fm += (double) 1 / (m - i);
                    }
                    fmList.add(fm);
                    gm = n / (m - A);
                    gmList.add(gm);
                    subFG = Math.abs(fm - gm);
                    subFGList.add(subFG);
                    if (subFGList.size() > 2 && subFGList.get(subFGList.size() - 2) < subFGList.get(subFGList.size() - 1)) {
                        minM = m - 1;
                        break;
                    }
                    if (m == 100) {
                        System.out.println("M == 100, too long ");
                        break;
                    }
                    m++;
                    fm = 0;
                }
                System.out.printf("|\tm\t|\tf(m)\t|\tg(m)\t|\tdelta\t|\n");
                for (i = 0; i < fmList.size(); i++) {
                    System.out.printf("|\t%d\t|\t%7.4f\t|\t%7.4f\t|\t%7.4f\t|\n",
                            n + i + 1,
                            fmList.get(i),
                            gmList.get(i),
                            subFGList.get(i));
                }
                System.out.printf("Min delta with M = %d\n", minM);
                B = minM - 1;
                System.out.printf("B = %d\n", B);
                K = n / ((B - 1) * sumX - sumXI);
                System.out.printf("K = %7.4f\n", K);
                tmp = 0;
                for (i = 1; i <= B - n; i++) {
                    tmp += (double) 1 / i;
                }
                Tk = (int) (tmp / K);
                System.out.printf("Tk = %d days\n", Tk);
            } else {
                System.out.println("A < condition !");
            }
            System.out.println("------------------FINISH-------------------");

        }
        out.close();

    }

    static double[] uniformDistibution(int n) {
        double[] X = new double[n];
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            X[i] = randomDoubleWithBorder(20);
        }
        return X;
    }

    static double[] exponentialDistibution(int n) {
        double[] X = new double[n];
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            X[i] = -Math.log(randomDoubleWithBorder(1)) / 0.1;
            if (X[i] == Double.POSITIVE_INFINITY) {
                i--;
            }
        }
        return X;
    }

    static double[] rayleighDistibution(int n) {
        double[] X = new double[n];

        for (int i = 0; i < n; i++) {
            X[i] = 8.0 * Math.sqrt(-2 * Math.log(randomDoubleWithBorder(1)));
            if (X[i] == Double.POSITIVE_INFINITY) {
                i--;
            }
        }
        return X;
    }

    static double randomDoubleWithBorder(int border) {
        Random random = new Random();
        return ((double) random.nextInt(border * 100)) / 100;
    }

    static void outArray(double[] a) {
        System.out.printf("|\ti\t|\tXi\t|\n");
        for (int i = 0; i < a.length; i++) {
            System.out.printf("|\t%d\t|\t%7.4f\t|\n", i + 1, a[i]);
        }

    }
}
