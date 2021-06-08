package utils;
/**
 *  This generic class is a reference wrapper for the Mouse listener function in the Utils Class 
 */

// generic class, subtype T
public class RefWrapper<T>
{
	public T val;
	
	public RefWrapper(T val)
	{
		this.val = val;
	}
	
}
