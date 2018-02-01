/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package badassvishnu;

import badassvishnu.ChunkAllocator.CarComb;
import java.util.Set;

/**
 *
 * @author ykonoclast
 */
public class BadassVishnu
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException
    {
	int nbCores = Runtime.getRuntime().availableProcessors();
	int[] tabND =
	{
	    5, 10, 15
	};

	CaracWalker walker = new CaracWalker(2);
	ChunkAllocator allocator = new ChunkAllocator(walker);
	Set<Set> chunks = allocator.allocate(nbCores);

	ResultProcessor processor = new ResultProcessor(2, tabND, walker);

	Thread[] threads = new Thread[nbCores];

	int i = 0;

	for (Set chunk : chunks)
	{
	    Thread thread = new Thread(new Worker(i, chunk, tabND, processor));
	    thread.start();
	    threads[i] = thread;
	    ++i;
	}

	for (i = 0; i < threads.length; i++)
	{
	    threads[i].join();
	}
	processor.process();
    }
}
