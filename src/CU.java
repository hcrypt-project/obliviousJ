/*
 * This class implements an encrypted control unit
 */

public class CU 
{
	final static boolean debug=true;
	
	static int[] pc=new int[Memory.ARRAY_COLS]; //0
	static int[] ac=new int[Memory.WORD_SIZE];
	static int[] b1=new int[Memory.WORD_SIZE];
	static int[] b_rol=new int[Memory.WORD_SIZE];
	static int[] b_ror=new int[Memory.WORD_SIZE];
	static int[] b_add=new int[Memory.WORD_SIZE];
	static int[] b_clc=new int[Memory.WORD_SIZE];
	static int[] b_sec=new int[Memory.WORD_SIZE];
	static int[] b_xor=new int[Memory.WORD_SIZE];
	static int[] b_and=new int[Memory.WORD_SIZE];
	static int[] b_or=new int[Memory.WORD_SIZE];
	//static int[] b_beq=new int[Memory.WORD_SIZE];
	static int[] b_cmp=new int[Memory.WORD_SIZE];
	static int cmd_store=0;
	static int cmd_rol=0;
	static int cmd_load=0;
	static int cmd_ror=0;
	static int cmd_add=0;
	static int cmd_clc=0;
	static int cmd_sec=0;
	static int cmd_xor=0;
	static int cmd_and=0;
	static int cmd_or=0;
	static int cmd_beq=0;
	static int cmd_jmp=0;
	static int cmd_la=0;
	static int cmd_bmi=0;
	static int cmd_cmp=0;
	static int cmd_a=0;
	static int cmd_index=0;
	static int cmd_arg=0;
	static int dumpid=0;
	
	static String[] command_list=new String[]{"NOP","CMP","BMI","La","J","BEQ","OR","AND","XOR","SEC","CLC","ADD","ROR","ROL","L","STa","NOPa","CMPa","BMIa","La","Ja","BEQa","ORa","ANDa","XORa","SECa","CLCa","ADDa","RORa","ROLa","La","STa"}; 
	
	public static long process()
	{
		long t=System.currentTimeMillis();
//		while(true)
		{
			if(debug) System.out.println("___START_PROCESS___");
			if(debug) dump("PC",pc,-1);
			if(debug) dump("AC",ac,ALU.carry);
			
//			ALU.minus=b1[7];
			//PHASE 1: fetch
			b1=Memory.access(pc,ac,Memory.READ);
			int[] load_arg=Memory.access(b1, ac, Memory.READ);
			if(debug) dump("load",b1,ALU.carry);
			if(debug) dump("aload",load_arg,ALU.carry);

			//PHASE 2: decode
			cmd_store=Function.and(Function.C_CU,b1[11],b1[10],b1[9],b1[8]);
			cmd_load=Function.and(Function.C_CU,b1[11],b1[10],b1[9],Function.not(Function.C_CU,b1[8]));
			cmd_rol=Function.and(Function.C_CU,b1[11],b1[10],Function.not(Function.C_CU,b1[9]),b1[8]);
			cmd_ror=Function.and(Function.C_CU,b1[11],b1[10],Function.not(Function.C_CU,b1[9]),Function.not(Function.C_CU,b1[8]));
			cmd_add=Function.and(Function.C_CU,b1[11],Function.not(Function.C_CU,b1[10]),b1[9],b1[8]);
			cmd_clc=Function.and(Function.C_CU,b1[11],Function.not(Function.C_CU,b1[10]),b1[9],Function.not(Function.C_CU,b1[8]));
			cmd_sec=Function.and(Function.C_CU,b1[11],Function.not(Function.C_CU,b1[10]),Function.not(Function.C_CU,b1[9]),b1[8]);
			cmd_xor=Function.and(Function.C_CU,b1[11],Function.not(Function.C_CU,b1[10]),Function.not(Function.C_CU,b1[9]),Function.not(Function.C_CU,b1[8]));
			cmd_and=Function.and(Function.C_CU,Function.not(Function.C_CU,b1[11]),b1[10],b1[9],b1[8]);
			cmd_or=Function.and(Function.C_CU,Function.not(Function.C_CU,b1[11]),b1[10],b1[9],Function.not(Function.C_CU,b1[8]));
			cmd_beq=Function.and(Function.C_CU,Function.not(Function.C_CU,b1[11]),b1[10],Function.not(Function.C_CU,b1[9]),b1[8]);
			cmd_jmp=Function.and(Function.C_CU,Function.not(Function.C_CU,b1[11]),b1[10],Function.not(Function.C_CU,b1[9]),Function.not(Function.C_CU,b1[8]));
			cmd_la=Function.and(Function.C_CU,Function.not(Function.C_CU,b1[11]),Function.not(Function.C_CU,b1[10]),b1[9],b1[8]);
			cmd_bmi=Function.and(Function.C_CU,Function.not(Function.C_CU,b1[11]),Function.not(Function.C_CU,b1[10]),b1[9],Function.not(Function.C_CU,b1[8]));
			cmd_cmp=Function.and(Function.C_CU,Function.not(Function.C_CU,b1[11]),Function.not(Function.C_CU,b1[10]),Function.not(Function.C_CU,b1[9]),b1[8]);
			cmd_a=b1[12];
			cmd_index=((b1[12]&1)*16)+((b1[11]&1)*8)+((b1[10]&1)*4)+((b1[9]&1)*2)+(b1[8]&1);
			cmd_arg=(b1[0]&1)+((b1[1]&1)*2)+((b1[2]&1)*4)+((b1[3]&1)*8)+((b1[4]&1)*16)+((b1[5]&1)*32)+((b1[6]&1)*64)+((b1[7]&1)*128);
			if(debug) System.out.println("CMD="+cmd_index+" ("+command_list[cmd_index]+" "+cmd_arg+")"); 
			//PHASE 3: execute
			
			//cmp
			for(int i=0;i<8;i++)
				b_cmp[i]=Function.not(Function.C_CU,Function.xor(Function.C_CU,Function.and(Function.C_CU,b1[i],Function.not(Function.C_CU,cmd_a)),Function.and(Function.C_CU,load_arg[i], cmd_a)));
			if(debug) dump("!cmp",b_cmp,-1);
			Object[] b_cmp_0=ALU.add(b_cmp,new int[]{1,2,2,2,2,2,2,2}, 2);
			b_cmp=(int[])b_cmp_0[0];
			if(debug) dump("-cmp",b_cmp,-1);
			if(debug) dump("  b1",b1,-1);
			b_cmp_0=ALU.add(b_cmp,ac,2);
			b_cmp=(int[])b_cmp_0[0];
			if(debug) dump("cmp",b_cmp,-1);
			
			//rol b21
			int carry_rol=ac[0];
			for(int i=0;i<7;i++)
				b_rol[i]=ac[i+1];
			b_rol[7]=ALU.carry;
			if(debug) dump("rol",b_rol,carry_rol);
			
			//ror b22
			int carry_ror=ac[7];
			for(int i=7;i>0;i--)
				b_ror[i]=ac[i-1];
			b_ror[0]=ALU.carry;
			if(debug) dump("ror",b_ror,carry_ror);
			
			//add b31
			Object[] temp31=ALU.add(ac,b1,ALU.carry);
			Object[] temp32=ALU.add(ac,load_arg,ALU.carry);
			int[] b_add_1=(int[])temp31[0];
			int[] b_add_2=(int[])temp32[0];
			
			for(int i=0;i<8;i++)
			{
				b_add[i]=Function.xor(Function.C_CU,Function.and(Function.C_CU,b_add_1[i],Function.not(Function.C_CU,cmd_a)),Function.and(Function.C_CU,b_add_2[i],cmd_a));
			}
			int carry_add=Function.xor(Function.C_CU,Function.and(Function.C_CU,((Integer)temp31[1]).intValue(),Function.not(Function.C_CU,cmd_a)),
						 			  Function.and(Function.C_CU,((Integer)temp32[1]).intValue(),cmd_a));
			
			if(debug) dump("add",b_add,carry_add);
			
			//clc b32
			int carry_clc=4;
			b_clc=ac;			
			if(debug) dump("clc",ac,carry_clc);
			//sec b33
			int carry_sec=3;
			b_sec=ac;
			if(debug) dump("sec",ac,carry_sec);
			
			//xor b41
			for(int i=0;i<8;i++)
				b_xor[i]=Function.xor(Function.C_ALU,ac[i], b1[i]);
			if(debug) dump("xor",b_xor,ALU.carry);
			//and b42
			for(int i=0;i<8;i++)
				b_and[i]=Function.and(Function.C_ALU,ac[i], b1[i]);
			if(debug) dump("and",b_and,ALU.carry);
			//or b43
			for(int i=0;i<8;i++)
				b_or[i]=Function.or(Function.C_ALU,ac[i], b1[i]);
			if(debug) dump("or",b_or,ALU.carry);			
			
			//PHASE 4: load/store
			int[] load_val=Memory.access(b1, ac, Function.not(Function.C_CU,cmd_store));
			
			//PHASE 5: rewrite registers / flags
			for(int i=0;i<8;i++)
			{
				ac[i]=Function.xor(Function.C_CU,Function.or(Function.C_CU,Function.or(Function.C_CU,Function.or(Function.C_CU,Function.or(Function.C_CU,Function.or(Function.C_CU,Function.or(Function.C_CU,Function.or(Function.C_CU,Function.or(Function.C_CU,Function.or(Function.C_CU,Function.or(Function.C_CU,Function.or(Function.C_CU,Function.or(Function.C_CU,Function.or(Function.C_CU,
					  Function.and(Function.C_CU,b1[i], cmd_load),
					  Function.and(Function.C_CU,b_ror[i],cmd_ror)),
					  Function.and(Function.C_CU,b_rol[i],cmd_rol)),
					  Function.and(Function.C_CU,b_sec[i],cmd_sec)),
					  Function.and(Function.C_CU,b_clc[i],cmd_clc)),
					  Function.and(Function.C_CU,b_add[i],cmd_add)),
					  Function.and(Function.C_CU,b_and[i],cmd_and)),
					  Function.and(Function.C_CU,b_xor[i],cmd_xor)),
					  Function.and(Function.C_CU,b_or[i],cmd_or)),
					  Function.and(Function.C_CU,ac[i],cmd_store)),
					  Function.and(Function.C_CU,load_val[i],cmd_la)),
					  Function.and(Function.C_CU,ac[i],cmd_beq)),
					  Function.and(Function.C_CU,ac[i],cmd_bmi)),
					  Function.and(Function.C_CU,ac[i],cmd_cmp)),
					  Function.and(Function.C_CU,ac[i],cmd_jmp));
			}
			
			int zero1=Function.and(Function.C_CU,Function.and(Function.C_CU,Function.and(Function.C_CU,Function.and(Function.C_CU,Function.and(Function.C_CU,Function.and(Function.C_CU,Function.and(Function.C_CU,
					 Function.not(Function.C_CU,ac[0]),
					 Function.not(Function.C_CU,ac[1])),
					 Function.not(Function.C_CU,ac[2])),
					 Function.not(Function.C_CU,ac[3])),
					 Function.not(Function.C_CU,ac[4])),
					 Function.not(Function.C_CU,ac[5])),
					 Function.not(Function.C_CU,ac[6])),
					 Function.not(Function.C_CU,ac[7]));
			int zero2=Function.and(Function.C_CU,Function.and(Function.C_CU,Function.and(Function.C_CU,Function.and(Function.C_CU,Function.and(Function.C_CU,Function.and(Function.C_CU,Function.and(Function.C_CU,
					 Function.not(Function.C_CU,b_cmp[0]),
					 Function.not(Function.C_CU,b_cmp[1])),
					 Function.not(Function.C_CU,b_cmp[2])),
					 Function.not(Function.C_CU,b_cmp[3])),
					 Function.not(Function.C_CU,b_cmp[4])),
					 Function.not(Function.C_CU,b_cmp[5])),
					 Function.not(Function.C_CU,b_cmp[6])),
					 Function.not(Function.C_CU,b_cmp[7]));
			ALU.zero=Function.xor(Function.C_CU,Function.and(Function.C_CU,zero1,Function.not(Function.C_CU,cmd_cmp)), Function.or(Function.C_CU,
					 Function.and(Function.C_CU,zero2,cmd_cmp),
					 Function.and(Function.C_CU,ALU.zero,cmd_bmi),
					 Function.and(Function.C_CU,ALU.zero,cmd_beq)));
//			if(debug) System.out.println("zero ac="+zero1+" zero cmp="+zero2+" alu.zero="+ALU.zero);
			
			
			ALU.minus=Function.or(Function.C_CU,Function.and(Function.C_CU,ALU.minus, Function.not(Function.C_CU,cmd_cmp)), Function.and(Function.C_CU,b_cmp[7], cmd_cmp));
//			if(debug) System.out.println("alu.minus="+ALU.minus+" b_cmp7="+b_cmp[7]);
			
			
			carry_add=Function.and(Function.C_CU,carry_add, cmd_add);
			carry_rol=Function.and(Function.C_CU,carry_rol, cmd_rol);
			carry_ror=Function.and(Function.C_CU,carry_ror, cmd_ror);
			carry_clc=Function.and(Function.C_CU,carry_clc, cmd_clc);
			carry_sec=Function.and(Function.C_CU,carry_sec, cmd_sec);
			int carry_non=Function.and(Function.C_CU,Function.and(Function.C_CU,Function.not(Function.C_CU,cmd_add),Function.not(Function.C_CU,cmd_rol),Function.not(Function.C_CU,cmd_ror),Function.not(Function.C_CU,cmd_clc)),Function.not(Function.C_CU,cmd_sec),ALU.carry);
			ALU.carry=Function.or(Function.C_CU,Function.or(Function.C_CU,Function.or(Function.C_CU,Function.or(Function.C_CU,Function.or(Function.C_CU,carry_add,carry_rol),carry_ror),carry_clc),carry_sec),carry_non);					
			
			int pc_linear[]=ALU.addadr(pc,Function.encode(8, 1));
			//int pc_branch[]=ALU.addadr(pc,b1);
			int pc_jump[]=b1;
			int pc_branch[]=b1;
			
			if(debug) dump("PC lin",pc_linear,-1);
			if(debug) dump("PC bra",pc_branch,-1);
			if(debug) dump("PC jmp",pc_jump,-1);
			
			for(int i=0;i<Memory.ARRAY_COLS;i++)
			{
//				System.out.println("pc["+i+"]=and("+cmd_beq+","+pc_branch[i]+","+ALU.zero+") or and("+Function.not(Function.C_CU,cmd_beq)+","+pc_linear[i]+")");
				pc[i]=Function.xor(Function.C_CU,Function.or(Function.C_CU,					
								  Function.and(Function.C_CU,cmd_beq,pc_branch[i],ALU.zero),
								  Function.and(Function.C_CU,cmd_bmi,pc_branch[i],ALU.minus),
								  Function.and(Function.C_CU,Function.not(Function.C_CU,cmd_bmi),Function.not(Function.C_CU,cmd_beq),Function.not(Function.C_CU,cmd_jmp),pc_linear[i])),
								  Function.and(Function.C_CU,cmd_beq,Function.not(Function.C_CU,ALU.zero),pc_linear[i]),
								  Function.and(Function.C_CU,cmd_bmi,Function.not(Function.C_CU,ALU.minus),pc_linear[i]),
								  Function.and(Function.C_CU,cmd_jmp,pc_jump[i]));
			}
			
			if(debug) dump("PC",pc,-1);
			if(debug) dump("AC",ac,ALU.carry);
			if(debug) System.out.println("zero="+(ALU.zero&1));
			
			if(debug) System.out.println("____END_PROCESS____");
		}		
		return System.currentTimeMillis()-t;
	}
	
	public static void dump(String cmd,int[] reg,int carry)
	{
		int max=8;
		if(reg.length<max)
			max=reg.length;
		
		System.out.print("("+(dumpid++)+") "+cmd+":");
		for(int i=0;i<max;i++)
			System.out.print(Function.decode(reg[i])+" ");
		if(carry>0)
			System.out.println(" carry="+Function.decode(carry));
		else
			System.out.println();
	}
}
