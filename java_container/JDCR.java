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
//TO-DO: Implement Junit Testing and write project output to files
public class JDCR {
	private static String masterPath;//Path to Root Folder
	private static String root;//Name of Root Folder
	private static String jUnitJarPath;
	private static String jUnitTestName;
	private static ArrayList<ArrayList<String>> projectFiles = new ArrayList<ArrayList<String>>();//ArrayList containing ArrayLists of each student's project files
	private static final boolean DEBUG = true;//disable needing command line arguments for testing
		
	
	public static void main(String[] args) {
		//Set the path to the root or use the default otherwise
		parseArgs(args);
		getProjectFiles(Paths.get(masterPath));
		//attempt to compile and run each project
		for(ArrayList<String> a: projectFiles) {
			compileAndRun(a);
		}
	}
	//Parses arguments passed into it from the command line
	private static void parseArgs(String[] args) {
		if(DEBUG) {
			masterPath = "C:\\Users\\Ocean\\Desktop\\College\\CSCD 300\\Root";//Default Root Path
			jUnitJarPath = "";
			jUnitTestName = "";
		}
		else {
			if(args.length == 0 || args.length == 2 || args[0].equals("--help")) {
				printHelp();
				System.exit(0);
			}
			else if(args.length == 1) {
				masterPath = args[0];
				jUnitJarPath = "";
				jUnitTestName = "";
			}
			else {
				masterPath = args[0];
				jUnitJarPath = args[1];
				jUnitTestName = args[2];
			}
		}
		String[] rt = masterPath.split("\\\\");
		root = rt[rt.length-1];
	}
	//Displays Command Line Help
	private static void printHelp() {
		System.out.println();
		System.out.println("JDCR <path_to_root_directory_for_projects> optional <path_to_junit.jar> <name of Testing File>");
		System.out.println();
		System.out.println("If no path to junit.jar or a name of a testing file is provided, will just execute projects in the root directory");
		System.out.println();
	}
	/*
	 * Determines if a java.class file has a main method in it. If the system doesn't recognize
	 * javap then it means the jdk/bin directory is not in your Path variable
	 * 
	 * Returns true if the class file has a main, false otherwise
	 */
	private static boolean isExecutable(String classFilePath)throws IOException {
		classFilePath = classFilePath.replaceAll(".java", ".class");
		String[] args = {"javap",classFilePath};
		Process proc = Runtime.getRuntime().exec(args);
		String out = getOutput("File Output",new BufferedReader(new InputStreamReader(proc.getInputStream())));
    	String err = getOutput("Error Output",new BufferedReader(new InputStreamReader(proc.getErrorStream())));
    	proc.destroy();
    	//check the class file for main
    		if(out.contains("public static void main")) {
    			return true;
    		}
    	//if there should be some error, display it
    	if(!err.isEmpty())System.out.print(err);
		return false;
	}
	/*
	 * Compiles and Executes java files
	 * String[]args will either contain the arguments for the compiler and args[0] will be javac or
	 * the arguments for the interpreter in which case args[0] will be java. Any projects that reach
	 * the interpreter will not contain any compile time exceptions but may crash with runtime exceptions
	 * 
	 * Returns true if no errors were encountered or false otherwise
	 */
	private static boolean execute(String[]args) throws IOException {
		//parse output as either a compile or execution depending on args[0]
		Process proc = Runtime.getRuntime().exec(args);
    	String out = getOutput("File Output",new BufferedReader(new InputStreamReader(proc.getInputStream())));
    	String err = getOutput("Error Output",new BufferedReader(new InputStreamReader(proc.getErrorStream())));
    	proc.destroy();
    	switch(args[0]) {
    		case "javac":
    			//If not empty, means the program failed to compile
    			if(!err.isEmpty()) {
    				String[]lineArray = err.split("\n");
    				System.out.println("---------------------------------------------------------------------------------------------");
    				for(int i =0; !(i != 0 &&  lineArray[i-1].contains(masterPath));i++) {
    					if(lineArray[i].contains(masterPath)) {
    		   				System.out.println("File: "+lineArray[i].substring(lineArray[i].lastIndexOf("\\")+1,lineArray[i].indexOf(".java")+5)+" did not compile.");
    		   				System.out.println("Path: "+lineArray[i].substring(0,lineArray[i].lastIndexOf("\\")));
    					}
    				}
    				System.out.println("---------------------------------------------------------------------------------------------");
     				return false;
    			}
    			//compiled successfully
    			else {
    				System.out.println(args[3].substring(args[3].lastIndexOf("\\")+1)+" Compiled Successfully.");
    				break;
    			}
    		case "java":
    			//display output of program
    			System.out.print(out);
    			System.out.print(err);
    			break;	
    	} 
    	return true;
	}
	/*
	 * Gets the output from the compiler or interpreter
	 */
	private static String getOutput(String outputType,BufferedReader out) throws IOException{
		String line = out.readLine(),output = "";
		if(line == null);
		else {
			output += outputType + "\n";
			output += "---------------------------------------------------------------------------------------------\n"; 
			do {
				output += line +"\n";
			}while((line = out.readLine()) != null);
			output += "---------------------------------------------------------------------------------------------\n"; 
		}
		out.close();
		return output;
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
	    	//create the default arguments to pass to the interpreter
	        String[] args2 = new String[4];
	        args2[0]="java";
	        args2[1] = "-cp";
	        args2[2] = f.get(0);
	        //test to see if the project will compile
	        if(execute(args)) {
		        for(int i = 3;i<args.length;i++) {
		        	//test each java file to see if it has a main. If it has one, will execute.
		        	if(isExecutable(args[i])) {
			        	args2[3]=args[i];
			            System.out.println("Running: "+args2[3].substring(args2[3].lastIndexOf("\\")+1,args2[3].length()));
			            System.out.println("Path: "+args2[2]);
			            System.out.println();
			            execute(args2);
		        	}
		        }
	        }
	    }
        catch(Exception e) {
        	System.out.println(e.getMessage());
        }	    		
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
	private static void getProjectFiles(Path dir) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
		    for (Path file: stream) {
		    	if(file.toFile().isDirectory()) {
		    		getProjectFiles(file.toAbsolutePath());
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

