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
import org.duckdns.spacedock.badassvishnu.WorkLoader.CarBlock;
import java.util.ArrayList;
import org.duckdns.spacedock.commonutils.PropertiesHandler;

/**
 * classe processant les résultats des workers ainsi que des éléments de
 * paramétrage utiles au démarrage de ceux-ci
 *
 * @author ykonoclast
 */
class DataProcessor implements ICaracUser
{//TODO string dans les resources
    /**
     * tableau interne des résultats moyens aux jets, indicé dans l'ordre par
     * traits, domaines et compétences
     */
    private final int[][][] m_tabMean;
    /**
     * tableau interne des résultats moyens aux jets, indicé dans l'ordre par
     * traits, domaines, compétences et ND
     */
    private final int[][][][] m_tabChances;
    /**
     * lignes de résultats en chances de réussites à écrire dans le fichier
     * final
     */
    private final ArrayList<String> m_chancesLines;
    /**
     * lignes de résultats moyens à écrire dans le fichier final
     */
    private final ArrayList<String> m_meanLines;
    /**
     * ligne en-tête pour les ND
     */
    private final String m_NDHeadersLine;
    /**
     * ligne en-tête pour les moyennes
     */
    private final String m_meanHeaderLine;
    /**
     * walker permettant de parcourir de façon uniforme toutes les combinaisons
     * de caractéristiques
     */
    private final CaracWalker m_walker;
    /**
     * tableau de tous les ND à traverser
     */
    private final int[] m_tabND;
    /**
     * nom du fichier de sortie des résultats moyens
     */
    private final String m_meanFileName;
    /**
     * nom du fichier de sortie des chances de réussite
     */
    private final String m_chancesFileName;

    /**
     * constructeur
     *
     * @param p_minCol
     * @param p_maxCol
     * @param p_step
     * @param p_walker
     * @param p_baseFileName prototype du nom du fichier de sortie auquel sera
     * accolé un préfixe et un suffixe idoine
     */
    public DataProcessor(int p_minCol, int p_maxCol, int p_step, CaracWalker p_walker, String p_baseFileName)
    {
	m_meanFileName = PropertiesHandler.getInstance("BadassVishnu").getString("mean") + p_baseFileName + PropertiesHandler.getInstance("BadassVishnu").getString("csv");
	m_chancesFileName = PropertiesHandler.getInstance("BadassVishnu").getString("chances") + p_baseFileName + PropertiesHandler.getInstance("BadassVishnu").getString("csv");

	int maxRang = p_walker.getMaxRang();
	m_tabMean = new int[maxRang][maxRang][maxRang + 1/*la compétence a des valeurs de 0 à rangMax*/];
	m_walker = p_walker;
	String subHeaderPrefix = PropertiesHandler.getInstance("BadassVishnu").getString("prefix");
	String buf = "";

	int nbND = ((p_maxCol - p_minCol) / p_step) + 1;//la division entre int est une troncature en Java
	m_tabND = new int[nbND];
	for (int i = 0; i < nbND; ++i)
	{
	    int ND = p_minCol + p_step * i;
	    m_tabND[i] = ND;
	}
	m_tabChances = new int[maxRang][maxRang][maxRang + 1/*la compétence a des valeurs de 0 à rangMax*/][m_tabND.length];

	for (int nd : m_tabND)
	{
	    buf = buf.concat("," + PropertiesHandler.getInstance("BadassVishnu").getString("ND") + nd);
	}
	m_NDHeadersLine = subHeaderPrefix.concat(buf + "\n");
	m_meanHeaderLine = subHeaderPrefix.concat("," + PropertiesHandler.getInstance("BadassVishnu").getString("moy") + "\n");
	m_chancesLines = new ArrayList<>();
	m_meanLines = new ArrayList<>();
    }

    int[] getListND()
    {
	return m_tabND;
    }

    /**
     * fonction appelée à la toute fin du programme quand tous les workers ont
     * rendu leurs résultats
     *
     * @throws IOException
     */
    void process() throws IOException//TODO paramétrer nom des fichiers de sorties
    {
	m_walker.walk(this);//appel du walker pour initialiser les éléments via la méthode useCarac

	FileWriter meanFile;
	FileWriter chanceFile;
	meanFile = new FileWriter(new File(m_meanFileName));
	chanceFile = new FileWriter(new File(m_chancesFileName));

	//écriture du fichier de chances de réussite
	chanceFile.write(m_NDHeadersLine);
	for (String line : m_chancesLines)
	{
	    chanceFile.write(line);
	}

	//écriture du fichier de résultats moyens
	meanFile.write(m_meanHeaderLine);
	for (String line : m_meanLines)
	{
	    meanFile.write(line);
	}

	//ménage, techniquement ce devrait être dans un finally mais le peu d'ampleur du programme ne fait pas craindre de fin prématurée (ou de difficulté particulière en ce cas)
	meanFile.close();
	chanceFile.close();
    }

    /**
     * insère un résultat moyen pour process en fin de programme
     *
     * @param p_combi
     * @param p_mean
     */
    synchronized void insertMean(CarBlock p_combi, int p_mean)
    {
	m_tabMean[p_combi.trait - 1][p_combi.dom - 1][p_combi.comp] = p_mean;//traits et domaines sont étagés de 1 à rang, il faut donc soustraire 1 pour convertir en indices, la compétence démarre bien à 0
    }

    /**
     * insère une chance de réussite pour process en fin de programme
     *
     * @param p_combi
     * @param p_indND
     * @param p_percent
     */
    synchronized void insertChance(CarBlock p_combi, int p_indND, int p_percent)
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
