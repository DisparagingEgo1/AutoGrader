
import java.util.Arrays;

public class Tester {
	
	public static MyLinkedList list1 = new MyLinkedList();
	public static MyLinkedList list2 = new MyLinkedList();
	public static MyLinkedList list3 = new MyLinkedList();
	
	public void init(){
		list2.addFirst("C");
		list2.addFirst("B");
		list2.addFirst("A");
		list2.addFirst("D");
		list3.addFirst("A");
		list3.addFirst("B");
	}
	
	public int countSpace(String str) {	
		if(str.length() == 1) {
			if(str.charAt(0)== ' ') return 1;
			return 0;
		}
		switch(str.charAt(0)) {
			case ' ':
				return 1 + countSpace(str.substring(1));
			default:
				return countSpace(str.substring(1));//FOR JDCR TESTING: should be return countSpace(str.substring(1));
		}

	}
	
	public boolean myContains(String s1, String s2){
		if(s1 == null || s1.isEmpty() || s2==null || s2.isEmpty()) return false;
		if(s1.charAt(0) ==s2.charAt(0)) {
			if(s1.length() == 1)return true;//FOR JDCR TESTING: should be if(s1.length() == 1)return true;
			if(myContains(s1.substring(1),s2.substring(1))) {
				return true;
			}
		}
		else if(s1.length() == 1) return false;
		return myContains(s1,s2.substring(1));
	}

	public int div(int m, int n) throws Exception {
		if(n == 0) throw new NullPointerException();//FOR JDCR TESTING: should be IllegalArgumentException
		if(m >= n) return 1 + div(m-n,n);
		return 1;//FOR JDCR TESTING: should be 0 
	}
	
	public boolean isSum24(int arr[])
	{
		return isSum24(arr, 24);
	}

	private boolean isSum24(int arr[], int targetSum) {
		if(arr.length == 0) return false;
		if(arr.length == 1 && targetSum - arr[0] == 0)return true;//FOR JDCR TESTING: should be return true
		if(arr.length == 1 && targetSum - arr[0] != 0) return false;
		int[] sub= new int [arr.length-1];
		for(int i = 0; i <arr.length-1;i++) {
			sub[i] = arr[i];
		}
		return isSum24(sub,targetSum-arr[arr.length-1]); 
	}
	

	public void reverseArray(int a[]) {
		reverseArray(a, 0, a.length - 1);
	}
	/*
   if(low >= high);
		else {
			int temp = a[low];//FOR JDCR TESTING: should be a[low]
			a[low]= a[high];
			a[high]=temp;
			reverseArray(a, ++low,--high);
		}
   */
	private void reverseArray(int a[], int low, int high) {
		}
	}
	

	public void recursiveSelectionSort(int a[]) {
		recursiveSelectionSort(a, 0, a.length - 1); 
	}
	
	private void recursiveSelectionSort(int a[], int low, int high) 
	{
			if(low == high);
			else {
				int smallest = low;
				for(int i = low; i <a.length;i++) {
					if(a[i]< a[smallest])smallest = i;
				}
				if(smallest == low);
				else {
					int temp = a[low];//FOR JDCR TESTING: should be a[low]
					a[low]=a[smallest];
					a[smallest] = temp;
				}
				recursiveSelectionSort(a, low +1,high);
			}


	}//end of method
	
	
	public static void main(String[] args) throws Exception {
		Tester test = new Tester();
		test.init();
		System.out.println("---------Test Reverse()-------");
		System.out.println(list1.reverse());
		System.out.println(list2.reverse());
		System.out.println(list3.reverse());
		System.out.println("-------Test Reverse2()---------");
		list1.reverse2();
		list2.reverse2();
		list3.reverse2();
		System.out.println(list1);
		System.out.println(list2);
		System.out.println(list3);
		System.out.println("-------Test countSpace()----------");
		System.out.println(test.countSpace("g  d  "));
		System.out.println(test.countSpace("good "));
		System.out.println(test.countSpace("  good"));
		System.out.println(test.countSpace("good  mornin g "));
		System.out.println("-----Test myContains()------------");
		System.out.println( test.myContains("an", "banana"));
		System.out.println( test.myContains("bn", "banana"));
		System.out.println( test.myContains("er", "richer"));
		System.out.println( test.myContains("a", "a"));
		System.out.println("-----Test div()------------");
		System.out.println( test.div(11, 3) );
		System.out.println( test.div(12, 5) );
		System.out.println( test.div(4, 4) );
		System.out.println( test.div(3, 7) );
		System.out.println( test.div(16, 4) );
		
		System.out.println("-----Test isSum24()------------");
		int a[] = {6, 3, 8, 3, 4};
		int b[] = {5, 6, 7};
		int c[] = {24};
		int d[] = {10, 14};
		int e[] = {};
		System.out.println( test.isSum24(a) ); //true
		System.out.println( test.isSum24(b) ); //false
		System.out.println( test.isSum24(c) ); //true
		System.out.println( test.isSum24(d) ); //true
		System.out.println( test.isSum24(e) ); //false
		
		System.out.println("-----Test reverseArray()------------");
		test.reverseArray(a);
		System.out.println(Arrays.toString(a));//43836
		test.reverseArray(b);
		System.out.println(Arrays.toString(b));//765
		test.reverseArray(d);
		System.out.println(Arrays.toString(d));//14,10
		
		System.out.println("-----Test recursiveSelectionSort()------------");
		test.recursiveSelectionSort(a, 0, a.length - 1);
		System.out.println(Arrays.toString(a)); //33468
		int f[] = {2, 5, 1, 7, 9, 3, 6, 8};
		test.recursiveSelectionSort(f, 0, f.length - 1);
		System.out.println(Arrays.toString(f));//12356789
	}

}
