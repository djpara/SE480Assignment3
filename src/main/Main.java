package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import enums.schedulers.SchedulerType;
import schedulers.DPFstpScheduler;
import schedulers.DPSedaScheduler;

public class Main {
	
	public static int NUM_ITEMS = 5_000;
	public static int NUM_THREADS = 50;
	
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
	
	private static long startTime	= (long) 0;
	private static long endTime   	= (long) 0;;
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		startTime = System.currentTimeMillis();
		run(SchedulerType.SYSTEM_CACHED_POOL_SCHEDULER);
		getFuture();
	}
	
	/**
	 * Runs the schedules
	 * @param st - Scheduler type. 
	 */
	private static void run(SchedulerType st) {
		
		switch (st) {
		case DP_SINGLE_THREAD_SCHEDULER:
			for (int n = 1; n <= NUM_ITEMS; ++n) {
				int p = calculateNthPrime(n);
				sleep(10);
				printToConsoleln(createPrimeOutputString(n, p));
			}
		case DP_FIXED_SCHEDULER:
			for (int i = 1; i <= NUM_ITEMS; ++i) {
				final int n = i;
				futures.add(CompletableFuture.supplyAsync(() -> calculateNthPrime(n), primeFstpScheduler)
						.thenApplyAsync((Integer p) -> { sleep(10); return p; }, sleepFstpScheduler)
						.thenAcceptAsync((Integer p) -> printToConsoleln(createPrimeOutputString(n, p)), printFstpScheduler));
			}
			break;
		case DP_SEDA_SCHEDULER:
			for (int i = 1; i <= NUM_ITEMS; ++i) {
				final int n = i;
				futures.add(CompletableFuture.supplyAsync(() -> calculateNthPrime(n), primeSedaScheduler)
						.thenApplyAsync((Integer p) -> { sleep(10); return p; }, sleepSedaScheduler)
						.thenAcceptAsync((Integer p) -> printToConsoleln(createPrimeOutputString(n, p)), printSedaScheduler));
			}
			break;
		case SYSTEM_FIXED_SCHEDULER:
			for (int i = 1; i <= NUM_ITEMS; ++i) {
				final int n = i;
				futures.add(CompletableFuture.supplyAsync(() -> calculateNthPrime(n), primeFixedPoolScheduler)
						.thenApplyAsync((Integer p) -> { sleep(10); return p; }, sleepFixedPoolScheduler)
						.thenAcceptAsync((Integer p) -> printToConsoleln(createPrimeOutputString(n, p)), printFixedPoolScheduler));
			}
			break;
		case SYSTEM_CACHED_POOL_SCHEDULER:
			for (int i = 1; i <= NUM_ITEMS; ++i) {
				final int n = i;
				futures.add(CompletableFuture.supplyAsync(() -> calculateNthPrime(n), primeCachedPoolScheduler)
						.thenApplyAsync((Integer p) -> { sleep(10); return p; }, sleepCachedPoolScheduler)
						.thenAcceptAsync((Integer p) -> printToConsoleln(createPrimeOutputString(n, p)), printCachedPoolScheduler));
			}
			break;
		case FORK_JOIN_POOL_SCHEDULER:
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
	
	/**
	 * Ensures that the entire job is executed to completion
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private static void getFuture() throws InterruptedException, ExecutionException {
		for (CompletableFuture<Void> future : futures) {
		    // Ensures that the entire job is executed to completion
			future.get();
		}
		endTime = System.currentTimeMillis();
		System.out.println("All threads executed @ "+calculateRuntime()+" ms!");
		System.exit(0);
	}
	
	/**
	 * Standard print statement
	 * @param s - string
	 */
	private static void printToConsoleln(String s) {
		System.out.println(s);
	}
	
	/**
	 * Simulates I/O intensive process
	 * @param n - number representing milliseconds
	 */
	private static void sleep(int n) {
		try {
			Thread.sleep(n);
		} catch (InterruptedException e) {
			System.out.println("Thread interrupted");
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculates and returns the Nth prime number
	 * @param n - number
	 * @return Nth prime number
	 */
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

	/**
	 * Appends suffix to n to represent Nth number
	 * @param n - number
	 * @return string
	 */
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
	
	/**
	 * Builds output string
	 * @param n - Nth number
	 * @param p - Nth prime number 
	 * @return string
	 */
	private static String createPrimeOutputString(int n, int p) {
		return "The "+printNthSuffixFor(n)+" prime is "+p;
	}
	
	/**
	 * Calculates and return application runtime in milliseconds 
	 */
	private static long calculateRuntime() {
		return endTime - startTime;
	}
}