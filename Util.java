import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;


public class Util {
	public static byte[] subArray(byte[] array, int begin, int end)
	{
		if (end > array.length)
			end = array.length-1;
		return Arrays.copyOfRange(array, begin, end+1);
	}
	
	public static byte[] intToBytes(int number)
	{
		return new byte[] {
				(byte)(number >>> 24),
	            (byte)(number >>> 16),
	            (byte)(number >>> 8),
	            (byte)number};
	}
	
	public static int bytesToInt(byte[] bytes)
	{
		return ByteBuffer.wrap(bytes).getInt();
	}
	
	public static byte[] joinArrays (byte[][] arrays)
	{
		int sizeSum = 0;
		for (byte[] array: arrays)
			sizeSum += array.length;
		byte[] masterArray = new byte[sizeSum];
		int i=0;
		for (byte[] array: arrays)
		{
			for(byte b: array)
			{
				masterArray[i] = b;
				i++;
			}
		}
		
		return masterArray;
	}
	
	public static<T> int existInList(T item, ArrayList<T> array)
	{
		int i=0;
		for (T element: array)
		{
			if (item.equals(element))
				return i;
		}
		return -1;
	}
	
	public static String bytesToString(byte[] bytes)
	{
		return new String(bytes, StandardCharsets.UTF_8);
	}
	
	public static byte[] buildPack(Action action, byte[] arg)
	{
		byte[][] jointArray = {{Action.toByte(action)}, arg};
		return Util.joinArrays(jointArray);
	}
	
	public static byte[] buildPack(Action action, byte[] arg1, byte[] arg2)
	{
		byte[][] jointArray = {{Action.toByte(action)}, arg1, arg2};
		return Util.joinArrays(jointArray);
	}
	
	public static byte[] buildPack(Action action, byte[] arg1, byte[] arg2, byte[] arg3)
	{
		byte[][] jointArray = {{Action.toByte(action)}, arg1, arg2, arg3};
		return Util.joinArrays(jointArray);
	}
}
