package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import schedulers.DPFstpScheduler;
import schedulers.DPSedaScheduler;

public class Main {
	public static int NUM_ITEMS = 10_000;
	public static int NUM_THREADS = 10;
	public static int SINGLE_THREAD_SCHEDULER = 0;
	public static int FIXED_SCHEDULER = 1;
	public static int SEDA_SCHEDULER = 2;
	public static int SYSTEM_FIXED_SCHEDULER = 3;
	public static int SYSTEM_CACHED_POOL_SCHEDULER = 4;
	public static int FORK_JOIN_POOL_SCHEDULER = 5;
	
	// Built-in fixed thread pool scheduler
	private static Executor primeFixedPoolScheduler = Executors.newFixedThreadPool(NUM_THREADS);
	private static Executor sleepFixedPoolScheduler = Executors.newFixedThreadPool(NUM_THREADS);
	private static Executor printFixedPoolScheduler = Executors.newFixedThreadPool(NUM_THREADS);
	
	// Built-in cached thread pool scheduler
	private static Executor primeCachedPoolScheduler = Executors.newCachedThreadPool();
	private static Executor sleepCachedPoolScheduler = Executors.newCachedThreadPool();
	private static Executor printCachedPoolScheduler = Executors.newCachedThreadPool();
	
	// Built-in fork join pool scheduler
	private static Executor primeForkJoinPoolScheduler = ForkJoinPool.commonPool();
	private static Executor sleepForkJoinPoolScheduler = ForkJoinPool.commonPool();
	private static Executor printForkJoinPoolScheduler = ForkJoinPool.commonPool();
	
	// DP fixed thread pool scheduler
	private static Executor primeFstpScheduler = new DPFstpScheduler(NUM_THREADS);
	private static Executor sleepFstpScheduler = new DPFstpScheduler(NUM_THREADS);
	private static Executor printFstpScheduler = new DPFstpScheduler(NUM_THREADS);
	
	// DP SEDA-design thread pool scheduler
	private static Executor primeSedaScheduler = new DPSedaScheduler();
	private static Executor sleepSedaScheduler = new DPSedaScheduler();
	private static Executor printSedaScheduler = new DPSedaScheduler();
	
	private static List<CompletableFuture<Void>> futures = new ArrayList<CompletableFuture<Void>>();
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		// Step 1: Implement a simple, multi-stage workload which simulates a combination of CPU-intensive and I/O-intensive tasks
		
		// Step 2: Implement a thread pool scheduler which can execute the workload
		
		// Step 3: Implement a Simple Event-Driven Architecture (SEDA) scheduler which can execute the workload
		
		// Step 4: Execute the workload on each of the above schedulers, as well as your platform’s default scheduler, and perform a performance analysis of all three schedulers
		
		// Step 5: Perform a comparative analysis of using each of the above schedulers considering performance, maintainability, etc.
		
		System.out.println("Run started");
		run(FIXED_SCHEDULER);
		getFuture();
	}
	
	private static void run(int scheduler) {
		
		switch (scheduler) {
		case 0:
			for (int n = 1; n <= NUM_ITEMS; ++n) {
				int p = calculateNthPrime(n);
				sleep(10);
				printToConsoleln(createPrimeOutputString(n, p));
			}
		case 1:
			for (int i = 1; i <= NUM_ITEMS; ++i) {
				final int n = i;
				futures.add(CompletableFuture.supplyAsync(() -> calculateNthPrime(n), primeFstpScheduler)
						.thenApplyAsync((Integer p) -> { sleep(10); return p; }, sleepFstpScheduler)
						.thenAcceptAsync((Integer p) -> printToConsoleln(createPrimeOutputString(n, p)), printFstpScheduler));
			}
			break;
		case 2:
			for (int i = 1; i <= NUM_ITEMS; ++i) {
				final int n = i;
				futures.add(CompletableFuture.supplyAsync(() -> calculateNthPrime(n), primeSedaScheduler)
						.thenApplyAsync((Integer p) -> { sleep(10); return p; }, sleepSedaScheduler)
						.thenAcceptAsync((Integer p) -> printToConsoleln(createPrimeOutputString(n, p)), printSedaScheduler));
			}
			break;
		case 3:
			for (int i = 1; i <= NUM_ITEMS; ++i) {
				final int n = i;
				futures.add(CompletableFuture.supplyAsync(() -> calculateNthPrime(n), primeFixedPoolScheduler)
						.thenApplyAsync((Integer p) -> { sleep(10); return p; }, sleepFixedPoolScheduler)
						.thenAcceptAsync((Integer p) -> printToConsoleln(createPrimeOutputString(n, p)), printFixedPoolScheduler));
			}
			break;
		case 4:
			for (int i = 1; i <= NUM_ITEMS; ++i) {
				final int n = i;
				futures.add(CompletableFuture.supplyAsync(() -> calculateNthPrime(n), primeCachedPoolScheduler)
						.thenApplyAsync((Integer p) -> { sleep(10); return p; }, sleepCachedPoolScheduler)
						.thenAcceptAsync((Integer p) -> printToConsoleln(createPrimeOutputString(n, p)), printCachedPoolScheduler));
			}
			break;
		case 5:
			for (int i = 1; i <= NUM_ITEMS; ++i) {
				final int n = i;
				futures.add(CompletableFuture.supplyAsync(() -> calculateNthPrime(n), primeForkJoinPoolScheduler)
						.thenApplyAsync((Integer p) -> { sleep(10); return p; }, sleepForkJoinPoolScheduler)
						.thenAcceptAsync((Integer p) -> printToConsoleln(createPrimeOutputString(n, p)), printForkJoinPoolScheduler));
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
	
	private static String createPrimeOutputString(int n, int p) {
		return "The "+printNthSuffixFor(n)+" prime is "+p;
	}

}