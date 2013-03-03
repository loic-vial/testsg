import testsg.Replayer;
import testsg.Tests;
import testsg.Tests.Context;

public abstract class T_ApplicationTest2_N3
{
	public static class MyContext extends Context
	{
		private static final long serialVersionUID = 42;

		public void Before()
		{
			c.clear();
			c.put("ctrl", new ApplicationTest2());
		}
	}
	
	public static void main(String[] args)
	{
		Tests.Begin("ApplicationTest2 (lot 3)", "1.0.0", 0);
		
		Replayer replayer = new Replayer();
		MyContext con = new MyContext();
		
		Tests.Design("Controle du bouton d'incrementation du compteur", 3);
		{
			con.Before();
			replayer.replay("incrementeCompteur10");
			Tests.Case("Cas de 10 pressions sur le bouton");
			{
				Tests.Unit(10, ((ApplicationTest2)con.c.get("ctrl")).getCompteur());
			}
		}

		con = null;
		
		Tests.End();
	}
}
