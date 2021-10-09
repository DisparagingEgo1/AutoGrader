import java.util.NoSuchElementException;
/*
 * Extra Credit Attempted
 */
public class MyLinkedList<E> {
	
	private ListNode head;
	private int size;
	
	//inner class for ListNode
	private class ListNode {
		private E data;
		private ListNode next;
		private ListNode(E d) {
			this.data = d;
			this.next = null;
		}
	}
	
	public MyLinkedList() {
		this.head = new ListNode(null); //with a dummy head node
		this.size = 0;
	}
	
	public int size() {
		return this.size;
	}

	public boolean isEmpty() {
		return this.size == 0;
	}

	// Add Object e to start of this LinkedList
	// Please DO NOT change this addFirst() method.
	// You must keep and include this provided addFirst() method in your source code.
	public void addFirst(final E e)
	{
		ListNode node = new ListNode(e);
		node.next = head.next;
		head.next = node;
		size++;
	}
	
	// Remove(cut out) the first data node(the node succeeding the dummy node) 
	//       in this list, then returns the data in the node removed.
	// If the size of this list is zero, throws an Exception.
	public E removeFirst() throws Exception {
		if(this.size == 0) throw new Exception("Empty List!");
		E data = this.head.next.data;
		this.head.next = this.head.next.next;
		this.size--;
		return data; //change this as you need.
	}
	
	// Returns true if this list contains the specified element o. 
	// More formally, returns true if and only if this list contains at least one element e 
	// such that (o==null ? e==null : o.equals(e)).
	// Note: you have to handle the case where a list node stores null data element.
	public boolean contains(final E o) {
		ListNode cur = this.head.next;
		while(cur!= null) {
			if(o == null &&cur.data == null) return true;
			if(!(o==null)&&o.equals(cur.data))return true;
			cur = cur.next;
		}
		return false; //change this as you need.
	}
	
	// Removes the first occurrence of the specified element o from this list and returns true, if it is present. 
	// If this list does not contain the element o, it is unchanged and the method returns false.
	// More formally, removes the element o with the lowest index i such that 
	//     (o==null ? get(i)==null : o.equals(get(i))) (if such an element exists).
	// Note: you have to handle the case where a list node stores null data element.
	public boolean remove(final E o) {
		ListNode prev = this.head,cur = this.head.next;
		while(cur!= null) {
			if(o == null &&cur.data == null) {
				prev.next = cur.next;
				this.size--;
				return true;
			}
			if(!(o==null)&&o.equals(cur.data)) {
				prev.next = cur.next;
				this.size--;
				return true;
			}
			prev = prev.next;
			cur = cur.next;
		}
		return false; //change this as you need.
	}

	// Removes all copies of o from this linked list.
	// You have to handle the cases where Object o may 
	//        have zero, one or multiple copies in this list.
	// If any element has been removed from this list, returns true. 
	//        Otherwise returns false.
	// Note: be careful when multiple copies of Object o are stored
	//        in consecutive(adjacent) list nodes.
	//        E.g. []->["A"]->["A"]->["B"]. 
	//        Be careful when removing all "A"s in this example.
	// Note: This list may contains zero, one or multiple copies of null data elements.
	//
	//public boolean removeAllCopies(final E o){ <- This is a much simpler solution, but slower
	// boolean ret = remove(o);
	// while(remove(o));
	// return ret;
	public boolean removeAllCopies( final E o ) { //passed test
		ListNode prev = this.head,cur = this.head.next;
		boolean remove = false;
		while(cur!= null) {
			if(o == null &&cur.data == null) {
				prev.next = cur.next;
				cur=cur.next;
				this.size--;
				remove = true;
			}
			else if(!(o==null)&&o.equals(cur.data)) {
				prev.next = cur.next;
				cur = cur.next;
				this.size--;
				remove = true;
			}
			else {
				prev = prev.next;
				cur = cur.next;
			}
		}
		return remove; //change this as you need.
	}
	
	// Insert data elements from linkedlist A and B alternately into 
	//    a new linkedlist C, then returns C.
        // Follow the pattern to pick items in list A and B, 
        //        linkedlist A->linkedlist B->linkedlist A->linkedlist B …
	// If A is longer than B, append remaining items in A to C
	//     when the end of B is first reached.
	// If B is longer than A, append remaining items in B to C
	//     when the end of A is first reached.
	// E.g1 A = {1, 3, 5, 7, 9} and B = {2, 4, 6}; and 
	//       C will be {1, 2, 3, 4, 5, 6, 7, 9}.
        //
	// E.g2 A = {1, 3, 5} and B = {2, 4, 6, 8, 10}; and 
	//       C will be {1, 2, 3, 4, 5, 6, 8, 10}.
	// Note: after this method is called, both list A and B are UNCHANGED.
	public static MyLinkedList<Object> interleave(final MyLinkedList<?> A, final MyLinkedList<?> B) {
		if(A == null || B == null)return null;
		int max = Math.max(A.size(),B.size());
		MyLinkedList<Object>nList = new MyLinkedList<Object>();
		for(int i = 0; i < max; i++) {
			if(i < A.size())nList.add(A.get(i));
			if(i < B.size())nList.add(B.get(i));
		}
		return nList; //change this as you need.
	}
	
	// Inserts the specified element at the specified position in this list. 
	// Shifts the element currently at that position (if any) and any subsequent
	//     elements to the right (adds one to their indices).
	// if(index < 0 or index > this.size), throws IndexOutOfBoundsException.
	
	// E.g, if this list is [dummy]->["A"]->["B"]->["C"] with size = 3.
	//   add(0,D) will result in [dummy]->["D"]->["A"]->["B"]->["C"].
	//   Continuing on the previous add() call, add(1,"E") will
	//   change the existing list to [dummy]->["D"]->["E"]->["A"]->["B"]->["C"].
	public void add(final int index, final E o) throws IndexOutOfBoundsException {
		if(index < 0 || index > this.size)throw new IndexOutOfBoundsException("Provided Index is out of bounds! "+index);
		int count = 0;
		ListNode cur = this.head, nNode = new ListNode(o);
		while(count != index) {
			count++;
			cur = cur.next;
		}
		nNode.next =cur.next;
		cur.next = nNode;
		this.size++;
	}
	

	// Returns the element at the specified index in this list.
	// Be noted that the listnode at head.next has index 0 and 
	//      the last list node has index of size()-1.
	// if index < 0 or index >= this.size, throws IndexOutOfBoundsException.
	public E get(final int index) throws IndexOutOfBoundsException{
		if(index < 0 || index >= this.size)throw new IndexOutOfBoundsException("Provided Index is out of bounds! "+index);
		ListNode cur = this.head.next;
		int count = 0;
		while(count != index) {
			count++;
			cur = cur.next;
		}
		return cur.data; //change this as you need.
	}
	
	// Removes (cuts out) the list node at the specified index in this list. 
	// Returns the data element in the node that is removed.
	// Be noted that the list node at head.next has index 0 and 
	//      the last list node has index of size()-1.
	// if index < 0 or index >= this.size, throws IndexOutOfBoundsException.
	public E remove(final int index) throws IndexOutOfBoundsException {
		if(index < 0 || index >= this.size)throw new IndexOutOfBoundsException("Provided Index is out of bounds! "+index);
		ListNode prev = this.head,cur = this.head.next;
		int count = 0;
		while(count != index) {
			count++;
			cur = cur.next;
			prev = prev.next;
		}
		prev.next = cur.next;
		this.size--;
		return cur.data; //change this as you need.
	}

	
	//Add the object e to the end of this list.
	// it returns true, after e is successfully added.
	public boolean add(final E e) {
		ListNode cur = this.head;
		while(cur.next!= null) {
			cur = cur.next;
		}
		cur.next = new ListNode(e);
		this.size++;
		return true; //change this as you need.
	}
	
        //Please DO NOT Change the following toString() method!!!
        //You must include the following toString() method in your source code.
	@Override
	public String toString() {
		String result = "{";
	    for (ListNode node = this.head.next; node != null; node = node.next) {
	    		if(node.next != null)
	    			result += node.data + "->"; 
	    		else
	    			result += node.data;
	    }
	    return result + "}";
	  }
}
