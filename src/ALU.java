/*
 * This class implements an encrypted ALU
 * 
 */

public class ALU 
{
	
	public static int carry=2;
	public static int zero=2;
	public static int minus=2;
	
	public static Object[] add(int[] a,int[] b,int carry)
	{
		int[] res=new int[8];
		int c=carry;
		
		for(int i=0;i<8;i++)
		{
			int[] t=Function.fa(Function.C_ALU,a[i], b[i], c);
			res[i]=t[0];
			c=t[1];
		}
		
		return new Object[]{res,new Integer(c)};
	}
	
	public static int[] addadr(int[] a,int[] b)
	{
		int[] res=new int[Memory.ARRAY_COLS];
		int c=0;
		
		for(int i=0;i<Memory.ARRAY_COLS;i++)
		{
			int[] t=Function.fa(Function.C_ALU,a[i], b[i], c);
			res[i]=t[0];
			c=t[1];
		}
		
		return res;
	}
}
