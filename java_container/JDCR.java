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
//currently run a junit on a project and capture the output, make program more modular and initiate from the command line
public class JDCR {
	private static String masterPath = "C:\\Users\\ostrc\\Desktop\\College\\CSCD 300\\Root";//Path to Root Folder
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
	private static void execute(String[]args) throws IOException {
		Process proc = Runtime.getRuntime().exec(args);
    	BufferedReader out = new BufferedReader(new InputStreamReader(proc.getInputStream()));
    	BufferedReader err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
    	String line = null;
    	boolean flag = true;
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
        	if(line.contains("Cannot run program \"-cp\""));
        	else if(line.contains("can't find main(String[]) method in class"))System.out.println("No Main Method: "+args[3].substring(args[3].lastIndexOf("\\")+1,args[3].length()));
        	else System.out.println(line);
           
        }
    	proc.destroy();
	}
	//Takes each ArrayList from projectFiles and attempts to compile and run it
	private static void compileAndRun(ArrayList<String>f) {
		String[] args = new String[f.size()];
		//create the arguments to pass to the compiler
	    for(int i = 0; i < f.size();i++) {
	    	if(i == 0)args[i] = "javac";
	    	else args[i]=f.get(i);
	    }
	    //compile the project
	    try {
	    	execute(args);
	    }
	    catch(Exception e) {
        	System.out.println(e.getMessage());
        }
        //This will be used to print out files that compiled or didn't compile. Currently prints as if all
        //files compiled
        for(int i = 0; i < f.size();i++) {
        	if(i < 3);
        	else {
        	System.out.println("Compiled: "+f.get(i));
        	}
        }
        //runs code
        String[] args2 = new String[4];
        //create the default arguments to pass to the interpreter
        args2[0]="java";
        args2[1] = "-cp";
        args2[2] = f.get(0);
        for(int i = 3;i<args.length;i++) {
        	//test each java file for a main. If it has one, will execute. Need to use javap to determine main
        	args2[3]=args[i];
        	try {
        		System.out.println();
            	System.out.println("Attempting to run: "+args2[3].substring(args2[3].lastIndexOf("\\")+1,args2[3].length()));
            	execute(args2);
            	
            }
            catch(Exception e) {
            	System.out.println(e.getMessage());
            }
    
        }
        System.out.println();
	    		
	}
/*
Creates a header String from pathName that is the path .\root_folder_name\project_folder_name
e.g pathName = C:\\Users\\ostrc\\Desktop\\College\\CSCD 300\\Root\\Test1\\Test.java
String header = C:\\Users\\ostrc\\Desktop\\College\\CSCD 300\\Root\\Test1
It is vitally important that the project folders are stored directly under root otherwise code will
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
		for(int i = 0; !(i != 0 && (i <projectFiles.size() || projectFiles.get(i-1).get(0).equals(header)));i++) {
			if(projectFiles.size() != 0 && projectFiles.get(i).get(0).equals(header)) {
				projectFiles.get(i).add(pathName);
			}
			else if(projectFiles.size() == 0 || i == projectFiles.size()-1) {
				projectFiles.add(new ArrayList<String>());
				projectFiles.get(projectFiles.size()-1).add(header);
				projectFiles.get(projectFiles.size()-1).add("-cp");
				projectFiles.get(projectFiles.size()-1).add(masterPath);
				projectFiles.get(projectFiles.size()-1).add(pathName);	
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

