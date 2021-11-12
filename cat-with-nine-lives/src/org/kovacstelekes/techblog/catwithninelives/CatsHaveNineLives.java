package org.kovacstelekes.techblog.catwithninelives;

public class CatsHaveNineLives {
    public static void main(String[] args) {
        Cat aCat = new Cat();
        System.out.println("Message from " + aCat + ": " + aCat.message());
        for (int i = 0; i < 9; i++) {
            aCat = null;
            System.out.println("Killed the cat. Message from " + aCat + ": " + aCat.message());
        }

        aCat = new Cat();
        System.out.println("Resurrected the cat. Message from " + aCat + ": " + aCat.message());
    }
}
