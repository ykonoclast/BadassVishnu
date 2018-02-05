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

import org.duckdns.spacedock.badassvishnu.WorkLoader.CarComb;
import java.util.Set;
import org.duckdns.spacedock.upengine.libupsystem.RollGenerator;

/**
 *
 * @author ykonoclast
 */
public class Worker implements Runnable//TODO JAVADOCER
{
    private final Set<CarComb> m_chunk;
    private final int m_id;
    private final int[] m_lND;
    private DataProcessor m_processor;
    private final int m_nbRoll;

    public Worker(int p_id, Set<CarComb> p_chunk, DataProcessor p_processor, int p_nbRoll)//TODO virer TABND, appeler le processor a la place
    {
	m_chunk = p_chunk;
	m_id = p_id;
	m_processor = p_processor;
	m_lND = m_processor.getListND();
	m_nbRoll = p_nbRoll;
    }

    @Override
    public void run()
    {
	String msg = "	==worker" + m_id + "==>Yep daddy I gonna slice through chunk : ";//TODO resources pour String
	for (CarComb c : m_chunk)
	{
	    msg = msg.concat(c.toString() + " ");
	}
	System.out.println(msg);

	for (CarComb c : m_chunk)
	{
	    System.out.println("    ==worker" + m_id + "==>Going for block:" + c.toString());
	    int sum = 0;
	    int[] chances = new int[m_lND.length];
	    for (int indRoll = 0; indRoll < m_nbRoll; ++indRoll)
	    {
		int alea = RollGenerator.getInstance().lancerGarder(c.dom + c.comp, c.trait, false);
		alea = (c.comp >= 3) ? alea + 5 : alea;
		sum += alea;
		for (int indND = 0; indND < m_lND.length; ++indND)
		{
		    if (alea >= m_lND[indND])
		    {
			++chances[indND];
		    }
		}
	    }

	    m_processor.insertMean(c, sum / m_nbRoll);
	    for (int indND = 0; indND < m_lND.length; ++indND)
	    {
		m_processor.insertChance(c, indND, (chances[indND] * 100) / m_nbRoll);
	    }
	}
    }
}
