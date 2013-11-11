package com.drock.networking.socket;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The socket listener listens for connections on a specified socket and spits
 * connections into a queue for workers to pickup.
 */
public class SocketListenerThread implements Runnable
{
	static Logger s_logger = LogManager.getLogger(SocketListenerThread.class);
	
	private ServerSocket m_socket;
	private ConcurrentLinkedQueue<Socket> m_queue;
	
	private boolean m_alive;
	/**
	 * CTOR
	 * @param socket - The socket being listened to
	 * @param socketQueue - The queue where socket connections are placed
	 */
	public SocketListenerThread(ServerSocket socket, ConcurrentLinkedQueue<Socket> socketQueue)
	{
		m_socket = socket;
		m_queue = socketQueue;
		
		m_alive = true;
	}
	
	@Override
	public void run() 
	{
		while (m_alive)
		{
			Socket newConnection = null;
			try
			{
				newConnection = m_socket.accept();
				if (newConnection.isConnected())
					m_queue.add(newConnection);
			}
			catch (Exception e)
			{
				s_logger.error("Error accepting socket connection:", e);
				try
				{
					if (newConnection != null && newConnection.isClosed() == false)
						newConnection.close();
				}
				catch (Exception e2)
				{
					s_logger.error("Error closing socket after a previous exception:" , e2);
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
