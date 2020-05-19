package me.selslack.codingame.basic;

import me.selslack.codingame.basic.pkg.Dep;
import me.selslack.codingame.basic.pkg.Enum;

import java.util.Scanner;

public class Player {
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int N = in.nextInt();
        int Q = in.nextInt();

        for (int i = 0; i < N; i++) {
            String EXT = in.next();
            String MT = in.next();

            in.nextLine();
        }

        for (int i = 0; i < Q; i++) {
            String FNAME = in.nextLine();
        }

        System.out.println(Dep.isGood());
        System.out.println(Enum.A);
        System.out.println(Enum.NestedEnum.C);
    }
}
