
public class ItemNotFoundException extends Exception {

	@Override
	public String toString() {
		return "The requested item was not available in this page";
	}

}
