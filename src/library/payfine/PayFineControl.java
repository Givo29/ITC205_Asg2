package library.payfine;
import library.entities.Library;
import library.entities.Member;

public class PayFineControl {

	private PayFineUI ui;
	private enum ControlState { INITIALISED, READY, PAYING, COMPLETED, CANCELLED };
	private ControlState state;

	private Library library;
	private Member member;


	public PayFineControl() {
		this.library = library.getInstance();
		state = controlState.INITIALISED;
	}
	
	
	public void setUI(PayFineUI ui) {
		if (!state.equals(controlState.INITIALISED)) {
			throw new RuntimeException("PayFineControl: cannot call setUI except in INITIALISED state");
		}	
		this.ui = ui;
		ui.setState(PayFineUI.uiState.READY);
		state = controlState.READY;		
	}


	public void cardSwiped(int memberId) {
		if (!state.equals(controlState.READY)) {
			throw new RuntimeException("PayFineControl: cannot call cardSwiped except in READY state");
		}

		member = library.getMember(memberId);

		if (member == null) {
			ui.display("Invalid Member Id");
			return;
		}
		String memberString = member.toString();
		ui.display(memberString);
		ui.setState(PayFineUI.uiState.PAYING);
		state = controlState.PAYING;
	}


	public void cancel() {
		ui.setState(PayFineUI.uiState.CANCELLED);
		state = controlState.CANCELLED;
	}


	public double payFine(double amount) {
		if (!state.equals(controlState.PAYING)) {
			throw new RuntimeException("PayFineControl: cannot call payFine except in PAYING state");
		}

		double change = member.payFine(amount);
		if (change > 0) {
			ui.display(String.format("Change: $%.2f", change));
		}

		String memberString = member.toString();
		ui.display(memberString);
		ui.setState(PayFineUI.uiState.COMPLETED);
		state = controlState.COMPLETED;
		return change;
	}
}
