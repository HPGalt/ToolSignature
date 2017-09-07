package it.corvallis;
import it.corvallis.libsvm.svm;
import it.corvallis.libsvm.svm_model;
import it.corvallis.libsvm.svm_node;
import it.corvallis.libsvm.svm_parameter;
import it.corvallis.libsvm.svm_problem;
import libsvm.*;

public class svmFunctions {

	/* public static void main(String [] args) {

      // to create te model
      double[][] xtrain = ...
      double[][] ytrain = ...
      
      //to create the test verify      
      double[][] xtest = ...     
      double[][] ytest = ...

      svm_model m = svmTrain(xtrain,ytrain);

      double[] ypred = svmPredict(xtest, m); 
      for (int i = 0; i < xtest.length; i++){
          System.out.println("(Actual:" + ytest[i][0] + " Prediction:" + ypred[i] + ")"); 
      }  
	  }
	  */
  public svm_model calculateSVM(double[][] dataSet, double[][] lableSet){
	  svm_model prediction = null;
	  
	  // data seeds
	  int record =  dataSet.length;
	  int features = dataSet[0].length;
	  
	  double[][] ytrain = lableSet;
      double[][] xtrain = dataSet;
      
	  
	  
	  return prediction;
  }
	
	
  static svm_model svmTrain(double[][] xtrain, double[][] ytrain) {
        svm_problem prob = new svm_problem();
        int recordCount = xtrain.length;
        int featureCount = xtrain[0].length;
        prob.y = new double[recordCount];
        prob.l = recordCount;
        prob.x = new svm_node[recordCount][featureCount];     

        for (int i = 0; i < recordCount; i++){            
            double[] features = xtrain[i];
            prob.x[i] = new svm_node[features.length];
            for (int j = 0; j < features.length; j++){
                svm_node node = new svm_node();
                node.index = j;
                node.value = features[j];
                prob.x[i][j] = node;
            }           
            prob.y[i] = ytrain[i][0];
        }               

        svm_parameter param = new svm_parameter();
        param.probability = 0;
        param.gamma = (1/featureCount);
        //param.nu = 0.5;
        //param.C = 100;
        param.svm_type = svm_parameter.ONE_CLASS;
        param.kernel_type = svm_parameter.ONE_CLASS;       
        param.cache_size = 2000;
        param.eps = 0.0013;      

        svm_model model = svm.svm_train(prob, param);

        return model;
    }  

  static double[] svmPredict(double[][] xtest, svm_model model) 
  {

      double[] yPred = new double[xtest.length];

      for(int k = 0; k < xtest.length; k++){

        double[] fVector = xtest[k];

        svm_node[] nodes = new svm_node[fVector.length];
        for (int i = 0; i < fVector.length; i++)
        {
            svm_node node = new svm_node();
            node.index = i;
            node.value = fVector[i];
            nodes[i] = node;
        }

        int totalClasses = 2;       
        int[] labels = new int[totalClasses];
        svm.svm_get_labels(model,labels);

        double[] prob_estimates = new double[totalClasses];
        yPred[k] = svm.svm_predict_probability(model, nodes, prob_estimates);

      }

      return yPred;
  } 


}