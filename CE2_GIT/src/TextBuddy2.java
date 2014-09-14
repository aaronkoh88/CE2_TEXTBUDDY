/* Done By: Aaron Koh Group C05 (CS2103T)
 * Name of Program: TextBuddy
 * This program works with file manipulation
 * User inputs a file name. Program will check if file exists.
 * If file exists, we will not create a new file
 * If file does not exists, we will create the file with the provided name
 * This program allows us to perform add/delete/display/clear functions
 * Internally, all operations are performed on a array list data structure. 
 * Upon user's input of exit, contents of data structure will be written to a 
 * temporary file, which will then replace the original file. 

*/
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;



public class TextBuddy2 {
	
	
	private static ArrayList<Entry> list;
	private static File newFile,temp;
	private static BufferedWriter buffW;
	private static FileWriter fileW;
	private static Scanner sc = new Scanner(System.in);
	
	private static final int INDEX_ZERO = 0;
	private static final int LIST_EMPTY =0;
	private static final int INDEX_OFFSET =1;
	
	enum COMMAND_TYPE {
	ADD, DELETE, CLEAR,DISPLAY, EXIT,INVALID
	};
	
	private static String inputName;
	private static final String MESSAGE_WELCOME_GREETING = "Welcome to TextBuddy. %1$s is ready for use";
	private static final String MESSAGE_INVALID_FORMAT = "%1$s is an invalid command";
	private static final String MESSAGE_INVALID_DELETE="invalid delete operation";
	private static final String MESSAGE_ADD = "added to %1$s \"%2$s\"";
	private static final String MESSAGE_DELETE = "deleted from %1$s \"%2$s\"";
	private static final String MESSAGE_CLEAR = "all content deleted from %1$s";
	
	
	//Main function direct input reading/ choice execution to respective methods
	public static void main(String[ ] args) throws Exception{
	
		inputName = args[INDEX_ZERO];
		list = new ArrayList<Entry>();
		String fileReady = makeFile(inputName);
		showToUser(fileReady);
		printCommand();
		loadToList();
		
		while(true){
			String userCommand = sc.nextLine();
			String feedback = executeCommand(userCommand);
			showToUser(feedback);
		}
	}
	
	//This method accepts the input name for the file, and checks if the file exists. 
	//If it does,we simply work on that file, if it doesn't, we will create this file
	private static String makeFile(String inputName) throws Exception {
		newFile = new File(inputName);
	
		if(!newFile.exists()){
			newFile.createNewFile();			
		}
		return String.format(MESSAGE_WELCOME_GREETING, inputName);
	}
	
	//This method is called to load all contents of the file into an array list 
	//data structure. This is to cater for the case in which file already exists.
	private static void loadToList() throws Exception{
	
		Scanner read = new Scanner(newFile);
		
		while(read.hasNextLine()){
			Entry details = new Entry(read.nextLine());
			list.add(details);
		}
		read.close();	
	}
	

	//This function is called if user's command is to add. It appends user's input
	//to the array list
	private static String addCall(String input, String inputName) throws Exception{
		
		Entry details = new Entry(input);
		list.add(details);
		saveToFile();
		return String.format(MESSAGE_ADD, inputName,input);
	}
	
	
	//This function is called if user's command is to delete. It determines the line 
	//to be deleted,and deletes that from the array list
	private static String deleteCall(String input, String inputName) throws Exception{
		int serial = getSerialNum(input);
		if(!listIsEmpty() && serialIsValid(serial)){
			String toClear = getStringToRemove(serial-INDEX_OFFSET);
			saveToFile();
			return String.format(MESSAGE_DELETE, inputName,toClear);
		}
			return MESSAGE_INVALID_DELETE;
	}
	
	//This function is called if command is to display. It displays all contents
	//of the array list
	private static void displayCall(String inputName){
		if(list.size()==LIST_EMPTY){
			System.out.println(inputName+" is empty");
		}
		else{
			    printList();
		}
	}
	
	//This function is called if user's command is to clear. It removes all contents 
	//from the array list 
	private static String clearCall(String inputName) throws Exception{
		if(list.size()!=0){
			list.clear();
			saveToFile();	
		}
		return String.format(MESSAGE_CLEAR, inputName);
	}
	
	//This function is called by the delete method. It does 3 mains tasks, it creates a
	//temporary file, copy all of the array list contents to this temporary file, then
	//replace the temporary file with the original
	private static void saveToFile() throws Exception{
		createTempFile();
		copyToTempFile();
		replaceFile();
	}
	
	//This function is called to create a temporary file
	private static void createTempFile(){
		temp = new File("temp.txt");
	}
	
	//This function is called to copy contents of array list into the temporary file
	private static void copyToTempFile() throws Exception{
		Scanner read = new Scanner(newFile);
		fileW = new FileWriter(temp);
		buffW = new BufferedWriter(fileW);
		
		for(int index=0;index<list.size();index++){
			buffW.write(list.get(index).getInput());
			buffW.newLine();buffW.flush();
		}
		read.close();
		buffW.close();
		fileW.close();
		
	}
	
	//This function is called to replace the temporary file with the original file
	private static void replaceFile(){
		if(newFile.delete()){
			temp.renameTo(newFile);
		}
	}
	
	//This function prints the word "command: "
	private static void printCommand(){
		System.out.print("command: ");
	}
	
	//This function accepts a string as input and prints it
	private static void showToUser(String text){
		if(!text.equals("")){
		System.out.println(text);
		}
	}
	 
	//This function checks to see if user is trying to delete from an empty list
	private static boolean listIsEmpty(){
		if(list.isEmpty()){
			System.out.println("Cant delete from an empty list");
		}
		
		return list.isEmpty();
	}
	
	//This function checks if user is specifying to delete an index that is is outside the arraylist range
	private static boolean serialIsValid(int serial){
		if(serial<0 || serial >list.size()){
			System.out.println("Out of Range");
			return false;
		}
		else{
			return true;
		}
	}
	
	
	
	private static String executeCommand(String userCommand) throws Exception {
		if (userCommand.trim().equals("")) {   //CHANGE 6, ADDED BRACES
			return String.format(MESSAGE_INVALID_FORMAT, userCommand);
		}
		
		String commandTypeString = getFirstWord(userCommand);

		COMMAND_TYPE commandType = determineCommandType(commandTypeString);

		switch (commandType) {
			case ADD:
				return addCall(removeFirstWord(userCommand),inputName); 
			case DISPLAY:
				displayCall(inputName); return"";
			case DELETE:
				return deleteCall(removeFirstWord(userCommand),inputName);
			case CLEAR:
				return clearCall(inputName);
			case INVALID:
				return String.format(MESSAGE_INVALID_FORMAT, userCommand);
			case EXIT:
				System.exit(0);
			default:
		//throw an error if the command is not recognized
				throw new Error("Unrecognized command type");
		}

}
	private static String getFirstWord(String userCommand) {
	String commandTypeString = userCommand.trim().split("\\s+")[0];
	return commandTypeString;
}
	
	
	private static COMMAND_TYPE determineCommandType(String commandTypeString) {
	if (commandTypeString == null)
		throw new Error("command type string cannot be null!");

	if (commandTypeString.equalsIgnoreCase("add")) {
		return COMMAND_TYPE.ADD;
	} else if (commandTypeString.equalsIgnoreCase("display")) {
		return COMMAND_TYPE.DISPLAY;
	} else if (commandTypeString.equalsIgnoreCase("delete")) {
	 	return COMMAND_TYPE.DELETE;
	} else if (commandTypeString.equalsIgnoreCase("clear")) {
 	return COMMAND_TYPE.CLEAR;
	} else if (commandTypeString.equalsIgnoreCase("exit")) {
 	return COMMAND_TYPE.EXIT;
	} else {
		return COMMAND_TYPE.INVALID;
	}
}
	
	private static String removeFirstWord(String userCommand) {
		return userCommand.replace(getFirstWord(userCommand), "").trim();
	}
	
	private static int getSerialNum(String input){
		return Integer.valueOf(input.trim());
	}
	
	public static String getStringToRemove(int serial){
		return list.remove(serial).getInput().trim();
	}
	
	public static void printList(){
		for(int index=0;index<list.size();index++){
			System.out.println((index+INDEX_OFFSET)+". "+list.get(index).getInput());
		}
	}
	
}
