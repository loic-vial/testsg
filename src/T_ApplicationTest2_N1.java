import testsg.Replayer;
import testsg.Tests;
import testsg.Tests.Context;

public abstract class T_ApplicationTest2_N1
{
	public static class MyContext extends Context
	{
		private static final long serialVersionUID = 42;

		public void Before()
		{
			c.clear();
			System.gc();
			c.put("ctrl", new ApplicationTest2());
		}
	}
	
	public static void main(String[] args)
	{
		Tests.Begin("ApplicationTest2 (lot 1)", "1.0.0", 0);
		
		Replayer replayer = new Replayer();
		MyContext con = new MyContext();
		
		Tests.Design("Controle du changement de titre de l'application", 3);
		{
			con.Before();
			replayer.replay("changeTitreVide");
			Tests.Case("Cas d'un titre vide");
			{
				Tests.Unit("", ((ApplicationTest2)con.c.get("ctrl")).getTitle());
			}
			
			con.Before();
			replayer.replay("changeTitreLong");
			Tests.Case("Cas d'un titre long");
			{
				Tests.Unit("Changement de titre !", 
						((ApplicationTest2)con.c.get("ctrl")).getTitle());
			}
		}

		con = null;
		
		Tests.End();
	}
}
