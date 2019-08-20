package com.dfh.instance;

import com.dfh.parameters.*;

public class TestReader {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Parameters parm = new Parameters();
		parm.updateParameters(args);
		
		VrptwInstance vrptwIns = new VrptwInstance();
		vrptwIns.readInstanceFromFile(parm.getInputFileName());
		
		System.out.println(parm.toString());
		
		System.out.println(vrptwIns.customersToString());
		System.out.println(vrptwIns.distancesToString());

		
	}

}
