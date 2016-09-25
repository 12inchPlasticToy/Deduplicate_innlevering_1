import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

/**
 * 0?) start the program by running a "warm up"
 * 1) run each algorithm an arbitrary 10 amounts of times for each sample size
 * 2) save the median runtime for each of the sample size, for each algorithm
 * 3) output each row as follow to a file in csv format:
 *         sampleSize, runTime, v1, v2, v3,
 *         where:
 *              v1 = time / sampleSize
 *              v2 = time / sampleSize * resultSize
 *              v3 = time / (log(sampleSize) * sampleSize)
 *
 *  N = sampleSize
 *  f(n) = dedup(N)
 *  T(N) = T(f(n)) = time
 *
 *
 * @author Clément Marescaux
 * Created by root on 9/17/16.
 */

public class Innlevering1 {

    private static final Dedup[] dedups =
            new Dedup[]{
                    Dedup.newBasicDedup(),
                    /*
                    Dedup.newArrayListDedup(),
                    Dedup.newSortDedup(),
                    Dedup.newTreeSetDedup(),
                    */
                    Dedup.newHashSetDedup()
            };
    private static long sysTimer;

    public static void main(String[] args) {
        if(args.length < 1){
            System.out.println("\nPlease run the program with at least one input file.");
            System.out.println("For example: java Innlevering1 shakespeare.txt tall100.txt");
            System.exit(0);
        }

        //createFileNoDupes();
        //createFileOnlyDupes();
        warmUp();

        for(String filename : args){
            for( Dedup dedup : dedups){
                run(new Utils.Sampler(filename), dedup);
            }
        }
        System.out.println("\nAll benchmarks done.");
    }

    /**
     * A quick "warm-up" method before running the benchmarks.
     * It fills an array with 500 duplicates, and runs all enabled dedup
     * algorithms on it, and "records" the time too.
     * Not sure how efficient that is, since the first value in benchmarks
     * has shown unusable anyway...
     */
    private static void warmUp() {
        System.out.println("Starting warmUp()...");

        String[] dummyArray = new String[1000];
        for (int i = 0; i < dummyArray.length - 1; i += 2) {
            dummyArray[i] = "" + i;
            dummyArray[i + 1] = "" + i;
        }

        sysTimer = System.nanoTime();
        for (Dedup dedup : dedups) dedup.dedup(dummyArray);
        sysTimer = System.nanoTime() - sysTimer;

        System.out.println("warm-up finished.");
    }

    /**
     * Core method for the exercise. Most of it is a copy from dedupTest and
     * Utils with customisations.
     * Writes to a text file the benchmark results of a deduplication algorithm
     * processing random elements from a file sample of increasing size over
     * each iteration.
     *
     * @param sample the sampled file used to provide random data
     * @param dedup  the algorithm used to deduplicate abovementioned data;
     */

    private static void run(Utils.Sampler sample, Dedup dedup) {

        String inputFileName = sample.getFilename();
        String algorithmName = dedup.getClass().getSimpleName().toLowerCase();

        PrintStream output = createFile(inputFileName, algorithmName);

        System.out.printf("Starting %s tests for %s...\n", algorithmName, inputFileName);

        // Only for console feedback, won't be included in the measurements
        long totalRuntime = System.currentTimeMillis();

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
            String[] sampleStrings = sample.get(size);

            // Storing values to calculate the median time afterwards
            long[] times = new long[runsPerSampleSize];

            String[] withoutDupes;
            int uniquesSize = 0;

            // start a batch of runs to collect a median runtime result
            for(int i = 0; i < runsPerSampleSize; i++){

                sysTimer = System.nanoTime();
                // not sure how badly it affects results to not assign
                // a size to withoutDupes[]...
                withoutDupes = dedup.dedup(sampleStrings);
                times[i] = System.nanoTime() - sysTimer;

                uniquesSize = withoutDupes.length;
            }

            // calculating the median runtime
            // Implemented in case runsPerSampleSize changes in future versions
            Arrays.sort(times);
            long medianTime = (runsPerSampleSize % 2 == 0) ?
                    times[times.length / 2] / 2 + times[(times.length / 2) - 1] / 2 :
                    times[times.length / 2];

            /* Write measurement to output */
            output.printf("%d,%d,%d,%d,%d\n",
                    size,
                    medianTime,
                    v1(medianTime, size),
                    v2(medianTime, size, uniquesSize),
                    v3(medianTime, size));
        }
        output.close();

        float elapsed = (float) (System.currentTimeMillis() - totalRuntime) / 1000;
        System.out.println();
        System.out.printf("Total benchmarking time for %s: %.2fs\n", algorithmName, elapsed);
        System.out.println();
    }

    /**
     * Creates a file to save benchmark results and returns
     * that file's printStream
     *
     * @param inputFileName the name of the sampled file
     * @param algorithmName the name of the algorithm
     * @return the file's printStream.
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

    /**
     * No idea what v2 returns.
     * <p>
     * If n was the string "banana", alg() a function returning unique chars,
     * then:
     * - alg(n) would be "abn"
     * - problemSize would be "banana".length = 6
     * - resultSize would be "abn".length = 3
     * if t = 9 seconds to pluck out all unique chars from "banana"
     * this would return... 9 / 6 x 3 = 1/2 = 0.5 seconds.
     * <p>
     * If n was "tundra" instead (no dupes)
     * - alg(n) would be "tundra"
     * - problemSize = resultSize = "tundra".length = 6
     * if t = 9 seconds because alg() is O(1)
     * 9 / 6 x 6 = 1/4 = 0.25 seconds = less than for "banana"
     * if t = 18 seconds because alg() is ~2n = O(n)
     * 18 / 6 x 6 = 1/2 = 0.50 seconds. Again.
     * <p>
     * 0.5s processing time for each character according to the total amount
     * of duplicates found?
     * Does it give the efficiency of the algorithm depending on the actual work done?
     * Is it the average case?
     * At any rate, resultSize <= problemSize
     *
     * @param time        the time taken to process a sample
     * @param problemSize the size of the sample
     * @param resultSize  the size of the processed sample
     * @return 42
     */

    private static long v2(long time, int problemSize, int resultSize){
        return (time / problemSize * resultSize);
    }

    /**
     * V3 most likely returns f(x) with N as x, which will show
     * if the algorithm is of the order O(n log(n)) if f(x) tends
     * towards a K value as N increases.
     *
     * @param time        the runtime for this problemSize
     * @param problemSize
     * @return
     */
    private static long v3(long time, int problemSize){
        return (long) (time / Math.log(problemSize) * problemSize);
    }


    /*
    All methods below are just helpers to test special case files
     */

    // printstream creation method
    private static PrintStream createFile(String filename) {
        try {
            PrintStream out = new PrintStream(filename);
            return out;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    // fills a file of same size as tall100000, except all 200000 are unique
    // Writes them to output randomly with poor entropy (just using hashset's
    // tree structure)
    private static void createFileNoDupes() {

        PrintStream out = createFile("tall200000-uniques.txt");

        HashSet<String> randoms = new HashSet<>();
        for (int i = 0; i < 200000; i++) {
            randoms.add("" + i);
        }
        Iterator<String> it = randoms.iterator();
        while (it.hasNext()) {
            out.printf("%s\n", it.next());
        }
        out.close();
    }

    // Opposite from above: all 200000 words are the same string
    // i.e. one unique with 199999 corresponding dupes.
    private static void createFileOnlyDupes() {
        PrintStream out = createFile("tall200000-dupes.txt");
        for (int i = 0; i < 200000; i++) {
            out.printf("100000\n");
        }
        out.close();
    }

}
