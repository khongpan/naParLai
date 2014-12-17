package th.or.nectec.naparlai;




public class DataCollector {
	
	int size;
	
	double latitude;
	double longitude;
	double[] val;
	char[] pos;
	
	int index;
	
	public DataCollector(int require_size)
	{
		size=require_size;
		val = new double[size];
		pos = new char[size];
	}
	
	public void reset()
	{
		int i;

		index=0;
		for (i=0;i<size;i++)
		{
			val[i] = 0;
		}
	
	}

	public void addNewData(double new_val, char new_pos)
	{
		val[index] = new_val;
		pos[index] = new_pos;
		index++;
	}
	
	
	public double getAvg(char req_pos)
	{
		int count = 0;
		double sum = 0;
		for(int i=0;i<index;i++){
			if(pos[i]==req_pos){
				sum += val[i];
				count++;
			}
		}
		if (count==0) return 0.0; else return (sum/count);
	}
	
	public String getReport()
	{
		String report;
		
		int count = 0;
		
		report = "Total sampling point " + index + "\n"; 
		
		for(int i=0;i<index;i++){

				report+= "Sampling no" + i +" "+ pos[i] +" "+ val[i] + " umol/m2/sec\n";
				
		} 
		
		return report;
	}
	


	
	
}
