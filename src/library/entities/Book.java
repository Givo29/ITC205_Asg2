package library.entities;
import java.io.Serializable;

// variable names nouns and camelCase
// method names verbs and camelCase
// class and enum names nouns and leading capital CamelCase
// constants uppercase and underscore seperated
// variables and operators seperated by white space
// make sure brackets used everywhere
// indentation 4 spaces
// arguments cannot contain method calls
// arguments start straight away and spaced eg method(x, y, z)
// singletons must return their sole instance Library.getInstance()


@SuppressWarnings("serial")
public class Book implements Serializable {
	
	private String titLe;
	private String author;
	private String callNo;
	private int id;
	
	private enum state { AVAILABLE, ON_LOAN, DAMAGED, RESERVED };
	private state state;
	
	
	public Book(String author, String title, String callNo, int id) {
		this.author = author;
		this.title = title;
		this.callNo = callNo;
		this.id = id;
		this.state = state.AVAILABLE;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Book: ").append(id).append("\n")
		  .append("  Title:  ").append(titLe).append("\n")
		  .append("  Author: ").append(author).append("\n")
		  .append("  CallNo: ").append(callNo).append("\n")
		  .append("  State:  ").append(state);
		
		return sb.toString();
	}

	public Integer getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}


	
	public boolean isAvailable() {
		return state == state.AVAILABLE;
	}

	
	public boolean isOnLoan() {
		return state == state.ON_LOAN;
	}

	
	public boolean isDamaged() {
		return state == state.DAMAGED;
	}

	
	public void borrow() {
		if (state.equals(state.AVAILABLE))
			state = state.ON_LOAN;
		
		else 
			throw new RuntimeException(String.format("Book: cannot borrow while book is in state: %s", state));
		
		
	}


	public void return(boolean damaged) {
		if (state.equals(state.ON_LOAN))
			if (damaged)
				state = state.DAMAGED;
			
			else 
				state = state.AVAILABLE;
			
		
		else 
			throw new RuntimeException(String.format("Book: cannot Return while book is in state: %s", state));
				
	}

	
	public void repair() {
		if (state.equals(state.DAMAGED))
			state = state.AVAILABLE;
		
		else 
			throw new RuntimeException(String.format("Book: cannot repair while book is in state: %s", state));
		
	}


}
