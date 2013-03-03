package testsg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

// Utilitaires de tests - Classe Tests
//
// Version 2.0.0    : version initiale complete
// Version 2.0.1    : formattage des commentaires pour impression
// Version 2.1.0    : correction du bug verboseLevel (ligne 111)
//                    + correction du bug statusDesign (ligne 100)
//                    + la classe devient abstraite
// Version 2.2.0    : modification de la methode Unit pour gerer les
//                    references nulles
//                    + suppression de la methode main (module de tests
//                    unitaires externalise)
// Version 2.3.0    : introduction d'une extension de la methode Case
// Version 2.4.0    : prise en compte des types primitifs int et double
//                    par la methode Unit
// Version 2.5.0    : introduction d'une forme polymorphe de la
//                    methode Unit pour autoriser une marge d'erreur
// Version 2.6.0    : introduction d'une forme polymorphe de la
//                    methode Unit pour autoriser le controle global de
//                    deux tableaux pris dans leur ensemble
// Version 2.7.0    : introduction d'une forme polymorphe de la
//                    methode Unit pour comparer la valeur obtenue avec
//                    un tableau de valeurs possibles et valides
// Version 3.0.0	: - ajout d'un compteur de Unit
//					  -	troisieme parametre dans le Begin etant le nombre de
//						Unit prevus
//					  - getteurs du compteur et de nbTU
//					  - journalisation
//					  - enchainement de TU
//					  - gestion de contextes
//
/**
 * 
 * La classe Tests fournit un ensemble complet de services destines a simplifier
 * et a uniformiser la mise en oeuvre de la charte de realisation des modules de
 * tests unitaires exposee en cours. La classe est egalement utilisee en TD
 * comme un exemple tres concret de codes sources "ecrits par d'autres".
 * 
 * Les services fournis sont :
 * 
 * Begin : debut d'execution des tests unitaires de la classe cible, 
 * Design : executer un "test design", 
 * Case : executer un "test case", 
 * Unit : executer un "test unit", 
 * End : fin d'execution des tests unitaires de la classe cible.
 * 
 * @author Alain Thuaire
 * 
 **/
abstract public class Tests {
	private static String className = ""; // Nom de la classe a tester
	private static String groupName = ""; // Nom du groupe de tests
	private static int levelDesign = 0; // Numero d'ordre du test_design
	private static int levelCase = 0; // Numero d'ordre du test_case
	private static int verboseLevel = 0; // Niveau courant de visualisation
	private static int statusDesign = 0; // CR du test_design courant
	private static int statusCase = 0; // CR de test_case courant
	private static boolean testBegin = false; // Indicateur execution Begin
	private static String designName = ""; // Nom du test design courant
	private static String caseName = ""; // Nom du test case courant
	private static int compteurTU = 0; // Compteur de TU valides
	private static int nbTU = 0; // Nombre de Unit attendu
	private static String journalisation = "";// Contenu de la journalisation
	private static final String LF = System.getProperty("line.separator");
	private static final String TAB = "\t";

	/**
	 * La methode Begin visualise la ligne d'entete du module de tests
	 * unitaires.
	 **/
	public static void Begin(String name, String version, int nbUnitAttendu) {
		// Initialiser les attributs
		className = name;
		levelDesign = 0;
		levelCase = 0;
		verboseLevel = 0;
		statusDesign = 0;
		statusCase = 0;
		designName = "";
		caseName = "";
		compteurTU = 0;
		nbTU = nbUnitAttendu;
		testBegin = true; // Service Begin execute
		
		if (journalisation.equals("")) {
			Date date = new Date();
			DateFormat shortDateFormat = DateFormat.getDateTimeInstance(
			DateFormat.SHORT, DateFormat.SHORT);
			journalisation = shortDateFormat.format(date) + LF + LF;
		}
		
		journalisation +=  "  Classe " + className + LF + LF + TAB + "Begin " 
			+ className + LF;

		// Visualiser entete des tests
		System.out.println(LF + "--- Tests unitaires de la classe " +
							name + " - Version " + version + " ---" + LF);
	}

	/**
	 * 
	 * La methode End visualise d'abord le compte rendu (CR) du dernier
	 * "test case" et/ou du dernier "test design" du module, en fonction du
	 * niveau de visualisation courant (attribut verboseLevel).
	 * 
	 * Seuls les CR non nuls sont visualises.
	 * 
	 * La methode End visualise ensuite la ligne de cloture du module de tests
	 * unitaires, si et seulement si ce dernier s'est termine globalement avec
	 * succes. Dans la charte, un "test design" (y compris le dernier) n'est
	 * execute que si le precedent se termine avec succes (CR=0).
	 * 
	 **/
	public static void End() {
		String lastLine = LF + "------------------------------------------------------------" + LF;

		// Visualiser le CR dernier test case et dernier test design
		//
		visuCR("Case");
		visuCR("Design");
		
		journalisation += TAB + "End " + className + LF;
	
		// Visualiser la ligne de cloture des tests unitaires
		//
		if (statusDesign == 0)
		{
			System.out.println(lastLine);
		}
		else
		{
			System.out.println();
		}
	}

	/**
	 * 
	 * La methode Design visualise d'abord le compte rendu (CR) du dernier
	 * "test case" et/ou du dernier "test design" executes, en fonction du
	 * niveau de visualisation courant (attribut verboseLevel).
	 * 
	 * La methode renseigne ensuite les attributs du "test design" et le niveau
	 * requis de visualisation (second parametre).
	 * 
	 * La methode visualise pour terminer la ligne d'entete des "test design".
	 * 
	 **/
	public static void Design(String name, int level) {
		// Controler les conditions initiales requises par la charte
		//
		if (!valid("Begin"))
			throw new RuntimeException();

		// Visualiser le CR du test case et du test design precedents
		//
		visuCR("Case");
		visuCR("Design");

		// Controler que le status OK du test design precedent
		//
		if (statusDesign != 0)
			throw new RuntimeException();

		// Initialiser les attributs du test design
		//
		levelCase = 0;
		statusCase = 0;
		statusDesign = 0;
		designName = name;
		levelDesign++;

		// Controler et affecter le nouveau niveau de visualisation
		//
		setVerboseLevel(level);
		
		// Visualiser la ligne d'entete du nouveau test design
		//
		visuEntete(name);
	}

	/**
	 * 
	 * La methode Case visualise d'abord le compte rendu (CR) du dernier
	 * "test case" execute, en fonction du niveau de visualisation courant
	 * (attribut verboseLevel).
	 * 
	 * La methode renseigne ensuite les attributs du "test case".
	 * 
	 * La methode visualise pour terminer la ligne d'entete des "test cases".
	 * 
	 **/
	public static void Case(String name) {
		// Controler les conditions initiales requises par la charte
		//
		if (!valid("Begin") || !valid("Design"))
			throw new RuntimeException();

		// Visualiser le CR du dernier test case execute
		//
		visuCR("Case");

		// Initialiser les attributs du nouveau test case
		//
		caseName = name;
		levelCase++;

		// Visualiser la ligne d'entete du nouveau test case
		//
		visuEntete();
	}

	public static void Case(String name, int level) {

		// Modifier le niveau verbose courant
		//
		setVerboseLevel(level);

		// Appliquer la methode d'origine
		//
		Case(name);
	}

	/**
	 * 
	 * La methode Unit visualise d'abord les deux messages standard d'un test
	 * elementaire, en fonction du niveau de visualisation courant (attribut
	 * verboseLevel).
	 * 
	 * La methode elabore pour terminer les CR du "test case" et du "test
	 * design" courant.
	 * 
	 **/
	public static void Unit(Object attendu, Object obtenu) {
		// Controler les conditions initiales requises par la charte
		//
		if (!valid("Begin") || !valid("Design") || !valid("Case"))
			throw new RuntimeException();

		// Visualiser les messages du test de ce niveau
		//
		visuEntete(attendu, obtenu);

		// Elaborer le CR du test case courant
		//
		if (statusCase != 0 || (obtenu == null && attendu == null))
			return;

		if ((obtenu == null && attendu != null)
				|| (obtenu != null && attendu == null)
				|| !obtenu.equals(attendu))
			statusCase = levelCase;
		else
			compteurTU++;

		// Elaborer le CR du test design courant
		//
		if (statusDesign == 0 && statusCase != 0)
			statusDesign = statusCase;
	}

	public static void Unit(int attendu, int obtenu) {
		// Effectuer un "boxing" des deux parametres
		//
		Unit(new Integer(attendu), new Integer(obtenu));
	}

	public static void Unit(double attendu, double obtenu) {
		// Effectuer un "boxing" des deux parametres
		//
		Unit(new Double(attendu), new Double(obtenu));
	}

	public static void Unit(double attendu, double obtenu, double epsilon) {
		// Controler les conditions initiales requises par la charte
		//
		if (!valid("Begin") || !valid("Design") || !valid("Case"))
			throw new RuntimeException();

		// Visualiser les messages du test de ce niveau
		//
		visuEntete(new Double(attendu), new Double(obtenu));

		// Elaborer le CR du test case courant
		//
		if (statusCase != 0)
			return;
		if (Math.abs(attendu - obtenu) > Math.abs(epsilon))
			statusCase = levelCase;

		// Incrementer le compteur si la valeur obtenue est egale
		// a la valeur obtenue
		//
		if (Math.abs(attendu - obtenu) < Math.abs(epsilon))
			compteurTU++;

		// Elaborer le CR du test design courant
		//
		if (statusDesign == 0 && statusCase != 0)
			statusDesign = statusCase;
	}

	public static void Unit(Object[] attendu, Object[] obtenu) {
		boolean ok = true;

		// Controler les conditions initiales requises par la charte
		//
		if (!valid("Begin") || !valid("Design") || !valid("Case"))
			throw new RuntimeException();

		// Visualiser les messages du test de ce niveau
		//
		visuEntete(attendu, obtenu);

		// Elaborer le CR du test case courant
		//
		if (statusCase != 0 || (obtenu == null && attendu == null))
			return;

		if ((obtenu == null && attendu != null)
				|| (obtenu != null && attendu == null)) {
			statusCase = levelCase;
			ok = false;
		}

		else {
			String classeAttendue, classeObtenue;

			// Traiter le cas de tableaux de tailles differentes
			//
			if (attendu.length != obtenu.length) {
				statusCase = levelCase;
				ok = false;
			}

			// Analyser tous les elements deux a deux
			//
			else
				for (int i = 0; i < attendu.length; i++) {
					// Traiter le cas particulier de deux references nulles
					//
					if (attendu[i] == null && obtenu[i] == null)
						continue;

					// Traiter le cas particulier d'une reference nulle
					// seulement
					//
					if ((attendu[i] == null && obtenu[i] != null)
							|| (attendu[i] != null && obtenu[i] == null)) {
						statusCase = levelCase;
						ok = false;
						break;
					}

					// Controler les deux classes d'origine
					//
					classeAttendue = attendu[i].getClass().getName();
					classeObtenue = obtenu[i].getClass().getName();
					if (!classeAttendue.equals(classeObtenue)) {
						statusCase = levelCase;
						ok = false;
						break;
					}

					// Controler les deux valeurs courante
					//
					if (!attendu[i].equals(obtenu[i])) {
						statusCase = levelCase;
						ok = false;
						break;
					}
				}

		}
		if (ok)
			compteurTU++;

		// Elaborer le CR du test design courant
		//
		if (statusDesign == 0 && statusCase != 0)
			statusDesign = statusCase;
	}

	public static void Unit(Object[] attendus, Object obtenu) {
		boolean ok = true;
		// Controler les conditions initiales requises par la charte
		//
		if (!valid("Begin") || !valid("Design") || !valid("Case"))
			throw new RuntimeException();

		// Visualiser les messages du test de ce niveau
		//
		visuEntete(attendus, obtenu);

		// Elaborer le CR du test case courant
		//
		if (statusCase != 0 || (obtenu == null && attendus == null))
			return;

		if (attendus.length == 0) {
			statusCase = levelCase;
			ok = false;
		} else {

			String classeAttendue, classeObtenue;

			// Comparer la valeur obtenue avec chaque valeur possible
			//
			for (int i = 0; i < attendus.length; i++) {

				// Controler les deux classes d'origine
				//
				classeAttendue = attendus[i].getClass().getName();
				classeObtenue = obtenu.getClass().getName();

				if (!classeAttendue.equals(classeObtenue)) {
					statusCase = levelCase;
					ok = false;
					break;
				}

				// Controler les deux valeurs courantes
				//
				if (attendus[i].equals(obtenu)) {
					compteurTU++;
					return;
				}
			}

			statusCase = levelCase;
		}
		if (ok)
			compteurTU++;

		// Elaborer le CR du test design courant
		//
		if (statusDesign == 0 && statusCase != 0)
			statusDesign = statusCase;
	}

	/**
	 * 
	 * La methode privee valid verifie les conditions d'execution du niveau de
	 * tests passe en parametre.
	 * 
	 **/
	private static boolean valid(String level) {
		String headText = "\n*** Erreur fatale (Tests) : ";
		String errorBegin = "Begin requis au prealable\n";
		String errorDesign = "Design requis au prealable\n";
		String errorCase = "Case requis au prealable\n";

		// Controler l'execution prealable du service Begin
		//
		if (level.equals("Begin") && !testBegin) {
			System.out.println(headText + errorBegin);
			return false;
		}

		// Controler l'execution prealable du service Design
		//
		if (level.equals("Design") && levelDesign == 0) {
			System.out.println(headText + errorDesign);
			return false;
		}

		// Controler l'execution prealable du service Case
		//
		if (level.equals("Case") && levelCase == 0) {
			System.out.println(headText + errorCase);
			return false;
		}

		return true;
	}

	private static void visuEntete(String name) {
		String beginPart = "\n** [";
		String endPart = "]\n";

		if (verboseLevel > 0)
			System.out.print(beginPart + name + endPart);
	}

	private static void visuEntete() {
		String beginPart = "\n   + (";
		String endPart = ")\n";

		if (verboseLevel > 1)
			System.out.print(beginPart + caseName + endPart);
	}

	private static void visuEntete(Object attendu, Object obtenu) {
		String beginPart = "\n     Valeur attendue : ";
		String middlePart = "\n     Valeur obtenue  : ";
		String LF = "\n";

		if (verboseLevel > 2) {
			System.out.print(beginPart + attendu);
			System.out.print(middlePart + obtenu + LF);
		}
	}

	private static void visuEntete(Object[] attendus, Object obtenu) {
		String beginPart = "\n     Valeur attendue : ";
		String middlePart = "\n     Valeur obtenue  : ";
		String LF = "\n";

		if (verboseLevel > 2) {

			if (attendus == null)
				System.out.print(beginPart + attendus);
			else if (attendus.length == 0)
				System.out.print(beginPart + "{}");
			else {
				System.out.print(beginPart + "{");
				System.out.print(attendus[0]);
				for (int i = 1; i < attendus.length; i++) {
					System.out.print(" | " + attendus[i]);
				}
				System.out.print("}");
			}

			System.out.print(middlePart + obtenu + LF);
		}
	}
	
	/**
	 * Definit le niveau de verbose
	 */
	private static void setVerboseLevel(int level)
	{
		if (level > 3) verboseLevel = 3;
		else if (level < 0) verboseLevel = 0;
		else verboseLevel = level;
	}

	/**
	 * 
	 * La methode privee visuCR visualise le compte rendu courant du niveau de
	 * tests fourni en parametre.
	 * 
	 **/
	private static void visuCR(String level) {
		String beginPartCase = "     => CR= ";
		String beginPartDesign = "=> CR= ";

		// Visualiser le CR du dernier test case execute
		//
		if (level.equals("Case") && levelCase > 0 && verboseLevel > 1) {
			if (statusCase != 0) {
				journalisation += TAB + TAB + "Case " + caseName + " : Ko" + LF;

				if (verboseLevel == 2)
					System.out.print(beginPartCase + statusCase + LF);
				else
					System.out.print(LF + beginPartCase + statusCase + LF);
			}
			else
				journalisation += TAB + TAB + "Case " + caseName + " : Ok" + LF;
		}

		// Visualiser le CR du dernier test design execute
		//
		if (level.equals("Design") && verboseLevel > 0) {
			if (statusDesign != 0) {
				journalisation += TAB + TAB + TAB + "Design " + designName 
					+ " : Ko" + LF;
	
				if (verboseLevel == 1)
					System.out.print(beginPartDesign + statusDesign + LF);
				else
					System.out.print(LF + beginPartDesign + statusDesign + LF);
			}
			else
				journalisation += TAB + TAB + TAB + "Design " + designName 
					+ " : Ok" + LF;
		}
	}

	public static void Traitement(ArrayList<String> list) {
		for (String nom : list) {
			if(!className.equals(""))
				journalisation += LF + LF;
			try {
				Class<?> cl = Class.forName(nom);
				if (groupName.equals("") && !cl.getName().equals("")) {
					groupName = cl.getSimpleName();
				}
				Method m = cl.getMethod("main", String[].class);
				m.invoke(null, (Object)new String[]{});
				journalisation += TAB + "Test complet reussi." + LF;
			} catch (InvocationTargetException e) {
				journalisation += TAB + "N premiers tests reussis : " + compteurTU + "/" + nbTU + LF;
				System.err.println(e.getTargetException());
			} catch (Exception e) {
				System.err.println(e);
			}
		}
		journalisation();
	}

	private static void journalisation() {

		String path = "journalisation" + groupName + ".txt";
		
		try {			
			File f= new File(path);
			boolean buff = f.exists();
			BufferedWriter out = new BufferedWriter(new FileWriter(path, true));
			if(buff){
				out.newLine();
				out.newLine();
				out.newLine();
			}
			out.write(journalisation);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static abstract class Context implements Serializable {
		
		private static final long serialVersionUID = 42;
		
		public HashMap<Object, Object> c = new HashMap<Object, Object>();
		
		public abstract void Before();
	}

}