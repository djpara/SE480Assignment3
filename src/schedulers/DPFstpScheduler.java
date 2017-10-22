package schedulers;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;

public class DPFstpScheduler implements Executor {

	private DPThread[] threadPool;
	private Queue<Runnable> commandQueue;
	
	public DPFstpScheduler(int numberOfThreads) {
		threadPool = new DPThread[numberOfThreads];
		commandQueue = new LinkedList<Runnable>();
	
		for (int i = 0; i < numberOfThreads; ++i) {
			threadPool[i] = new DPThread();
			threadPool[i].start();
		}
	}
	
	/**
	 * Internal class allows the thread pool to create its own threads.
	 * @author davidpara
	 *
	 */
	public class DPThread extends Thread {
		
		private boolean execute = false;
		
		public DPThread() {
			super();
			this.execute = true;
		}
		
		/**
		 * Executes awaiting commands.
		 */
		@Override
		public void run() {
			try {
				while (this.execute || !commandQueue.isEmpty()) {
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
	}
	
	/**
	 * Schedules command to the command queue
	 */
	@Override
	public void execute(Runnable command) {
		enqueue(command);
	}
	
	/**
	 * Synchronized command scheduling.
	 * @param command - Runnable command.
	 */
	private synchronized void enqueue(Runnable command) {
		commandQueue.add(command);
	}
	
	/**
	 * Synchronized command execution.
	 * @return Runnable command.
	 */
	private synchronized Runnable dequeue() {
		return commandQueue.poll();
	}
}