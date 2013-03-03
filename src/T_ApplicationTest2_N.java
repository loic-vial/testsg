import java.util.ArrayList;
import testsg.Tests;

/**
 * Tests Unitaires de l'application ApplicationTest2
 */
public class T_ApplicationTest2_N
{
	public static void main(String[] args)
	{
		ArrayList<String> classes = new ArrayList<String>();
		
		classes.add("T_ApplicationTest2_N1");
		classes.add("T_ApplicationTest2_N2");
		classes.add("T_ApplicationTest2_N3");

		Tests.Traitement(classes);
	}
}
