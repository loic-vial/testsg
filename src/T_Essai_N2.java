
import testsg.Tests;
import testsg.Tests.Context;


public class T_Essai_N2
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
		Tests.Begin("Essai_2", "osef", 10);
		
		Context con = new MyContext();

		con.Before();
		
		Tests.Design("D1", 3);
		{
			Tests.Case("C1.1");
			{
				Tests.Unit(con.c.get("val1"), 19);
				Tests.Unit(con.c.get("val2"), 41);
			}
			Tests.Case("C1.2");
			{
				Tests.Unit(con.c.get("val1"), 15);
				Tests.Unit(con.c.get("val2"), 41);
			}
			Tests.Case("C1.3");
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
