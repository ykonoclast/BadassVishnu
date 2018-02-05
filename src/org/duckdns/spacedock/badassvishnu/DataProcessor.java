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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.duckdns.spacedock.badassvishnu.WorkLoader.CarComb;
import java.util.ArrayList;

/**
 *
 * @author ykonoclast
 */
class DataProcessor implements ICaracUser//TODO JAVADOCER
{//TODO string dans les resources
    private final int[][][] m_tabMean;
    private final int[][][][] m_tabChances;
    private final ArrayList<String> m_chancesLines;
    private final ArrayList<String> m_meanLines;
    private final String m_NDHeadersLine;
    private final String m_meanHeaderLine;
    private final CaracWalker m_walker;
    private final int[] m_tabND;
    private final String m_meanFileName;
    private final String m_chancesFileName;

    public DataProcessor(int p_minCol, int p_maxCol, int p_step, CaracWalker p_walker, String p_baseFileName)
    {
	m_meanFileName = "MEAN_" + p_baseFileName + ".csv";
	m_chancesFileName = "CHANCES_" + p_baseFileName + ".csv";

	int maxRang = p_walker.getMaxRang();//TODO virer l'utilisation de getRang (et supprimer getRang) et faire initialiser ces tableaux par le caracwalker qui sera le seul du coup à connaître le maxrang
	m_tabMean = new int[maxRang][maxRang][maxRang + 1/*la compétence a des valeurs de 0 à rangMax*/];
	m_walker = p_walker;
	String subHeaderPrefix = "T,D,C";
	String buf = "";

	int nbND = ((p_maxCol - p_minCol) / p_step) + 1;
	m_tabND = new int[nbND];
	for (int i = 0; i < nbND; ++i)
	{
	    int ND = p_minCol + p_step * i;
	    m_tabND[i] = ND;
	}
	m_tabChances = new int[maxRang][maxRang][maxRang + 1/*la compétence a des valeurs de 0 à rangMax*/][m_tabND.length];

	for (int nd : m_tabND)
	{
	    buf = buf.concat(",ND" + nd);
	}
	m_NDHeadersLine = subHeaderPrefix.concat(buf + "\n");
	m_meanHeaderLine = subHeaderPrefix.concat(",moyenne\n");
	m_chancesLines = new ArrayList<>();
	m_meanLines = new ArrayList<>();
    }

    int[] getListND()
    {
	return m_tabND;
    }

    void process() throws IOException//TODO paramétrer nom des fichiers de sorties
    {
	m_walker.walk(this);

	FileWriter meanFile;
	FileWriter chanceFile;
	meanFile = new FileWriter(new File(m_meanFileName));
	chanceFile = new FileWriter(new File(m_chancesFileName));

	System.out.println("\n\nCHANCES");
	System.out.print(m_NDHeadersLine);
	chanceFile.write(m_NDHeadersLine);

	for (String line : m_chancesLines)
	{
	    System.out.print(line);
	    chanceFile.write(line);

	}
	System.out.print("\n\nMEAN RESULTS\n" + m_meanHeaderLine);
	meanFile.write(m_meanHeaderLine);
	for (String line : m_meanLines)
	{
	    System.out.print(line);
	    meanFile.write(line);
	}
	meanFile.close();
	chanceFile.close();
    }

    synchronized void insertMean(CarComb p_combi, int p_mean)
    {
	m_tabMean[p_combi.trait - 1][p_combi.dom - 1][p_combi.comp] = p_mean;//traits et domaines sont étagés de 1 à rang, il faut donc soustraire 1 pour convertir en indices, la compétence démarre bien à 0
    }

    synchronized void insertChance(CarComb p_combi, int p_indND, int p_percent)
    {
	m_tabChances[p_combi.trait - 1][p_combi.dom - 1][p_combi.comp][p_indND] = p_percent;//traits et domaines sont étagés de 1 à rang, il faut donc soustraire 1 pour convertir en indices, la compétence démarre bien à 0
    }

    @Override
    public void useCarac(int p_trait, int p_dom, int p_comp)
    {
	String carHeader = p_trait + "," + p_dom + "," + p_comp;
	String chanceLine = carHeader;
	String meanLine = carHeader.concat("," + m_tabMean[p_trait - 1][p_dom - 1][p_comp] + "\n");
	for (int percent : m_tabChances[p_trait - 1][p_dom - 1][p_comp])
	{
	    chanceLine = chanceLine.concat("," + percent);
	}
	chanceLine = chanceLine.concat("\n");
	m_chancesLines.add(chanceLine);
	m_meanLines.add(meanLine);
    }
}
