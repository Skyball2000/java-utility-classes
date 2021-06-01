package yanwittmann.utils;

import yanwittmann.log.Progress;

class ProgressTest {

    public static void main(String[] args) {
        regularProgressBarTest();
    }

    public static void regularProgressBarTest() {
        Progress progress = new Progress(100, 100);
        long before = System.currentTimeMillis();
        for (int i = 0; i < 101; i++) {
            progress.update(i);
            System.out.print("\r" + progress.getBar() + " Remaining time: " + progress.getRemainingTimeSeconds() + "s       ");
            Sleep.milliseconds(30);
        }
        long after = System.currentTimeMillis();
        System.out.println("\n" + (after - before));
    }

}