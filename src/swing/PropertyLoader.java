package swing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import utility.SMessageDialog;

public class PropertyLoader
{
	public static final String filename = Slyum.getPathAppDir() + Slyum.FILE_SEPARATOR + "config.properties";

	private static PropertyLoader instance;
	
	private Properties properties = new Properties();

	private PropertyLoader() throws IOException
	{
		createPropertiesFile();				
		
		final FileInputStream input = new FileInputStream(filename);

		properties.load(input);

		input.close();
	}

	public void createPropertiesFile()
	{
		final File file = new File(filename);
		
		if (file.exists())
			return;
	
		try
		{
			file.createNewFile();
		}
		catch (final IOException e)
		{
			SMessageDialog.showErrorMessage("Error to create config file.");
		}
	}
	
	/**
	 * Push properties in properties file.
	 */
	public void push()
	{
		final File file = new File(filename);
		
		try
		{
			OutputStream out = new FileOutputStream(file);
			
			properties.store(out, "Generals properties of Slyum");
		}
		catch (FileNotFoundException e)
		{
			createPropertiesFile();
			push();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static PropertyLoader getInstance()
	{
		if (instance == null)
			
			try
			{
				instance = new PropertyLoader();
			} catch (IOException e)
			{
				SMessageDialog.showErrorMessage("Error with properties. Cannot load or save properties file.\nTry to launch Slyum with administrators rights.");
			}
		
		return instance;
	}

	public Properties getProperties()
	{
		return properties;
	}
}
