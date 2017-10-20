package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import schedulers.DPFstpScheduler;

public class Main {
	public static int NUM_ITEMS = 10_000;
	public static int FIXED_SCHEDULER = 1;
	public static int SEDA_SCHEDULER = 2;
	
	private static Executor primeFstpScheduler = new DPFstpScheduler(10);
	private static Executor sleepFstpScheduler = new DPFstpScheduler(10);
	private static Executor printFstpScheduler = new DPFstpScheduler(10);
	
	private static Executor primeSedaScheduler = new DPFstpScheduler(10);
	private static Executor sleepSedaScheduler = new DPFstpScheduler(10);
	private static Executor printSedaScheduler = new DPFstpScheduler(10);
	
	private static List<CompletableFuture<Void>> futures = new ArrayList<CompletableFuture<Void>>();
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		// Step 1: Implement a simple, multi-stage workload which simulates a combination of CPU-intensive and I/O-intensive tasks
		
		// Step 2: Implement a thread pool scheduler which can execute the workload
		
		// Step 3: Implement a Simple Event-Driven Architecture (SEDA) scheduler which can execute the workload
		
		// Step 4: Execute the workload on each of the above schedulers, as well as your platform’s default scheduler, and perform a performance analysis of all three schedulers
		
		// Step 5: Perform a comparative analysis of using each of the above schedulers considering performance, maintainability, etc.
		
		run(FIXED_SCHEDULER);
		getFuture();
	}
	
	private static void run(int scheduler) {
		
		switch (scheduler) {
		case 1:
			for (int i = 1; i <= 10_000; ++i) {
				final int n = i;
				futures.add(CompletableFuture.supplyAsync(() -> calculateNthPrime(n), primeFstpScheduler)
						.thenApplyAsync((Integer p) -> { sleep(10); return p; }, sleepFstpScheduler)
						.thenAcceptAsync((Integer p) -> printToConsoleln("The "+printNthSuffixFor(n)+
								" prime is "+p), printFstpScheduler));
			}
			break;
		case 2:
			for (int i = 1; i <= 10_000; ++i) {
				final int n = i;
				futures.add(CompletableFuture.supplyAsync(() -> calculateNthPrime(n), primeSedaScheduler)
						.thenApplyAsync((Integer p) -> { sleep(10); return p; }, sleepSedaScheduler)
						.thenAcceptAsync((Integer p) -> printToConsoleln("The "+printNthSuffixFor(n)+
								" prime is "+p), printSedaScheduler));
			}
			break;
		default:
			break;	
		}
	}
	
	private static void getFuture() throws InterruptedException, ExecutionException {
		for (CompletableFuture<Void> future : futures) {
		    // Ensures that the entire job is executed to completion
			future.get();
		}
		System.out.println("All threads executed!");
	}
	
	private static void printToConsoleln(String s) {
		System.out.println(s);
	}
	
	private static void sleep(int n) {
		try {
			Thread.sleep(n);
		} catch (InterruptedException e) {
			System.out.println("Thread interrupted");
			e.printStackTrace();
		}
	}
	
	private static int calculateNthPrime(int n) {
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
