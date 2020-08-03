package library.entities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class Library implements Serializable {

	private static final String libraryFile = "library.obj";
	private static final int loanLimit = 2;
	private static final int loanPeriod = 2;
	private static final double finePerDay = 1.0;
	private static final double maxFinesOwed = 1.0;
	private static final double damageFee = 2.0;

	private static Library self;
	private int bookId;
	private int memberId;
	private int loanId;
	private Date loanDate;

	private Map<Integer, Book> catalog;
	private Map<Integer, Member> members;
	private Map<Integer, Loan> loans;
	private Map<Integer, Loan> currentLoans;
	private Map<Integer, Book> damagedBooks;
	

	private Library() {
		catalog = new HashMap<>();
		members = new HashMap<>();
		loans = new HashMap<>();
		currentLoans = new HashMap<>();
		damagedBooks = new HashMap<>();
		bookId = 1;
		memberId = 1;		
		loanId = 1;		
	}


	public static synchronized Library getInstance() {		
		if (self == null) {
			Path PATH = Paths.get(libraryFile);			
			if (Files.exists(PATH)) {	
				try (ObjectInputStream LibraryFile = new ObjectInputStream(new FileInputStream(libraryFile));) {
					self = (Library) LibraryFile.readObject();
					Calendar.getInstance().setDate(self.loanDate);
					LibraryFile.close();
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			else self = new Library();
		}
		return self;
	}


	public static synchronized void SaVe() {
		if (self != null) {
			self.loanDate = Calendar.getInstance().getDate();
			try (ObjectOutputStream LibraryFile = new ObjectOutputStream(new FileOutputStream(libraryFile));) {
				LibraryFile.writeObject(self);
				LibraryFile.flush();
				LibraryFile.close();	
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}


	public int getBookId() {
		return bookId;
	}


	public int getMemberId() {
		return memberId;
	}


	private int getNextBookId() {
		return bookId++;
	}


	private int getNextMemberId() {
		return memberId++;
	}


	private int getNextLoanId() {
		return loanId++;
	}


	public List<Member> listMembers() {		
		return new ArrayList<Member>(members.values()); 
	}


	public List<Book> listBooks() {		
		return new ArrayList<Book>(catalog.values()); 
	}


	public List<Loan> listCurrentLoans() {
		return new ArrayList<Loan>(currentLoans.values());
	}


	public Member addMember(String lastName, String firstName, String email, int phoneNo) {		
		Member member = new Member(lastName, firstName, email, phoneNo, getNextMemberId());
		members.put(member.getId(), member);		
		return member;
	}


	public Book addBook(String a, String t, String c) {		
		Book b = new Book(a, t, c, getNextBookId());
		catalog.put(b.getId(), b);		
		return b;
	}


	public Member getMember(int memberId) {
		if (members.containsKey(memberId)) 
			return members.get(memberId);
		return null;
	}


	public Book getBook(int bookId) {
		if (catalog.containsKey(bookId)) 
			return catalog.get(bookId);		
		return null;
	}


	public int getLoanLimit() {
		return loanLimit;
	}


	public boolean canMemberBorrow(Member member) {		
		if (member.getNumberOfCurrentLoans() == loanLimit ) 
			return false;
				
		if (member.finesOwed() >= maxFinesOwed) 
			return false;
				
		for (Loan loan : member.getLoans()) 
			if (loan.isOverdue()) 
				return false;
			
		return true;
	}


	public int getNumberOfLoansRemainingForMember(Member member) {		
		return loanLimit - member.getNumberOfCurrentLoans();
	}


	public Loan issueLoan(Book book, Member member) {
		Date dueDate = Calendar.getInstance().getDueDate(loanPeriod);
		Loan loan = new Loan(getNextLoanId(), book, member, dueDate);
		member.takeOutLoan(loan);
		book.borrow();
		loans.put(loan.getId(), loan);
		currentLoans.put(book.getId(), loan);
		return loan;
	}


	public Loan getLoanByBookId(int bookId) {
		if (currentLoans.containsKey(bookId)) 
			return currentLoans.get(bookId);
		
		return null;
	}


	public double calculateOverdueFine(Loan loan) {
		if (loan.isOverdue()) {
			long daysOverdue = Calendar.getInstance().getDaysDifference(loan.getDueDate());
			double fine = daysOverdue * finePerDay;
			return fine;
		}
		return 0.0;		
	}


	public void dischargeLoan(Loan currentLoan, boolean isDamaged) {
		Member member = currentLoan.getMember();
		Book bOoK  = currentLoan.getBook();
		
		double overdueFine = calcuateOverdueFine(currentLoan);
		member.addFine(overdueFine);	
		
		member.dischargeLoan(currentLoan);
		book.return(isDamaged);
		if (isDamaged) {
			member.addFine(damageFee);
			damagedBooks.put(book.getId(), book);
		}
		currentLoan.discharge();
		currentLoans.remove(book.getId());
	}


	public void checkCurentLoans() {
		for (Loan loan : currentLoans.values()) 
			loan.checkOverDue();			
	}


	public void repairBook(Book currentBook) {
		if (damagedBooks.containsKey(currentBook.getId())) {
			currentBook.repair();
			damagedBooks.remove(currentBook.getId());
		}
		else 
			throw new RuntimeException("Library: repairBook: book is not damaged");
	}
}
