/*
 * This class implements encrypted memory access.
 * 
 * input
 * -----
 * cellarray 	is an array of encrypted memory bit values
 * a			is the encrypted memory address
 * 
 * output
 * ------
 * b			is the encrypted word at the given address
 * 
 * Access is realized as a series of 1-out-of-n-bit boolean data selectors 
 * 
 * Example for 3 address lines and 7 single-column memory rows:
 * 
 * ------and-circuit---->
 * 
 * o= !a0 & !a1 & !a2 & m0 | \		|
 *     a0 & !a1 & !a2 & m1 | \		|
 *    !a0 &  a1 & !a2 & m2 | \		|
 *     a0 &  a1 & !a2 & m3 | \		or-circuit
 *    !a0 & !a1 &  a2 & m4 | \		|
 *     a0 & !a1 &  a2 & m5 | \		|
 *    !a0 &  a1 &  a2 & m6 | \		|
 *     a0 &  a1 &  a2 & m7 			\/
 * 
 * Writing to a cell applies the following assumption: after read or write
 * access, the register and the cell have the same values, so the function
 * for a cell-bit and a register-bit in dependency to a READ/WRITE signal 
 * is:
 * 
 * reg=cell= (cell & WRITE) | (reg & READ)
 * 
 * this can be derived from the following truth table
 * 
 * R = register bit
 * M = memory (cell bit)
 * W = write signal (opposite common circuit logic: 0=read 1=write)
 * 
 * R M W  R' M'
 * ------------
 * 0 0 0  0  0
 * 0 0 1  0  0
 * 0 1 0  1  1
 * 0 1 1  0  0
 * 1 0 0  0  0
 * 1 0 1  1  1
 * 1 1 0  1  1
 * 1 1 1  1  1
 * 
 * this can be simplified to R'=M'=(C and !W) or (R and W)
 * 
 * assuming that the cell has been selected (and-circuit)
 */
public class Memory 
{
	static final int WORD_SIZE=13;
	static final int ARRAY_ROWS=256;
	static final int ARRAY_COLS=Integer.bitCount(ARRAY_ROWS-1);
	static final int READ=39;
	static final int WRITE=42;
	
	public static int[][] cellarray=new int[ARRAY_ROWS][WORD_SIZE];
	
	public static int[] access(int[] a,int[] reg,int rw)
	{
		int[] r=new int[ARRAY_ROWS];
		int[] b1=new int[WORD_SIZE];

		
		//generate row selects	(and-circuit)
		for(int row=0;row<ARRAY_ROWS;row++)
		{
			int mask=4;
				
			r[row]=Function.and(Function.C_MEMORY,(row&1)>0?a[0]:Function.not(Function.C_MEMORY,a[0]),(row&2)>0?a[1]:Function.not(Function.C_MEMORY,a[1]));				
			for(int m=2;m<ARRAY_COLS;m++,mask<<=1)
				r[row]=Function.and(Function.C_MEMORY,r[row],(row&mask)>0?a[m]:Function.not(Function.C_MEMORY,a[m]));
		}
		
		//write into selected cells (reg=cell=(cell & !read | reg & read))
		for(int row=0;row<ARRAY_ROWS;row++)
		{
			for(int i=0;i<8;i++)
				cellarray[row][i]=Function.xor(Function.C_MEMORY,Function.and(Function.C_MEMORY,r[row],Function.not(Function.C_MEMORY,rw),reg[i]),
											   Function.and(Function.C_MEMORY,r[row],rw,cellarray[row][i]),
											   Function.and(Function.C_MEMORY,Function.not(Function.C_MEMORY,r[row]), cellarray[row][i]));
//			for(int i=8;i<ARRAY_COLS;i++)
//				cellarray[row][i]=Function.or(Function.and(r[row],Function.not(rw),reg[i]),
//											  Function.and(r[row],rw,cellarray[row][i]),
//											  Function.and(Function.not(r[row]), cellarray[row][i]));
		}
//		System.out.println();
		//combine row signals and cell bits (or-circuit)
		//load b1
		for(int i=0;i<WORD_SIZE;i++)
		{
			b1[i]=Function.xor(Function.C_MEMORY,Function.and(Function.C_MEMORY,r[0],cellarray[0][i]),Function.and(Function.C_MEMORY,r[1],cellarray[1][i]));
			for(int j=2;j<ARRAY_ROWS;j++)
				b1[i]=Function.xor(Function.C_MEMORY,b1[i],Function.and(Function.C_MEMORY,r[j],cellarray[j][i]));
		}
		
		return b1;
	}
}