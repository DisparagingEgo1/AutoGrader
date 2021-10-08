package java_container;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
//Java Dynamic Compile and Run
//Must add your jdk/bin to the Path variable for your system if javap does not work.
//currently run a junit on a project and capture the output, make program more modular and initiate from the command line
public class JDCR {
	private static String masterPath = "C:\\Users\\Ocean\\Desktop\\College\\CSCD 300\\Root";//Path to Root Folder
	private static String root;//Name of Root Folder
	private static ArrayList<ArrayList<String>> projectFiles = new ArrayList<ArrayList<String>>();//ArrayList containing ArrayLists of each student's project files
	private static final boolean DEBUG = true;
		
	public static void main(String[] args) {
		//Create an ArrayList of each project
		if(args.length == 0 && !DEBUG) {
			System.out.println("No path specified for project folders.");
			System.exit(0);
		}
		else if(args.length != 0) {
			masterPath = args[0];
		}
		String[] rt = masterPath.split("\\\\");
		root = rt[rt.length-1];
		compile(Paths.get(masterPath));
		//attempt to compile and run each project
		for(ArrayList<String> a: projectFiles) {
			compileAndRun(a);
		}
	}
	//isExecutable, output function for output and error
	private static boolean isExecutable(String classFilePath)throws IOException {
		classFilePath = classFilePath.replaceAll(".java", ".class");
		String[] args = {"javap",classFilePath};
		Process proc = Runtime.getRuntime().exec(args);
    	BufferedReader out = new BufferedReader(new InputStreamReader(proc.getInputStream()));
    	BufferedReader err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
    	String line = null;
    	while((line = out.readLine()) != null) {
    		if(line.contains("public static void main")) {
    			proc.destroy();
    			return true;
    		}
    	}
    	while ((line = err.readLine()) != null) {
    		System.out.println(line);
    	}
    	proc.destroy();
		return false;
	}
	private static boolean execute(String[]args) throws IOException {
		Process proc = Runtime.getRuntime().exec(args);
    	BufferedReader out = new BufferedReader(new InputStreamReader(proc.getInputStream()));
    	BufferedReader err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
    	String line = null;
    	boolean flag = true;
    	switch(args[0]) {
    		case "javac":
    			if((line = err.readLine()) != null) {
    				String[]lineArray = line.split("\\\\");
    				System.out.println("File: "+lineArray[lineArray.length -1].substring(0,lineArray[lineArray.length -1].indexOf(":"))+" did not compile.");
    				System.out.println();
    				return false;
    			}
    			else {
    				System.out.println("Project Compiled Successfully.");
    				System.out.println();
    				return true;
    			}
    		case "java":
    			while ((line = out.readLine()) != null)
    	        {
    	        	while(flag) { 
    	        		System.out.println("File Output");
    	        		System.out.println();
    	        		flag = false;
    	        	}
    	           System.out.println(line);
    	        }
    	        flag = true;
    	        while ((line = err.readLine()) != null)
    	        {
    	        	while(flag) { 
    	        		System.out.println("File Error Output");
    	        		System.out.println();
    	        		flag = false;
    	        	}
    	        	System.out.println(line);
    	           
    	        }
    			break;	
    	} 
    	proc.destroy();
    	return true;
	}
	//Takes each ArrayList from projectFiles and attempts to compile and run it
	private static void compileAndRun(ArrayList<String>f) {
		String[] args = new String[f.size()];
		//create the arguments to pass to the compiler
	    for(int i = 0; i < f.size();i++) {
	    	if(i == 0)args[i] = "javac";
	    	else args[i]=f.get(i);
	    }
	    try {
	    	//compile the project
	        //runs the project
	        String[] args2 = new String[4];
	        //create the default arguments to pass to the interpreter
	        args2[0]="java";
	        args2[1] = "-cp";
	        args2[2] = f.get(0);
	        for(int i = 3;i<args.length;i++) {
	        	//test each java file to see if it compiled and has a main. If it has one, will execute.
	        	if(execute(args) && isExecutable(args[i])) {
	        		System.out.println("Testing if file "+args[i].substring(args[i].lastIndexOf("\\")+1,args[i].length())+" compiled and has a main: "+isExecutable(args[i]));
		        	args2[3]=args[i];
		        	System.out.println();
		            System.out.println("Attempting to run: "+args2[3].substring(args2[3].lastIndexOf("\\")+1,args2[3].length()));
		            execute(args2);
		            System.out.println();
	        	}
	        }
	    }
        catch(Exception e) {
        	System.out.println(e.getMessage());
        }
        System.out.println();
	    		
	}
/*
Creates a header String from pathName that is the path .\root_folder_name\project_folder_name
e.g pathName = C:\\Users\\ostrc\\Desktop\\College\\CSCD 300\\Root\\Test1\\Test.java
String header = C:\\Users\\ostrc\\Desktop\\College\\CSCD 300\\Root\\Test1
It is vitally important that the project folders are stored directly under root otherwise code may
not compile and/or run
		
Then checks if any arrayList in projectFiles has the same header, headers are always
the first entry in the ArrayLists in projectFiles
		
If it does then it adds pathName to that arrayList
pathName is always a file path to a java file
		
Otherwise creates a new ArrayList with the following default values and adds it to projectFiles
ArrayList.get(0)= header
ArrayList.get(1) = "-cp"
ArrayList.get(2) = masterPath
ArrayList.get(3) = pathName
		 
For each ArrayList in projectFiles, indexes 0,1, and 2 are reserved and must not be assigned to anything new
Indices 3 and on are absolute paths to java files.
		 
TO-DO: Need to handle potential duplicate file names, perhaps when the files are submitted?, e.g. SmithJLab1, 2SmithJLab1
*/
	private static void editprojectFiles(String pathName) {
		String[] temp = pathName.split("\\\\");//Split path name into parts for parsing
		String header = "";
		//create the source file path for each project
		for(int i = 0; !(i != 0 && temp[i-1].equals(root));i++) {
			
			if( temp[i].equals(root) && i +1 <temp.length) {
				header = header.concat(temp[i]+"\\"+temp[i+1]);
			}
			
			else if(temp[i].equals(root)) header = header.concat(temp[i]);
			
			else header = header.concat(temp[i]+"\\");
		}
		//check if this file is part of any other project
		for(int i = 0; !(i != 0 && projectFiles.get(i-1).get(0).equals(header));i++) {
			
			if(projectFiles.size() != 0 && projectFiles.get(i).get(0).equals(header)) {
				projectFiles.get(i).add(pathName);
			}
			//if projectFiles is empty or we are at the end of it without a match
			else if(projectFiles.size() == 0 || i == projectFiles.size()-1) {
				projectFiles.add(i,new ArrayList<String>());
				projectFiles.get(i).add(header);
				projectFiles.get(i).add("-cp");
				projectFiles.get(i).add(masterPath);
				projectFiles.get(i).add(pathName);	
			}
		}
			
	}
/*
This grabs all java files in the directory path dir. For each .java file it sends the path to that file
to editprojectFiles which will add it into the projectFiles ArrayList
*/
	private static void compile(Path dir) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
		    for (Path file: stream) {
		    	if(file.toFile().isDirectory()) {
		    		compile(file.toAbsolutePath());
		    	}
		    	else if(file.toString().endsWith(".java")) {
				    editprojectFiles(file.toString());
		    	}
		    }
		    stream.close();
		} catch (IOException | DirectoryIteratorException x) {
		    // IOException can never be thrown by the iteration.
		    // In this snippet, it can only be thrown by newDirectoryStream.
		    System.err.println(x);
		}
		
	}
}

