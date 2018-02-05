/*
 * Copyright (C) 2018 ykonoclast
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.duckdns.spacedock.badassvishnu;

import org.duckdns.spacedock.badassvishnu.WorkLoader.CarBlock;
import java.util.Set;
import org.duckdns.spacedock.upengine.libupsystem.RollGenerator;

/**
 * classe implementant la logique de traitement statistique par ligne
 *
 * @author ykonoclast
 */
public class Worker implements Runnable
{
    /**
     * le chunk de blocs de caractéristiques dont ce worker aura la charge
     */
    private final Set<CarBlock> m_chunk;
    /**
     * l'identifiant du worker, pour les IO
     */
    private final int m_id;
    /**
     * la liste des ND à tester
     */
    private final int[] m_lND;
    /**
     * le processeur de résultats qui recevra les sorties de ce worker
     */
    private DataProcessor m_processor;
    /**
     * le nombre de jets à effectuer
     */
    private final int m_nbRoll;
    /**
     * déclenche des sorties terminal supplémentaires
     */
    private boolean verbose;

    /**
     * constructeur
     *
     * @param p_id
     * @param p_chunk
     * @param p_processor
     * @param p_nbRoll
     * @param p_verbose
     */
    public Worker(int p_id, Set<CarBlock> p_chunk, DataProcessor p_processor, int p_nbRoll, boolean p_verbose)
    {
	m_chunk = p_chunk;
	m_id = p_id;
	m_processor = p_processor;
	m_lND = m_processor.getListND();
	m_nbRoll = p_nbRoll;
	verbose = p_verbose;
    }

    @Override
    public void run()
    {//méthode qui sera automatiquement appelée par le run du Thread
	if (verbose)
	{
	    String msg = "        ==worker" + m_id + "==>Yep daddy I gonna slice through chunk : ";//TODO resources pour String
	    for (CarBlock c : m_chunk)
	    {
		msg = msg.concat(c.toString() + " ");
	    }
	    System.out.println(msg);
	}

	//pour chaque bloc de caractéristiques
	for (CarBlock c : m_chunk)
	{
	    if (verbose)
	    {
		System.out.println("        ==worker" + m_id + "==>Going for block:" + c.toString());
	    }
	    int sum = 0;
	    int[] chances = new int[m_lND.length];
	    for (int indRoll = 0; indRoll < m_nbRoll; ++indRoll)
	    {//on lance les dés autant de fois que spécifié, vérifiant au fur et à mesure le respect des ND et loggant les résultats pour calculer les moyennes
		int alea = RollGenerator.getInstance().lancerGarder(c.dom + c.comp, c.trait, false);
		alea = (c.comp >= 3) ? alea + 5 : alea;//bonus de rang
		sum += alea;//pour les ;oyennes
		for (int indND = 0; indND < m_lND.length; ++indND)
		{//vérification du respect des divers ND spécifiés
		    if (alea >= m_lND[indND])
		    {
			++chances[indND];
		    }
		}
	    }
	    //insertion des résultats dans le processeur de résultats
	    m_processor.insertMean(c, sum / m_nbRoll);
	    for (int indND = 0; indND < m_lND.length; ++indND)
	    {
		m_processor.insertChance(c, indND, (chances[indND] * 100) / m_nbRoll);
	    }
	}
    }
}
