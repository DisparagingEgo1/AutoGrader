package JContainer;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
		ByteArrayOutputStream bos; 
		parseArgs(args);
		getProjectFiles(Paths.get(masterPath));
		//attempt to compile and run each project
		for(ArrayList<String> a: projectFiles) {
			if(TESTING) {
				bos = new ByteArrayOutputStream();
                System.setOut(new PrintStream(bos));
                moveTestFile(a,1);
                compileAndRun(a);
                moveTestFile(a,2);
                System.setOut(originalOut);
                System.out.println(parseResults(bos.toString()));
			}
			else {
				compileAndRun(a);
			}
		}
	}
	/*
	 * Captures the wanted output from testOuput and discards the rest
	 * 
	 * Desired output for each failure is:
	 * Method Name: <name_of_test_method>  Expected Output: <expected_output>  Actual Output: <actual_output>
	 * 
	 * After all failures have been found we process the final block of text for the completed vs failed tests and
	 * display the following output:
	 * 
	 * Successful  Tests: #successful_tests  Failed Tests: #failed_tests  Total Tests: successful_tests + failed_tests
	 */
	private static String parseResults(String testOutput){
		testOutput = testOutput.replaceAll("\n", " ");
		if(testOutput.contains("Failures (")) {
			testOutput = testOutput.substring(testOutput.indexOf("Failures ("));
		}
		else return "";
		
		char[] testOutputArray = testOutput.toCharArray();
		String pattern = "";
		StringBuilder result = new StringBuilder();
		int numFailures = 0, recordedFailures = 0;

		for(int i = 0; i < testOutputArray.length;i++) {
			char c = testOutputArray[i];
			//Parse the final block of code from the test for completed vs failed
			if(numFailures != 0 && recordedFailures == numFailures) {
				if(pattern.contains("Test run finished after ")){
					i = parseFinishedRun(testOutputArray,i,result);
				}
				pattern += c;
			}
			//search for certain patterns in our array from testOutput, patterns are determined by spaces or <
			else if((c == ' '||c == '<')&& !pattern.isEmpty() && (numFailures == 0 ? true: numFailures != recordedFailures)) {
				switch(pattern) {
					case "Failures":
						StringBuilder failures = new StringBuilder();
						i = parseFailure(testOutputArray,i+1,failures);
						numFailures = Integer.parseInt(failures.toString());
						failures = null;
						break;
					case "methodName":
						i = parseMethodName(testOutputArray,i+1,result);
						break;
					case "expected:":
						result.append("Expected Output: ");
						i = parseTestResults(testOutputArray,i,result, "> but");
						result.append("  ");
						break;
					case "was:":
						result.append("Actual Output: ");
						i = parseTestResults(testOutputArray,i,result,"[...]");
						recordedFailures++;
						result.append("\n");
						break;
				}
				pattern = "";
			}
			else if(!(c == ' ')) pattern += c;
		}
		return result.toString();
	}
	/*
	 * Processes the final code block of a junit test for the completed and failed test sections
	 * returns the current index when done
	 * 
	 * Looks through each result line which is denoted by []
	 * 
	 * Example Result Line
	 * [         3 containers found      ]
	 */
	private static int parseFinishedRun(char[] testOutputArray, int index, StringBuilder results) {
		boolean bracket = false;
		String testItem = "",num = "";
		int completed = 0, failed, total;
		
		while(index < testOutputArray.length) {
			char c = testOutputArray[index];
			//if we are looking at a new result line
			if(c == '[' && !bracket)bracket = true;
			//we've reached the end of the current result
			else if(c == ']') {
				bracket = false;
				if(testItem.contains("tests successful")) {
					results.append("Successful Tests: "+num+ "  ");
					completed = Integer.parseInt(num);
				}
				else if(testItem.contains("tests failed")) {
					results.append("Failed Tests: "+num+"  ");
					failed = Integer.parseInt(num);
					total = completed + failed;
					results.append("Total Tests: "+ total);
				}
				num = "";
				testItem = "";
			}
			//if we are in a result line look for any digits
			else if(bracket) {
				testItem += c;
				if(Character.isDigit(c)) {
					num += c;
				}
			}
			index++;
		}
		return index;
	}
	/*
	 * Parses either the expected or actual  output from testOutputArray into results. This is determined by 
	 * the endingSequence String
	 * 
	 * removes [] from the strings if they are in them
	 * 
	 * Returns the current index position after parsing the output
	 * 
	 * Expected Output Example
	 * 
	 * Captures: "<[3, 3, 4, [6, 8][1, 2, 3, 5, 6, 7, 8, 9]]> but"
	 * Stores: 3, 3, 4, 6, 8 1, 2, 3, 5, 6, 7, 8, 9
	 * 
	 * Actual Output Example
	 * Captures: "<[3, 3, 4, [4, 4][1, 3, 6, 7, 8, 8, 8, 8]]>" + all other data until [...]
	 * Stores:3, 3, 4, 4, 4 1, 3, 6, 7, 8, 8, 8, 8
	 */
	private static int parseTestResults(char[] testOutputArray, int index, StringBuilder results, String endingSequence) {
		boolean carrot = false, complete = false;
		String output = "";
		while(!complete) {
			char c = testOutputArray[index];
			if(c == '<' && !carrot)carrot = true;
			else if(carrot && !output.contains(endingSequence)) output+=c;
			else if(output.contains(endingSequence)) complete = true;
			if(!complete)index++;
		}
		output = output.replace("<", "").substring(0,output.lastIndexOf(">"));
		output = output.replaceAll("\\[", "").replaceAll("]", "");
		results.append(output);
		return index;
	}
	/*
	 * Parses the method name of the test that was failed from testOutputArray into results.
	 * 
	 * Returns the current index position after parsing the output
	 * 
	 * the method name is always in the following pattern:
	 * 
	 * methodName = 'TestMethodName'
	 */
	private static int parseMethodName(char[] testOutputArray, int index, StringBuilder results) {
		results.append("Method Name: ");
		boolean quote = false, complete = false;
		while(!complete) {
			char c = testOutputArray[index];
			if(c == '\''&&!quote)quote = true;
			else if(quote && c == '\'')complete = true;
			else if(quote)results.append(c);
			index++;
		}
		results.append("  ");
		return index;
	}
	/*
	 * Parses the number of failures reported and stores it in the temporary reference failures
	 * 
	 * returns the current index position after parsing
	 * 
	 * The number of failures is always reported as the following:
	 * 
	 * Failures (num_failures):
	 */
	private static int parseFailure(char[] testOutputArray, int index, StringBuilder failures) {
		char c = testOutputArray[index];
		while(c != ' ') {
			if(c == '(');
			else if(c == ')')return index;
			else failures.append(c);
			index++;
			c = testOutputArray[index];
		}
		return index;
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
	 * <fully_qualified_path_to_junit.jar> <fully_qualified_path_of_testing_file>
	 * 
	 * Fully Qualified Paths(FQP) include the file in the path.
	 * 
	 * If a path to the junit.jar and a testing file is not provided, then it will simply compile and run
	 * the programs stored under <path_to_root_directory_for_projects>
	 * 
	 * If they are supplied then TESTING will be set to true to allow the program to run the junit test at <fully_qualified_path_of_testing_file> 
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
	 * Displays the resulting output from execute
	 * 
	 * Does not display compiler output unless DEBUG is true
	 */
	private static void displayOutput(String cmdType,String[] args, String out,String err) {
		System.out.println("---------------------------------------------------------------------------------------------");
		switch(cmdType) {
			case "javac":
				System.out.print("[");
				for(int i = 3; i <args.length; i++) {
					if(i == args.length -1)System.out.print(args[i].substring(args[i].lastIndexOf("\\")+1));
					else System.out.print(args[i].substring(args[i].lastIndexOf("\\")+1)+" , ");
				}
				//If not empty, means the program failed to compile
				if(!err.isEmpty()) {
					
					System.out.print("] Did Not Compile Successfully" + "\n");
					//Print full error output from the compiler if using DEBUG mode
					if(DEBUG) {
						String[]lineArray = err.split("\n");
						for(String s: lineArray) {
							System.out.println(s);
						}
					}
				}
				//compiled successfully
				else {
					System.out.print("] Compiled Successfully" + "\n");
				}
				break;
			case "java":
				//display output of a program
				System.out.print(out);
				System.out.print(err);
				break;	
			} 
		System.out.println("---------------------------------------------------------------------------------------------");
	}
	
	/*
	 * Compiles the project represented by args
	 * 
	 * Returns true if it successfully compiles, false otherwise
	 */
	private static boolean compile(String[]args) throws IOException{
		String[] results = execute(args);
		displayOutput("javac",args,results[0],results[1]);
		//If the program failed to compile return false
		if(!results[1].isEmpty())return false;	
		return true;
	}
	/*
	 * Runs the project represented in args
	 */
	private static void run(String[]args) throws IOException{
		String[] results = execute(args);
		displayOutput("java",args,results[0],results[1]);
	}
	/*
	 * Determines if a the class stored in args[3] has a main method. This is only used
	 * if TESTING is false
	 * 
	 * Returns true if it does, false otherwise.
	 */
	private static boolean hasMain(String[] args) throws IOException{
		String temp = args[0],temp2 = args[3];
		args[0] = "javap";
		args[3] = args[3].replaceAll(".java", ".class");
		String[] results = execute(args);
		args[0] = temp;
		args[3] = temp2;
		if(results[0].contains("public static void main")) return true;
		return false;
	}
	/*
	 * Executes command line code stored in args.
	 * 
	 * Returns the resulting output from stdout and stderr
	 */
	private static String[] execute(String[]args) throws IOException {
		Process proc = Runtime.getRuntime().exec(args);
    	String out = getOutput("File Output",new BufferedReader(new InputStreamReader(proc.getInputStream())));
    	String err = getOutput("Error Output",new BufferedReader(new InputStreamReader(proc.getErrorStream())));
    	proc.destroy();
    	return new String[] {out,err};
	}
	/*
	 * Gets the output from the compiler or interpreter and returns it
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
	        if(compile(args)) {
	        	if(TESTING) {
	        		String[] args2 = {"java","-jar",jUnitJarPath,"-cp",f.get(0),"-c",jUnitTestPath.substring(jUnitTestPath.lastIndexOf("\\")+1,jUnitTestPath.length()).replace(".java", "")};
	        		System.out.println("Running Test");
	        		System.out.println("Path: "+f.get(0));
	        		System.out.println();
	        		run(args2);
	        	}
	        	else {
	        		String[] args2 = {"java","-cp",f.get(0),""};
			        for(int i = 3;i<args.length;i++) {
			        	//test each java file to see if it has a main. If it has one, will run it.
			        	args2[3]=args[i];
			        	if(hasMain(args2)) {
				            System.out.println("Running: "+args2[3].substring(args2[3].lastIndexOf("\\")+1,args2[3].length()));
				            System.out.println("Path: "+args2[2]);
				            System.out.println();
				            run(args2);
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

