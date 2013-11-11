package com.drock.networking.socket.worker;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.drock.networking.messaging.MessageHandlingSystem;
import com.drock.networking.messaging.WrapperMessage;

public class SocketWorkerThread implements Runnable 
{
	static Logger s_logger = LogManager.getLogger(SocketWorkerThread.class);
	
	private ConcurrentLinkedQueue<Socket> m_socketQueue;
	private MessageHandlingSystem m_handlers;
	private boolean m_alive;
	
	/**
	 * CTOR
	 * @param queue - The queue with the sockets the thread will be working on
	 */
	public SocketWorkerThread(ConcurrentLinkedQueue<Socket> queue, MessageHandlingSystem messageHandlingSystem)
	{
		m_socketQueue = queue;
		m_handlers = messageHandlingSystem;
	}
	
	@Override
	public void run() 
	{
		while (m_alive)
		{
			if (m_socketQueue.isEmpty())
			{
				try 
				{
					Thread.sleep(100);
				} 
				catch (InterruptedException e) 
				{
					s_logger.warn("Worker thread sleep was interrupted...not really a big deal", e);
				}
			}
			
			Socket currentSocket = m_socketQueue.poll();
			try
			{
				InputStream socketInput = currentSocket.getInputStream();
				if (socketInput.available() > 0)
				{
					ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
					byte buffer[] = new byte[1024];
					
					for(int read; (read = socketInput.read(buffer)) != -1;)
					{
					  	byteStream.write(buffer, 0, read);
					}
					
					ByteBuffer rawData = ByteBuffer.wrap(byteStream.toByteArray());
					if (rawData.capacity() < 7)
					{
						s_logger.error("Invalid network message: not enough bytes!");
					}
					else
					{
						WrapperMessage response = m_handlers.handleMessage(rawData);
						if (response != null)
							currentSocket.getOutputStream().write(response.serializeToBytes());
					}
				}
			}
			catch (Exception e)
			{
				s_logger.error("Error readying data from the socket", e);
			}
			finally
			{
				if (currentSocket != null && currentSocket.isClosed() == false)
				{
					m_socketQueue.add(currentSocket);
				}
			}
		}
	}
	
	/**
	 * Terminates the thread after its current run loop iteration.
	 */
	public void kill()
	{
		m_alive = false;
	}
}
