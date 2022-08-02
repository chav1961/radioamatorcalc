package chav1961.calc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

public class PrimeNumber extends RecursiveAction {
	private static final long serialVersionUID = 1L;
	
	private int lowerBound;
    private int upperBound;
    private int granularity;
    private AtomicInteger noOfPrimeNumbers;

    PrimeNumber(int lowerBound, int upperBound, int granularity, AtomicInteger noOfPrimeNumbers) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.granularity = granularity;
        this.noOfPrimeNumbers = noOfPrimeNumbers;
    }

    // other constructors and methods

    private List<PrimeNumber> subTasks() {
        List<PrimeNumber> subTasks = new ArrayList<>();

        for (int i = 1; i <= this.upperBound / granularity; i++) {
            int upper = i * granularity;
            int lower = (upper - granularity) + 1;
            subTasks.add(new PrimeNumber(lower, upper, granularity, noOfPrimeNumbers));
        }
        return subTasks;
    }

    @Override
    protected void compute() {
    	ForkJoinTask.invokeAll(subTasks());
    }

    void findPrimeNumbers() {
        for (int num = lowerBound; num <= upperBound; num++) {
            if (isPrime(num)) {
                noOfPrimeNumbers.getAndIncrement();
            }
        }
    }

    private boolean isPrime(final int num) {
    	for (int index = 2, maxIndex = (int)Math.sqrt(num); index < maxIndex; index++) {
    		if (num % index != 0) {
    			return false;
    		}
    	}
		return true;
	}

	public int noOfPrimeNumbers() {
        return noOfPrimeNumbers.intValue();
    }
}