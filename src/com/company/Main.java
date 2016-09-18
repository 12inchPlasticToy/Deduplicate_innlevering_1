package com.company;


public class Main {

    public static void main(String[] args) {
	// write your code here
      /*  Utils.Sampler sampler = new Utils.Sampler("/root/IdeaProjects/Deduplicate/src/com/company/shakespeare.txt");
        Utils.Output out = new Utils.Output("arrayListDedup.csv");
        for(int i = 0; i < 100; i++){
            int size = i*10;
            String[] sample = sampler.get(size);
            Utils.Stopwatch timer = new Utils.Stopwatch();
            Dedup.arrayListDedup(sample);
            out.addMeasurement(size,timer.elapsedTime());
        }*/
        String shakespearePath = "/root/IdeaProjects/Deduplicate/src/com/company/shakespeare.txt";
        DedupTest test = new DedupTest(shakespearePath);
        test.run();
    }
}
