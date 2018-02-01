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
package badassvishnu;

import badassvishnu.ChunkAllocator.CarComb;
import java.util.Set;

/**
 *
 * @author ykonoclast
 */
public class Worker implements Runnable//TODO JAVADOCER
{
    private final Set<CarComb> m_chunk;
    private final int m_id;
    private final int[] m_lND;
    private ResultProcessor m_processor;

    public Worker(int p_id, Set<CarComb> p_chunk, int[] p_lND, ResultProcessor p_processor)
    {
	m_chunk = p_chunk;
	m_id = p_id;
	m_lND = p_lND;
	m_processor = p_processor;
    }

    @Override
    public void run()
    {
	String msg = "	==worker" + m_id + "==>Yep daddy I gonna slice through chunk : ";
	for (CarComb c : m_chunk)
	{
	    msg = msg.concat(c.toString() + " ");
	}
	System.out.println(msg);

	for (CarComb c : m_chunk)
	{
	    m_processor.insertMean(c, c.comp);
	    for (int indND = 0; indND < m_lND.length; ++indND)
	    {
		m_processor.insertChance(c, indND, c.comp);
	    }
	}

    }

    static class Chance
    {
	int ND;
	int percent;
    }
}
