package org.kovacstelekes.techblog.catwithninelives;

class Cat {
    private static int lives;

    Cat() {
        lives = 9;
    }

    static String message() {
        String message = "I've run out of lives. Please resurrect me.";
        if (lives > 0) {
            message = "I'm a Cat with " + lives + (lives == 1 ? " life" : " lives");
            lives--;
        }
        return message;
    }
}
