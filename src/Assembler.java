import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.StringTokenizer;


public class Assembler 
{
	public static void main(String[] args) throws Exception
	{
		BufferedReader r=new BufferedReader(new FileReader("test.asm"));
		BufferedWriter w=new BufferedWriter(new FileWriter("test.obj"));
		
		System.out.println("PASS 1");
		int org=0;
		HashMap<String,Integer> labels=new HashMap<String,Integer>();
		while(true)
		{
			String label=null;
			String line=r.readLine();
			if(line==null)
				break;
			
			if(line.contains("INITAC")||line.contains("INITPC"))
				continue;
			
			StringTokenizer st=new StringTokenizer(line," \t\n;");
			
			if(st.countTokens()==0)
				continue;
			
			if(st.countTokens()==2)
			{
				if(!(line.charAt(0)==' '||line.charAt(0)=='\t'))
				{
					label=st.nextToken();
					labels.put(label, new Integer(org));
					System.out.println("symbol "+label+"="+org);
				}
			}
			else if(st.countTokens()>2)
			{
				label=st.nextToken();
				labels.put(label, new Integer(org));
				System.out.println("symbol "+label+"="+org);
			}
			org++;
		}
		
		System.out.println("PASS 2");
		r.close();
		
		r=new BufferedReader(new FileReader("test.asm"));
		while(true)
		{
			String label=null;
			
			String line=r.readLine();
			if(line==null)
				break;
			
			StringTokenizer st=new StringTokenizer(line," \n\t;");
			
			if(line.contains("INITAC"))
			{
				st.nextToken();
				String ac_string=st.nextToken();
				
				int[] ac=Function.encode(8, Integer.parseInt(ac_string));
				
				System.out.print("INITAC ");
				w.write("INITAC ");
				for(int i=0;i<8;i++)
				{
					System.out.print(hex(ac[i])+" ");
					w.write(hex(ac[i])+" ");
				}
				w.write("\n");
				System.out.println();
				continue;
			}

			if(line.contains("INITPC"))
			{
				String s=st.nextToken();
				s=st.nextToken();
				if(labels.containsKey(s))
					s=labels.get(s).toString();
				
				int[]pc=Function.encode(5,Integer.parseInt(s));
				
				System.out.print("INITPC ");
				w.write("INITPC ");
				for(int i=0;i<5;i++)
				{
					System.out.print(hex(pc[i])+" ");
					w.write(hex(pc[i])+" ");
				}
				w.write("\n");
				System.out.println();
				continue;
			}

			if(st.countTokens()==0)
				continue;
			
			if(st.countTokens()>2||(st.countTokens()==2&&!(line.charAt(0)==' '||line.charAt(0)=='\t')))
				label=st.nextToken();
			String opcode=st.nextToken();
			String operand=null;
			try{
				operand=st.nextToken();
			}catch(Exception e)
			{
				operand=null;
			}
			
			String lookup=null;
			if(operand==null)
				operand="0";
				
			Integer lookupInt=labels.get(operand);
			if(lookupInt!=null)
				lookup=lookupInt.toString();
				
			System.out.println(label+","+opcode+","+operand+"("+lookup+")");
			
			int[] operation=null;

			int cmd=0;
			
			if(opcode.equals("STa"))
				cmd=15;
			if(opcode.equals("L"))
				cmd=14;
			if(opcode.equals("ROL"))
				cmd=13;
			if(opcode.equals("ROR"))
				cmd=12;
			if(opcode.startsWith("ADD"))
				cmd=11;
			if(opcode.equals("CLC"))
				cmd=10;
			if(opcode.equals("SEC"))
				cmd=9;
			if(opcode.startsWith("XOR"))
				cmd=8;
			if(opcode.startsWith("AND"))
				cmd=7;
			if(opcode.startsWith("OR"))
				cmd=6;
			if(opcode.equals("BEQ"))
				cmd=5;
			if(opcode.equals("J"))
				cmd=4;
			if(opcode.equals("La"))
				cmd=3;
			if(opcode.equals("BMI"))
				cmd=2;
			if(opcode.startsWith("CMP"))
				cmd=1;
			
			if(opcode.charAt(opcode.length()-1)=='a')
				cmd+=16;
			
			operation=Function.encode(5, cmd);
			
			int[] argument=Function.encode(8, Integer.parseInt(lookup!=null?lookup:operand));
			
			for(int i=0;i<8;i++)
			{
				System.out.print(hex(argument[i])+" ");
				w.write(hex(argument[i])+" ");
			}
			for(int i=0;i<5;i++)
			{
				System.out.print(hex(operation[i])+" ");
				w.write(hex(operation[i])+" ");
			}
			System.out.println("\t\t"+line);
			w.write("\n");
		}
		r.close();
		w.close();
	}
	
	static String hex(int i)
	{
		String s=Integer.toHexString(i);
		if(s.length()==1)
			s="0"+s;
		
		return s;
	}

}
