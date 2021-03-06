/**
 * 
 */
package cs2420;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * @author jjgarzella
 *
 * The binary search tree class is a Binary Search Tree
 * implementation of a Sorted Set. There are no duplicates allowed, 
 * and when an element is added or removed, boolean information 
 * about whether the set changed is returned. 
 * 
 */
public class BinarySearchTree<Type extends Comparable<? super Type>> implements SortedSet<Type> {

	/**
	 * The root element of the tree, protected for unit tests.
	 */
	protected Node<Type> root;
	/**
	 * The size of the tree, manually computed as elements or added
	 */
	private int size = 0;

	/**
	 * Protected for unit testing.
	 *
	 * Pictorially, a node is:
	 *
	 * left data right --------------- <--+ | 5 | +---> ---------------
	 * 
	 * Note, while a 5 is used above any "Type" could be contained in the node
	 */
	protected static class Node<Type extends Comparable<? super Type>> {

		Type data;
		Node<Type> left;
		Node<Type> right;

		/**
		 * constructor that simplifies building an initial node
		 * 
		 * @param the_data
		 *            - data
		 */
		Node(Type the_data) {
			data = the_data;
		}

		/**
		 * 
		 * This function must be written recursively.
		 *
		 * Height is defined as the 1 plus the maximum height of the left vs
		 * right sub tree
		 * 
		 * @return the height from this node to its leaves
		 * 
		 * 
		 */
		int height() {
			int leftHeight = 0;
			int rightHeight = 0;
			// Base case both are null - return 0 + 1;
			if (left != null) {
				leftHeight = left.height();
			}
			if (right != null) {
				rightHeight = right.height();
			}

			// Find bigger of two heights.
			int biggerNumber = (leftHeight < rightHeight) ? rightHeight : leftHeight;
			return biggerNumber + 1;
		}

		/**
		 * recursive determine if the item is in this node or the nodes after
		 * 
		 * @param item
		 *            - needle
		 * @return true if item in tree
		 */
		boolean contains(Type item) {
			if (item.compareTo(data) == 0) {
				return true;
			} else if (item.compareTo(data) < 0) {
				// Go left.
				if (left == null)
					return false;
				return left.contains(item);
			} else {
				// Go right.
				if (right == null)
					return false;
				return right.contains(item);
			}
		}

		/**
		 * recursive - add a node
		 * 
		 * @param item
		 *            - data to add
		 * @return
		 */
		void insert(Type item) {
			// Node is part of set, no duplicates allowed.
			if (item.compareTo(data) == 0)
				return;
			else if (item.compareTo(data) < 0) {
				// Go left.
				if (left == null) {
					left = new Node<Type>(item);
				}
				left.insert(item);
			} else {
				// Go right.
				if (right == null) {
					right = new Node<Type>(item);
					return;
				}
				right.insert(item);
			}
			


		}
		
		/**
		 * Remove the value "item" from this node.
		 * 
		 * This is not technically recursive, 
		 * (we call left.remove() and right.remove()), it 
		 * gets it's recursiveness from the fact that 
		 * Node is a recursively defined type.
		 * 
		 * @param item the item to delete
		 * @param parent must be the parent node of this
		 * @param nodeIsLeftOfParent 
		 * @return
		 */
		boolean remove(Type item, Node<Type> parent, boolean nodeIsLeftOfParent) {
			
			
			int comparison = item.compareTo(data);
			if (comparison < 0) {
				// Go left
				if (left == null)  return false;
				
				return left.remove(item, this, true);
			}
			else if (0 < comparison) {
				if (right == null) return false;
				
				return right.remove(item, this, false);
			}
			
			else {
				// case 1: this has no children
				if (left == null && right == null) {
					if (nodeIsLeftOfParent) {
						parent.left = null;
					} else {
						parent.right = null;
					}
					return true;
				}
				
				// case 2: this node has one child
				Node<Type> singleChild;
				if (right != null && left == null) {
					singleChild = right;
				} else if (right == null && left != null) {
					singleChild = left;
				} else { // two children, we will skip next code
					singleChild = null;
				}
				
				if (singleChild != null) {
					if (nodeIsLeftOfParent) {
						parent.left = singleChild;
					} else {
						parent.right = singleChild;
					}
					return true;
				}
				
				// case 3: this node has more than one child
				
				// in this case, get the 'successor' from the
				// right side
				Node<Type> successor = right;
				while(successor.left != null) {
					successor = successor.left;
				}
				
				data = successor.data;
				// The tree is no longer a [unique] binary tree, temporarily.
				// Yet this.right IS a binary tree. 
				// Also, the successor is guarenteed to have one or less children
				// So, to solve the problem we just remove it normally.
				boolean removeResult = right.remove(successor.data, this, false);
				
				// gut check, could maybe be an assert statement?
				if (!removeResult) {
					System.out.println("Error: the successor node was not found on the right, \n"
										+ "       though it should be guarenteed to be in the tree");
				}
				
				return true;
			}

			
		}
		
		/**
		 * recursive helper method for writing node data
		 * in the dot format
		 * 
		 * @param builder a StringBuilder that to which the data will be written
		 */
		public void writeDotHelper(StringBuilder builder) {
			String nodeDataString = this.data.toString();
			if (nodeDataString.contains("-")) {
				
				nodeDataString = nodeDataString.replace('-', 'n');
			}
			String leftStr = left == null ? "null" + nodeDataString + "L" : left.data.toString();
			String rightStr = right == null ? "null" + nodeDataString + "R" : right.data.toString();
			builder.append("\t" 
						   + data 
						   + " -> { " 
						   + leftStr 
						   + ", " 
						   + rightStr 
						   + " };\n");
			if(left != null) {
				left.writeDotHelper(builder);
			}
			
			if(this.right != null) {
				right.writeDotHelper(builder);
			}
		}

		
		/**
		 * helper method for the toArray() function
		 * 
		 * Traverses the tree.
		 * 
		 * Note that this method computes and index to insert
		 * the next node at, rather than adding elements in between.
		 * 
		 * @param root
		 */
		private ArrayList<Type> toArrayHelper(int index, ArrayList<Type> arrayList) {
			arrayList.add(index, data);

			if(left != null) {
				left.toArrayHelper(arrayList.indexOf(data), arrayList);
			}

			if(right != null) {
				right.toArrayHelper(arrayList.indexOf(data)+1, arrayList);
			}
			
			return arrayList;
		}

	}

	
	/**
	 * Creates a BinarySearchTree, putting in the root as item
	 * 
	 * @param item the root of the tree
	 */
	public BinarySearchTree(Type item) {
		root = new Node<>(item);
		size = 1;
	}

	/**
	 * Creates an empty tree
	 */
	public BinarySearchTree() {
		// The defaults of the properties are already in proper
		// state for an empty tree
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(Type item) {
		// Technically don't have to - for clarity.
		// item will be dereferenced in contains().
		if (item == null) {
			throw new NullPointerException();
		}
		
		if(root == null) {
			root = new Node<Type>(item);
			size++;
			return true;
		}

		if (root.contains(item)) {
			return false;
		} else {
			root.insert(item);
			size++;
			return true;
		}
	}

	/**
	 * The collection items will not be automatically shuffled,
	 * as Java will not allow use of generic methods on a 
	 * wildcard type, preventing us from using Collections.shuffle
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public boolean addAll(Collection<? extends Type> items) {
		// Technically don't have to - for clarity.
		if (items == null) {
			throw new NullPointerException();
		}
		
		// We would like to shuffle the array here 
		
		boolean isChanged = false;
		for (Type item : items) {
			if (add(item))
				isChanged = true;
		}
		return isChanged;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		root = null;
		size = 0;
		// Let garbage be collected.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(Type item) {
		// Technically don't have to do this but clarity is nice.
		if (item == null) {
			throw new NullPointerException();
		}
		
		if(root == null) {
			return false;
		}
		
		return root.contains(item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsAll(Collection<? extends Type> items) {
		for(Type item: items) {
			if(!contains(item)) return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Type first() throws NoSuchElementException {
		if(root == null) {
			throw new NoSuchElementException();
		}
		
		// follow the left path all the way down...
		Node<Type> currentNode = root;
		while(currentNode.left != null) {
			currentNode = currentNode.left;
		}
		
		return currentNode.data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return root == null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Type last() throws NoSuchElementException {
		if(root == null) {
			throw new NoSuchElementException();
		}
		
		// follow the right path all the way down...
		Node<Type> currentNode = root;
		while(currentNode.right != null) {
			currentNode = currentNode.right;
		}
		
		return currentNode.data;
	}

	/**
	 * Makes use of the helper method in the node class
	 * 
	 * Uses a dummy node to allow the root to be removed
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public boolean remove(Type item) {
		
		
		if (item == null) {
			throw new NullPointerException();
		}
		if (root == null) return false;
		
		Node<Type> dummyNode = new Node<>(null);
		dummyNode.left = root;
		boolean treeWasChanged = root.remove(item, dummyNode, true);
		
		// this won't change anything unless 
		// 1) root was deleted AND
		// 2) root had 0 or 1 children
		root = dummyNode.left; 
		
		return treeWasChanged;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeAll(Collection<? extends Type> items) {
		boolean isChanged = false;
		for(Type item: items) {
			if(remove(item)) isChanged = true;
		}
		return isChanged;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<Type> toArrayList() {
		ArrayList<Type> arrayList = new ArrayList<>();
		if(root == null) {
			return arrayList;
		}
		return root.toArrayHelper(0, arrayList);
	}
	
	/**
	 * Writes this graph to a .dot file with the name filename
	 * 
	 * @param filename
	 */
	public void writeDot(String filename) {
		
		
		StringBuilder dotFile = new StringBuilder();
		
		dotFile.append("digraph\n");
		dotFile.append("{\n");
		if (root == null) {
			dotFile.append("\tnull;");
		} else {
			root.writeDotHelper(dotFile);
		}
		
		dotFile.append("}\n");
		
		try {
			FileWriter csvWriter = new FileWriter(filename + ".dot");
			csvWriter.write(dotFile.toString());
			csvWriter.close();
		} catch (IOException e) {
			System.out.println("Unable to write dot file.");
		}

		
	}
	
	/**
	 * @return height of our binary search tree.
	 */
	public int getHeight() {
		return root.height();
	}

}
