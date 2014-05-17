package com.borisp.faces.classifiers.nearest_n;

import com.borisp.faces.classifiers.attribute_weighting.ReliefF;
import com.borisp.faces.classifiers.examples.Example;

/**
 * A class implementing weighted nearest neighbors with weighted attributes algorithm
 *
 * @author Anton
 */
public class WeightedNearestNeighboursWithWeightedAttributes extends
		WeightedNearestNeighbours {
	
	protected double[] attributeWeights;
	
	public WeightedNearestNeighboursWithWeightedAttributes(int numClasses) {
		super(numClasses);
	}
	
	@Override 
	public void learnExamples(Example[] examples) {		
		ReliefF reliefF = new ReliefF(numClasses);
		this.examples = examples;
        int bestK = 0;
        int bestCnt = 0;
        double[] bestAttributeWeights = null;
		reliefF.setIterationsCount(examples.length*8);
		for (int j = 1; j < examples.length - 1; j++) {
			
			this.attributeWeights = reliefF.weightAttributes(examples, j);
	        for (int i = 1; i < examples.length - 1; i++) {
	            int cnt = 0;
	            this.k = i;
	            for (Example example : examples) {
	                if (classifyExampleHelper(example, true) == example.classification) {
	                    cnt++;
	                }
	            }
	            if (cnt > bestCnt) {
	                bestCnt = cnt;
	                bestK = i;
	                bestAttributeWeights = attributeWeights;
	            }
	        }
		}
	
		this.attributeWeights = bestAttributeWeights;
        this.k = bestK;
	}
	
	@Override 
	protected double distance(Example ex1, Example ex2) {
	 	double sum = 0;
        for (int i = 0; i < ex1.measures.length; i++) {
            sum += attributeWeights[i] *attributeWeights[i] * (ex1.measures[i] - ex2.measures[i]) * (ex1.measures[i] - ex2.measures[i]);
        }
	        
		return Math.sqrt(sum);
	}
}
