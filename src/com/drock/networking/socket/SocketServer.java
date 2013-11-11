package com.drock.networking.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import com.drock.networking.messaging.MessageHandlingSystem;
import com.drock.networking.socket.listener.SocketListenerThread;
import com.drock.networking.socket.worker.SocketWorkerThread;

public class SocketServer 
{
	static Logger s_logger = Logger.getLogger(SocketServer.class);
	
	private ServerSocket m_socket;
	private MessageHandlingSystem m_messageHandler;
	
	private List<SocketListenerThread> m_listenerThreadPool;
	private List<SocketWorkerThread>   m_workerThreadPool;
	
	private ConcurrentLinkedQueue<Socket> m_activeSockets;
	
	/**
	 * CTOR
	 * @param port - The port the server should listen on
	 * @param listenerCount - The number of listener threads to startup
	 * @param workerCount - The number of worker threads to startup
	 */
	public SocketServer(int port, int listenerCount, int workerCount)
	{
		try
		{
			m_socket = new ServerSocket(port);
		}
		catch (Exception e)
		{
			m_socket = null;
			s_logger.error("There was a problem creating the server socket:", e);
		}
		
		m_messageHandler = new MessageHandlingSystem();
		m_messageHandler.addDefaultHandlers();
		m_activeSockets = new ConcurrentLinkedQueue<Socket>();
		
		m_listenerThreadPool = new LinkedList<SocketListenerThread>();
		m_workerThreadPool = new LinkedList<SocketWorkerThread>();
		
		//Create listener thread pool
		for (int i = 0; i < listenerCount; i++)
		{
			SocketListenerThread t = new SocketListenerThread(m_socket, m_activeSockets);
			t.start();
			m_listenerThreadPool.add(t);
		}
		
		//Create worker thread pool
		for (int j = 0; j < workerCount; j++)
		{
			SocketWorkerThread t = new SocketWorkerThread(m_activeSockets, m_messageHandler);
			t.start();
			m_workerThreadPool.add(t);
		}
	}
	
	public boolean isAlive()
	{
		return (m_socket != null && m_socket.isClosed() == false);
	}
	
	/**
	 * Shuts down the server.
	 * 	1) Kills Socket listeners (server stops accepting new connections)
	 *  2) Kills Socket workers (server stops communicating with existing connections)
	 *  3) Closes all existing connections
	 *  4) Closes server socket (stops listening for new connections)
	 */
	public void shutdown()
	{
		for (SocketListenerThread t : m_listenerThreadPool)
		{
			t.kill();
			try
			{
				t.join();
			}
			catch (InterruptedException e)
			{
				s_logger.error("Socket listener interrupted trying to shutdown:", e);
			}
		}
		for (SocketWorkerThread w : m_workerThreadPool)
		{
			w.kill();
			try
			{
				w.join();
			}
			catch (InterruptedException e)
			{
				s_logger.error("Socket worker interrupted trying to shutdown:", e);
			}
		}
		for (Socket s : m_activeSockets)
		{
			try
			{
				s.close();
			}
			catch (IOException e)
			{
				s_logger.error("Socket exception trying to close a socket connection:", e);
			}
		}
		
		try
		{
			m_socket.close();
		}
		catch (IOException e)
		{
			s_logger.error("Socket exception trying to shutdown the server socket: ", e);
		}
	}
	
	/**
	 * Simple Main Method
	 */
	public static void main(String [] args)
	{
		SocketServer server = new SocketServer(6969, 1, 8);
		
		while (server.isAlive())
		{
			//TODO: Some sort of shutdown conditionals
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				s_logger.warn("Main thread interrupted:", e);
			}
		}
	}
}
