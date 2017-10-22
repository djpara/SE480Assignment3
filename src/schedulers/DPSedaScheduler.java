package schedulers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;

public class DPSedaScheduler implements Executor {
	
	private int MAX_THREAD_POOL_SIZE = 50;

	private ArrayList<Thread> threadPool;
	private Queue<Runnable> commandQueue;
	
	public DPSedaScheduler() {
		threadPool = new ArrayList<Thread>();
		commandQueue = new LinkedList<Runnable>();
	
		threadPool.add(new DPThread());
	}
	
	/**
	 * Internal class allows the thread pool to create its own threads.
	 * @author davidpara
	 *
	 */
	public class DPThread extends Thread {
		
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
		}
		return commandQueue.poll();
	}

}