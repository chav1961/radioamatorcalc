package chav1961.calc;

public class Test6 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double k = 2 * Math.PI / 625;
		
		for(int index = 0; index < 625; index++) {
			final int value = (int) (127 * Math.sin(k * index) + 128);
			System.err.println(value+", ");
		}
	}

}
