import java.util.*;

/**
 * Interface for deduplication algorithms, i.e. classes that implement the <tt>dedup</tt>-method.
 * 
 * This interface also provide some static methods:
 * <ul>
 * <li> Methods for deduplication of <tt>String[]</tt>-objects.</li>
 * <li> Factory methods that produce <tt>Dedup</tt>-implementations of different types</li>
 * </ul>
 */
public interface Dedup {
    /*
     * Definition of interface
     */
    /**
     * Deduplication method that use a <tt>HashSet</tt> to
     * remove duplicates from a list of words.
     */
    static String[] hashSetDedup(String[] strs) {
        return newHashSetDedup().dedup(strs);
    }


    
    /*
     * Static deduplication methods
     */

    /**
     * Deduplication method that use a <tt>TreeSet</tt> to
     * remove duplicates from a list of words.
     */
    static String[] treeSetDedup(String[] strs) {
        return newTreeSetDedup().dedup(strs);
    }
    
    /**
     * Deduplication method that use an <tt>ArrayList</tt> to
     * remove duplicates from a list of words.
     */
    static String[] arrayListDedup(String[] strs) {
        return newArrayListDedup().dedup(strs);
    }
    
    /**
     * Deduplication method that use a brute force method to
     * remove duplicates from a list of words.
     */
    static String[] basicDedup(String[] strs) {
        return newBasicDedup().dedup(strs);
    }
    
    /**
     * Deduplication method that prepare the data by sorting the
     * words before removing the duplicates.
     */
    static String[] sortDedup(String[] strs) {
        return newSortDedup().dedup(strs);
    }

    /**
     * Returns a new <tt>Dedup</tt>-object.
     *
     * @see hashSetDedup
     */
    static Dedup newHashSetDedup() {
        return new HashSetDedup();
    }

    
    /*
     * Static factory methods
     */
    
    /**
     * Returns a new <tt>Dedup</tt>-object.
     * @see treeSetDedup
     */
    static Dedup newTreeSetDedup() {
        return new TreeSetDedup();}
    
    /**
     * Returns a new <tt>Dedup</tt>-object.
     * @see arrayListDedup
     */
    static Dedup newArrayListDedup() {
        return new ArrayListDedup();}
    
    /**
     * Returns a new <tt>Dedup</tt>-object.
     * @see basicDedup
     */
    static Dedup newBasicDedup() {
        return new BasicDedup();}
    
    /**
     * Returns a new <tt>Dedup</tt>-object.
     * @see sortedDedup
     */
    static Dedup newSortDedup() {
        return new SortDedup();
    }

    /**
     * Very crude unit testing. Tests the code simply
     * by running <tt>DedupTest.main</tt>
     *
     * @see DedupTest.main
     */
    static void main(String[] args) {
        DedupTest.main(args);}

    
    /*
     * Unit testing
     */

    /**
     * Returns a <tt>String[]</tt>-object that contains the same strings as the
     * <tt>String[]</tt>-object<tt>words</tt>, but with the duplicates removed.
     */
    String[] dedup(String[] words);
}


/**
 * Example: input array {"så", "gjør", "vi", "så"}
 * 1) is duplicate? -> i = 0, len = count = 0, isDuplicate return false
 *      >> strings[0] (så) = så, count = 1;
 * 2) is duplicate? -> isDuplicate loop runs once (while i < 1), compares gjør to så.
 *      >> strings[1] (gjør) = gjør, count = 2;
 * 3) is duplicate? -> isDuplicate loop runs twice (i < 2), compares vi to så and to gjør,
 *      >> string[2] (vi) = vi, count 3;
 * 4) is duplicate? -> isDuplicate returns true;
 *
 * Basic dedup returns the first "count" elements of the input array -> {"så, gjør, vi"}
 *
 * Comments:
 *      in the case of early duplicates or few duplicates, all first instances of words following the first duplicate
 *      will have to be copied
 *
 *
 */
class BasicDedup implements Dedup {
    public String[] dedup(String[] strings){
        int count = 0; 
        for(String current: strings){
            if(isDuplicate(strings,count,current))
                continue;
            else
                strings[count++] = current;
        }
        return Arrays.copyOf(strings,count);
    }

    private boolean isDuplicate(Object[] objects, int len, Object obj){
        for(int i = 0; i < len; i++)
            if(obj.equals(objects[i]))
                return true;
        return false;
    }

}




class SortDedup implements Dedup{
    public String[] dedup(String[] strings){
        Arrays.sort(strings);
        int count = 1;
        for(int i = 1; i < strings.length;i++){
            if(strings[i].equals(strings[i-1]))
                continue;
            else 
                strings[count++] = strings[i];
        }
        return Arrays.copyOf(strings,count);
    }
}


abstract class CollectionDedup implements Dedup {
    protected Collection<String> coll;
    
    CollectionDedup(Collection<String> theCollection){coll = theCollection;}
    
    public String[] dedup(String[] strings){
        coll.clear();
        for(String s : strings)add(s);
        
        String[] output = new String[coll.size()];
        
        int i = 0;
        for(String str: coll) output[i++] = str;
        return output;
    }
    protected abstract boolean add(String s);
}

class ListDedup extends CollectionDedup {
    ListDedup(List<String> theList){super(theList);}
    protected boolean add(String s){return coll.contains(s) || coll.add(s);}
}


class SetDedup extends CollectionDedup {
    SetDedup(Set<String> theSet){super(theSet);}       
    protected boolean add(String s){return coll.add(s);}
}

class ArrayListDedup extends ListDedup {ArrayListDedup(){super(new ArrayList<String>());}}
class HashSetDedup extends SetDedup {HashSetDedup(){super(new HashSet<String>());}}
class TreeSetDedup extends SetDedup {
    TreeSetDedup(){
    super(new TreeSet<String>());
    }
}
