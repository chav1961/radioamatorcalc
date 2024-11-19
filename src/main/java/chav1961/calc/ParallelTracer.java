package chav1961.calc;

import java.io.PrintStream;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ParallelTracer {
	private final ConcurrentLinkedQueue<String>	queue = new ConcurrentLinkedQueue<>();
	private final Thread		t = new Thread(()->printQueue());
	private final PrintStream	os;
	
	public ParallelTracer(final PrintStream os) {
		this.os = os;
		t.setDaemon(true);
		t.start();
	}
	
	public void print(final String data) {
		queue.add(data == null ? "null" : data);
	}

	private void printQueue() {
		for(;;) {
			final String s = queue.poll();
			
			if (s == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
			else {
				os.println(s);
			}
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		final ParallelTracer	pt = new ParallelTracer(System.err);
		
		pt.print("test");
		Thread.sleep(1000);
	}
}
