package org.example;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static List<Thread> fishThreadList;
    public static Random random;
    public static int aquariumHeight;
    public static int aquariumWidth;
    public static int availableSpaceInAquarium;
    volatile public static AtomicInteger numberOfFishesInAquarium;
    volatile public static AtomicInteger numberOfMaleFish;
    volatile public static AtomicInteger numberOfFemaleFish;
    public static int averageAquariumSize;
    volatile public static Fish[][] fishesInAquarium;
    volatile public static AtomicBoolean isReachedLimit;


    public static void main(String[] args) {
        random = new Random();

        //initializing aquarium width
        int aquariumWidth = random.nextInt(5, 10);

        //initializing aquarium height
        int aquariumHeight = random.nextInt(3, aquariumWidth);

        Main.aquariumWidth = aquariumWidth;

        Main.aquariumHeight = aquariumHeight;

        //crating doubled array as sample aquarium (rows = height, cols = width)
        Main.fishesInAquarium =
                new Fish[Main.aquariumHeight][Main.aquariumWidth];

        //setting average aquarium size for
        Main.averageAquariumSize = (Main.aquariumHeight + Main.aquariumWidth) / 2;

        //setting available space multiplying height and width
        availableSpaceInAquarium = Main.aquariumHeight * Main.aquariumWidth;

        System.out.printf("\n\nSize have been approved. Aquarium size: [%s,%s]\n\n",
                aquariumHeight, aquariumWidth);

        //creating initial male fishes
        int numberOfMaleFish = random.nextInt(2,
                Math.min(Main.aquariumWidth, Main.aquariumHeight));

        //creating initial female fishes
        int numberOfFemaleFish = random.nextInt(2,
                Math.min(Main.aquariumWidth, Main.aquariumHeight));

        Main.numberOfMaleFish = new AtomicInteger(numberOfMaleFish);

        Main.numberOfFemaleFish = new AtomicInteger(numberOfFemaleFish);

        System.out.printf("""
                \nAmount of fish MALE fish -> %s
                Amount of fish FEMALE fish -> %s
                                     
                """, Main.numberOfMaleFish, Main.numberOfFemaleFish);

        //this 1.5 sek sleeping is for showing info about initial
        //parameters about aquarium
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //setting false, it means aquarium is not full
        Main.isReachedLimit = new AtomicBoolean(false);

        //all fishes in the aquarium, addition of male and female fishes
        numberOfFishesInAquarium = new AtomicInteger(
                Main.numberOfMaleFish.get() + Main.numberOfFemaleFish.get());

        //it validates that whether 'numberOfFishesInAquarium' is over from
        //'availableSpaceInAquarium' field, if it is, it stops the program
        if (numberOfFishesInAquarium.get() >= availableSpaceInAquarium)
            Main.isReachedLimit.set(true);

        //if 'isReachedLimit' is true, program stops
        if (isReachedLimit.get()) return;

        fishThreadList = new LinkedList<>();

        //putting male and female fishes into aquarium...

        //creating initial male fish
        for (int i = 0; i < Main.numberOfMaleFish.get(); i++) {
            fishThreadList.add(new Thread(
                    new Fish(random.nextInt(3, averageAquariumSize),
                            i + 1,
                            0)));
        }

        //creating initial female fish
        for (int i = 0; i < Main.numberOfFemaleFish.get(); i++)
            fishThreadList.add(new Thread(
                    new Fish(random.nextInt(3, averageAquariumSize),
                            i + 1,
                            1)));

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
    }
}
