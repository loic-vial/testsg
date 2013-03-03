package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class Data
{
	public static boolean store(Object data, String name)
	{                          	
		if (data == null) return false;

		if (!name.endsWith(".data")) name += ".data";
  
		try
		{
			File f = new File(name);
			f.getParentFile().mkdirs();
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(data);
		}
		catch (Exception e)
		{
			return false;
		}
  
		System.out.println("Enregistrement du fichier " + name + " : OK");
		return true;
	}

	public static Object load (String name)
	{
		Object data = null;
		
		if (!name.endsWith(".data")) name += ".data";
		  
		try
		{
			FileInputStream f = new FileInputStream(name);
			ObjectInputStream in = new ObjectInputStream(f);
			data = in.readObject();
		}
		catch (Exception e)
		{
			return null;
		}
		
		System.out.println("Chargement du fichier " + name + " : OK");
		return data;
   }
}
