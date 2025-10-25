/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package objectiveethics.test;

import java.text.DecimalFormat;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;
import objectiveethics.values.logic.method.information.GZIPInfo;
import objectiveethics.values.structure.Value;

public class BinaryTestParallel {

    public static void main(String[] args) {
        DecimalFormat format = new DecimalFormat("0.0000");
        Value info = new GZIPInfo();

        int n = 100000000;
        double log2 = Math.log(2);

        // Use 80% of available processors
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int poolSize = (int) (availableProcessors * 0.8);  // 80% of CPU
        
        ForkJoinPool pool = new ForkJoinPool(poolSize);
        
        long start = System.currentTimeMillis();
        InfoTask task = new InfoTask(1, n, info, log2, n, start);
        try {
            InfoResult result = pool.invoke(task);
            pool.shutdown();

            System.out.println("logInfo: " + format.format(result.logInfo / n));
            System.out.println("binaryInfo: " + format.format(result.binaryInfo / n));
            System.out.println("ratio: " + format.format(result.binaryInfo / result.logInfo));

        } catch (Exception e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();
        System.out.println("Execution Time: " + (end - start) + " ms");
    }

    // Helper class to store results
    static class InfoResult {
        double logInfo;
        double binaryInfo;

        public InfoResult(double logInfo, double binaryInfo) {
            this.logInfo = logInfo;
            this.binaryInfo = binaryInfo;
        }

        public InfoResult add(InfoResult other) {
            this.logInfo += other.logInfo;
            this.binaryInfo += other.binaryInfo;
            return this;
        }
    }

    // RecursiveTask for parallel execution
    static class InfoTask extends RecursiveTask<InfoResult> {
        private static final int THRESHOLD = 10000; // Define a reasonable threshold for splitting
        private int start, end;
        private Value info;
        private double log2;
        private int totalTasks;
        private long globalStartTime;

        public InfoTask(int start, int end, Value info, double log2, int totalTasks, long globalStartTime) {
            this.start = start;
            this.end = end;
            this.info = info;
            this.log2 = log2;
            this.totalTasks = totalTasks;
            this.globalStartTime = globalStartTime;
        }

        @Override
        protected InfoResult compute() {
            if ((end - start) < THRESHOLD) {
                return computeDirectly();
            } else {
                int middle = (start + end) / 2;
                InfoTask leftTask = new InfoTask(start, middle, info, log2, totalTasks, globalStartTime);
                InfoTask rightTask = new InfoTask(middle, end, info, log2, totalTasks, globalStartTime);

                leftTask.fork(); // asynchronously execute the left task
                InfoResult rightResult = rightTask.compute();
                InfoResult leftResult = leftTask.join(); // wait for the left task to complete

                return leftResult.add(rightResult);
            }
        }

        private InfoResult computeDirectly() {
            double logInfo = 0;
            double binaryInfo = 0;

            for (int x = start; x < end; x++) {
                String binary = Integer.toBinaryString(x);

                logInfo += Math.log(x) / log2;
                binaryInfo += info.value(binary);
            }

            return new InfoResult(logInfo, binaryInfo);
        }
    }
}


