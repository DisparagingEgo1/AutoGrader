public class MyLinkedList {
	
	private ListNode head;
	private int size;
	/**
	 * inner class for ListNode
	 */
	private class ListNode {
		private Object data;
		private ListNode next;
		private ListNode(Object d) {
			this.data = d;
			this.next = null;
		}
		private ListNode() {
		}
	}
	/**
	 * Constructor of linked list, creating an empty linked list
	 * with a dummy head node.
	 */
	public MyLinkedList() {
		this.head = new ListNode(null); //an empty list with a dummy head node
		this.size = 0;
	}
	
	/**
	 * This method reverses the order of data items in this list. Particularly, 
	 * the first data item (stored in the node succeeding the dummy node) in the original list
	 * becomes the last item in the resultant reversed list, 
	 * the second data item in the original list (the one succeeding the first data item)
	 * becomes the second last item in the reversed list, and so on.
	 * This method invokes a recursive helper method, which 
	 * you have to write right below this method.
	 * Note: when you reverse the data items, please do NOT reverse the dummy 
	 * head node. Instead, keep a dummy head node in the reversed list.
	 * 
	 * You are allowed to create *new* list node when building the returned LinkedList object.
	 * You are NOT allowed to change method signatures that have been provided.
	 * You are NOT allowed to change the provided implementation in the *public* method reverse().
	 * 
	 * @return A new linked list object that contains the original data set,
	 *         but in a reversed order. The returned linked list object has a 
	 *         dummy head node at the beginning of the list.
	 */
	public MyLinkedList reverse() {
		// Please implement this recursive helper method below.
		// You have to use recursion.
		return reverse(this.head.next);
	}
	
	/**
	 * 
	 * Implement the private reverse(ListNode node) method using recursion.
         * You are allowed to add helper method into this class if needed.
	 * @param node -- the reference of the first data node(successor of the dummy) in the original list.
	 * @return A new MyLinkedList object contains the same set of data items, but in a reversed order.
	 */
	private MyLinkedList reverse(ListNode node) {
		if (node == null)
			return new MyLinkedList();
		
		MyLinkedList reversedSubList = reverse(node.next);
		
		reversedSubList.addLast(node.data);
		
		return reversedSubList;
	}
	
	public void addLast(Object elem) {
		ListNode nn = new ListNode(elem);
		
		if(this.head.next == null)
			addFirst(elem);
		else {
			ListNode start = this.head.next;
			while(start.next != null) {
				start = start.next;
		}
			start.next = nn;
			this.size++;
		}
		
	}
	
	
	/**
	 * This method reverse2() will reverse all data nodes in this list, WITHOUT
	 * creating(introducing) new list nodes, by simply re-wiring the next reference in
	 * the existing list node. For example, list1 = []—>[A]—>[B],
	 * the reversed list1 will be []-->[B]-->[A], 
	 * after assigning node A to B's next reference and setting A's next to null.
	 * 
	 */
	public void reverse2() {
		if(this.size <= 1) 
			return;
		// The following method call works on a *sublist* without a Dummy Node.
		// Namely, we preserved the OLD dummy head node in the reversed list.
		this.head.next = reverse(this.head.next, this.head.next.next);
	}
	
	/**
	 * Please implement the helper method below for reverse2().
	 * @param first, the first node of the list to be reversed.
	 * @param second, the second node of the list to be reversed.
	 * @return the new head node of the reversed list.
	 * Note: you are NOT allowed to create new list node, but have to
	 * re-wiring the existing nodes by changing their next references.
	 * Write this method using recursion.
	 */
	private ListNode reverse(ListNode first, ListNode second) {
		if(second == null) {
            return first;
    }
    ListNode remainingReversed = reverse(second, second.next);
    second.next = first;
    first.next = null;
    
    return remainingReversed; // change this line of code as needed.
}
	
	/**
	 * Please do NOT change this method. Instead, you have to include it 
	 * 		into you source code.
	 * @param elem
	 */
	public void addFirst(Object elem) {
		ListNode nn = new ListNode(elem);
		nn.next = this.head.next;
		this.head.next = nn;
		this.size ++;
	}
	
	/**
	 * Please do NOT change this method. Instead, you have to include it 
	 * 		into you source code.
	 * Dump this list into a single string.
	 * @return A string
	 */
	public String toString() {
		String result = "{";
	    for (ListNode node = this.head.next; node != null;
	    		node = node.next) {
	    		result += node.data;
	    	
	    		if(node.next != null)
	    			result += "->";
	    }
	    return result + "}";
	  }

}

