package com.ka.billingsystem.java;

public class numbertoword {

    static String string;
    static String st1[] = { "", "One", "Two", "Three", "Four", "Five", "Six", "Seven",
            "Eight", "Nine", };
    static String st2[] = { "Hundred", "Thousand", "Lakh", "Crore", "Million", "Billion", "Trillion" };
    static String st3[] = { "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen",
            "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen", };
    static String st4[] = { "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy",
            "Eighty", "Ninety" };

    public static String convert(long number) {
        int n = 1;
        long word;
        string = "";
        while (number != 0) {
            switch (n) {
                case 1:
                    word = number % 100;
                    pass((int) word);
                    if (number > 100 && number % 100 != 0) {
                        show("And ");
                    }
                    number /= 100;
                    break;

                case 2:
                case 4:
                case 6:
                case 8:
                case 10:
                case 12:
                    word = number % 100;
                    if (word != 0) {
                        show(" ");
                        show(st2[n / 2]);
                        show(" ");
                        pass((int) word);
                    }
                    number /= 100;
                    break;

                case 3:
                case 5:
                case 7:
                case 9:
                case 11:
                case 13:
                    word = number % 10;
                    if (word != 0) {
                        show(" ");
                        show(st2[n / 2]);
                        show(" ");
                        pass((int) word);
                    }
                    number /= 10;
                    break;
            }
            n++;
        }
        return string;
    }

    public static void pass(int number) {
        long word;
        int q;
        if (number < 10) {
            show(st1[number]);
        }
        if (number > 9 && number < 20) {
            show(st3[number - 10]);
        }
        if (number > 19) {
            word = number % 10;
            if (word == 0) {
                q = number / 10;
                show(st4[q - 2]);
            } else {
                q = number / 10;
                show(st1[(int) word]);
                show(" ");
                show(st4[q - 2]);
            }
        }
    }

    public static void show(String s) {
        String st;
        st = string;
        string = s;
        string += st;
    }
}
