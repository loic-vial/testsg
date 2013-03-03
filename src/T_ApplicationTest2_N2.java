import testsg.Replayer;
import testsg.Tests;
import testsg.Tests.Context;

public abstract class T_ApplicationTest2_N2
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
		Tests.Begin("ApplicationTest2 (lot 2)", "1.0.0", 0);
		
		Replayer replayer = new Replayer();
		MyContext con = new MyContext();
		
		Tests.Design("Controle du changement de selection dans la liste", 3);
		{
			con.Before();
			replayer.replay("changeItemItalie");
			Tests.Case("Cas de la selection de l'Italie");
			{
				Tests.Unit("Italie", ((ApplicationTest2)con.c.get("ctrl")).getSelectedItem());
			}
			
			con.Before();
			replayer.replay("changeItemEspagne");
			Tests.Case("Cas de la selection de l'Espagne");
			{
				Tests.Unit("Espagne", 
						((ApplicationTest2)con.c.get("ctrl")).getSelectedItem());
			}
		}

		con = null;
		
		Tests.End();
	}
}
