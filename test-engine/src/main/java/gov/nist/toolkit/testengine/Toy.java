package gov.nist.toolkit.testengine;

public class Toy {
	
	static public void main(String[] arg) {
		String pattern = "\\d(?=\\d*\\.)(?:\\.(?=\\d)|\\d){0,63}";
		
		System.out.println("12".matches(pattern));
		System.out.println("1.2".matches(pattern));
	}

}
