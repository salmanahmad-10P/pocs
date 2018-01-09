public class UpCast {
	public static void main(String args[]) {
		Fruit fruit = new Fruit();
		Apple apple = new Apple(fruit);
		System.out.println("The color of the apple = " + apple.color
				+ " and the size = " + apple.size);
		if (apple instanceof Fruit)
			System.out.println("apple is instanceof fruit");
		else
			System.out.println("apple is not instanceof fruit");
		if (fruit instanceof Fruit)
			System.out.println("fruit is instanceof fruit");
		else
			System.out.println("fruit is not instanceof fruit");
		if (fruit instanceof Apple)
			System.out.println("fruit is instanceof apple");
		else
			System.out.println("fruit is not instanceof apple");
	}
}

class Fruit {
	String size = "small";
}

class Apple extends Fruit {
	String color = "RED";

	public Apple(Fruit aFruit) {
		this.size = aFruit.size;
	}
}
