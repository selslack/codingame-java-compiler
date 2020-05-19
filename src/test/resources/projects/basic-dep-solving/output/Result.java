class Player {
    public static void main(String args[]) {
        java.util.Scanner in = new java.util.Scanner(System.in);
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

class Dep {
    public static boolean isGood() {
        return Utils.isGood();
    }
}

class Utils {
    public static boolean isGood() {
        return true;
    }
}

enum Enum {
    A,
    B;

    public enum NestedEnum {
        C;
    }
}