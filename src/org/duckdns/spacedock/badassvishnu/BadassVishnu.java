/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.duckdns.spacedock.badassvishnu;

import java.io.IOException;
import java.util.Set;
import org.duckdns.spacedock.commonutils.PropertiesHandler;

/**
 *
 * @author ykonoclast
 */
public class BadassVishnu
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, IOException
    {
	long startTime = System.nanoTime();

	//traitement des paramétres
	final ArgP argp = new ArgP();
	argp.addOption("-v", "Verbose");
	argp.addOption("--help", PropertiesHandler.getInstance("BadassVishnu").getString("helpparam"));
	argp.addOption("-c", "CARMAX", PropertiesHandler.getInstance("BadassVishnu").getString("carmaxparam"));
	argp.addOption("-d", "NBROLLS", PropertiesHandler.getInstance("BadassVishnu").getString("rollparam"));
	argp.addOption("-m", "MIN", PropertiesHandler.getInstance("BadassVishnu").getString("minparam"));
	argp.addOption("-M", "MAX", PropertiesHandler.getInstance("BadassVishnu").getString("maxparam"));
	argp.addOption("-s", "STEP", PropertiesHandler.getInstance("BadassVishnu").getString("stepparam"));
	argp.addOption("-o", "PATH", PropertiesHandler.getInstance("BadassVishnu").getString("pathparam"));

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

	if (argp.has("--help"))
	{//affiche le ;essage d'aide et quitte
	    System.out.println((argp.usage()));
	}
	else
	{//c'est parti pour le programme normal
	    final boolean verbose = argp.has("-v");
	    final int maxRang = (argp.has("-c")) ? Integer.parseInt(argp.get("-c")) : 7;
	    final String filename = (argp.has("-o")) ? argp.get("-o") : "vishnout";
	    final int nbRoll = (argp.has("-d")) ? Integer.parseInt(argp.get("-d")) : 1000000;
	    final int minCol = (argp.has("-m")) ? Integer.parseInt(argp.get("-m")) : 5;
	    final int maxCol = (argp.has("-M")) ? Integer.parseInt(argp.get("-M")) : 55;
	    final int step = (argp.has("-s")) ? Integer.parseInt(argp.get("-s")) : 5;

	    int nbCores = Runtime.getRuntime().availableProcessors();

	    if (verbose)
	    {
		System.out.println(PropertiesHandler.getInstance("BadassVishnu").getString("intro"));
		System.out.println(PropertiesHandler.getInstance("BadassVishnu").getString("paramannounc") + " NDmin=" + minCol + ", NDmax=" + maxCol + ", step=" + step + ", file name base=" + filename + ", nbRolls=" + nbRoll + ", carac max=" + maxRang + ", detected cores=" + nbCores);
		System.out.println(PropertiesHandler.getInstance("BadassVishnu").getString("ready"));
	    }

	    //initialisation des objets
	    CaracWalker walker = new CaracWalker(maxRang);
	    WorkLoader allocator = new WorkLoader(walker);
	    Set<Set> chunks = allocator.allocate(nbCores);
	    DataProcessor processor = new DataProcessor(minCol, maxCol, step, walker, filename);

	    //création des workers
	    Thread[] threads = new Thread[nbCores];
	    int threadCounter = 0;
	    for (Set chunk : chunks)
	    {
		Thread thread = new Thread(new Worker(threadCounter, chunk, processor, nbRoll, verbose));
		thread.start();
		threads[threadCounter] = thread;
		++threadCounter;
	    }

	    //attente de la fin de tous les traitements
	    for (int indThread = 0; indThread < threads.length; indThread++)
	    {
		threads[indThread].join();
	    }

	    //exploitation des résultats
	    if (verbose)
	    {
		System.out.println(PropertiesHandler.getInstance("BadassVishnu").getString("processing"));
	    }
	    processor.process();
	    long endTime = System.nanoTime();
	    if (verbose)
	    {
		System.out.println(PropertiesHandler.getInstance("BadassVishnu").getString("done1") + " " + ((endTime - startTime) / 1000000000) + PropertiesHandler.getInstance("BadassVishnu").getString("done2"));
	    }
	}
    }
}
