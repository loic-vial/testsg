
import testsg.Tests;
import testsg.Tests.Context;


public class T_Essai_N3
{
	public static class MyContext extends Context
	{
		private static final long serialVersionUID = 1L;

		public void Before()
		{
			c.put("val1", 19);
			c.put("val2", 41);
		}		
	}
	
	public static void main(String[] args)
	{
		Tests.Begin("Essai_3", "osef", 6);
		
		Context con = new MyContext();

		con.Before();
		
		Tests.Design("D1", 3);
		{
			Tests.Case("C1.1");
			{
				Tests.Unit(con.c.get("val1"), 19);
				Tests.Unit(con.c.get("val2"), 41);
			}
		}
		
		Tests.Design("D2", 3);
		{
			Tests.Case("C2.1");
			{
				Tests.Unit(con.c.get("val1"), 19);
				Tests.Unit(con.c.get("val2"), 41);
			}
			Tests.Case("C2.2");
			{
				Tests.Unit(con.c.get("val1"), 19);
				Tests.Unit(con.c.get("val2"), 41);
			}
		}
		
		Tests.End();
	}

}
