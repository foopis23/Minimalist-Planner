package planner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileUtilities
{
	static Object readFile(File file)
	{
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object o = ois.readObject();
			ois.close();
			fis.close();
			return o;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	static File[] getAllFiles(String location)
	{
		File file = new File(location);
		if(!file.exists()||!file.isDirectory())
		{
			file.mkdirs();
		}
		File folder = new File(location);
		File[] listOfFiles = folder.listFiles();
		return listOfFiles;
	}
	
	static boolean writeFile(Object o, String location)
	{
		try {
			File file = new File(location);
			if(file.exists())
				file.createNewFile();
			
			FileOutputStream fos = new FileOutputStream(location);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(o);
			oos.close();
			fos.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	static void deleteFile(String location)
	{
		File file = new File(location);
		file.delete();
	}
}