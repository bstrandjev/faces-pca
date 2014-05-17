package com.borisp.faces.classifiers.attribute_weighting;
import java.lang.Math;
import java.util.*;

import com.borisp.faces.classifiers.examples.Example;

import edu.wlu.cs.levy.CG.Checker;
import edu.wlu.cs.levy.CG.KDTree;

/**
 * A class implementing ReliefF algorithm for weighting the attributes of an example
 *
 * @author Anton
 */
public class ReliefF 
{
	private Example[] instances;
    private int iterationsCount;
    private int neighboursCount;
    private int classesCount;
    private Random randomGenerator;
    private KDTree<Example> searchTree;
    
    private HashMap<Integer, Double> attributeMinHashMap;
    private HashMap<Integer, Double> attributeMaxHashMap;
    private HashMap<Integer, Double> classPorbabilityHashMap;
    
    /**
     * @param classesCount The number of classes of classification.
     */
    public ReliefF(int classesCount)
    {    	
    	this.classesCount = classesCount;
    	this.randomGenerator = new Random(System.currentTimeMillis());    	
    }
    /**
     * 
     * @param iterationsCount The number of iterations of the ReliefF algorithm
     */
    public void setIterationsCount(int iterationsCount)
    {
    	this.iterationsCount = iterationsCount;
    }
    
    /**
     * 
     * @param instances The examples from the training set.
     * @param neighboursCount The count of nearest hit/misses used by ReliefF
     * @return Weights of all of the attributes
     */
    public double[] weightAttributes(Example[] instances, int neighboursCount)
    {        
    	init(instances, neighboursCount);  	
    	int attributesCount = getAttributesCount();
    	double[] weights = new double [attributesCount];
   
    	for(int i=0;i<iterationsCount;i++)
    	{
    		int rand = getRandom(0 , instances.length - 1);
    		Example currentInstance = instances[rand];
    		int currentInstanceClassId = currentInstance.classification;
    		
			for(int classId=0;classId < classesCount;classId++)
			{
				Example[] neighbours = getNearestNeghbours(currentInstance, classId);
				
			    for(int a = 0; a < attributesCount ;a++)
	    		{
					double delta = 0;
					for (Example neighbour : neighbours)
	    			{
	    				double currentDelta = diff(currentInstance, neighbour, a);
	    				if(classId != currentInstanceClassId)
	    				{
	    					delta += currentDelta*getClassProbability(classId)/(1-getClassProbability(currentInstanceClassId));
	    				}
	    				else
	    				{
		    				delta -= currentDelta;	    					
	    				}
	    			}
					
					weights[a] += delta / (iterationsCount*neighboursCount);
	    		}   
			}  		
    	}
    	    	
    	for(double weight :weights)
    	{
    		if(weight<0)
    		{
    			weight=0;
    		}
    	}
    	
    	
        return weights;
    }
        
    private void init(Example[] instances, int neighboursCount)
    {
    	this.instances = instances;
    	this.neighboursCount = neighboursCount; 	
    	initSearchTree();
    	initHashes();
    }
        
    private double diff(Example a, Example b, int attributeNumber)
    {
        double diff = 0;
     
        double delta = (getAttributeMax(attributeNumber) - getAttributeMin(attributeNumber));
    	if(delta != 0)
    	{
    		diff = Math.abs(a.measures[attributeNumber] - b.measures[attributeNumber])/delta;
    	}
      
        return diff;
    }
  
 
    private Example[] getNearestNeghbours(final Example instance,final int classId)
    {
    	Checker<Example> checker = new Checker<Example>()
    	{
    		@Override public boolean usable(Example inst)
    		{
    			return !inst.equals(instance) && inst.classification == classId;
    		}
    	};
    	
    	List<Example> nearestNeghbours = null;    	
    	try {
			nearestNeghbours = searchTree.nearest(instance.measures, neighboursCount, checker);
		} catch (Exception e) {}
    	
    	Example[] instances = new Example[nearestNeghbours.size()];   
    	nearestNeghbours.toArray(instances);	
        return instances;
    }
 
    private int getRandom(int min, int max)
    {
        
        int randomNum = randomGenerator.nextInt((max - min) + 1) + min;

        return randomNum;
    }
  
    private double getClassProbability(int classId)
    {  	
    	return classPorbabilityHashMap.get(classId);
    }
        
    private int getAttributesCount()
    {
    	return instances[0].measures.length;
    }
    
    private double getAttributeMin(int attributeNumber)
    {
    	return attributeMinHashMap.get(attributeNumber);
    }

    private double getAttributeMax(int attributeNumber)
    {    	    	
    	return attributeMaxHashMap.get(attributeNumber);
    }
    
    private void initHashes()
    {
    	initAttributesHashes();
    	initClassProbabilityHash();
    }
    
    private void initAttributesHashes()
    {
    	attributeMinHashMap = new HashMap<Integer, Double>();
    	attributeMaxHashMap = new HashMap<Integer, Double>();
    	for(int i=0;i<getAttributesCount();i++)
    	{
	    	double minValue = instances[0].measures[i];
	    	double maxValue = instances[0].measures[i];
	    	for(Example instance : instances)
	    	{
	    		double currentValue = instance.measures[i];
	    		if(minValue>currentValue)
	    		{
	    			minValue = currentValue;
	    		}
	    		if(maxValue<currentValue)
	    		{
	    			maxValue = currentValue;
	    		}
	    	}
	    	attributeMinHashMap.put(i, minValue);
	    	attributeMaxHashMap.put(i, maxValue);
	    }
    }
    
    private void initClassProbabilityHash()
    {
    	classPorbabilityHashMap=new HashMap<Integer, Double>();
    	for(int i=0;i<classesCount ;i++)
    	{
    		int classCount = 0;
        	for(Example instance : instances)
        	{
        		if(instance.classification == i)
        		{
        			classCount++;
        		}
        	}
        	
	    	classPorbabilityHashMap.put(i, classCount/(double)instances.length);
	    }
    }
    
    private void initSearchTree()
    {
    	searchTree = new KDTree<>(getAttributesCount());
    	for(Example instance : instances)
    	{   	
    		try {
    			searchTree.insert(instance.measures, instance);
			} catch (Exception e) {} 
    	}	
    }
}
