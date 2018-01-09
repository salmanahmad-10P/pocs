import java.io.*;
import java.lang.*;
 
public class GetFileTimeStamp {
 public static void main (String args[]){
  try {
     Runtime runtime = Runtime.getRuntime();
	 BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
	 System.out.println("Enter filename: ");
	 String fname=(String)br.readLine();
         String absoluteFilePath="/tmp"+fname;
     Process output = runtime.exec("dd if=/dev/zero bs=1024 count=0 of="+absoluteFilePath);
	 BufferedReader bufferedReader = new BufferedReader (new InputStreamReader(output.getInputStream()));
	 String out="";
	 String line = null;
     
	 int step=1;
      while((line = bufferedReader.readLine()) != null ) {
		  if(step==6){
		 out=line;
		 }
		  step++;
		  }               
	 try{
	 out=out.replaceAll(" ","");
	 System.out.println("Modification Date: "+out.substring(0,10));
	 System.out.println("Modification Time: "+out.substring(10,16)+"m");
	 }
	 catch(Exception e){
            e.printStackTrace();
	 }
     }
	 catch(Exception e){e.printStackTrace();}
  }
}
