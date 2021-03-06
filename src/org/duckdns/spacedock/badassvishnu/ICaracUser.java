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

/**
 * interface pour les objets ayant besoin qu'une de leurs méthodes soit appelée
 * pour chaque combinaison possible de caractéristiques
 *
 * @author ykonoclast
 */
interface ICaracUser
{
    /**
     * méthode ayant vocation à être appelée un grand nombre de fois par un
     * CaracWalker : autant de fois que de combinaisons possibles de caracs
     *
     * @param p_trait
     * @param p_dom
     * @param p_comp
     */
    public void useCarac(int p_trait, int p_dom, int p_comp);
}
