package net.scapeemulator.api;

public abstract class Job<T extends Service> {

	public abstract void perform(T service);

}
