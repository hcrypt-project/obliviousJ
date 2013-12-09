import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;
import java.util.StringTokenizer;

public class Main 
{
	static Random rnd=new Random();
	
	
	public static void main(String[] args) throws Exception
	{
		long cycletime=0;
		long systime=0;
		loadMemory();
		
//		memdump(2);
//		int reg[]=new int[]{1,0,1,0,1,0,1,0,1,0,1,0};
//		int r2[]=Memory.access(new int[]{0,0,0,0,0}, reg, 1);
//		
//		for(int i=0;i<8;i++)
//			System.out.println(r2[i]);
//		
//		memdump(2);
		
//		memdump(5);
		
		memdump(8);
		
		systime=System.currentTimeMillis();
		for(int i=0;i<300;i++) //115 creditcard, 239 bubble sort
		{
			System.out.println("CYCLE "+i);
			cycletime+=CU.process();
//			System.out.println("cycle time="+cycletime);
			
//			if(decode(CU.pc)==49)
//				System.out.println("end after cycle "+i);
		}
		systime=System.currentTimeMillis()-systime;
//		CU.dump("AC", CU.ac, ALU.carry);
		
		memdump(8);
		
//		System.out.println("and:"+Function.opcountand);
//		System.out.println("xor:"+Function.opcountxor);
//		System.out.println("not:"+Function.opcountnot);
		
		System.out.println("time="+systime+"ms");
		System.out.println("avg. cycletime="+cycletime+"ms");
		System.out.println("processed gates");
		System.out.println("MEMORY");
		System.out.println("AND "+Function.opcountand[Function.C_MEMORY]+" XOR "+Function.opcountxor[Function.C_MEMORY]+" NOT "+Function.opcountnot[Function.C_MEMORY]);
		System.out.println("CU");
		System.out.println("AND "+Function.opcountand[Function.C_CU]+" XOR "+Function.opcountxor[Function.C_CU]+" NOT "+Function.opcountnot[Function.C_CU]);
		System.out.println("ALU");
		System.out.println("AND "+Function.opcountand[Function.C_ALU]+" XOR "+Function.opcountxor[Function.C_ALU]+" NOT "+Function.opcountnot[Function.C_ALU]);
	}
	
	public static void loadMemory() throws Exception
	{
		for(int i=0;i<Memory.ARRAY_ROWS;i++)
			for(int j=0;j<Memory.WORD_SIZE;j++)
				Memory.cellarray[i][j]=rnd.nextInt()&62;
		
		BufferedReader r=new BufferedReader(new FileReader("test.obj"));
		
		int memrow=0;
		while(true)
		{
			String line=r.readLine();
			if(line==null)
				break;
			StringTokenizer st=new StringTokenizer(line," \n");
			
			if(line.contains("INITAC"))
			{
				st.nextToken();
				for(int i=0;i<8;i++)
				{
					String s=st.nextToken();
					CU.ac[i]=Integer.parseInt(s,16);
				}
				continue;
			}
			
			if(line.contains("INITPC"))
			{
				st.nextToken();
				for(int i=0;i<5;i++)
				{
					String s=st.nextToken();
					CU.pc[i]=Integer.parseInt(s,16);
				}
				continue;
			}
			for(int i=0;i<13;i++)
			{
				String bit=st.nextToken();
				Memory.cellarray[memrow][i]=Integer.parseInt(bit,16);				
			}
			memrow++;
		}
		

	}


	public static void memdump(int rows)
	{
		System.out.println("___DUMP_START___");
		for(int i=0;i<rows;i++)
		{
			int shift=1,val=0;
			System.out.print(i+"\t");
			for(int j=0;j<Memory.WORD_SIZE;j++)
			{
				if(j<8)
					if((Memory.cellarray[i][j]&1)==1)
						val+=shift;
				shift*=2;
				
				System.out.print((Memory.cellarray[i][j]&1)+" ");
			}
			System.out.println(" "+val);
		}
		System.out.println("___DUMP_END_____");
	}
	
	static String hex(int i)
	{
		String s=Integer.toHexString(i);
		if(s.length()==1)
			s="0"+s;
		
		return s;
	}
	
	static int decode(int[] adr)
	{
		int d=0;
		int mask=1;
		
		for(int i=0;i<adr.length;i++)
		{
			if((adr[i]&1)==1)
				d+=mask;
			mask*=2;
		}
//		System.out.println("decode="+d);
		return d;
	}
}
//int r0=and(and(and(not(a0),not(a1)),not(a2)),m0);
//or(or(or(or(or(or(or(r0,r1),r2),r3),r4),r5),r6),r7);
/*
 * memory cell circuit
 * 
 * in  m0..m7 memory bits
 * in  a0..a2 address bits
 * out o      addressed memory bit
 * 
 * o= !a0 & !a1 & !a2 & m0 |
 *     a0 & !a1 & !a2 & m1 |
 *    !a0 &  a1 & !a2 & m2 |
 *     a0 &  a1 & !a2 & m3 |
 *    !a0 & !a1 &  a2 & m4 |
 *     a0 & !a1 &  a2 & m5 |
 *    !a0 &  a1 &  a2 & m6 |
 *     a0 &  a1 &  a2 & m7 
 *     
 *     
 	public static void main(String[] args)
	{
		int ARRAY_ROWS=8;
		int ARRAY_COLS=Integer.bitCount(ARRAY_ROWS-1);
		int WORD_SIZE=4;
		
		System.out.println("static int[][] cellarray=new int["+ARRAY_ROWS+"]["+WORD_SIZE+"];");
		System.out.println("static int[] r=new int["+ARRAY_ROWS+"];");
		System.out.println("static int[] b=new int["+WORD_SIZE+"];");
		
		
		System.out.println("for(word=0;word<"+WORD_SIZE+";word++){");
		for(int i=0;i<ARRAY_ROWS;i++)
		{
			System.out.print("r["+i+"]=");
			for(int j=0;j<ARRAY_COLS;j++)
				System.out.print("Function.and(");
			
			int jmask=1;
			for(int j=0;j<ARRAY_COLS;j++)
			{
				if((jmask&i)==0)
					System.out.print("Function.not(");
				else
					System.out.print("(");
				
				System.out.print("a["+j+"])");
				
				if(j>=1)
					System.out.print(")");
				
				if(j<ARRAY_COLS-1)
					System.out.print(",");
				jmask<<=1;
			}
			
			System.out.println(",cellarray["+i+"]"+"[word]);");						
		}
		System.out.print("b[word]=Function.or(r[0],r[1]);");
		System.out.println("for(int i=2;i<"+ARRAY_ROWS+";i++){b[word]=Function.or(b[word],r[i]);}");
		System.out.println("}");
	}
	
	
	
			int p=BigInteger.probablePrime(8, rnd).intValue();
		int q=BigInteger.probablePrime(8, rnd).intValue();
		int n=p*q;
		
		int res=0;
		
		System.out.println("n="+n);
		System.out.println("p="+p);
		System.out.println("q="+q);
		
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		
		System.out.print("num >");
		res=Integer.parseInt(br.readLine())+(rnd.nextInt(100)*p);
		
		while(true)
		{
			System.out.println("  res="+res+" (="+((res%p)&1)+")");
			System.out.print("op  >");
			char op=br.readLine().charAt(0);
			if(op=='X')
				break;
			
		
			
			System.out.print("num >");
			int res2=Integer.parseInt(br.readLine())+(rnd.nextInt(100)*p);
			System.out.println("  num="+res2+" (="+((res2%p)&1)+")");
			
			switch(op)
			{
				case '+':res=(res+res2)%n;break;
				case '*':res=(res*res2)%n;break;			
			}
			
		}
*/