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

/**
 * Cette classe encapsule les opérations de parcours de l'ensemble des caracs
 * pour ne pas avoir à répéter plusieurs le code des trois boucles imbriauées
 * pour trait/domaine/compétence
 *
 * @author ykonoclast
 */
class CaracWalker
{

    /**
     * rang max des caractéristiques explorées
     */
    private final int maxRang;

    /**
     * constructeur
     *
     * @param p_maxRang rang max des caracs explorées
     */
    CaracWalker(int p_maxRang)
    {
	maxRang = p_maxRang;
    }

    /**
     * parcours toutes les combinaisons de caracs jusqu'à maxRang
     *
     * @param p_user un objet implémentant l'interface ICaracUser et ayant
     * besoin d'avoir sa méthode useCarac appelée répétitivement pour chaque
     * combinaison
     */
    void walk(ICaracUser p_user)
    {
	for (int t = 1; t <= maxRang; ++t)
	{
	    for (int d = 1; d <= maxRang; ++d)
	    {
		for (int c = 0; c <= d; ++c)
		{
		    p_user.useCarac(t, d, c);
		}
	    }
	}
    }
}
