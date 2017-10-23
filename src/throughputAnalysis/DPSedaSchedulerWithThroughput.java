package throughputAnalysis;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;

public class DPSedaSchedulerWithThroughput implements Executor {
	
	private int MAX_THREAD_POOL_SIZE = 50;

	private ArrayList<Thread> threadPool;
	private Queue<Runnable> commandQueue;
	
	// Throughput
	private Integer secondMarker		= 0;
	private Integer processedTasks	= 0;
	private Timer timer;
	
	ArrayList<Integer> secondsArray;
	ArrayList<Integer> tasksProcessedArray;
	ArrayList<Integer> numberOfThreadsArray;
	
	public DPSedaSchedulerWithThroughput() {
		threadPool		= new ArrayList<Thread>();
		commandQueue 	= new LinkedList<Runnable>();
	
		threadPool.add(new DPThread());
		
		secondsArray			= new ArrayList<Integer>();
		tasksProcessedArray	= new ArrayList<Integer>();
		numberOfThreadsArray	= new ArrayList<Integer>();
		
		timer = new Timer();
		timer.schedule(new RecordData(), 0, 1000);
	}
	
	/**
	 * Internal class allows the thread pool to create its own threads.
	 * @author davidpara
	 *
	 */
	private class DPThread extends Thread {
		
		private boolean execute = false;
		
		// Start on initialization
		public DPThread() {
			super();
			this.execute = true;
			this.start();
		}
		
		/**
		 * Executes awaiting commands.
		 */
		@Override
		public void run() {
			try {
				while (this.execute) {
					Runnable command;
					while ((command = dequeue()) != null) {
						command.run();
						addToProcessedTasks();
					}
					Thread.sleep(1);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public void kill() {
			this.execute = false;
		}
	}
	
	/**
	 * Internal class allows for throughput and latency calculation.
	 * @author davidpara
	 *
	 */
	private class RecordData extends TimerTask {
	    public void run() {
	    		secondsArray.add(secondMarker);
	    		tasksProcessedArray.add(processedTasks);
	    		numberOfThreadsArray.add(threadPool.size());
	    		clearProcessedTasks();
	    		++secondMarker;
	    }
	}
	
	/**
	 * Creates and returns a string representation of the throughput analysis
	 * @return String
	 */
	public String getDataString() {
		StringBuilder s = new StringBuilder();
		
		for (int i = 0; i < secondsArray.size(); ++i) {
			s.append("Second Marker: "+secondsArray.get(i)+"; "
					+ "Tasks Processed: "+tasksProcessedArray.get(i)+"; "
					+ "Number of Active Threads: "+numberOfThreadsArray.get(i)+"\n");
		}
		
		return s.toString();
	}
	
	/**
	 * Schedules command to the command queue
	 */
	@Override
	public void execute(Runnable command) {
		enqueue(command);
	}
	
	/**
	 * Synchronized command scheduling with dynamic thread creation.
	 * @param command - Runnable command.
	 */
	private synchronized void enqueue(Runnable command) {
		if (commandQueue.size() > 50 
				&& threadPool.size() < MAX_THREAD_POOL_SIZE) {
			threadPool.add(new DPThread());
//			System.out.println("Thread pool size = "+threadPool.size());
		}
		commandQueue.add(command);
	}
	
	/**
	 * Synchronized command execution with dynamic thread destruction.
	 * @return Runnable command.
	 */
	private synchronized Runnable dequeue() {
		if (commandQueue.size() < 50 
				&& threadPool.size() > 1) {
			DPThread t = ((DPThread) threadPool.get(0));
			threadPool.remove(t);
			t.kill();
//			System.out.println("Thread pool size = "+threadPool.size());
		}
		return commandQueue.poll();
	}
	
	/**
	 * Synchronize processed tasks count
	 */
	private synchronized void addToProcessedTasks() {
		++processedTasks;
	}
	
	/**
	 * Synchronize clearing processed tasks count
	 */
	private synchronized void clearProcessedTasks() {
		processedTasks = 0;
	}
}