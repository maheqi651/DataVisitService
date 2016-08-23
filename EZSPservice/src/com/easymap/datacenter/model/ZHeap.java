package com.easymap.datacenter.model;

import java.util.Comparator;



/**
 * 堆数据结构
 *  
 */
public class ZHeap<T> {

	/**
	 * 以数组形式存储堆元素
	 */
	private T[] heap;
	private int sizenow =0; // 当前的树可排序的大小

	/**
	 * 用于比较堆中的元素。c.compare(根,叶子) > 0。 
	 * 使用相反的Comparator可以创建最大堆、最小堆。
	 */
	private Comparator<? super T> c;

	public ZHeap(T[] a, Comparator<? super T> c) {
		this.heap = a;
		this.c = c;
		this.sizenow  = a.length;
		buildHeap();
	}

	// 或者传入一个空间，但是指定了大小要比这个小的一个topN也是可以的
	public ZHeap(T[] a, Comparator<? super T> c,int size) {
		if(a.length < size)
	 	{
	 		 throw new  
	 	    IllegalArgumentException("a.length != sizeN");  
	 	   
	 	}
		this.heap = a;
		this.c = c;
		this.sizenow  = size;
		buildHeap();
	}

	/**
	 * 返回值为i/2
	 * 
	 * @param i
	 * @return
	 */
	private int parent(int i) {
		return (i - 1) >> 1;
	}

	/**
	 * 返回值为2*i
	 * 
	 * @param i
	 * @return
	 */
	private int left(int i) {
		return ((i + 1) << 1) - 1;
	}

	/**
	 * 返回值为2*i+1
	 * 
	 * @param i
	 * @return
	 */
	private int right(int i) {
		return (i + 1) << 1;
	}

	/**
	 * 堆化
	 * restore函数
	 * @param i
	 *           堆化的起始节点
	 */
	private void heapify(int i) {
		heapify(i, this.sizenow);
	}

	/**
	 * 堆化，
	 *  restore函数
	 * @param i
	 * @param size 堆化的范围
	 */
	public void heapify(int i, int size) {
		int l = left(i);
		int r = right(i);
		int next = i;
		if (l < size && c.compare(heap[l], heap[i]) > 0)
			next = l;
		if (r < size && c.compare(heap[r], heap[next]) > 0)
			next = r;
		if (i == next)
			return;
		swap(i, next);
		heapify(next, size);
	}

	/**
	 * 对堆进行排序
	 */
	public void sort() {
		// buildHeap();
		for (int i = this.sizenow - 1; i > 0; i--) {
			swap(0, i);
			heapify(0, i);
		}
	}

	/**
	 * 交换数组值
	 * 
	 * @param arr
	 * @param i
	 * @param j
	 */
	private void swap(int i, int j) {
		T tmp = heap[i];
		heap[i] = heap[j];;
		heap[j] = tmp;
	}

	/**
	 * 创建堆,生成的一棵平衡树，孩子都比父亲小（或者大）
	 */
	public void buildHeap() {
		for (int i = (this.sizenow) / 2 - 1; i >= 0; i--) {
			heapify(i);
		}
	}

	public void setRoot(T root) {
		heap[0] = root;
		heapify(0);
	}

	public T root() {
		return heap[0];
	}
	
	// 当前堆的大小
	public int getsizenow(){
		return this.sizenow;
	}

	/**
	 * 取出最大元素并从堆中删除最大元素。
	 * 
	 * @param
	 * @param a
	 * @param comp
	 * @return
	 */
	 public T extractMax(T[] a, Comparator<? super T> comp) {
	 if (a.length == 0) {
	 throw new
	 IllegalArgumentException("can not extract max element in empty heap");
	 }
	 T max = a[0];
	 a[0] = a[this.sizenow - 1];
	 heapify(0, this.sizenow - 1);
	 this.sizenow--;
	 return max;
	 }

	 // 后续再写 20151028
	 public void clear(){
		 // 清空
	 }
	 
	 
	/**
	 * @param args
	 */
	/*public static void main(String[] args) {
		Integer[] temp = null;
		temp = new Integer[] { 5, 2, 4, 6, 1, 3, 2, 6 };
		temp = new Integer[] { 16, 14, 8, 7, 9, 3, 2, 4, 1 };

		
		Comparator<Integer> comp = new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o1 - o2;
				 //生成最大堆使用o1-o2,
				// 生成最小堆使用o2-o1   
			}
		};
		
		
	//创建最大堆
		ZHeap<Integer> heap = new ZHeap<Integer>(temp, comp);
		 heap.buildHeap();
		for (int i : temp) {
			System.out.print(i + " ");
		}
		System.out.println();
		
		heap.sort();
		for (int i : temp) {
			System.out.print(i + " ");
		}
		System.out.println();
	}*/

}

