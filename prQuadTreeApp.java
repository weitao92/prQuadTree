import java.util.Random;
import java.util.Vector;


@SuppressWarnings("unused")
public class prQuadTreeApp {
	
	public static void main(String args[])
	{
		/**
		prQuadTree1 <Point> tree = new prQuadTree1 <Point> (-128,128,-128,128);
		
		tree.insert(new Point(30,30));
		tree.insert(new Point(20,-30));
		tree.insert(new Point(-30,-30));
		tree.insert(new Point(-30,30));
		tree.insert(new Point(90,80));
		
		System.out.println(tree.display());
		System.out.println(" ");

		System.out.println(tree.remove(new Point(-30,-30)));
		System.out.println(tree.display());
		System.out.println(" ");
		/**
		System.out.println(tree.remove(new Point(20,-30)));
		System.out.println(tree.remove(new Point(-30,30)));
		System.out.println(tree.remove(new Point(30,30)));
		System.out.println(tree.remove(new Point(90,80)));

		/**
		System.out.println(tree.find(new Point(30,30)));
		System.out.println(tree.find(new Point(20,-30)));
		System.out.println(tree.find(new Point(-30,-30)));
		System.out.println(tree.find(new Point(-30,30)));
		System.out.println(tree.find(new Point(90,80)));	
		
		System.out.println("");
		**/
		
		prQuadTree <Point> tree1 = new prQuadTree <Point> (-128,128,-128,128);
		
		tree1.insert(new Point(30,30));
		tree1.insert(new Point(20,-30));
		tree1.insert(new Point(-30,-30));
		tree1.insert(new Point(-30,30));
		tree1.insert(new Point(90,80));
		
		System.out.println(tree1.display());
		System.out.println("");
		
		/**
		System.out.println(tree1.remove(new Point(90,80)));
		System.out.println(tree1.display());	
		System.out.println("");
		
		System.out.println(tree1.remove(new Point(-30,-30)));
		System.out.println(tree1.display());
		System.out.println("");
		

		System.out.println(tree1.remove(new Point(20,-30)));
		System.out.println(tree1.display());
		System.out.println("");
		
		System.out.println(tree1.remove(new Point(-30,30)));
		System.out.println(tree1.display());
		System.out.println("");
		
		System.out.println(tree1.remove(new Point(30,30)));
		System.out.println(tree1.display());
		System.out.println("");
		**/
		
		Vector <Point> vector;
		
		vector = tree1.find(0, 128, 0, 128);
		System.out.println(vector);
		
		vector = tree1.find(0, 128, -128, 0);
		System.out.println(vector);
		
		vector = tree1.find(-128, 128, -128, 128);
		System.out.println(vector);
		
		vector = tree1.find(-50, 300, -35, 70);
		System.out.println(vector);
	
	}

}
