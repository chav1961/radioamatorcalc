package chav1961.calc.fb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class Test {
	public static final int[]	template = {1,2,3,4,5,6,7,8,9,10}; 
	public static final int[]	content = {1,2,3,4,5,6,7,8,9,10}; 
	public static final ExecutorService	ex = Executors.newCachedThreadPool();
	public static final ForkJoinPool	fjp = ForkJoinPool.commonPool();

	public static void main(String[] args) throws InterruptedException, ExecutionException, BrokenBarrierException {
		try{test1();
			test2();
			test3();
			test4();
		} finally {
			ex.shutdown();
			fjp.shutdown();
		}
	}
	
	static void test1() throws InterruptedException, ExecutionException {
		final List<Future<Void>>	result = new ArrayList<>();

		System.arraycopy(template, 0, content, 0, content.length);
		for(int index = 0; index < content.length; index++) {
			final int	current = index;
			
			result.add(ex.submit(()->{
				content[current] *= content[current]; 
				return null;
			}));
		}
		for(Future<Void> item : result) {
			item.get();
		}
	}

	static void test2() throws InterruptedException, ExecutionException {
		System.arraycopy(template, 0, content, 0, content.length);
		
		fjp.invoke(new Square(content, 0, content.length));
	}
	
	static class Square extends RecursiveTask<Void> {
		private static final long serialVersionUID = 1L;
		
		int[] 	data; 
	    int 	start, end; 
	  
	    Square(final int[] data, final int start, final int end) { 
	        this.data = data; 
	        this.start = start; 
	        this.end = end; 
	    } 
	  		
		@Override
		protected Void compute() {
			if ((end - start) == 1) { 
				data[start] *= data[start]; 
	        } 
	        else { 
	            final int 		middle = (start + end) / 2; 
	            final Square 	subtaskA = new Square(data, start, middle); 
	            final Square 	subtaskB = new Square(data, middle, end); 
	  
	            subtaskA.fork(); 
	            subtaskB.fork(); 
	            subtaskA.join();
	            subtaskB.join(); 
	        } 			
			return null;
		}
		
	}

	static void test3() throws InterruptedException, ExecutionException, BrokenBarrierException {
		final CyclicBarrier	cbBefore = new CyclicBarrier(content.length+1);
		final CyclicBarrier	cbAfter = new CyclicBarrier(content.length+1);
		
		for(int index = 0; index < content.length; index++) {
			makeSquare(content, index, ()->cbBefore.await(), ()->cbAfter.await());
		}
		System.arraycopy(template, 0, content, 0, content.length);
		cbBefore.await();
		cbBefore.reset();
		// TODO: ....
		cbAfter.await();
		cbBefore.reset();
	}

	static void test4() throws InterruptedException, ExecutionException, BrokenBarrierException {
		final List<CompletableFuture<Void>>	temp = new ArrayList<>();
		
		System.arraycopy(template, 0, content, 0, content.length);
		for(int index = 0; index < content.length; index++) {
			final int	current = index;
			
			temp.add(CompletableFuture.runAsync(()->{content[current] *= content[current];}));
		}
		CompletableFuture.allOf(temp.toArray(new CompletableFuture[temp.size()])).join();
	}
	
	
	static interface InterruptedCall {
		void run() throws InterruptedException, BrokenBarrierException;
	}
	
	static Thread makeSquare(final int[] content, final int index, final InterruptedCall before, final InterruptedCall after) {
		final Thread	t = new Thread(()->{
							try {
								before.run();
								content[index] *= content[index];
							} catch (InterruptedException | BrokenBarrierException e) {
							} finally {
								try {
									after.run();
								} catch (InterruptedException | BrokenBarrierException e) {
								}
							}
						});
		t.setDaemon(true);
		t.start();
		return t;
	}
	
}
