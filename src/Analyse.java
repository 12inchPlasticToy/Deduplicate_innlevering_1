import java.util.Arrays;

/**
 * This file contains a copy-pasted version of BasicDedup with the analysis
 * notations to find the theoretical runtime t(N).
 */

public class Analyse {

    class BasicDedup implements Dedup {

        public String[] dedup(String[] strings) { // definition of n -> strings
            int count = 0; // constant: A
            for (String current : strings) { // loop: n iterations
                if (isDuplicate(strings, count, current))  // inner loop: n iterations
                    continue;                           // (c.f. isduplicate)
                    // constant: A
                else
                    strings[count++] = current;
                // constant: A
            } // either of the A conditions will run once, so A * n

            return Arrays.copyOf(strings, count); // constant: B (depends on count?)
        }

        private boolean isDuplicate(Object[] objects, int len, Object obj) {
            // inner loop, runs count times.
            // worst case: count is at is maximum
            // count increases if isDuplicate returns false
            // isDuplicate returns false if obj doesn't equal another from n
            // worst case is therefore when no obj equals another for each obj check
            // i.e. no duplicates -> count++ n times -> count = n.
            for (int i = 0; i < len; i++)    // loop: i < n at worst
                if (obj.equals(objects[i]))  // constant : C
                    return true;
            return false;
        }
        /*
            A +
                n iteration outer
                A
                n iteration inner
                    C
                    worst case: inner loop runtime = n * C

           A + outer + B
           A + n(A + inner)) + B
           A + n(A + nC) + B
           A + nA + n^2C + B
           Therefore:
           T(N) = n^2C + nA + A + B
         */
    }
}
