package JContainer;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/*  Java Dynamic Compile and Run
 * 
 * -Notes: Must add your jdk/bin path to the System Path variable, otherwise the isExecutable() function
 * 		   will not work.
 * 
 * TO-DO: Tristan: File and Testing Output for each project. 
 * 		  Trace: Gui
 * 		  Unassigned/Undecided: Simply Gui for uploading files to a server, Output graded test results to csv file in root(folder_name,Homework2,score)Flexibility to add more. Constant java
 * 								file that holds all the constants. Enhanced Command Line abilities such as setting default paths, reading an arguments file, setting debug/testing
 */

public class JDCR {
	private static String masterPath;//Path to Root Folder
	private static String root;//Name of Root Folder
	private static String jUnitJarPath;//path to junit.jar
	private static String jUnitTestPath;//path for junit testing file that will be run this session
	private static ArrayList<ArrayList<String>> projectFiles = new ArrayList<ArrayList<String>>();//ArrayList containing ArrayLists of each student's project files
	private static final boolean DEBUG = true;//disable needing command line arguments for testing
	private static boolean TESTING = true;//Used when a junit test will be run
		
	
	public static void main(String[] args)throws Exception {
		//Setup
		PrintStream originalOut = System.out;
		parseArgs(args);
		getProjectFiles(Paths.get(masterPath));
		//attempt to compile and run each project
		for(ArrayList<String> a: projectFiles) {
			if(TESTING) {
				//FileOutputStream f = new FileOutputStream(a.get(0) + "\\Results.txt");
                //System.setOut(new PrintStream(f));
                moveTestFile(a,1);
                compileAndRun(a);
                moveTestFile(a,2);
                //modify Results.txt method here
                //System.setOut(originalOut);

			}
			else {
				compileAndRun(a);
			}
		}
	}
	/*
	 * If TESTING is true then this will move the testing file stored at jUnitTestPath into each
	 * project folder and then return it after the test is complete.
	 * 
	 * -NOTE: If this program is interrupted before it completes it is possible that the junit Test file
	 * 		   will not be returned to the correct directory or may be deleted. Keep a backup and remember
	 * 		   to move it back.
	 */
	private static void moveTestFile(ArrayList<String> project,int flag) throws IOException {
		//move test file in
		String[] rt = jUnitTestPath.split("\\\\");
		if(flag == 1) {
			project.add((Files.move(Paths.get(jUnitTestPath),Paths.get(project.get(0)+"\\\\"+rt[rt.length-1]))).toString());
			System.out.println();
		}
		else {
			//clean up the testing file .class file
			try {
				Files.delete(Paths.get((project.get(0)+"\\\\"+rt[rt.length-1]).replace(".java", ".class")));
			}
			catch(Exception e){
				
			}
			//move test to its original directory
			project.remove(project.size()-1);
			Files.move(Paths.get(project.get(0)+"\\\\"+rt[rt.length-1]),Paths.get(jUnitTestPath));
			System.out.println();
		}
		
	}
	/*
	 * Parses the arguments passed from the command line
	 * 
	 * If DEBUG is true, then hard coded paths can be used
	 * Otherwise will accept the following
	 * 
	 * <path_to_root_directory_for_projects> 
	 * 
	 * OPTIONAL
	 * <fully_qualified_path_to_junit.jar> <fully_qualified_path of testing file>
	 * 
	 * Fully Qualified Paths(FQP) include the file in the path.
	 * 
	 * If a path to the junit.jar and a testing file is not provided, then it will simply compile and run
	 * the programs stored under <path_to_root_directory_for_projects>
	 * 
	 * If they are supplied then TESTING will be set to true to allow the program to run the junit test at <fully_qualified_path of testing file> 
	 * against each project stored under <path_to_root_directory_for_projects>
	 */
	private static void parseArgs(String[] args) {
		//Test Paths
		if(DEBUG) {
			masterPath = ".\\JContainer\\RootForProjects";
			jUnitJarPath = ".\\JContainer\\JunitJar\\junit.jar";//FQP
			jUnitTestPath = ".\\JContainer\\JunitTestFile\\MyLinkedListTest.java";//FQP
			masterPath = Paths.get(masterPath).toAbsolutePath().toString().replace("\\.\\", "\\");
			jUnitJarPath = Paths.get(jUnitJarPath).toAbsolutePath().toString().replace("\\.\\", "\\");
			jUnitTestPath = Paths.get(jUnitTestPath).toAbsolutePath().toString().replace("\\.\\", "\\");
		}
		//Command Line Arguments
		else {
			if(args.length == 0 || args.length == 2 || args[0].equals("--help")) {
				printHelp();
				System.exit(0);
			}
			else if(args.length == 1) {
				masterPath = args[0];
				jUnitJarPath = "";
				jUnitTestPath = "";
			}
			else {
				masterPath = args[0];
				jUnitJarPath = args[1];
				jUnitTestPath = args[2];
				TESTING = true;
			}
		}
		//Get the root folder filename
		String[] rt = masterPath.split("\\\\");
		root = rt[rt.length-1];
	}
	
	//Displays Command Line Help
	private static void printHelp() {
		System.out.println();
		System.out.println("JDCR <path_to_root_directory_for_projects> OPTIONAL: <fully_qualified_path_to_junit.jar> <fully_qualified_path of testing file>");
		System.out.println();
		System.out.println("If no path to junit.jar or a name of a testing file is provided, will just execute projects in the root directory");
		System.out.println("<fully_qualified_path_to_junit.jar> must end with the .jar file");
		System.out.println("<fully_qualified_path of testing file> Must end with a .java file");
		System.out.println();
	}
	
	/*
	 * Determines if a java.class file has a main method in it. If the system doesn't recognize
	 * javap then it means the jdk/bin directory is not in your Path variable
	 * 
	 * Returns true if the class file has a main, false otherwise.
	 *  
	 * This method is only used when the user wants to run the projects individually and isn't 
	 * testing them. TESTING will be false
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
    				//Print full error Output
    				if(DEBUG) {
    					for(String s: lineArray) {
    						System.out.println(s);
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
	 * Gets the output from the compiler or interpreter and displays it
	 * 
	 * This or execute() will be the entry point for writing to a file and grading projects
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
	
	/*
	 * Attempts to compile and either run or test each project stored in the projectFiles arrayList
	 */
	private static void compileAndRun(ArrayList<String>f) {
		String[] args = new String[f.size()];
		//create the arguments to pass to the compiler
	    for(int i = 0; i < f.size();i++) {
	    	if(i == 0)args[i] = "javac";
	    	else args[i]=f.get(i);
	    }
	    try {
	        //test to see if the project will compile
	        if(execute(args)) {
	        	if(TESTING) {
	        		String[] args2 = new String[7];
	    	        args2[0]="java";
	    	        args2[1] = "-jar";
	        		args2[2] = jUnitJarPath;
	        		args2[3] = "-cp";
	        		args2[4] = f.get(0);
	        		args2[5] = "-c";
	        		args2[6] = jUnitTestPath.substring(jUnitTestPath.lastIndexOf("\\")+1,jUnitTestPath.length()).replace(".java", "");
	        		System.out.println("Running Test");
	        		System.out.println("Path: "+f.get(0));
	        		System.out.println();
	        		execute(args2);
	        	}
	        	else {
	        		String[] args2 = new String[4];
	    	        args2[0]="java";
	    	        args2[1] = "-cp";
	        		args2[2] = f.get(0);
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
	    }
        catch(Exception e) {
        	System.out.println(e.getMessage());
        }	    		
	}
/*
Creates a header String from pathName that is the path .\root_folder_name\project_folder_name
e.g pathName = C:\\Users\\ostrc\\Desktop\\College\\CSCD 300\\Root\\Test1\\Test.java
String header = C:\\Users\\ostrc\\Desktop\\College\\CSCD 300\\Root\\Test1
It is vitally important that the project folders are stored directly under masterPath otherwise code may
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
	private static void editProjectFiles(String pathName) {
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
			//if it is, add it to that project
			if(projectFiles.size() != 0 && projectFiles.get(i).get(0).equals(header)) {
				projectFiles.get(i).add(pathName);
			}
			//if projectFiles is empty or we dont match make a new project
			else if(projectFiles.size() == 0 || i == projectFiles.size()-1) {
				projectFiles.add(i,new ArrayList<String>());
				projectFiles.get(i).add(header);
				projectFiles.get(i).add("-cp");
				//If we are testing, append the path to the junit jar to the rest of the class path
				//for compilation and execution
				if(TESTING)projectFiles.get(i).add(masterPath+";"+jUnitJarPath);
				else projectFiles.get(i).add(masterPath);
				projectFiles.get(i).add(pathName);	
			}
		}
			
	}
/*
This grabs all java files in the directory path dir. For each .java file it sends the path to that file
to editProjectFiles which will add it into the projectFiles ArrayList

Searchs directories recursively
*/
	private static void getProjectFiles(Path dir) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
		    for (Path file: stream) {
		    	if(file.toFile().isDirectory()) {
		    		getProjectFiles(file.toAbsolutePath());
		    	}
		    	else if(file.toString().endsWith(".java")) {
				    editProjectFiles(file.toString());
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

