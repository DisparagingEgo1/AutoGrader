import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class MyLinkedListTest {
	public MyLinkedList list1;
	public MyLinkedList list2;
	public MyLinkedList list3;
	public Tester test;
	public ByteArrayOutputStream bos;
	public PrintStream originalOut;
	
	@BeforeEach
	public void init() {
		list1 = new MyLinkedList();
		list2 = new MyLinkedList();
		list3 = new MyLinkedList();
		test = new Tester();
		list2.addFirst("C");
		list2.addFirst("B");
		list2.addFirst("A");
		list2.addFirst("D");
		list3.addFirst("A");
		list3.addFirst("B");
		originalOut = System.out;
		bos = new ByteArrayOutputStream();
		System.setOut(new PrintStream(bos));
	}
	@AfterEach
	public void cleanup() {
		System.setOut(originalOut);
	}
	@Test
	@DisplayName("Reverse Linked List with new nodes")
	public void testLinkedListNewNodes() {
		System.out.print(list1.reverse()+"\n");
		System.out.print(list2.reverse()+"\n");
		System.out.print(list3.reverse()+"\n");
		assertEquals("{}\n"+"{C->B->A->D}\n"+"{A->B}\n",bos.toString());
	}
	@Test
	@DisplayName("Reverse Linked List by rewiring")
	public void testLinkedListRewire() {
		list1.reverse2();
		list2.reverse2();
		list3.reverse2();
		System.out.print(list1+"\n");
		System.out.print(list2+"\n");
		System.out.print(list3+"\n");
		assertEquals("{}\n"+"{C->B->A->D}\n"+"{A->B}\n",bos.toString());
	}
	@Test
	@DisplayName("Count White Space")
	public void testCountWhiteSpace() {
		System.out.print(test.countSpace("g  d  ")+"\n");
		System.out.print(test.countSpace("good ")+"\n");
		System.out.print(test.countSpace("  good")+"\n");
		System.out.print(test.countSpace("good  mornin g ")+"\n");
		assertEquals("4\n1\n2\n4\n",bos.toString());
	}
	@Test
	@DisplayName("Substring contained in String")
	public void testMyContains(){
		System.out.print( test.myContains("an", "banana")+"\n");
		System.out.print( test.myContains("bn", "banana")+"\n");
		System.out.print( test.myContains("er", "richer")+"\n");
		System.out.print( test.myContains("a", "a")+"\n");
		assertEquals("true\nfalse\ntrue\ntrue\n",bos.toString());
	}
	@Test
	@DisplayName("div throws IllegalArgument")
	public void testDivThrowsIllegalArgument() {
		Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
			test.div(12,0);
		});
	}
	@Test
	@DisplayName("div calculates correctly")
	public void testDiv() throws Exception {
		System.out.print( test.div(11, 3)+"\n" );
		System.out.print( test.div(12, 5)+"\n" );
		System.out.print( test.div(4, 4) +"\n");
		System.out.print( test.div(3, 7) +"\n");
		System.out.print( test.div(16, 4)+"\n" );
		assertEquals("3\n2\n1\n0\n4\n",bos.toString());
	}
	@Test
	@DisplayName("isSum24 calculates sums correctly")
	public void testIsSum24() {
		int a[] = {6, 3, 8, 3, 4};
		int b[] = {5, 6, 7};
		int c[] = {24};
		int d[] = {10, 14};
		int e[] = {};
		System.out.print( test.isSum24(a) + "\n" ); //true
		System.out.print( test.isSum24(b)+ "\n" ); //false
		System.out.print( test.isSum24(c)+ "\n" ); //true
		System.out.print( test.isSum24(d)+ "\n" ); //true
		System.out.print( test.isSum24(e)+ "\n" ); //false
		assertEquals("true\nfalse\ntrue\ntrue\nfalse\n",bos.toString());
	}
	@Test
	@DisplayName("reverseArray reverses correctly")
	public void testReverseArray() {
		int a[] = {6, 3, 8, 3, 4};
		int b[] = {5, 6, 7};
		int d[] = {10, 14};
		test.reverseArray(a);
		System.out.print(Arrays.toString(a)+"\n");//43836
		test.reverseArray(b);
		System.out.print(Arrays.toString(b)+"\n");//765
		test.reverseArray(d);
		System.out.print(Arrays.toString(d)+"\n");//14,10
		assertEquals("[4, 3, 8, 3, 6]\n"+"[7, 6, 5]\n"+"[14, 10]\n",bos.toString());
	}
	@Test
	@DisplayName("Selection Sort works")
	public void testRecursiveSelectionSort() {
		int a[] = {6, 3, 8, 3, 4};
		test.recursiveSelectionSort(a);
		System.out.print(Arrays.toString(a)+"\n"); //33468
		int f[] = {2, 5, 1, 7, 9, 3, 6, 8};
		test.recursiveSelectionSort(f);
		System.out.print(Arrays.toString(f)+"\n");//12356789
		assertEquals("[3, 3, 4, 6, 8]\n"+"[1, 2, 3, 5, 6, 7, 8, 9]\n",bos.toString());
	}
}
