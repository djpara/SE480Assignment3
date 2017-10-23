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
import throughputAnalysis.DPSedaSchedulerWithThroughput;

public class Main {
	
	public static int NUM_ITEMS = 10_000;
	public static int NUM_THREADS = 50;
	
	private static SchedulerType SCHEDULER_TYPE = SchedulerType.DP_SEDA_THROUGHPUT_SCHEDULER;
	
	private static Executor primeScheduler;
	private static Executor sleepScheduler;
	private static Executor printScheduler;
	
	private static List<CompletableFuture<Void>> futures = new ArrayList<CompletableFuture<Void>>();
	
	private static long startTime	= (long) 0;
	private static long endTime   	= (long) 0;
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		startTime = System.currentTimeMillis();
		run(SCHEDULER_TYPE);
		getFuture();
	}
	
	/**
	 * Runs the schedules
	 * @param st - Scheduler type. 
	 */
	private static void run(SchedulerType st) {
		
		switch (st) {
		// DP single thread
		case DP_SINGLE_THREAD_SCHEDULER:
			for (int n = 1; n <= NUM_ITEMS; ++n) {
				int p = calculateNthPrime(n);
				sleep(10);
				printToConsoleln(createPrimeOutputString(n, p));
			}
			break;
		// DP fixed thread pool scheduler
		case DP_FIXED_SCHEDULER:
			primeScheduler = new DPFstpScheduler(NUM_THREADS);
			sleepScheduler = new DPFstpScheduler(NUM_THREADS);
			printScheduler = new DPFstpScheduler(NUM_THREADS);
			runCompletableFutures();
			break;
		// DP SEDA-design thread pool scheduler
		case DP_SEDA_SCHEDULER:
			primeScheduler = new DPSedaScheduler();
			sleepScheduler = new DPSedaScheduler();
			printScheduler = new DPSedaScheduler();
			runCompletableFutures();
			break;
		// DP SEDA-design thread pool with throughput calculator
		case DP_SEDA_THROUGHPUT_SCHEDULER:
			primeScheduler = new DPSedaSchedulerWithThroughput();
			sleepScheduler = new DPSedaSchedulerWithThroughput();
			printScheduler = new DPSedaSchedulerWithThroughput();
			runCompletableFutures();
			break;
		// Built-in fixed thread pool scheduler
		case SYSTEM_FIXED_SCHEDULER:
			primeScheduler = Executors.newFixedThreadPool(NUM_THREADS);
			sleepScheduler = Executors.newFixedThreadPool(NUM_THREADS);
			printScheduler = Executors.newFixedThreadPool(NUM_THREADS);
			runCompletableFutures();
			break;
		// Built-in cached thread pool scheduler
		case SYSTEM_CACHED_POOL_SCHEDULER:
			primeScheduler = Executors.newCachedThreadPool();
			sleepScheduler = Executors.newCachedThreadPool();
			printScheduler = Executors.newCachedThreadPool();
			runCompletableFutures();
			break;
		// Built-in fork join pool scheduler
		case FORK_JOIN_POOL_SCHEDULER:
			primeScheduler = ForkJoinPool.commonPool();
			sleepScheduler = ForkJoinPool.commonPool();
			printScheduler = ForkJoinPool.commonPool();
			runCompletableFutures();
			break;
		default:
			break;	
		}
	}
	
	private static void runCompletableFutures() {
		for (int i = 1; i <= NUM_ITEMS; ++i) {
			final int n = i;
			futures.add(CompletableFuture.supplyAsync(() -> calculateNthPrime(n), primeScheduler)
					.thenApplyAsync((Integer p) -> { sleep(10); return p; }, sleepScheduler)
					.thenAcceptAsync((Integer p) -> printToConsoleln(createPrimeOutputString(n, p)), printScheduler));
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
		
		if (SCHEDULER_TYPE == SchedulerType.DP_SEDA_THROUGHPUT_SCHEDULER) {
			System.out.println(((DPSedaSchedulerWithThroughput) primeScheduler).getDataString());
		}
		
		System.exit(0);
	}
	
	/**
	 * Standard print statement
	 * @param s - string
	 */
	private static void printToConsoleln(String s) {
//		System.out.println(s);
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