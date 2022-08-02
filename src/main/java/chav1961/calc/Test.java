package chav1961.calc;

import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final AtomicInteger	ai = new AtomicInteger();
		final PrimeNumber 	primes = new PrimeNumber(1, 1000, 100, ai);
		
		System.err.println("Started...");
		long	start = System.currentTimeMillis();
		final ForkJoinPool pool = ForkJoinPool.commonPool();
		pool.invoke(primes);
		pool.shutdown();		
		System.err.println("Completed ["+ai+"], duration="+(System.currentTimeMillis()-start));
		
		ai.set(0);
		System.err.println("Started...");
		start = System.currentTimeMillis();
		final int parallelism = ForkJoinPool.getCommonPoolParallelism();
		ForkJoinPool stealer = (ForkJoinPool) Executors.newWorkStealingPool(parallelism);
		stealer.invoke(primes);
		stealer.shutdown();		
		System.err.println("Completed ["+ai+"], duration="+(System.currentTimeMillis()-start));
	}

}
