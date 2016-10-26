package lib;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class _Base {

	Properties propConfig = null;
	Properties propTestData = null;
	
	public void loadConfig(){
		propConfig = new Properties();
		InputStream in = null;
		try{
			in = new FileInputStream("config.properties");
			propConfig.load(in);
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				
		}
	}
	
	public String getConfig(String itemName){
		return propConfig.getProperty(itemName);
	}
	
	public void loadTestData(){
		propTestData = new Properties();
		InputStream in = null;
		try{
			in = new FileInputStream("./src/test/resources/testdata.properties");
			propTestData.load(in);
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				
		}
	}
	
	public String getTestData(String itemName){
		return propTestData.getProperty(itemName);
	}
	
	public void wait(int seconds){
		try {
			Thread.sleep(seconds*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
