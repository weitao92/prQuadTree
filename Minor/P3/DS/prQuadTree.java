package Minor.P3.DS;
import java.util.Vector;

public class prQuadTree < T extends Compare2D <? super T> >
{
	abstract class prQuadNode
	{		
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
		}
		
		public prQuadLeaf()
		{
			Elements = new Vector <T>();
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
		
		public prQuadInternal()
		{
			NE = null;
			NW = null;
			SW = null;
			SE = null;
		}

		prQuadNode NW, NE, SE, SW;
	}
	
	prQuadNode root;
	boolean isInserted = false;
	boolean isRemoved = false;
	long xMin, xMax, yMin, yMax;
	String outString = "";
	String pad = "---";

	// Initialize QuadTree to empty state, representing the specified region.
	public prQuadTree(long xMin, long xMax, long yMin, long yMax)
	{
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		root = null;
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
			isInserted = false;
			root = insertHelper(root, elem, xMin, xMax, yMin, yMax);
			return isInserted;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private prQuadNode insertHelper(prQuadNode sub, T elem, long xl, long xh, long yl, long yh)
	{
		if(sub == null)
		{
			prQuadLeaf leaf = new prQuadLeaf(elem);
			isInserted = true;
			return leaf;
		}
		else
		{
			if(sub instanceof prQuadTree.prQuadInternal)
			{
				prQuadInternal internal = (prQuadTree.prQuadInternal) sub;
				Direction direction = elem.inQuadrant(xl, xh, yl, yh);
				
				switch (direction)
				{
					case NE:
						internal.NE = insertHelper(internal.NE, elem, (xl+xh)/2+1, xh, (yl+yh)/2, yh);
						break;	
					case NW:
						internal.NW = insertHelper(internal.NW, elem, xl, (xl+xh)/2, (yl+yh)/2+1, yh);
						break;
					case SW:
						internal.SW = insertHelper(internal.SW, elem, xl, (xl+xh)/2-1, yl, (yl+yh)/2);
						break;
					case SE:
						internal.SE = insertHelper(internal.SE, elem, (xl+xh)/2, xh, yl, (yl+yh)/2-1);
						break;
					default:
						break;
				}
				
				return internal;
			}
			else
			{
				prQuadLeaf leaf = (prQuadTree.prQuadLeaf) sub;
				
				if(elem.equals(leaf.get()))
				{
					isInserted = false;
					return sub;
				}
				else
				{
					prQuadNode internal = partition(leaf, xl, xh, yl, yh);
					internal = insertHelper(internal, elem, xl, xh, yl, yh);
					return internal;
				}
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private prQuadNode partition(prQuadNode sub, long xl, long xh, long yl, long yh)
	{
		prQuadLeaf leaf = (prQuadTree.prQuadLeaf) sub;
		prQuadInternal internal = new prQuadInternal();
		Direction direction1 = leaf.get().inQuadrant(xl, xh, yl, yh);
		
		switch (direction1)
		{
			case NE:
				internal.NE = leaf;
				break;
			case NW:
				internal.NW = leaf;
				break;
			case SW:
				internal.SW = leaf;
				break;
			case SE:
				internal.SE = leaf;
				break;
			default:
				break;
		}

		return internal;
	}
	
	// Pre: elem != null
	// Returns reference to an element x within the tree such that
	// elem.equals(x)is true, provided such a matching element occurs within
	// the tree; returns null otherwise.
	public T find(T elem)
	{
		if(elem == null)
		{
			return null;
		}
		else if(!elem.inBox(xMin, xMax, yMin, yMax))
		{
			return null;
		}
		else
		{
			return findHelper(root, elem, xMin, xMax, yMin, yMax);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private T findHelper(prQuadNode sub, T elem, double xl, double xh, double yl, double yh)
	{
		if(sub == null)
		{
			return null;
		}
		else
		{
			if(sub instanceof prQuadTree.prQuadLeaf)
			{
				prQuadLeaf leaf = (prQuadTree.prQuadLeaf) sub;
				if(elem.equals(leaf.get()))
				{
					return leaf.get();
				}
				else
				{
					return null;
				}
			}
			else
			{
				prQuadInternal internal = (prQuadTree.prQuadInternal) sub;
				Direction direction = elem.inQuadrant(xl, xh, yl, yh);
				
				switch (direction)
				{
					case NE:
						return findHelper(internal.NE, elem, (xl+xh)/2+1, xh, (yl+yh)/2, yh);	
					case NW:
						return findHelper(internal.NW, elem, xl, (xl+xh)/2, (yl+yh)/2+1, yh);
					case SW:
						return findHelper(internal.SW, elem, xl, (xl+xh)/2-1, yl, (yl+yh)/2);
					case SE:
						return findHelper(internal.SE, elem, (xl+xh)/2, xh, yl, (yl+yh)/2-1);
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
	public boolean delete(T Elem)
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
			isRemoved = false;
			root = removeHelper(root, Elem, xMin, xMax, yMin, yMax);
			return isRemoved;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private prQuadNode removeHelper(prQuadNode sub, T elem, double xl, double xh, double yl, double yh)
	{
		if(sub == null)
		{
			isRemoved = false;
			return null;
		}
		else
		{
			if(sub instanceof prQuadTree.prQuadLeaf)
			{
				prQuadLeaf leaf = (prQuadTree.prQuadLeaf) sub;
				if(elem.equals(leaf.get()))
				{
					isRemoved = true;
					return null;
				}
				else
				{
					isRemoved = false;
					return leaf;
				}
			}
			else
			{
				prQuadInternal internal = (prQuadTree.prQuadInternal) sub;
				Direction direction = elem.inQuadrant(xl, xh, yl, yh);
				
				switch (direction)
				{
					case NE:
						internal.NE = removeHelper(internal.NE, elem, (xl+xh)/2+1, xh, (yl+yh)/2, yh);
						break;
					case NW:
						internal.NW = removeHelper(internal.NW, elem, xl, (xl+xh)/2, (yl+yh)/2+1, yh);
						break;
					case SW:
						internal.SW = removeHelper(internal.SW, elem, xl, (xl+xh)/2-1, yl, (yl+yh)/2);
						break;
					case SE:
						internal.SE = removeHelper(internal.SE, elem, (xl+xh)/2, xh, yl, (yl+yh)/2-1);
						break;
					default:
						break;
				}
				
				prQuadNode result = contraction(internal);
				return result;
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private prQuadNode contraction(prQuadNode sub)
	{
		prQuadInternal internal = (prQuadTree.prQuadInternal) sub;
		int num = 0;
		
		if(internal.NE != null)
		{
			num++;
		}
		if(internal.NW != null)
		{
			num++;
		}
		if(internal.SW != null)
		{
			num++;
		}
		if(internal.SE != null)
		{
			num++;
		}
		
		if(((internal.NE != null && internal.NE instanceof prQuadTree.prQuadInternal) 
			|| (internal.NW != null && internal.NW instanceof prQuadTree.prQuadInternal)
			|| (internal.SW != null && internal.SW instanceof prQuadTree.prQuadInternal)
			|| (internal.SE != null && internal.SE instanceof prQuadTree.prQuadInternal))
			|| num > 1)
		{
			return internal;
		}
		else
		{
			prQuadLeaf leaf;
			
			if(internal.NE != null)
			{
				prQuadLeaf temp = (prQuadTree.prQuadLeaf) internal.NE;
				leaf = new prQuadLeaf(temp.get());
			}
			else if(internal.NW != null)
			{
				prQuadLeaf temp = (prQuadTree.prQuadLeaf) internal.NW;
				leaf = new prQuadLeaf(temp.get());
			}
			else if(internal.SW != null)
			{
				prQuadLeaf temp = (prQuadTree.prQuadLeaf) internal.SW;
				leaf = new prQuadLeaf(temp.get());
			}
			else
			{
				prQuadLeaf temp = (prQuadTree.prQuadLeaf) internal.SE;
				leaf = new prQuadLeaf(temp.get());
			}
			
			return leaf;
		}
	}
	
	// Pre: xLo, xHi, yLo and yHi define a rectangular region
	// Returns a collection of (references to) all elements x such that x is
	//in the tree and x lies at coordinates within the defined rectangular
	// region, including the boundary of the region.
	public Vector<T> find(long xLo, long xHi, long yLo, long yHi)
	{
		Vector<T> vector = new Vector<T> ();
		areaSearch(root, xLo, xHi, yLo, yHi, xMin, xMax, yMin, yMax, vector);
		return vector;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void areaSearch(prQuadNode sub, long xl1, long xh1, long yl1, long yh1, 
			long xl2, long xh2, long yl2, long yh2, Vector<T> vector)
	{
		if(sub == null)
		{
			return;
		}
		else
		{
			if(sub instanceof prQuadTree.prQuadLeaf)
			{
				prQuadLeaf leaf = (prQuadTree.prQuadLeaf) sub;
				
				if(leaf.get().inBox(xl1, xh1, yl1, yh1))
				{
					vector.addElement(leaf.get());
				}
				else
				{
					return;
				}
			}
			else
			{
				prQuadInternal internal = (prQuadTree.prQuadInternal) sub;
				
				if((xl2 <= xh1 && xh2 >= xl1) && (yl2 <= yh1 && yh2 >= yl1))
				{
					areaSearch(internal.NE, xl1, xh1, yl1, yh1, (xl2+xh2)/2+1, xh2, (yl2+yh2)/2, yh2, vector);
					areaSearch(internal.NW, xl1, xh1, yl1, yh1, xl2, (xl2+xh2)/2, (yl2+yh2)/2+1, yh2, vector);
					areaSearch(internal.SW, xl1, xh1, yl1, yh1, xl2, (xl2+xh2)/2-1, yl2, (yl2+yh2)/2, vector);
					areaSearch(internal.SE, xl1, xh1, yl1, yh1, (xl2+xh2)/2, xh2, yl2, (yl2+yh2)/2-1, vector);
				}
				
				return;
			}
		}
	}
	
	public String display()
	{
    	
        if (root == null )
        {
            outString = "  Empty tree.\n";
        }
        else
        {
        	 printTreeHelper(root, "");
        }
        
        String result = outString;
        outString = "";
    	pad = "---";
    	return result;
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
           if ( sRoot instanceof prQuadTree.prQuadInternal ) {
              prQuadInternal p = (prQuadInternal) sRoot;
              printTreeHelper(p.SW, Padding + pad);
              printTreeHelper(p.SE, Padding + pad);
           }
   
           // Determine if at leaf or internal and display accordingly
           if ( sRoot instanceof prQuadTree.prQuadLeaf ) {
              prQuadLeaf p = (prQuadLeaf) sRoot;
              outString = outString + Padding;
              for (int pos = 0; pos < p.Elements.size(); pos++) { 
                 outString = outString + "[" +p.Elements.get(pos) + "]";
              }
              outString = outString + "\n";
           }
           else if ( sRoot instanceof prQuadTree.prQuadInternal )
              outString = outString + Padding + "@\n" ;
           else
              outString = outString + sRoot.getClass().getName() + "#\n";

           // Check for and process NE and NW subtrees
           if (sRoot instanceof prQuadTree.prQuadInternal ) {
              prQuadInternal p = (prQuadInternal) sRoot;
              printTreeHelper( p.NE, Padding + pad);
              printTreeHelper( p.NW, Padding + pad);
           }
     }	
}