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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * classe partitionnant l'ensemble des groupes de caracs en "chunks", chacun
 * ayant vocation à être exploité par un thread. Initialisé via parcours d'un
 * CaracWalker
 *
 * @author ykonoclast
 */
class WorkLoader implements ICaracUser//TODO blinder méthodes partout
{
    /**
     * ensemble de tous les groupes de caracs
     */
    private final HashSet<CarComb> m_lCarGroup;
    /**
     * CaracWalker allant permettre de remplir m_lCarGroup
     */
    private final CaracWalker m_walker;

    /**
     * constructeur
     *
     * @param p_walker permettant initialisation
     */
    WorkLoader(CaracWalker p_walker)
    {
	m_walker = p_walker;
	m_lCarGroup = new HashSet<>();
    }

    /**
     * méthode implémentant l'algo de partitionnement de l'ensemble des
     * combimaisons de caracs à dessein de répartition de la charge entre les
     * threads
     *
     * @param nbProcessors
     * @return
     */
    Set allocate(int p_nbProcessors)
    {
	initializeAllocator();
	HashSet<HashSet<CarComb>> result = new HashSet<>();
	int nbLines = m_lCarGroup.size();
	int nbWorkers = (p_nbProcessors < nbLines) ? p_nbProcessors : nbLines;
	int baseChunkSize = nbLines / nbWorkers;//troncature, le chunk de base vaut la partie entière de la division du nombre de lignes par le nombre de threads
	int nbFloodWorkers = nbLines % nbWorkers;//le reste : c'est à dire le nombre de threads de débordement (qui auront un CaracGroup de plus que les autres)
	int floodCounter = 0;
	Iterator<CarComb> iterator = m_lCarGroup.iterator();

	for (int w = 0; w < nbWorkers; ++w)
	{//pour chaque thread
	    HashSet<CarComb> chunk = new HashSet<>();
	    int nElts = baseChunkSize + ((floodCounter < nbFloodWorkers) ? 1 : 0);//si c'est un thread de débordement il recevra un CaracGroup de plus
	    for (int c = 0; c < nElts; ++c)
	    {//pour chaque élément du chunk final
		chunk.add(iterator.next());
	    }
	    ++floodCounter;
	    result.add(chunk);
	}
	return result;
    }

    @Override
    public void useCarac(int p_trait, int p_dom, int p_comp)
    {
	m_lCarGroup.add(new CarComb(p_trait, p_dom, p_comp));//la boucle de CaracWalker aura pour effet d'ajouter toutes les combinaisons possibles de caractéristiques à ce WorkLoader
    }

    /**
     * appelle le CaracWalker pour initialisation
     */
    private void initializeAllocator()
    {
	if (m_lCarGroup.isEmpty())
	{//on ne recrée pas le groupe une fois qu'il a été établi : il faut un autre objet pour cela. Par contre on pourra du coup refaire des allocations avec le même ensemble de caracs mais pour un nombre de threads différent
	    m_walker.walk(this);
	}
    }

    /**
     * petit struct pour une combinaison de carac donnée
     */
    static class CarComb
    {
	CarComb(int p_trait, int p_dom, int p_comp)
	{
	    trait = p_trait;
	    dom = p_dom;
	    comp = p_comp;
	}
	int trait;
	int dom;
	int comp;

	@Override
	public String toString()
	{
	    String val = ("T" + trait + "D" + dom + "C" + comp);
	    return val;
	}
    }
}
