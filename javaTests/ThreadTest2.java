/*
	-- tests for number of max threads for a given heap size.  Us the following shell script
		-- for i in 32 64 128 256 512 768 1024; do echo $j $i `java -Xmx${i}M -Xss64k ThreadTest2`; done
	-- Memory allocated for managed heap inside JVM can't be used as thread stack space. So increasing one number will reduce the other.
		(Managed Heap + native heap + thread stack size * number of threads) couldn't exceed 2 GB on 32bit x86 systems.
  Results:
	-- appears that # of threads is not a set quantity rather a function of memory capabilities
*/
public class ThreadTest2 {

	public static void main(String[] pArgs) throws Exception {
		try {
			while (true) {
				new TestThread().start();
				Thread.sleep(50);
			}
		} catch ( OutOfMemoryError e ) {
			System.out.println(TestThread.CREATE_COUNT);
			Thread.sleep(5000);
			System.exit(-1);
		}
	}

	static class TestThread extends Thread {
		private static int CREATE_COUNT = 0;

		public TestThread() {
			CREATE_COUNT++;
	}

	public void run() {
		try {
			sleep(Integer.MAX_VALUE);
		} catch (InterruptedException e) {
		}
	}
	}
}
