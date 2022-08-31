package com.example.latencies;

public class Latencies {
	public static void main(String[] args) {
		Thread[] threads;
		threads = new Thread[20];
		boolean first = true;
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new WorkerThread(10000000, first);
			threads[i].start();
			first = false;
			try {
		         System.out.println("Hit enter to add threads");
		         System.in.read();
		      } catch (Exception e) {
		         e.printStackTrace();
		      }

		}
	}
}
