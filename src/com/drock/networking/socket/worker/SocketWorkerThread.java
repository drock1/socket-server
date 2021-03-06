package com.drock.networking.socket.worker;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import com.drock.networking.messaging.MessageHandlingSystem;
import com.drock.networking.messaging.WrapperMessage;

public class SocketWorkerThread extends Thread
{
	static Logger s_logger = Logger.getLogger(SocketWorkerThread.class);
	
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
		m_alive = true;
	}
	
	@Override
	public void run() 
	{
		while (m_alive)
		{
			while (m_socketQueue.isEmpty())
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
			if (currentSocket == null)
				continue;
			
			try
			{
				InputStream socketInput = currentSocket.getInputStream();
				if (socketInput.available() > 0) //So we aren't blocking our threads on empty sockets
				{
					ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

					while(true) //Broken by a '0' byte or a client socket termination
					{
						int lastByte = socketInput.read();
						
						if (lastByte == -1) //Reading client close
						{
							currentSocket.close();
							break;
						}
						
						if (byteStream.size() >= 7 && lastByte == 0) //Reading a '\0' in the ASCII part
							break;
						
						byteStream.write(lastByte);
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
				if (currentSocket.isConnected() == false)
				{
					s_logger.debug("Client closed the connection. Closing it on our end.");
					try
					{
						currentSocket.close();
					}
					catch (Exception e)
					{
						s_logger.error("Exception closing disconnected socket: ", e);
					}
				}
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
