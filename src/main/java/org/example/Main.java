package org.example;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static List<Thread> fishThreadList;
    public static Random random;
    volatile public static AtomicInteger aquariumHeight;
    volatile public static AtomicInteger aquariumWidth;
    volatile public static AtomicInteger availableSpaceInAquarium;
    volatile public static AtomicInteger numberOfFishesInAquarium;
    volatile public static AtomicInteger numberOfMaleFish;
    volatile public static AtomicInteger numberOfFemaleFish;
    volatile public static AtomicInteger averageAquariumSize;
    volatile public static Fish[][] fishesInAquarium;
    volatile public static AtomicBoolean isReachedLimit;


    public static void main(String[] args) {
        random = new Random();

        int aquariumWidth = random.nextInt(5, 10);

        int aquariumHeight = random.nextInt(3, aquariumWidth);

        Main.aquariumWidth = new AtomicInteger(aquariumWidth);

        Main.aquariumHeight = new AtomicInteger(aquariumHeight);

        Main.fishesInAquarium =
                new Fish[Main.aquariumHeight.get()][Main.aquariumWidth.get()];

        Main.averageAquariumSize = new AtomicInteger(
                (Main.aquariumHeight.get() + Main.aquariumWidth.get()) / 2);

        availableSpaceInAquarium = new AtomicInteger(
                Main.aquariumHeight.get() * Main.aquariumWidth.get());

        System.out.printf("\n\nSize have been approved. Aquarium size: [%s,%s]\n\n",
                aquariumHeight, aquariumWidth);

        int numberOfMaleFish = random.nextInt(2,
                Math.min(Main.aquariumWidth.get(), Main.aquariumHeight.get()));

        int numberOfFemaleFish = random.nextInt(2,
                Math.min(Main.aquariumWidth.get(), Main.aquariumHeight.get()));

        Main.numberOfMaleFish = new AtomicInteger(numberOfMaleFish);

        Main.numberOfFemaleFish = new AtomicInteger(numberOfFemaleFish);

        System.out.printf("""
                \nAmount of fish MALE fish -> %s
                Amount of fish FEMALE fish -> %s
                                     
                """, Main.numberOfMaleFish, Main.numberOfFemaleFish);

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Main.isReachedLimit = new AtomicBoolean(false);

        numberOfFishesInAquarium = new AtomicInteger(
                Main.numberOfMaleFish.get() + Main.numberOfFemaleFish.get());

        if (numberOfFishesInAquarium.get() >= availableSpaceInAquarium.get())
            Main.isReachedLimit.set(true);

        if (isReachedLimit.get()) return;

        fishThreadList = new LinkedList<>();

        //putting male and female fishes into aquarium...
        //male fish
        for (int i = 0; i < Main.numberOfMaleFish.get(); i++) {
            Thread thread = new Thread(
                    new Fish(random.nextInt(3, averageAquariumSize.get()),
                            i + 1,
                            0));
            fishThreadList.add(thread);
        }

        //female fish
        for (int i = 0; i < Main.numberOfFemaleFish.get(); i++) {
            Thread thread = new Thread(
                    new Fish(random.nextInt(3, averageAquariumSize.get()),
                            i + 1,
                            1));
            fishThreadList.add(thread);
        }

        //after that, the moving process will begin
        for (Thread thread1 : fishThreadList)
            thread1.start();

        //it provides main thread ends after fishThreadList threads
        for (Thread thread : fishThreadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        int num = 0;
    }
}
