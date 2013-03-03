
import testsg.Tests;
import testsg.Tests.Context;


public class T_Essai_N1
{
	public static class MyContext extends Context
	{
		private static final long serialVersionUID = 1L;

		public void Before()
		{
			c.put("val1", 42);
			c.put("val2", 21);
		}		
	}
	
	public static void main(String[] args)
	{
		Tests.Begin("Essai_1", "osef", 3);
		
		Context con = new MyContext();

		con.Before();
		
		Tests.Design("D1", 3);
		{
			Tests.Case("osefu");
			{
				Tests.Unit(con.c.get("val1"), 4);
				Tests.Unit(con.c.get("val2"), 21);
			}
			Tests.Case("gfgfg");
			{
				Tests.Unit(con.c.get("val1"), 42);
			}
		}
		
		Tests.End();
	}

}
