package main;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.CompletableFuture;

public class Main {
	public static int NUM_ITEMS = 10_000;
	
	private static Executor primeScheduler;
	private static Executor sleepScheduler;
	private static Executor printScheduler;
	
	public static void main(String[] args) {
		
		// Step 1: Implement a simple, multi-stage workload which simulates a combination of CPU-intensive and I/O-intensive tasks
		
		// Step 2: Implement a thread pool scheduler which can execute the workload
		
		// Step 3: Implement a Simple Event-Driven Architecture (SEDA) scheduler which can execute the workload
		
		// Step 4: Execute the workload on each of the above schedulers, as well as your platform’s default scheduler, and perform a performance analysis of all three schedulers
		
		// Step 5: Perform a comparative analysis of using each of the above schedulers considering performance, maintainability, etc.
		
		run();		
	}
	
	private static void run() {
		
		List<CompletableFuture<Void>> futures = new ArrayList<CompletableFuture<Void>>();
		
		for (int i = 0; i < NUM_ITEMS; ++i) {
			final int n = i;
			futures.add(CompletableFuture.supplyAsync(() -> calculateNthPrime(n), primeScheduler)
					.thenApplyAsync((Long p) -> { try { Thread.sleep(10); } catch (InterruptedException e) {} return p; }, sleepScheduler)
					.thenAcceptAsync((Long p) -> System.out.println(p), printScheduler));
		}
		
		try {
			for (int n = 1; n <= 10_000; ++n) {
				int prime = calculateNthPrime(n);

				Thread.sleep(10);
				System.out.println("The "+printNthSuffixFor(n)+" prime is "+prime);
			}
		} catch (InterruptedException e) {
			System.out.println("Thread Interrupted");
			e.printStackTrace();
		}
	}
	
	private static int calculateNthPrime(int n) throws InterruptedException {
		int primeCount = 2;
		int lastPrime = 3;
		
		if (n == 0) {
			return 0;
		}
		
		if (n == 1) {
			return 2;
		}
		
		int counter = 4;
	
		while (primeCount < n) {
			
			if(counter % 2 != 0 && counter % 3 != 0) {
				int temp = 4;
				while(temp*temp <= counter) {
					if (counter % temp == 0) {
						break;
					}
					temp++;
				}
				if (temp*temp > counter) {
					lastPrime = counter;
					primeCount++;
				}
			}
			
			counter++;
			
			if (primeCount == n) {
				return lastPrime;
			}
		}
		
		return lastPrime;
	}

	private static String printNthSuffixFor(int n) {
		int onesDigit = n % 10;
		int tensDigit = n % 100;
		
		// Odd ball
		if (tensDigit > 10 && tensDigit < 14) {
			return n+"th";
		}
		
		switch (onesDigit) {
		case 1:
			return n+"st";
		case 2:
			return n+"nd";
		case 3:
			return n+"rd";
		default:
			return n+"th";
		}
	}

}
