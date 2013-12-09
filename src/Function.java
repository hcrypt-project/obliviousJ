/*
 * This class implements sample encrypted basic boolean functions based on an integer parity assumption
 * 		
 * 		odd integers represent 1-bits
 * 		even integers represent 0-bits
 *
 * and
 * or
 * xor
 * not
 * ha 		half-adder
 * fa 		full-adder
 * 
 */
import java.util.Random;

public class Function 
{
	static final int C_MEMORY=0;
	static final int C_CU=1;
	static final int C_ALU=2;
	
	static int[] opcountand=new int[3];
	static int[] opcountxor=new int[3];
	static int[] opcountnot=new int[3];
	static Random rnd=new Random();
	
	static int decode(int cipher)
	{
		return cipher&1;
	}
	
	static int[] encode(int size,int value)
	{
		int r[]=new int[size];
		
		int mask=1;
		for(int i=0;i<size;i++,mask<<=1)
		{
			r[i]=rnd.nextInt()&63;
			if((value&mask)>0)
				r[i]|=1;
			else
				r[i]&=62;
		}
		
		return r;
	}
	
	static int and(int aspect,int a,int b)
	{
		opcountand[aspect]++;
		return ((a*b)+(rnd.nextInt()&62))&63;
	}
	
	static int and(int aspect,int a,int b,int c,int d)
	{
		return and(aspect,and(aspect,a,b,c),d);
	}
	
	static int and(int aspect,int a,int b,int c)
	{
		return and(aspect,and(aspect,a,b),c);
	}
	
	static int xor(int aspect,int a,int b)
	{
		opcountxor[aspect]++;
		return ((a+b)+(rnd.nextInt()&62))&63;
	}
	
	static int xor(int aspect,int a,int b,int c)
	{
		return xor(aspect,xor(aspect,a,b),c);
	}
	
	static int xor(int aspect,int a,int b,int c,int d)
	{
		return xor(aspect,xor(aspect,xor(aspect,a,b),c),d);
	}
	static int not(int aspect,int a)
	{
		opcountnot[aspect]++;
		return (a+29)&63;
	}
	
	static int or(int aspect,int a,int b,int c,int d)
	{
		return or(aspect,or(aspect,a,b,c),d);
	}
	
	static int or(int aspect,int a,int b,int c)
	{
		return or(aspect,or(aspect,a,b),c);
	}
	
	static int or(int aspect,int a,int b)
	{
		return(xor(aspect,xor(aspect,a,b),and(aspect,a,b)));
	}
	
	static int[] ha(int aspect,int a,int b)
	{
		int temp[]=new int[2];
		
		temp[0]=xor(aspect,a,b);
		temp[1]=and(aspect,a,b);
		
		return temp;
	}
	
	static int[] fa(int aspect,int a,int b,int c)
	{
		int temp[]=new int[2];
		
		int[] ha1=ha(aspect,a,b);
		int[] ha2=ha(aspect,ha1[0],c);
		
		temp[0]=ha2[0];
		temp[1]=or(aspect,ha1[1],ha2[1]);
		
		return temp;		
	}
}
