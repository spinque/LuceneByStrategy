package com.spinque.rest.client;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * Limited implementation of a wrapper from JsArray to List
 * 
 * FIXME: far from complete.
 * 
 * @param <C> The type of the resulting list (interface that all list-items have implemented).
 * @param <D> The JavaScriptObject that the underlying JsArray is filled with.
 */
public class JsArrayList<C, D extends JavaScriptObject> implements List<C> {
	
	private final JsArray<D> _source;

	public JsArrayList(JsArray<D> jsArray) {
		if (jsArray == null)
			throw new NullPointerException();
		_source = jsArray;
	}
	

	@Override
	public int size() {
		return _source.length();
	}

	@Override
	public boolean isEmpty() {
		return _source.length() == 0;
	}

	@Override
	public boolean contains(Object o) {
		// FIXME: null will not be found at the moment.
		if (!(o instanceof JavaScriptObject))
			return false;
		for (int i = 0;i < _source.length();i++)
			if (_source.get(i) != null && _source.get(i).equals(o))
				return true;
		return false;
	}
	
	class JsArrayIterator implements Iterator<C> {
		
		private int _pos;
		private int _to;
		
		public JsArrayIterator(int from, int to) {
			if (_to > _source.length())
				throw new IndexOutOfBoundsException();
			_pos = from;
			_to = to;
		}

		@Override
		public boolean hasNext() {
			return _pos < _to;
		}

		@Override
		@SuppressWarnings("unchecked")
		public C next() {
			if (!hasNext())
				throw new NoSuchElementException();
			return (C) _source.get(_pos++);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}

	@Override
	public Iterator<C> iterator() {
        return new JsArrayIterator(0, _source.length());
	}

	@Override
	public Object[] toArray() {
		JavaScriptObject[] result = new JavaScriptObject[_source.length()];
		return toArray(result);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] result) {
		for (int i = 0;i < _source.length();i++)
			result[i] = (T) _source.get(i);
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public C get(int index) {
		return (C) _source.get(index);
	}

	@Override
	@SuppressWarnings("unchecked")
	public C set(int index, C element) {
		if (!(element instanceof JavaScriptObject)) {
			throw new IllegalArgumentException();
		}
		C oldValue = (C) _source.get(index);
		_source.set(index, (D) element);
		return oldValue;
	}

	@Override
	public List<C> subList(int fromIndex, int toIndex) {
		if (fromIndex == toIndex)
			return Collections.emptyList();
		if (fromIndex > toIndex || toIndex > size())
			throw new IndexOutOfBoundsException();
		return new SubList(fromIndex, toIndex);
	}
	
	class SubList implements List<C> {

		private int _from;
		private int _to;
		
		public SubList(int fromIndex, int toIndex) {
			_from = fromIndex;
			_to = toIndex;
		}

		@Override
		public int size() {
			return _to-_from;
		}

		@Override
		public boolean isEmpty() {
			return _to == _from;
		}

		@Override
		public boolean contains(Object o) {
			return indexOf(o) != -1;
		}

		@Override
		public Iterator<C> iterator() {
			return new JsArrayIterator(_from, _to);
		}

		@Override
		public boolean add(C e) {
			JsArrayList.this.add(_to, e);
			_to++;
			return true;
		}

		@Override
		public boolean remove(Object o) {
			int pos = JsArrayList.this.indexOf(o, _from, _to);
			if (pos != -1) {
				JsArrayList.this.remove(pos);
				_to--;
				return true;
			} else {
				return false;
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public C get(int index) {
			if (index > size())
				throw new IndexOutOfBoundsException();
			return (C) _source.get(_from + index);
		}

		@Override
		@SuppressWarnings("unchecked")
		public C set(int index, C element) {
			if (!(element instanceof JavaScriptObject)) {
				throw new IllegalArgumentException();
			}
			if (index > size())
				throw new IndexOutOfBoundsException();
			C oldValue = get(index); 
			_source.set(_from + index, (D) element);
			return oldValue;
		}

		@Override
		public void add(int index, C element) {
			if (index > size()) 
				throw new IndexOutOfBoundsException();
			JsArrayList.this.add(_from + index, element);
			_to++; // increment our range...
		}

		@Override
		public C remove(int index) {
			if (index > size()) 
				throw new IndexOutOfBoundsException();
			C value = JsArrayList.this.remove(index);
			_to--;
			return value;
		}

		@Override
		public int indexOf(Object o) {
			return JsArrayList.this.indexOf(o, _from, _to);
		}

		@Override
		public int lastIndexOf(Object o) {
			return JsArrayList.this.lastIndexOf(o, _from, _to);
		}

		@Override
		public List<C> subList(int fromIndex, int toIndex) {
			return JsArrayList.this.subList(_from + fromIndex, _from + toIndex);
		}

		@Override
		public void clear() {
			removeRange(_from, _to);
			_to = _from;
		}

		@Override
		public Object[] toArray() {
			throw new UnsupportedOperationException();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public ListIterator<C> listIterator() {
			throw new UnsupportedOperationException();
		}

		@Override
		public ListIterator<C> listIterator(int index) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override		
		public boolean addAll(Collection<? extends C> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(int index, Collection<? extends C> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean add(C e) {
		if (!(e instanceof JavaScriptObject)) {
			throw new IllegalArgumentException();
		}
		_source.push((D) e);
		return true;
	}
	
	@Override
	public boolean addAll(Collection<? extends C> c) {
		for (C item : c) {
			add(item);
		}
		return true;
	}

	@Override
	public void clear() {
		_source.setLength(0);
	}
	
	@Override
	public boolean remove(Object o) {
		int pos = indexOf(o);
		if (pos == -1) {
			return false;
		} else {
			removeRange(pos, 1);
			return true;
		}
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean addAll(int index, Collection<? extends C> c) {
		if (index > size())
			throw new IndexOutOfBoundsException();
		createRange(index, c.size());
		int pos = index;
		for (C item : c) {
			if (!(item instanceof JavaScriptObject)) {
				throw new IllegalArgumentException();
			}
			_source.set(pos++, (D) item);
		}
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void add(int index, C element) {
		if (!(element instanceof JavaScriptObject)) {
			throw new IllegalArgumentException();
		}
		if (index > size())
			throw new IndexOutOfBoundsException();
		createRange(index, 1);
		_source.set(index, (D) element);
	}

	@Override
	public C remove(int index) {
		if (index >= size())
			throw new IndexOutOfBoundsException();
		C oldValue = get(index);
		removeRange(index, 1);
		return oldValue;
	}

	@Override
	public int indexOf(Object o) {
		return indexOf(o, 0, size());
	}

	@Override
	public int lastIndexOf(Object o) {
		return lastIndexOf(o, 0, size());
	}

	/* non-supported operations */
	
	@Override
	public ListIterator<C> listIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<C> listIterator(int index) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	/* helpers */
	
	private int indexOf(Object o, int from, int to) {
		if (!(o instanceof JavaScriptObject))
			return -1;
		for (int i = from;i < to;i++)
			if (o == null ? get(i) == null : o.equals(_source.get(i)))
				return i;
		return -1;
	}
	
	private int lastIndexOf(Object o, int from, int to) {
		if (!(o instanceof JavaScriptObject))
			return -1;
		for (int i = to - 1;i >= from;i--)
			if (o == null ? get(i) == null : o.equals(_source.get(i)))
				return i;
		return -1;
	}
	
	/**
	 * Inserts a number of (unset) elements in the array at the given index.
	 * @param index
	 * @param size
	 */
	private void createRange(int index, int size) {
		int originalSize = _source.length();
		_source.setLength(originalSize + size);
		
		// move all items further down the list to 
		// make space.
		for (int i = index; i < originalSize; i++) {
			_source.set(i+size, _source.get(i)); 
		}
	}

	/**
	 * Removes a number of elements from the array at the given index.
	 * @param index
	 * @param size
	 */
	private void removeRange(int index, int size) {
		if (index + size > _source.length())
			throw new IndexOutOfBoundsException();
			
		int originalSize = _source.length();
		// move all items higher up the list, so that 
		// the removed values get overwritten.
		for (int i = index; i < originalSize; i++) {
			_source.set(i, _source.get(i+size)); 
		}
		_source.setLength(originalSize - size);
		
	}

}
