public class MyLinkedList {
	
	private ListNode head;
	private int size;

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

	public MyLinkedList() {
		this.head = new ListNode(null); 
		this.size = 0;
	}

	public MyLinkedList reverse() {
		return reverse(this.head.next);
	}

	private MyLinkedList reverse(ListNode node) {
		if(node == null) return new MyLinkedList();
		MyLinkedList list = reverse(node.next);
		list.addFirst(node.data);//FOR JDCR TESTING: should be list.addLast(node.data);
		return list;
	}

	public void reverse2() {
   /*
		if(this.size <= 1);
		else {
			this.head.next = reverse(this.head.next, this.head.next.next);
		}
      */
	}
	
	private ListNode reverse(ListNode first, ListNode second) {
		if(second == null) {
			return second;//FOR JDCR TESTING: should be return first;
		}
		ListNode hd = reverse(first.next,second.next);
		second.next = first;
		first.next = null;
		return hd; 
	}
	public void addLast(Object elem) {
		if(this.size == 0) addFirst(elem);
		else {
			ListNode cur = this.head.next;
			while(cur.next != null) {
				cur = cur.next;
			}
			cur.next = new ListNode(elem);	
			this.size++;
		}
	}
	public void addFirst(Object elem) {
		ListNode nn = new ListNode(elem);
		nn.next = this.head.next;
		this.head.next = nn;
		this.size ++;
	}
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

