package com.magicengine;

import java.util.Iterator;
import java.util.List;

public class ListPointer<T> implements Iterator<T> {

	private List<T> listReference;
	private T object=null;
	private int index;
	
	public ListPointer(List<T> list) {
		this.listReference = list;
		this.index = -1;
	}
	
	public ListPointer() {
		this.index = -1;
	}

	@Override
	public boolean hasNext() {
		if (index < listReference.size()-1)
			return true;
		return false;
	}

	@Override
	public T next() {
		index++;
		this.object = listReference.get(index);
		return object;
	}

	@Override
	public void remove() {
		this.listReference.remove(index);
	}
	
	public void remove(T obj){
		this.listReference.remove(obj);
	}
	
	public void addObject(T obj){
		int position = listReference.indexOf(obj);
		if(position != -1){
			listReference.add(position, obj);
		}else{
			listReference.add(obj);
		}
	}
		
	public void setListReference(List<T> listReference) {
		this.listReference = listReference;
		this.index=-1;
		this.object=null;
	}
	

	public List<T> getListReference() {
		return listReference;
	}

	public T getObject() {
		return this.object;
	}
	
	public T getObject(int position) {
		return this.listReference.get(position);
	}
	
	public void setObject(T object) {
		this.object = object;
	}

	public void toHead() {
		this.index = -1;
		this.object = null;
	}
	
	public void movePointer(int position){
		this.index=position;
		this.object = listReference.get(index);
	}
		
	public void movePointer(T obj){
		this.index = listReference.indexOf(obj);
		this.object = listReference.get(index);
		
	}
	
	public int getSize(){
		return this.listReference.size();
	}	
	
	
	
	
	public T getPrevious(){
		try{
			return this.listReference.get(index-1);
		}catch(IndexOutOfBoundsException ex){return null;
		}
		
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	

	/**
	 * Removes an element and if it's possible goes to the next
	 * element, otherwise sets the object to null.
	 */
	public void removeAndGoToNext() {
		this.getListReference().remove(this.getIndex());
		if (this.getListReference().size() > this.getIndex()) {
			this.setObject(this.getObject(this.getIndex()));
		} else {
			this.setObject(null);
		}
	}
	
}
