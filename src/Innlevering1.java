import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.LocalDateTime;

/**
 * 0?) start the program by running a "warm up"
 * 1) run each algorithm an arbitrary 10 amounts of times for each sample size
 * 2) save the average runtime for each of the sample size, for each algorithm
 * 3) output each row as follow to a file in csv format:
 *         sampleSize, runTime, v1, v2, v3,
 *         where:
 *              v1 = time / sampleSize >> looks like purely empirical analysis?
 *              v2 = time / sampleSize * resultSize
 *              v3 = time / (log(sampleSize) * sampleSize)
 *
 * 4) make a graph for word inputs, as well as numbers >> sorted, sorted in reverse, unsorted.
 *
 *
 * @author Clément Marescaux
 * Created by root on 9/17/16.
 */

public class Innlevering1 {

    private static final Dedup[] dedups =
            new Dedup[]{
                    Dedup.newBasicDedup(),
                    Dedup.newHashSetDedup()
            };

    // Alternative to measure time (use System.nanoTime())
    private static long sysTimer;

    public static void main(String[] args) {
        if(args.length < 1){
            System.out.println("\nPlease run the program with at least one input file.");
            System.out.println("For example: java Innlevering1 shakespeare.txt tall100.txt");
            System.exit(0);
        }

        //dummy run
        warmUp();

        for(String filename : args){
            for( Dedup dedup : dedups){
                runAlgorithm(new Utils.Sampler(filename), dedup);
            }
        }
    }

    private static void warmUp() {
        System.out.println("Starting warmUp()...");

        String[] dummyArray = new String[1000];
        for (int i = 0; i < dummyArray.length - 1; i += 2) {
            dummyArray[i] = "" + i;
            dummyArray[i + 1] = "" + i;
        }
        sysTimer = System.nanoTime();
        dedups[1].dedup(dummyArray);
        sysTimer = System.nanoTime() - sysTimer;

        System.out.println("warm-up finished.");
    }


    private static void runAlgorithm(Utils.Sampler sample, Dedup dedup) {

        String inputFileName = sample.getFilename();
        String algorithmName = dedup.getClass().getSimpleName().toLowerCase();

        PrintStream output = createFile(inputFileName, algorithmName);

        System.out.printf("Starting %s tests for %s...\n", algorithmName, inputFileName);

        // Set up range of experiments
        int lo = 1;
        int hi = 10000;
        int numberOfLevels = 100;
        int runsPerSampleSize = 10;

        // Calculation of step size
        int step = (hi - lo) < numberOfLevels ? 1 : (hi - lo) / (numberOfLevels - 1);

        // Main loop
        for (int size = lo; size <= hi; size += step) {
            System.out.print(".");
            String[] uniques = sample.get(size);

            long avgTime = 0;   // store an average time for each sample size
            String[] withoutDupes = dedup.dedup(uniques);

            for(int i = 0; i < runsPerSampleSize; i++){

                sysTimer = System.nanoTime();
                withoutDupes = dedup.dedup(uniques);
                long elapsedTime = System.nanoTime() - sysTimer;
                avgTime += elapsedTime;
            }

            avgTime /= runsPerSampleSize;
            int uniquesSize = withoutDupes.length;

            /* Write measurement to output */
            output.printf("%d,%d,%d,%d,%d\n",
                    size,
                    avgTime,
                    v1(avgTime, size),
                    v2(avgTime, size, uniquesSize),
                    v3(avgTime, size));
        }
        output.close();
        System.out.println();
    }

    /**
     * Creates the output for the results of an algorithm on a given input file.
     *
     * @param inputFileName
     * @param algorithmName
     * @return a PrintStream.
     */
    private static PrintStream createFile(String inputFileName, String algorithmName) {

        String outputFilename =
                String.format("%s - %s.csv", algorithmName, inputFileName);
        try {
            PrintStream output = new PrintStream(outputFilename);
            output.printf("# Measurements: %s\n", LocalDateTime.now());
            output.printf("# size(s),runtime(t),v1( t/s ),v2( t/s*u ),v3( t/s*log(s) )\n");
            return output;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("ERROR ON OPENING FILE '" + outputFilename + "'\n");
        }
    }

    /**
     * Returns the average runtime per element for each algorithm.
     * @param time          the time taken for an algorithm to solve a problem
     * @param problemSize   the size of the problem (i.e. number of elements to process)
     * @return  the average runtime per element
     */
    private static long v1(long time, int problemSize){
        return (time / problemSize);
    }

    private static long v2(long time, int problemSize, int resultSize){
        return (time / problemSize * resultSize);
    }

    private static long v3(long time, int problemSize){
        return (long) (time / Math.log(problemSize) * problemSize);
    }

}
