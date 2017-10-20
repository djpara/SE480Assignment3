package schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class DPSedaScheduler implements Executor {

	List<Thread> threads = new ArrayList<Thread>();
	
	public DPSedaScheduler() {
		
	}
	
	@Override
	public void execute(Runnable command) {
		
	}

}
