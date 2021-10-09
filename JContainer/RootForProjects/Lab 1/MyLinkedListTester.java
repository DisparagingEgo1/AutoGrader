
/* NOTE: PLEASE DO NOT CHANGE OR REVISE ANY CODE IN THIS TESTER */
/* !!! IF YOU CHANGE THIS FILE, YOU GET A ZERO FOR THIS PROJECT !!!! */

public class MyLinkedListTester {

	private static MyLinkedList<String> list0;
	private static MyLinkedList<String> list2;
	private static MyLinkedList<String> list3;
	
	private static void init() {
		list0 = new MyLinkedList<String>();
		list2 = new MyLinkedList<String>();
		list3 = new MyLinkedList<String>();
		list2.addFirst("6:Tony");
		list2.addFirst("5:Tom");
		list2.addFirst("4:Tim");
		list2.addFirst("3:Abby");
		list2.addFirst("2:Morning");
		list2.addFirst("1:Good");
		list0.addFirst(null);
		list0.addFirst("bad");
		list0.addFirst("apple");
		list0.addFirst(null);
	}
	public static void drawLine() {
		System.out.println("-----------------------------");
	}
	
	public static void testAddFirst() { //passed test
		init();
		drawLine();
		System.out.println(list0);
		System.out.println(list2);
		System.out.println(list3);
		drawLine();
	}
	
	public static void testAddLast() { //passed
		System.out.println("------------------testAddLast()----");
		init();
		list3.add("A");
		System.out.println(list3);
		list3.add("B");
		System.out.println(list3);
		list3.add(null);
		System.out.println(list3);
		list3.add("C");
		System.out.println(list3);
		drawLine();
	}
	
	public static void testAddIndex() { //passed test
		init();
		System.out.println("--------------testAddIndex()------");
		try {
			list0.add(0,"A0");
			System.out.println(list0);
			list0.add(5, "A5");
			System.out.println(list0);
			list0.add(3, "A3");
			System.out.println(list0);
			list3.add(0,"B0");
			System.out.println(list3);
			list3.add(2,"B2");
			System.out.println(list3);
		}catch(IndexOutOfBoundsException e) {
			System.out.println(e);
		}
		drawLine();
	}
	
	public static void testRemoveIndex() { //passed test
		init();
		System.out.println("--------------testRemoveIndex()------");
		try {
			list0.remove(0);
			System.out.println(list0);
			list0.remove(2);
			System.out.println(list0);
			list0.remove(2);
			System.out.println(list0);
			
		}catch(IndexOutOfBoundsException e) {
			System.out.println(e);
		}
		
		list0.remove(1);
		System.out.println(list0);
		list0.remove(0);
		System.out.println(list0);
		
		drawLine();
	}

	
	public static void testGetIndex() { // passed test
		init();
		System.out.println("--------------testGetIndex()------");
		try {
			System.out.println(list0.get(0));
			System.out.println(list0.get(1));
			System.out.println(list0.get(2));
			System.out.println(list0.get(3));
			System.out.println(list0.get(4));
			
		}catch(IndexOutOfBoundsException e) {
			System.out.println(e);
		}
		
		System.out.println(list2.get(3));
		
		drawLine();
	}

	public static void testContains() { //passed test
		init();
		System.out.println("----------------testContains()------");
		System.out.println(list0.contains(null)); 
		System.out.println(list2.contains("1:Good"));
		System.out.println(list2.contains("6:Tony"));
		System.out.println(list2.contains("notexist"));
		System.out.println(list2.contains(""));
		System.out.println(list2.contains(null));
		System.out.println(list3.contains("notexist"));
		System.out.println(list3.contains(null));
		drawLine();
	}
	
	public static void testRemoveObject() { //passed test
		init();
		System.out.println("----------------testRemove(Object)---");
		list2.remove("1:Good");
		System.out.println(list2.toString());
		list2.remove("notexist");
		System.out.println(list2.toString());
		list2.remove("6:Tony");
		System.out.println(list2.toString());
		list2.remove(null);
		System.out.println(list2.toString());
		list2.remove("4:Tim");
		System.out.println(list2.toString());
		System.out.println(list0);
		list0.remove(null);
		System.out.println(list0);
		list0.remove(null);
		System.out.println(list0);
		list3.remove("notexist");
		System.out.println(list3);
		drawLine();
	}
	
	public static void testRemoveFirst(){ //passed
	init();
		System.out.println("------------testRemoveFirst()------");
		Object tmp = null;
		try {
			
			tmp = list0.removeFirst();
			System.out.println(list0 + ", removed:" + tmp);
			tmp = list0.removeFirst();
			System.out.println(list0 + ", removed:" + tmp);
			tmp = list0.removeFirst();
			System.out.println(list0 + ", removed:" + tmp);
			tmp = list0.removeFirst();
			System.out.println(list0 + ", removed:" + tmp);
			tmp = list0.removeFirst();
			System.out.println(list0 + ", removed:" + tmp);
		} catch (Exception e) {
			System.out.println(e);
		}
		drawLine();
	}
	
	public static void testRemoveAllCopies() {  //pass
		init();
		System.out.println("---------------Test RemoveAllCopies()---");
		list3.addFirst("A");
		list3.addFirst("A");
		list3.addFirst("A");
		list3.addFirst("B");
		list3.removeAllCopies("notexist");
		System.out.println(list3);
		list3.removeAllCopies("B");
		System.out.println(list3);
		list3.removeAllCopies("A");
		System.out.println(list3);
		init();
		list3.addFirst("A");
		list3.addFirst(null);
		list3.addFirst(null);
		list3.removeAllCopies(null);
		System.out.println(list3);
		drawLine();
	}

	public static void testInterleave() { //passed
		init();
		System.out.println("-----------------testInterleave()---");
		MyLinkedList c = MyLinkedList.interleave(list0, list2);
		System.out.println(c);
		c = MyLinkedList.interleave(list0, list3);
		System.out.println(c);
		drawLine();
	}

	public static void main(String argv[]) {

                // When debugging ONE particular method below, 
                // you can comment out all other methods, so that you can focus on
                // the method you are currently debugging.  
                // When submitting your code, please uncomment all methods below.
		testAddLast(); //
		testRemoveFirst(); //
		testContains(); //
		testRemoveObject();//
		testRemoveAllCopies();//
		testInterleave(); //
		testAddIndex(); //
		testRemoveIndex(); //
		testGetIndex(); //
	
	}//end of main
}
