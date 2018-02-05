/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.duckdns.spacedock.badassvishnu;

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

	final ArgP argp = new ArgP();
	argp.addOption("-v", "Verbose");
	argp.addOption("-c", "CARMAX", "max value for caracteristics, default=7");
	argp.addOption("-d", "NBROLLS", "number of dicerolls per line, default=1 000 000");
	argp.addOption("-m", "MIN", "minimum target number, default=5");
	argp.addOption("-M", "MAX", "maximum target number, default=55");
	argp.addOption("-s", "STEP", "step between two target numbers, default=5");
	argp.addOption("-o", "PATH", "base name of output files [e.g.: myfile -> MEAN_myfile.csv & CHANCES_myfile.csv], default=vishnout");

	try
	{
	    args = argp.parse(args);
	}
	catch (IllegalArgumentException e)
	{
	    System.err.println(e.getMessage());
	    System.err.print(argp.usage());
	    System.exit(1);
	}
	final boolean p_verbose = argp.has("-v");
	int maxRang = (argp.has("-c")) ? Integer.parseInt(argp.get("-c")) : 7;
	final String p_filename = (argp.has("-o")) ? argp.get("-o") : "vishnout";
	int nbRoll = (argp.has("-d")) ? Integer.parseInt(argp.get("-d")) : 1000000;
	int minCol = (argp.has("-m")) ? Integer.parseInt(argp.get("-m")) : 5;
	int maxCol = (argp.has("-M")) ? Integer.parseInt(argp.get("-M")) : 55;
	int step = (argp.has("-s")) ? Integer.parseInt(argp.get("-s")) : 5;

	int nbCores = Runtime.getRuntime().availableProcessors();

	CaracWalker walker = new CaracWalker(maxRang);
	WorkLoader allocator = new WorkLoader(walker);
	Set<Set> chunks = allocator.allocate(nbCores);

	DataProcessor processor = new DataProcessor(minCol, maxCol, step, walker);

	Thread[] threads = new Thread[nbCores];

	int i = 0;

	for (Set chunk : chunks)
	{
	    Thread thread = new Thread(new Worker(i, chunk, processor, nbRoll));
	    thread.start();
	    threads[i] = thread;
	    ++i;
	}

	for (i = 0;
		i < threads.length;
		i++)
	{
	    threads[i].join();
	}

	processor.process();
    }
}
