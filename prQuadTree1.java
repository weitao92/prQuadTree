
import java.awt.Rectangle;
import java.util.Vector;

//The test harness will belong to the following package; the QuadTree
//implementation must belong to it as well. In addition, the QuadTree
//implementation must specify package access for the node types and tree
//members so that the test harness may have access to it.
//

public class prQuadTree1< T extends Compare2D<? super T> >
{
	
	public enum overlap
	{
		none,partial;
	}

	abstract class prQuadNode
	{
		boolean isLeaf;
		
		public prQuadNode()
		{
			
		}
	}
	
	class prQuadLeaf extends prQuadNode
	{
		public prQuadLeaf(T x)
				{
			Elements = new Vector <T>();
			Elements.add(0, x);
			isLeaf = true;
		}
		
		public prQuadLeaf()
		{
			Elements = new Vector <T>();
			isLeaf = true;
		}
		
		public void insert(T x)
		{
			Elements.add(x);
		}
		
		public T get()
		{
			return Elements.get(0);
		}
		
		Vector<T> Elements;
	}
	
	class prQuadInternal extends prQuadNode
	{
		long xMin, xMax, yMax, yMin;
		
		public prQuadInternal(long xMin, long xMax, long yMin, long yMax)
		{
			isLeaf = false;
			this.xMin = xMin;
			this.xMax = xMax;
			this.yMin = yMin;
			this.yMax = yMax;
			NE = null;
			NW = null;
			SW = null;
			SE = null;
		}

		prQuadNode NW, NE, SE, SW;
	}

	prQuadNode root;
	
	Boolean isRemoved = false;
	
	long xMin, xMax, yMin, yMax;
	
	long xLo, xHi, yLo, yHi;
	
	int size;
	
	String outString;
	
	String pad = "---";

	// Initialize QuadTree to empty state, representing the specified region.
	public prQuadTree1(long xMin, long xMax, long yMin, long yMax)
	{
		outString = "";
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		size = 0;
		
		root = null;
		isRemoved = false;
	}
	
	// Pre: elem != null
	// Post: If elem lies within the tree's region, and elem is not already
	// present in the tree, elem has been inserted into the tree.
	// Return true iff elem is inserted into the tree.
	public boolean insert(T elem)
	{
		if(elem == null)
		{
			return false;
		}
		else if(!elem.inBox(xMin, xMax, yMin, yMax))
		{
			return false;
		}
		else
		{
			return insertHelper(root, root, elem, Direction.NOQUADRANT);
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	private boolean insertHelper(prQuadNode current, prQuadNode father, 
			T elem, Direction direction)
	{
		//current node is a null.
		if(current == null)
		{
			if(current == root)
			{
				prQuadLeaf leaf = new prQuadLeaf(elem);
				root = leaf;
				size++;
				return true;
			}
			else
			{		
				
				switch (direction)
				{
					case NE:
						prQuadLeaf ne = new prQuadLeaf(elem);
						((prQuadInternal)father).NE = ne;
						size++;
						return true;
						
					case NW:
						prQuadLeaf nw = new prQuadLeaf(elem);
						((prQuadInternal)father).NW = nw;
						size++;
						return true;
						
					case SW:
						prQuadLeaf sw = new prQuadLeaf(elem);
						((prQuadInternal)father).SW = sw;
						size++;
						return true;
						
					case SE:
						prQuadLeaf se = new prQuadLeaf(elem);
						((prQuadInternal)father).SE = se;
						size++;
						return true;
						
					default:
						return false;
				}
			}
		}
		//current node isn't null.
		else
		{
			//current node is a leaf.
			if(current.isLeaf)
			{
				prQuadLeaf temp = (prQuadLeaf) current;
				
				//can't insert a duplicate point.
				if(temp.get().equals(elem))
				{
					return false;
				}
				//the inserted point isn't duplicate.
				else
				{
					Direction outDirection;
					
					if(current == root)
					{
						outDirection = temp.get().inQuadrant(xMin, xMax, yMin, yMax);
					}
					else
					{
						prQuadInternal internalFather = (prQuadInternal) father;
						
						outDirection = temp.get().inQuadrant
								(internalFather.xMin, internalFather.xMax, internalFather.yMin, internalFather.yMax);
					}
					
					prQuadInternal changed = partition(current, father, outDirection, elem);
					Direction inner = elem.inQuadrant(changed.xMin, changed.xMax, changed.yMin, changed.yMax);
					
					switch (inner)
					{
						case NE:
							return insertHelper(changed.NE, changed, elem, inner);
							
						case NW:
							return insertHelper(changed.NW, changed, elem, inner);
							
						case SW:
							return insertHelper(changed.SW, changed, elem, inner);
							
						case SE:
							return insertHelper(changed.SE, changed, elem, inner);
							
						default:
							return false;
					}				
				}
			}
			//current is a internal node.
			else
			{
				prQuadInternal currentInternal = (prQuadInternal) current;
				
				Direction direction3 = elem.inQuadrant(currentInternal.xMin, 
						currentInternal.xMax, currentInternal.yMin, currentInternal.yMax);
				
				switch (direction3)
				{
					case NE:
						return insertHelper(((prQuadInternal)current).NE, 
								current, elem, direction3);
						
					case NW:
						return insertHelper(((prQuadInternal)current).NW, 
								current, elem, direction3);
						
					case SW:
						return insertHelper(((prQuadInternal)current).SW, 
								current, elem, direction3);
						
					case SE:
						return insertHelper(((prQuadInternal)current).SE, 
								current, elem, direction3);
						
					default:
						return false;
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private prQuadInternal partition(prQuadNode leaf, prQuadNode father, Direction direction, T x)
	{
		prQuadLeaf Leaf = (prQuadLeaf)leaf;
		long xmin, xmax, ymin, ymax;
		T elem = Leaf.get();
		
		if(leaf == root)
		{
			xmin = xMin;
			xmax = xMax;
			ymin = yMin;
			ymax = yMax;
		}
		else
		{
			prQuadInternal internal = (prQuadInternal)father;
			long originX = (internal.xMax + internal.xMin) / 2;
			long originY = (internal.yMax + internal.yMin) / 2;
			
			switch (direction)
			{
				case NE:
					xmin = originX + 1;
					xmax = internal.xMax;
					ymin = originY;
					ymax = internal.yMax;
					break;
					
				case NW:
					xmin = internal.xMin;
					xmax = originX;
					ymin = originY + 1;
					ymax = internal.yMax;
					break;
					
				case SW:
					xmin = internal.xMin;
					xmax = originX - 1;
					ymin = internal.yMin;
					ymax = originY;
					break;
					
				case SE:
					xmin = originX;
					xmax = internal.xMax;
					ymin = internal.yMin;
					ymax = originY - 1;
					break;
					
				default:
					xmin = 0;
					xmax = 0;
					ymin = 0;
					ymax = 0;
					break;
			}
		}
		
		prQuadInternal internalLeaf = new prQuadInternal(xmin, xmax, ymin, ymax);
		prQuadLeaf newLeaf = new prQuadLeaf(elem);
		Direction inner = elem.inQuadrant(xmin, xmax, ymin, ymax);
		 	
		switch (inner)
		{
			case NE:
				internalLeaf.NE = newLeaf;
				break;
				
			case NW:
				internalLeaf.NW = newLeaf;
				break;
				
			case SW:
				internalLeaf.SW = newLeaf;
				break;
				
			case SE:
				internalLeaf.SE = newLeaf;
				break;
				
			case NOQUADRANT:
				break;
		}
		
		if(leaf == root)
		{
			root = internalLeaf;

		}
		else
		{
			switch (direction)
			{
				case NE:
					((prQuadInternal)father).NE = internalLeaf;
					break;
					
				case NW:
					((prQuadInternal)father).NW = internalLeaf;
					break;
					
				case SW:
					((prQuadInternal)father).SW = internalLeaf;
					break;
					
				case SE:
					((prQuadInternal)father).SE = internalLeaf;
					break;
					
				case NOQUADRANT:
					break;
			}
		}
		
		return internalLeaf;
	}
	
	// Pre: elem != null
	// Returns reference to an element x within the tree such that
	// elem.equals(x)is true, provided such a matching element occurs within
	// the tree; returns null otherwise.
	public T find(T Elem)
	{
		if(Elem == null)
		{
			return null;
		}
		else if(!Elem.inBox(xMin, xMax, yMin, yMax))
		{
			return null;
		}
		else
		{
			return findHelper(root, Elem);
		}
	}
	
	@SuppressWarnings("unchecked")
	private T findHelper(prQuadNode subRoot, T Elem)
	{
		//reached end, return to upper level.
		if(subRoot == null)
		{
			return null;
		}
		else
		{
			//reach a leaf.
			if(subRoot.isLeaf)
			{
				prQuadLeaf current = (prQuadLeaf) subRoot;
				
				if(Elem.equals(current.get()))
				{
					return current.get();
				}
				else
				{
					return null;
				}
			}
			//reach a internal.
			else
			{
				prQuadInternal current = (prQuadInternal) subRoot;
				
				Direction direction = Elem.inQuadrant(current.xMin, current.xMax, 
						current.yMin, current.yMax);
				
				switch (direction)
				{
					case NE:
						return findHelper(current.NE, Elem);
						
					case NW:
						return findHelper(current.NW, Elem);
						
					case SW:
						return findHelper(current.SW, Elem);
						
					case SE:
						return findHelper(current.SE, Elem);
						
					default:
						return null;
				}
			}
		}
	}
	
	// Pre: elem != null
	// Post: If elem lies in the tree's region, and a matching element occurs
	// in the tree, then that element has been removed.
	// Returns true iff a matching element has been removed from the tree.
	public boolean remove(T Elem)
	{
		if(Elem == null)
		{
			return false;
		}
		else if(!Elem.inBox(xMin, xMax, yMin, yMax))
		{
			return false;
		}
		else
		{
			removeHelper(root, root, Elem, Direction.NOQUADRANT);
			
			if(isRemoved)
			{
				isRemoved = false;
				return true;
			}
			else
			{
				return false;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void removeHelper(prQuadNode current, prQuadNode father, 
			T elem, Direction direction1)
	{
		//reach the end, return to upper level.
		if(current == null)
		{
			isRemoved = false;
			return;
		}
		else
		{
			//reach a leaf.
			if(current.isLeaf)
			{
				if(!((prQuadLeaf) current).get().equals(elem))
				{
					isRemoved = false;
					return;
				}
				else
				{
					if(current == root)
					{
						root = null;
						isRemoved = true;
						size--;
						return;
					}
					else
					{
						switch (direction1)
						{
							case NE:
								((prQuadInternal)father).NE = null;
								break;
								
							case NW:
								((prQuadInternal)father).NW = null;
								break;
								
							case SW:
								((prQuadInternal)father).SW = null;
								break;
								
							case SE:
								((prQuadInternal)father).SE = null;
								break;
							default:
								break;
							
						}
						isRemoved = true;
						size--;
						return;
					}
				}
			}
			//reach a internal node.
			else
			{
				Direction direction = elem.inQuadrant(((prQuadInternal)current).xMin, 
						((prQuadInternal)current).xMax, ((prQuadInternal)current).yMin, 
						((prQuadInternal)current).yMax);
				
				switch (direction)
				{
					case NE:
						removeHelper(((prQuadInternal)current).NE, 
								current, elem, Direction.NE);
						break;
						
					case NW:
						removeHelper(((prQuadInternal)current).NW, 
								current, elem, Direction.NW);
						break;
						
					case SW:
						removeHelper(((prQuadInternal)current).SW, 
								current, elem, Direction.SW);
						break;
						
					case SE:
						removeHelper(((prQuadInternal)current).SE, 
								current, elem, Direction.SE);
						break;
						
					default:
						break;
				}
				if(isRemoved)
				{
				
					int numOfLeaf = 0;
					boolean isInternal = false;
					prQuadNode son = new prQuadLeaf();
					
					if(((prQuadInternal)current).NE != null)
					{
						if(((prQuadInternal)current).NE.isLeaf == false)
						{
							isInternal = true;
						}
						else
						{
							son = ((prQuadInternal)current).NE;
							numOfLeaf++;
						}
					}
					
					if(((prQuadInternal)current).NW != null)
					{
						if(((prQuadInternal)current).NW.isLeaf == false)
						{
							isInternal = true;
						}
						else
						{
							son = ((prQuadInternal)current).NW;
							numOfLeaf++;
						}
					}
					
					if(((prQuadInternal)current).SW != null)
					{
						if(((prQuadInternal)current).SW.isLeaf == false)
						{
							isInternal = true;
						}
						else
						{
							son = ((prQuadInternal)current).SW;
							numOfLeaf++;
						}
					}
					
					if(((prQuadInternal)current).SE != null)
					{
						if(((prQuadInternal)current).SE.isLeaf == false)
						{
							isInternal = true;
						}
						else
						{
							son = ((prQuadInternal)current).SE;
							numOfLeaf++;
						}
					}
					
					if(!isInternal && numOfLeaf < 2)
					{
						contraction(father, current, son, direction1);
					}
					
					return;
				}
				else
				{
					return;
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void contraction(prQuadNode father, prQuadNode current, 
			prQuadNode son, Direction direction)
	{
		T x = ((prQuadLeaf)son).get();
		prQuadLeaf leaf = new prQuadLeaf(x);
		
		//if we contracting the root.
		if(current == root)
		{
			root = leaf;
		}
		else
		{			
			switch (direction)
			{
				case NE:
					((prQuadInternal)father).NE = leaf;
					break;
					
				case NW:
					((prQuadInternal)father).NW = leaf;
					break;
					
				case SW:
					((prQuadInternal)father).SW = leaf;
					break;
					
				case SE:
					((prQuadInternal)father).SE = leaf;
					break;
					
				default:
					break;	
			}
		}
	}
	
	// Pre: xLo, xHi, yLo and yHi define a rectangular region
	// Returns a collection of (references to) all elements x such that x is
	//in the tree and x lies at coordinates within the defined rectangular
	// region, including the boundary of the region.
	public Vector<T> find(long xLo, long xHi, long yLo, long yHi)
	{
		Vector<T> vector = new Vector<T> ();
		
		Rectangle thisTree = new Rectangle((int)xMin, (int)yMax, 
				(int)(xMax - xMin), (int)(yMax - yMin));
		
		Rectangle searchArea = new Rectangle((int)xLo, (int)yHi,
				(int)(xHi - xLo), (int)(yHi - yLo));
		
		areaSearch(thisTree, searchArea, root, vector);
		
		return vector;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void areaSearch(Rectangle a, Rectangle b, 
			prQuadNode subRoot, Vector<T> vector)
	{
		Rectangle overlap = findOverlap(a, b);

		if(overlap == null)
		{
			return;
		}
		else
		{
			
			if(overlap.equals(a))
			{
				traverse(subRoot, vector);
				return;
			}
			else
			{
				if(subRoot == null)
				{
					return;
				}
				else
				{
					if(subRoot.isLeaf)
					{
						T point = ((prQuadLeaf)subRoot).get();
						
						if(contains(overlap, point))
						{
							vector.addElement(((T) ((prQuadTree1.prQuadLeaf) subRoot).get()));
						}
						return;
					}
					else
					{					
						int X = (int) a.getX();
						int Y = (int) a.getY();
						int width = (int) a.getWidth();
						int height = (int) a.getHeight();
						
						Rectangle NE = new Rectangle(X + width / 2, Y, width / 2, height / 2);
						Rectangle NW = new Rectangle(X, Y, width / 2, height / 2);
						Rectangle SW = new Rectangle(X, Y - height / 2, width / 2, height / 2);
						Rectangle SE = new Rectangle(X + width / 2, Y - height / 2, width / 2, height / 2);
						
						areaSearch(NE, overlap, ((prQuadInternal) subRoot).NE, vector);
						areaSearch(NW, overlap, ((prQuadInternal) subRoot).NW, vector);
						areaSearch(SW, overlap, ((prQuadInternal) subRoot).SW, vector);
						areaSearch(SE, overlap, ((prQuadInternal) subRoot).SE, vector);
						
						return;
					}
				}
			}
		}
	}
	
	private Rectangle findOverlap(Rectangle a, Rectangle b)
	{
		/**
		if(a.intersects(b))
		{
			System.out.println(a.intersection(b));
			return a.intersection(b);
		}
		else
		{
			return null;
		}
		**/
		
		if((b.getY() >= a.getY() && b.getX() <= a.getX()) 
				&& ((b.getX() + b.getWidth()) >= (a.getX() + a.getWidth())
				&& (b.getY() - b.getHeight()) <= (a.getY() - a.getHeight())))
		{
			return a;
		}
		else if(((b.getY() - b.getHeight()) > a.getY()) 
			|| (b.getY() < (a.getY() - a.getHeight())) 
			|| (b.getX() > (a.getX() + a.getWidth()))
			|| ((b.getX() + b.getWidth()) < a.getX()))
		{
			return null;
		}
		else
		{
			double X;
			double Y;
			double width;
			double height;

			if(b.getX() < a.getX() && b.getY() > a.getY())
			{
				X = a.getX();
				Y = a.getY();
				
				if((b.getX() - b.getHeight()) <= (a.getY() - a.getHeight()))
				{
					height = a.getHeight();
				}
				else
				{
					height = (a.getY() - (b.getY() - b.getHeight()));
				}
				
				if((b.getX() + b.getWidth()) >= (a.getX() + a.getWidth()))
				{
					width = a.getWidth();
				}
				else
				{
					width = (b.getX() + b.getWidth() - a.getX());
				}
			}
			else if(b.getX() < a.getX() && b.getY() < a.getY())
			{
				X = a.getX();
				Y = b.getY();
				
				if((b.getY() - b.getHeight()) <= (a.getY() - a.getHeight()))
				{
					height = b.getY() - (a.getY() - a.getHeight());
				}
				else
				{
					height = b.getHeight();
				}
				
				if((b.getX() + b.getWidth()) >= (a.getX() + a.getWidth()))
				{
					width = a.getWidth();
				}
				else
				{
					width = b.getX() + b.getWidth() - a.getX();
				}
			}
			else if(b.getX() > a.getX() && b.getY() < a.getY())
			{
				X = b.getX();
				Y = b.getY();
				
				if((b.getY() - b.getHeight()) <= (a.getY() - a.getHeight()))
				{
					height = b.getY() - (a.getY() - a.getHeight());
				}
				else
				{
					height = b.getHeight();
				}
				
				if((b.getX() + b.getWidth()) >= (a.getX() + a.getWidth()))
				{
					width = a.getX() + a.getWidth() - b.getX();
				}
				else
				{
					width = b.getWidth();
				}
			}
			else
			{
				X = b.getX();
				Y = a.getY();
				
				if((b.getY() - b.getHeight()) <= (a.getY() - a.getHeight()))
				{
					height = a.getHeight();
				}
				else
				{
					height = a.getY() - (b.getY() - b.getHeight());
				}
				
				if((b.getY() + b.getWidth()) >= (a.getX() + a.getWidth()))
				{
					width = a.getX() + a.getWidth() - b.getX();
				}
				else
				{
					width = b.getWidth();
				}
			}
			
			Rectangle overlap = new Rectangle();
			overlap.setBounds((int)X, (int)Y, (int)width, (int)height);
			
			
			return overlap;
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	private void traverse(prQuadNode subRoot, Vector<T> vector)
	{
		if(subRoot == null)
		{
			return;
		}
		else
		{
			if(subRoot.isLeaf)
			{
				vector.addElement((T) ((prQuadLeaf) subRoot).get());				
				return;
			}
			else
			{
				traverse(((prQuadInternal)subRoot).NE, vector);
				traverse(((prQuadInternal)subRoot).NW, vector);
				traverse(((prQuadInternal)subRoot).SW, vector);
				traverse(((prQuadInternal)subRoot).SE, vector);
				
				return;
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes" })
	private boolean contains(Rectangle area, Compare2D compare2d)
	{
		if((compare2d.getX() < area.getX()) || (compare2d.getX() > (area.getX() + area.getWidth()))
			|| (compare2d.getY() > area.getY()) 
			|| (compare2d.getY() < (area.getY() - area.getHeight())))
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public String display() {
    	
        if (root == null )
        {
            outString = "  Empty tree.\n";
        	return outString;
        }
         else
        {
        	 printTreeHelper(root, "");
        	 return outString;
        }
      }

    /**
     * Recursive function used to traverse the tree and add its contents to outString which can be printed
     * into a file
     * 
     * @param sRoot - current node
     * @param Padding - padding used during each recursive call
     */
    @SuppressWarnings("unchecked")
	public void printTreeHelper(prQuadNode sRoot, String Padding) {

        // Check for empty leaf
           if ( sRoot == null ) {
              outString = outString + " " + Padding + "*\n";
              return;
           }
           // Check for and process SW and SE subtrees
           if ( !sRoot.isLeaf ) {
              prQuadInternal p = (prQuadInternal) sRoot;
              printTreeHelper(p.SW, Padding + pad);
              printTreeHelper(p.SE, Padding + pad);
           }
   
           // Determine if at leaf or internal and display accordingly
           if ( sRoot.isLeaf ) {
              prQuadLeaf p = (prQuadLeaf) sRoot;
              outString = outString + Padding;
              for (int pos = 0; pos < p.Elements.size(); pos++) { 
                 outString = outString + "[" +p.Elements.get(pos) + "]";
              }
              outString = outString + "\n";
           }
           else if ( !sRoot.isLeaf )
              outString = outString + Padding + "@\n" ;
           else
              outString = outString + sRoot.getClass().getName() + "#\n";

           // Check for and process NE and NW subtrees
           if ( !sRoot.isLeaf ) {
              prQuadInternal p = (prQuadInternal) sRoot;
              printTreeHelper( p.NE, Padding + pad);
              printTreeHelper( p.NW, Padding + pad);
           }
     }
}