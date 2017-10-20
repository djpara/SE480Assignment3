package schedulers;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Executor;

public class DPFstpScheduler implements Executor {

	private int maxNumberOfThreads;
	private Thread[] threadPool;
	
	private Queue<Runnable> commandQueue;
	
	public DPFstpScheduler(int numberOfThreads) {
		maxNumberOfThreads = numberOfThreads;
		threadPool = new Thread[numberOfThreads];
		commandQueue = new PriorityQueue<Runnable>();
	}
	
	@Override
	public void execute(Runnable command) {
		commandQueue.add(command);
		findAvailableThread();
	}
	
	private void findAvailableThread() {
		while (!commandQueue.isEmpty()) {
			for (int i = 0; i < maxNumberOfThreads; ++i) {
				if (!threadPool[i].isAlive() 
						|| threadPool[i] != null) {
					threadPool[i] = new Thread(commandQueue.remove());
					threadPool[i].start();
				}
			}
		}
	}

}
