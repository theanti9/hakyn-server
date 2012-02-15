package com.hakyn.srv;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.hakyn.config.HakynConfig;
import com.hakyn.queue.HKBlockingIDQueue;
import com.hakyn.queue.HKQueueRunner;
import com.hakyn.util.HKServiceConnectionCollection;
import com.snow.IO.SnowTcpClient;
import com.snow.IO.SnowTcpServer;
import com.snow.IO.EventCallback.IOConnectEventCallback;
import com.snow.IO.EventCallback.IODisconnectEventCallback;
import com.snow.IO.EventCallback.IOReadEventCallback;

public class HKServiceListener implements Runnable {
	
	private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors()*10;
	private static final int MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors()*25;
	private static final int THREAD_TIMEOUT = 30;
	private static final int QUEUE_SIZE = 100;
	
	public static HKServiceConnectionCollection svcCons;
	public static HKBlockingIDQueue dataQueue;
	public static HKQueueRunner queueRunner;
	private ThreadPoolExecutor threadPool;
	private BlockingQueue<Runnable> threadQueue;
	
	@Override
	public void run() {
		try {
			// instantiate server
			SnowTcpServer server = new SnowTcpServer(HakynConfig.getGameListenPort(), CORE_POOL_SIZE, MAX_POOL_SIZE, THREAD_TIMEOUT, QUEUE_SIZE);
			
			// Set up thread queue and pool
			threadQueue = new LinkedBlockingQueue<Runnable>(QUEUE_SIZE);
			threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, THREAD_TIMEOUT, TimeUnit.SECONDS, threadQueue);
			
			// init HKServiceConnectioCollection
			svcCons = new HKServiceConnectionCollection();
			
			// instantiate data queue
			Date d = new Date();
			dataQueue = new HKBlockingIDQueue(d.toString().replace(" ", "").length());
			d = null;
			// Register connect callback
			server.RegisterCallback(new IOConnectEventCallback() {
				public void Invoke(SnowTcpClient client) {
					// When someone connects add create an HKServiceConnection object
					// and add it to the list
					HKServiceConnection svcCon = new HKServiceConnection(client);
					// Set the name to the date string with no spaces.
					client.SetName(client.GetConnectionTime().toString().replace(" ", ""));
					svcCons.add(svcCon);
					System.out.println("Service connection added from "+client);
				}
			});
			// Register disconnect callback
			server.RegisterCallback(new IODisconnectEventCallback() {
				public void Invoke(SnowTcpClient client) {
					svcCons.remove(svcCons.getConnectionForCharacter(client.GetName()));
					System.out.println(client+" disconnected from services");
				}
			});
			// Register read callback
			server.RegisterCallback(new IOReadEventCallback() {
				public void Invoke(SnowTcpClient client, byte[] buff) {
					try {
						dataQueue.EnqueueData(client.GetName(), buff);
						threadPool.execute(new Thread(new HKQueueRunner()));
						
					} catch (Exception e) {
						e.printStackTrace();
					}	
				}
			});
			server.Start();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(-1);
		}
		
	}
	public void stop() {
		threadPool.shutdownNow();
	}
}
