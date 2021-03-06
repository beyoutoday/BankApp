import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Menu {
	// Instance Variables 
	Scanner keyboard = new Scanner(System.in);
	Bank bank = new Bank();
	boolean exit;
	
	public static void main(String[] args){
		Menu menu = new Menu();
		menu.runMenu();
	}
	
	public void runMenu(){
		printHeader();
		while(!exit) {
			printMenu();
			int choice = getInput();
			performAction(choice);
		}
	}

	private void printHeader() {
		System.out.println("+-----------------------------+");
		System.out.println("| Welcome to Top Secret Funds |");
		System.out.println("+-----------------------------+");
	}

	private void printMenu() {
		System.out.println("Please make a selection");
		System.out.println("1) Create a secret Account");
		System.out.println("2) Deposit");
		System.out.println("3) Withdraw");
		System.out.println("4) Account Information");
		System.out.println("0) Exit");
	}

	private int getInput() {
		int choice = -1;
		do { // going to run at least once 
			System.out.println("Enter your secret choice: ");
			try {
				choice = Integer.parseInt(keyboard.nextLine()); // User input, stored and captured into choice as an int
			} catch(NumberFormatException e) {
				System.out.println("Invalid selection. Numbers only please?");
			}
			if (choice < 0 || choice > 5) {
				System.out.println("Selection was outside of range. Try a lower number!");
			}
		} while(choice < 0 || choice > 4);
		return choice;
	}

	private void performAction(int choice){
		switch(choice) {
			case 0:
				System.out.println("Thank you for using the application! :-)");
				System.exit(0);
				break;
			case 1:{
				try {
					createAccount();
				} catch (InvalidAccountTypeException ex) {
					System.out.println("Account was not created sucessfully");
				}
			}
			break;
			case 2:
				makeADeposit();
				break;
			case 3:
				makeAWithdraw();
				break;
			case 4:
				listBalances();
				break;
			default:
				System.out.println("Unknown, but secret error has occured");
		}
		
	}
	
	private String accountQuestion(String question, List<String> answers) {
		String res = "";
		boolean choices = ((answers == null) || answers.size() == 0) ? false : true;
		boolean firstRun = true;
		do {
			if(!firstRun) {
				System.out.println("Invalid selection. Please try again.");
			}
			System.out.print(question);
			if(choices) {
				System.out.print("{");
				for( int i = 0; i < answers.size() - 1; ++i){
					System.out.print(answers.get(i) + "/");
				}
				System.out.print(answers.get(answers.size() - 1));
				System.out.print("}: ");
			}
			res = keyboard.nextLine();
			firstRun = false;
			if(!choices) {
				break;
			}
		} while (!answers.contains(res));
		return res;
	}
	
	private double getDeposit(String accountType) {
		double initialDeposit = 0;
		Boolean valid = false;
		while(!valid){
			System.out.println("Please enter an initial deposit:");
			try {
				initialDeposit = Double.parseDouble(keyboard.nextLine());
			} catch(NumberFormatException e) {
				System.out.println("Deposit must be a number.");
			}
			if(accountType.equalsIgnoreCase("checking")) {
				if(initialDeposit < 199) {
					System.out.println("A top secret checking account requires a minimum of a $199 deposit. ");
				} else {
					valid = true;
				}
			}
			else if(accountType.equalsIgnoreCase("savings")) {
				if(initialDeposit < 59) {
					System.out.println("A top secret savings account requires a minimum of a $59 deposit. ");
				} else {
					valid = true;
				}
			}
		}
		return initialDeposit;
	}

	private void createAccount() throws InvalidAccountTypeException {
		displayHeader("Create an Account");
		String accountType = accountQuestion("Please enter an account type: ", Arrays.asList("checking", "savings"));
		String firstName = accountQuestion("Please enter your first name: ", null);
		String lastName = accountQuestion("Please enter your last name: ", null);	
		String social = accountQuestion("Please enter your fake social security number: ", null);
		double initialDeposit = getDeposit(accountType);
		
		Account account;
		if(accountType.equalsIgnoreCase("checking")) {
			account = new Checking(initialDeposit);
		} else if (accountType.equalsIgnoreCase("savings")){
			account = new Savings(initialDeposit);
		} else {
			throw new InvalidAccountTypeException();
		}
		Customer customer = new Customer(firstName, lastName, social, account);
		bank.addCustomer(customer);
	}

	private double getAmount(String question) {
		System.out.print(question);
		double amount = 0;
		try {
			amount = Double.parseDouble(keyboard.nextLine());
		} catch(NumberFormatException e) {
			amount = 0;
		}
		return amount;
	}
	
	private void listBalances() {
		displayHeader("List Account Details");
		int account = selectAccount();
		if(account >= 0) {
			System.out.println(bank.getCustomer(account).getAccount());
		}
	}

	private void makeAWithdraw() {
		displayHeader("Make a Withdraw");
		int account = selectAccount();
		if(account >= 0) {
			double amount = getAmount("How much would you like to withdraw?: ");
			bank.getCustomer(account).getAccount().withdraw(amount);
		}
	}
	
	private void displayHeader(String header) {
		System.out.println();
		int width = header.length() + 6;
		StringBuilder sb = new StringBuilder();
		sb.append("+");
		for(int i = 0; i < width; ++i) {
			sb.append("-");
		}
		sb.append("+");
		System.out.println(sb.toString());
		System.out.println("|   "  + header + "   |");
		System.out.println(sb.toString());
	}

	private void makeADeposit() {
		displayHeader("Make a Deposit");
		int account = selectAccount();
		if(account >= 0) {
			double amount = getAmount("How much would you like to deposit?: ");
			bank.getCustomer(account).getAccount().deposit(amount);
		}
	}
	
	private int selectAccount() {
        ArrayList<Customer> customers = bank.getCustomers();
        if (customers.size() <= 0) {
            System.out.println("No customers at your bank.");
            return -1;
        }
        System.out.println("Select an account:");
        for (int i = 0; i < customers.size(); i++) {
            System.out.println("\t" + (i + 1) + ") " + customers.get(i).basicInfo());
        }
        int account;
        System.out.print("Please enter your selection: ");
        try {
            account = Integer.parseInt(keyboard.nextLine()) - 1;
        } catch (NumberFormatException e) {
            account = -1;
        }
        if (account < 0 || account > customers.size()) {
            System.out.println("Invalid account selected.");
            account = -1;
        }
        return account;
    }
}
