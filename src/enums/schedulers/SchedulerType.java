package enums.schedulers;

public enum SchedulerType {
	DP_SINGLE_THREAD_SCHEDULER,
	DP_FIXED_SCHEDULER,
	DP_SEDA_SCHEDULER,
	DP_SEDA_THROUGHPUT_SCHEDULER,
	SYSTEM_FIXED_SCHEDULER,
	SYSTEM_CACHED_POOL_SCHEDULER,
	FORK_JOIN_POOL_SCHEDULER;
}