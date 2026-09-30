package net.scapeemulator.game.model.mob;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class MobList<T extends Mob> implements Iterable<T> {
	private final Mob[] mobs;
	private int size = 0;

	private class MobListIterator implements Iterator<T> {

		private int index = 0;

		@Override
		public boolean hasNext() {
			for (int i = index; i < mobs.length; i++) {
				if (mobs[i] != null)
					return true;
			}

			return false;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T next() {
			for (; index < mobs.length; index++) {
				if (mobs[index] != null)
					return (T) mobs[index++];
			}

			throw new NoSuchElementException();
		}

		@SuppressWarnings("unchecked")
		@Override
		public void remove() {
			if (index == 0 || mobs[index - 1] == null)
				throw new IllegalStateException();

			MobList.this.remove((T) mobs[index - 1]);
		}
	}

	public MobList(int capacity) {
		mobs = new Mob[capacity];
	}

	public boolean add(T mob) {
		for (int id = 0; id < mobs.length; id++) {
			if (mobs[id] == null) {
				mobs[id] = mob;
				size++;

				mob.setId(id + 1);
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public T get(int id) {
		id--;
		return (T) mobs[id];
	}

	public void remove(T mob) {
		int id = mob.getId();
		id--;
		assert mobs[id] == mob;

		mobs[id] = null;
		size--;

		mob.unlist();
	}

	@Override
	public Iterator<T> iterator() {
		return new MobListIterator();
	}

	public int getSize() {
		return size;
	}

	@SuppressWarnings("unchecked")
	public void clean() {
		for (Mob mob : mobs) {
			if (mob != null) {
				if (!mob.isListed()) {
					remove((T) mob);
				}
			}
		}
	}
}
