import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
public class CompileTest {
	
	private static final String masterPath = "C:\\Users\\ostrc\\Desktop\\College\\CSCD 300\\Root";//Path to Root Folder
	private static final JavaCompiler jCompiler = ToolProvider.getSystemJavaCompiler(); // compiler
	private static ArrayList<ArrayList<String>> codeFiles = new ArrayList<ArrayList<String>>();//ArrayList containing ArrayLists of each student's project files
	private static final String root = "Root";//Name of Root Folder
	
	public static void main(String[] args) {
		//Create the project files
		compile(Paths.get(masterPath));
		//attempt to compile and run each project
		for(ArrayList<String> a: codeFiles) {
			compileAndRun(a);
		}
	}
	//Takes each ArrayList from codeFiles and attempts to compile it
	public static void compileAndRun(ArrayList<String>f) {
		String[] args = null;
		args = new String[f.size()-1];
	    for(int i = 1; i < f.size();i++) {
	    	args[i-1]=f.get(i);

	    }
        jCompiler.run(null,null,null,args);
        //This will be used to print out files that compiled or didn't compile. Currently prints as if all
        //files compiled
        for(int i = 0; i < f.size();i++) {
        	if(i < 3);
        	else {
        	System.out.println("Compiled: "+f.get(i));
        	}
        }
        //runs compiled code
        String[] ar = new String[4];
        ar[0]="java";
        ar[1] = "-cp";
        ar[2] = f.get(0);
        for(int i = 2;i<args.length;i++) {
        	ar[3]=args[i];
        	try {
            	Process proc = Runtime.getRuntime().exec(ar);
            	BufferedReader out = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            	BufferedReader err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            	String line = null;
            	System.out.println();
            	System.out.println("Attempting to run: "+ar[3].substring(ar[3].lastIndexOf("\\")+1,ar[3].length()));
                while ((line = out.readLine()) != null)
                {
                   System.out.println(line);
                }
                while ((line = err.readLine()) != null)
                {
                	if(!line.contains("can't find main(String[]) method in class"))System.out.println(line);
                	else if(line.contains("can't find main(String[]) method in class"))System.out.println("No Main Method: "+ar[3].substring(ar[3].lastIndexOf("\\")+1,ar[3].length()));
                   
                }
            	proc.destroy();
            	
            }
            catch(Exception e) {
            	System.out.println(e.getMessage());
            }
    
        }
	    		
	}
/*
Creates a header String from pathName that is the path .\root_folder_name\project_folder_name
e.g pathName = C:\\Users\\ostrc\\Desktop\\College\\CSCD 300\\Root\\Test1\\Test.java
String header = C:\\Users\\ostrc\\Desktop\\College\\CSCD 300\\Root\\Test1
It is vitally important that the project folders are stored directly under root otherwise code will
not compile or run
		
Then checks if any arrayList in codeFiles has the same header, headers are always
the first entry in the ArrayLists in codeFiles
		
If it does then it adds pathName to that arrayList pathName is always a file path to a java file
		
Otherwise creates a new ArrayList with the following default values and adds it to codeFiles
ArrayList.get(0)= header
ArrayList.get(1) = "-cp"
ArrayList.get(2) = masterPath
ArrayList.get(3) = pathName
		 
For each ArrayList in codeFiles, indexes 0,1, and 2 are reserved and must not be assigned to anything new
		 
TO-DO: Need to handle potential duplicate file names, perhaps when the files are submitted?, e.g. SmithJLab1 
*/
	public static void editCodeFiles(String pathName) {
		String[] temp = pathName.split("\\\\");
		String header = "";
		for(int i = 0; i <temp.length;i++) {
			if(i != 0 && temp[i-1].equals(root) ) {
				header = header.concat(temp[i]);
				break;
			}
			header = header.concat(temp[i]+"\\");
		}
		if(codeFiles.size() == 0) {
			codeFiles.add(new ArrayList<String>());
			codeFiles.get(0).add(header);
			codeFiles.get(0).add("-cp");
			codeFiles.get(0).add(masterPath);
			codeFiles.get(0).add(pathName);
			
		}
		else {
			for(ArrayList<String> a : codeFiles) {
				if(a.get(0).equals(header)) {
					a.add(pathName);
					return;
				}
			}
			codeFiles.add(new ArrayList<String>());
			codeFiles.get(codeFiles.size()-1).add(header);
			codeFiles.get(codeFiles.size()-1).add("-cp");
			codeFiles.get(codeFiles.size()-1).add(masterPath);
			codeFiles.get(codeFiles.size()-1).add(pathName);
			
		}
		
	}
	/*
	 	This grabs all java files in the directory path dir. For each .java file it sends the path to that file
	 	to editCodeFiles which will add it into the codeFiles ArrayList
	 */
	public static void compile(Path dir) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
		    for (Path file: stream) {
		    	if(file.toFile().isDirectory()) {
		    		compile(file.toAbsolutePath());
		    	}
		    	else if(file.toString().endsWith(".java")) {
				    editCodeFiles(file.toString());
		    	}
		    }
		    stream.close();
		} catch (IOException | DirectoryIteratorException x) {
		    // IOException can never be thrown by the iteration.
		    // In this snippet, it can only be thrown by newDirectoryStream.
		    System.err.println(x);
		}
		
	}
	//Testing code
	//
	/*String [] s = new String[3];
	s[0] = "-cp";
	s[1] = "C:\\Users\\ostrc\\Desktop\\College\\CSCD 300\\TestFIle";
	//s[3]= "C:\\Users\\ostrc\\Desktop\\College\\CSCD 300\\TestFIle\\Test1\\Test.java";
	s[2] ="C:\\Users\\ostrc\\Desktop\\College\\CSCD 300\\TestFIle\\Test1\\test2\\TestClass.java";
	jCompiler.run(null, null, null, s);
	s[2] ="C:\\Users\\ostrc\\Desktop\\College\\CSCD 300\\TestFIle\\Test1\\Test.java";
	jCompiler.run(null, null, null, s);*/
}
