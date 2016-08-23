package com.easymap.datacenter.model;

import java.util.Comparator;

public class ZTopK<T>
{
    private ZHeap<T> heap;
	private int sizeN;
	 
	private T[] heapdata;
	private int sizenow =0; // 当前的树可排序的大小
	private boolean ifsizeok = false;
		
		
	 // 一课topN的树，每次把新元素和最大（或者最小值）比较，是加入还是删除，再说
	 public  ZTopK(T[] a, Comparator<? super T> c,int sizeN) { 
		 	if(a.length < sizeN)
		 	{
		 		 throw new  
		 	    IllegalArgumentException("a.length +1!= sizeN");  
		 	   
		 	}
		 	int sizenow = 0;
		 	this.sizeN = sizeN;
		 	this.heapdata = a;
	    }  
	 public void insertAnelement(T a,Comparator<? super T> c){
		 // 每次都插入一个
		 
		 
		 // 判断之前是否填完了
		 if(!this.ifsizeok){
			 this.heapdata[this.sizenow++] = a;
			 if(this.sizenow==this.sizeN){
				 this.ifsizeok = true;// 最后一个也填满了,建表
				 	this.heap = new ZHeap(this.heapdata,c,this.sizeN);
			 }
			 return;
		 }
		 
		 if (heap.getsizenow()== 0) {
			 throw new
			 IllegalArgumentException("can not extract max element in empty heap");
			 }
		 
			 T max = heap.root();
			 // 比最大的小，或者比最小的大
			 if(c.compare(a, max) < 0)
			 {
				 this.heap.setRoot(a);
				 this.heap.buildHeap();
			 }
			 else
				 ;// 不用考虑			 			 			
	 }
	 
	 // 排序
	 public void sort(){
		 this.heap.sort();
	 }
	 
	 // 测试使用
	/*public  void main(String[] args)
	{
		// 源数据
		Integer[] data = {56,275,12,6,45,478,41,1236,456,12,546,45};
		Integer[] heapdata = {56,275,12};
		
		// 最大堆，每次都是最大的数字在上面，通过对比，比最大的小的才加入，比最大的大的，直接忽略不管
		// 留下了top最小的几个数
		Comparator<Integer> compMin = new Comparator<Integer>() {  
      @Override  
      public int compare(Integer o1, Integer o2) {  
          return o1 - o2;  
      }  
  }; 
  
  		//最小堆，每次都是最小的数字在上面，通过对比，比最小的大的才加入，比最小的小的，直接忽略不管
		// 留下了top最大的几个数
	Comparator<Integer> compMax = new Comparator<Integer>() {  
	      @Override  
	      public int compare(Integer o1, Integer o2) {  
	     
	        return o2-o1 ;  
	      }  
	  }; 
	  
	  
  // 注意，这里生成之后就可以进行build的操作，形成堆了
		ZTopK atopk = new ZTopK(heapdata,compMin,heapdata.length);
		
		
		for (int i : heapdata) {
			System.out.print(i + " ");
		}
		System.out.println();
		
		for(int j=3;j<data.length;j++){
			atopk.insertAnelement(data[j], compMin);
			System.out.println("insert: "+data[j]);
			for (int i : heapdata) {
				System.out.print(i + " ");
			}
			System.out.println();
		}
		
		atopk.sort();
		for (int i : heapdata) {
			System.out.print(i + " ");
		}
		System.out.println();
		
		
		

	}*/
	

}
